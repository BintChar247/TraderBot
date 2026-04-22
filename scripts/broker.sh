#!/usr/bin/env bash
# scripts/broker.sh — IDX Broker API Wrapper
#
# This is the ONLY way the agent touches the broker. Never call broker APIs
# with curl directly. All auth, mode enforcement, and guardrails live here.
#
# Usage:
#   broker.sh portfolio              Print equity, cash, positions (JSON)
#   broker.sh quote SYMBOL           Last price, avg daily volume, lot size
#   broker.sh buy SYMBOL SHARES      Buy (gate-checked); places 10% trailing stop
#   broker.sh sell SYMBOL SHARES     Sell shares of SYMBOL
#   broker.sh set-stop SYMBOL PCT    Set/replace trailing stop at PCT% below price
#   broker.sh positions              List open positions only
#   broker.sh cash                   Print available cash (IDR integer)
#   broker.sh -h | --help            Show this help
#
# Environment (sourced from .env if present, else inherited):
#   TRADING_MODE         "paper" (default) or "live"
#   BROKER_API_KEY       API key from your IDX broker
#   BROKER_API_SECRET    API secret (used for HMAC signing in live mode)
#   BROKER_ACCOUNT_ID    Account ID for order and position requests
#   BROKER_BASE_URL      Base URL (default: https://api.example-broker.id/v1)
#
# Layer-2 guardrail note:
#   The agent's prompt (CLAUDE.md + TRADING-STRATEGY.md) is Layer 1.
#   This script is Layer 2. The buy subcommand unconditionally calls
#   gate-check.sh before any order reaches the broker. Even if the
#   prompt layer is bypassed or the agent reasons past it, no buy order
#   can pass without clearing all 9 gates enforced in code.

set -euo pipefail

# ---- locate repo root so relative paths work wherever script is called ----

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# ---- load .env if present (local dev only; cloud routines use env vars) ---

if [[ -f "${REPO_ROOT}/.env" ]]; then
  # shellcheck disable=SC1091
  set -o allexport
  source "${REPO_ROOT}/.env"
  set +o allexport
fi

# ---- configuration --------------------------------------------------------

TRADING_MODE="${TRADING_MODE:-paper}"
BROKER_BASE_URL="${BROKER_BASE_URL:-https://api.example-broker.id/v1}"
BROKER_API_KEY="${BROKER_API_KEY:-}"
BROKER_API_SECRET="${BROKER_API_SECRET:-}"
BROKER_ACCOUNT_ID="${BROKER_ACCOUNT_ID:-}"

# ---- mode validation ------------------------------------------------------

if [[ "${TRADING_MODE}" != "paper" && "${TRADING_MODE}" != "live" ]]; then
  echo "ERROR: TRADING_MODE='${TRADING_MODE}' is invalid. Must be 'paper' or 'live'." >&2
  exit 2
fi

# ---- helpers --------------------------------------------------------------

die()  { echo "ERROR: $*" >&2; exit 1; }
warn() { echo "WARN:  $*" >&2; }
info() { echo "INFO:  $*" >&2; }

require_live_env() {
  local missing=()
  for v in BROKER_API_KEY BROKER_API_SECRET BROKER_ACCOUNT_ID; do
    [[ -n "${!v:-}" ]] || missing+=("$v")
  done
  if [[ ${#missing[@]} -gt 0 ]]; then
    die "Live mode requires env vars not set: ${missing[*]}"
  fi
}

# _broker_api METHOD PATH [BODY]
# Paper mode: echo the call details and return a stub JSON response.
# Live mode:  call the real broker REST endpoint via curl with bearer auth.
# TODO: adapt _broker_api authentication to your broker's actual scheme.
#       Common patterns: Bearer token, HMAC-SHA256 signed headers, Basic auth.
_broker_api() {
  local method="$1"
  local path="$2"
  local body="${3:-}"

  if [[ "${TRADING_MODE}" == "paper" ]]; then
    info "PAPER MODE stub: ${method} ${BROKER_BASE_URL}${path}"
    echo '{"paper_mode":true,"status":"stub","note":"TODO: wire to real broker"}'
    return 0
  fi

  require_live_env

  # Build authentication headers.
  # TODO: replace with your broker's actual signing spec exactly.
  local timestamp
  timestamp="$(date -u +"%Y-%m-%dT%H:%M:%SZ")"

  local sig_payload="${timestamp}${method}${path}${body}"
  local signature
  signature="$(printf '%s' "${sig_payload}" \
    | openssl dgst -sha256 -hmac "${BROKER_API_SECRET}" \
    | awk '{print $2}')"

  local curl_args=(
    --silent
    --show-error
    --fail-with-body
    -X "${method}"
    -H "Content-Type: application/json"
    -H "Authorization: Bearer ${BROKER_API_KEY}"
    -H "X-Timestamp: ${timestamp}"
    -H "X-Signature: ${signature}"
    -H "X-Account-Id: ${BROKER_ACCOUNT_ID}"
  )
  [[ -n "${body}" ]] && curl_args+=(-d "${body}")

  curl "${curl_args[@]}" "${BROKER_BASE_URL}${path}"
}

# ---- paper-mode stub responses --------------------------------------------

# ---- paper ledger helpers (read/write PAPER-STATE.json) ------------------
# All paper subcommands now persist state so performance.py and gate-check
# see real P&L progression throughout the trial, not hard-coded stubs.

PAPER_STATE="${REPO_ROOT}/memory/PAPER-STATE.json"
STOPS_FILE="${REPO_ROOT}/memory/STOPS.json"

_paper_state_read() {
  if [[ -f "${PAPER_STATE}" ]]; then
    cat "${PAPER_STATE}"
  else
    echo '{"mode":"paper","starting_capital":10000000000,"cash":10000000000,"realised_pnl":0,"positions":[],"closed_trades":[]}'
  fi
}

_paper_state_write() {
  local json="$1"
  echo "${json}" | python3 -c "import json,sys; print(json.dumps(json.load(sys.stdin), indent=2, ensure_ascii=False))" \
    > "${PAPER_STATE}" 2>/dev/null || echo "${json}" > "${PAPER_STATE}"
}

_paper_portfolio() {
  local state
  state="$(_paper_state_read)"
  python3 -c "
import json, subprocess, sys
d = json.loads('''${state}''')
starting = d.get('starting_capital', 10000000000)
cash = d.get('cash', starting)
realised = d.get('realised_pnl', 0)
# Compute unrealised from live quotes (best-effort; falls back to entry cost)
unrealised = 0
positions_out = []
for pos in d.get('positions', []):
    sym = pos['ticker']
    ep  = pos['entry_price']
    sh  = pos['shares']
    # Try market-data.sh quote; fall back to entry price on failure
    try:
        res = subprocess.run(['${SCRIPT_DIR}/market-data.sh','quote',sym],
                             capture_output=True, text=True, timeout=10)
        q = json.loads(res.stdout)
        last = int(q.get('last') or q.get('last_price') or ep)
    except Exception:
        last = ep
    unreal = (last - ep) * sh
    unrealised += unreal
    positions_out.append({**pos, 'last_price': last, 'unrealised_idr': unreal,
                           'unrealised_pct': round((last-ep)/ep*100,2) if ep else 0})

equity = cash + sum(p['entry_price']*p['shares'] for p in d.get('positions',[])) + unrealised
print(json.dumps({
    'mode': 'paper',
    'account_id': 'PAPER_ACCT',
    'equity': int(equity),
    'cash': int(cash),
    'buying_power': int(cash),
    'currency': 'IDR',
    'positions': positions_out,
    'realised_pnl': realised,
    'unrealised_pnl': int(unrealised),
}, indent=2))
" 2>/dev/null || python3 -c "
import json
d = json.loads(open('${PAPER_STATE}').read()) if __import__('os').path.exists('${PAPER_STATE}') else {}
eq = d.get('cash', 10000000000)
print(json.dumps({'mode':'paper','account_id':'PAPER_ACCT','equity':eq,'cash':eq,'buying_power':eq,'currency':'IDR','positions':d.get('positions',[])},indent=2))
"
}

_paper_positions() {
  local state
  state="$(_paper_state_read)"
  python3 -c "
import json
d = json.loads('''${state}''')
positions = d.get('positions', [])
print(json.dumps({'mode':'paper','positions':positions,'count':len(positions)},indent=2))
" 2>/dev/null || echo '{"mode":"paper","positions":[],"count":0}'
}

_paper_quote() {
  local sym="$1"
  # Live quote via market-data.sh (GoAPI → yfinance). If that fails, try to
  # return the last-known entry price from PAPER-STATE.json for an existing
  # position. Never return a hardcoded stub — that would corrupt gate checks.
  local result
  result="$("${SCRIPT_DIR}/market-data.sh" quote "${sym}" 2>/dev/null || true)"
  if [[ -n "${result}" ]] && echo "${result}" | python3 -c "import json,sys; d=json.load(sys.stdin); sys.exit(0 if d.get('last') or d.get('last_price') else 1)" 2>/dev/null; then
    echo "${result}"
    return 0
  fi

  # Fallback: last-known entry price for a held position
  local last_known
  last_known="$(python3 -c "
import json, os, sys
path = '${PAPER_STATE}'
if not os.path.exists(path): sys.exit(1)
d = json.load(open(path))
for p in d.get('positions', []):
    if p.get('ticker') == '${sym}':
        print(int(p.get('entry_price', 0)))
        sys.exit(0)
sys.exit(1)
" 2>/dev/null || true)"

  if [[ -n "${last_known}" ]] && [[ "${last_known}" -gt 0 ]] 2>/dev/null; then
    cat <<JSON
{
  "mode": "paper",
  "symbol": "${sym}",
  "last_price": ${last_known},
  "avg_daily_volume": 1500000,
  "lot_size": 100,
  "currency": "IDR",
  "note": "Stale quote — live market-data.sh unavailable; using last-known entry_price."
}
JSON
    return 0
  fi

  # No live quote AND no held position → cannot safely produce a price.
  # Exit non-zero so gate-check fails closed rather than accepting bad data.
  echo "ERROR: _paper_quote cannot produce a price for ${sym} — market-data.sh failed and no held position in PAPER-STATE.json. Refusing to stub." >&2
  return 1
}

_paper_cash() {
  local state
  state="$(_paper_state_read)"
  python3 -c "import json; d=json.loads('''${state}'''); print(int(d.get('cash', 10000000000)))" 2>/dev/null || echo "10000000000"
}

_stops_write() {
  local sym="$1" entry="$2" stop="$3" state_val="$4" trail="${5:-null}" hwm="$6"
  local ts
  ts="$(python3 -c "from datetime import datetime,timezone,timedelta; print(datetime.now(timezone(timedelta(hours=7))).strftime('%Y-%m-%dT%H:%M:%S+07:00'))" 2>/dev/null || date +%Y-%m-%dT%H:%M:%S%z)"
  python3 -c "
import json, os
path = '${STOPS_FILE}'
d = {}
if os.path.exists(path):
    try: d = json.load(open(path))
    except Exception: pass
# Remove internal _comment keys before writing entry
d['${sym}'] = {
    'entry': ${entry},
    'current_stop': ${stop},
    'state': '${state_val}',
    'trail_pct': ${trail},
    'high_water_price': ${hwm},
    'last_updated': '${ts}'
}
with open(path, 'w') as f:
    json.dump(d, f, indent=2)
print('stop written: ${sym} stop=${stop} state=${state_val}')
" 2>/dev/null || warn "Could not write to STOPS.json for ${sym}"
}

# ---- subcommand: portfolio ------------------------------------------------

cmd_portfolio() {
  if [[ "${TRADING_MODE}" == "paper" ]]; then
    _paper_portfolio
  else
    # TODO: map broker response fields to equity, cash, buying_power, positions
    _broker_api GET "/accounts/${BROKER_ACCOUNT_ID}/portfolio"
  fi
}

# ---- subcommand: quote SYMBOL ---------------------------------------------

cmd_quote() {
  local sym="${1:-}"
  [[ -n "${sym}" ]] || die "Usage: broker.sh quote SYMBOL"

  if [[ "${TRADING_MODE}" == "paper" ]]; then
    _paper_quote "${sym}"
  else
    # TODO: map broker quote response to last_price, avg_daily_volume, lot_size
    _broker_api GET "/market/quotes/${sym}"
  fi
}

# ---- subcommand: buy SYMBOL SHARES ----------------------------------------
#
# LAYER-2 GUARDRAIL ENFORCEMENT
# -----------------------------------------------------------------------
# Layer 1 = prompt-level: the agent reads TRADING-STRATEGY.md every session
#   and validates all 9 buy-side gate checks in its own reasoning before
#   calling this script. This is the agent's self-discipline.
#
# Layer 2 = code-level: THIS function unconditionally calls gate-check.sh
#   before forwarding any order to the broker. If gate-check.sh exits
#   non-zero for any reason — even if called directly, bypassing the prompt
#   layer entirely — the buy is refused here. This script is the last line
#   of defense and cannot be reasoned past.
# -----------------------------------------------------------------------

cmd_buy() {
  local sym="${1:-}"
  local shares="${2:-}"
  [[ -n "${sym}" ]]    || die "Usage: broker.sh buy SYMBOL SHARES"
  [[ -n "${shares}" ]] || die "Usage: broker.sh buy SYMBOL SHARES"

  # CRITICAL: gate-check.sh MUST pass before any order logic proceeds.
  # This is non-negotiable. Do not remove or bypass this call.
  local gate_script="${SCRIPT_DIR}/gate-check.sh"
  [[ -x "${gate_script}" ]] \
    || die "gate-check.sh not found or not executable at ${gate_script}"

  echo "--- Running buy-side gate check (Layer 2 guardrail) ---"
  if ! "${gate_script}" "${sym}" "${shares}"; then
    die "Buy order REJECTED: gate-check.sh failed for ${sym} x${shares}. See output above."
  fi
  echo "--- Gate check passed. Proceeding to order placement. ---"

  if [[ "${TRADING_MODE}" == "paper" ]]; then
    info "PAPER MODE — simulating buy of ${shares} shares of ${sym}"
    local order_id="PAPER-BUY-$(date +%s)"
    local fill_price

    # WebSearch override takes precedence when market-data.sh is down.
    # Gate-check already validated against this same override, so the fill
    # price stays consistent with the price the gates saw.
    if [[ -n "${MD_LAST_PRICE_OVERRIDE:-}" ]] && [[ "${MD_LAST_PRICE_OVERRIDE}" -gt 0 ]] 2>/dev/null; then
      fill_price="${MD_LAST_PRICE_OVERRIDE}"
      info "Using WebSearch override fill_price=${fill_price} (yfinance unavailable)"
    else
      fill_price="$(cmd_quote "${sym}" 2>/dev/null \
        | python3 -c "import json,sys; d=json.load(sys.stdin); print(d.get('last_price') or d.get('last') or 5000)" 2>/dev/null \
        || echo 5000)"
    fi

    cat <<JSON
{"paper_mode":true,"status":"filled","order_id":"${order_id}","symbol":"${sym}","shares":${shares},"fill_price":${fill_price},"side":"buy"}
JSON

    # Update PAPER-STATE.json — deduct cash, add position
    python3 -c "
import json, os
path = '${PAPER_STATE}'
d = {}
if os.path.exists(path):
    try: d = json.load(open(path))
    except Exception: pass
cash = d.get('cash', 10000000000)
cost = ${fill_price} * ${shares}
d['cash'] = cash - cost
positions = d.get('positions', [])
positions.append({'ticker':'${sym}','shares':${shares},'entry_price':${fill_price},'entry_date':'$(TZ=Asia/Jakarta date +%Y-%m-%d 2>/dev/null || date +%Y-%m-%d)'})
d['positions'] = positions
d['updated'] = '$(TZ=Asia/Jakarta date +%Y-%m-%d 2>/dev/null || date +%Y-%m-%d)'
with open(path, 'w') as f: json.dump(d, f, indent=2)
print('PAPER-STATE.json updated: ${sym} added, cash reduced by IDR', cost)
" 2>/dev/null || warn "Could not update PAPER-STATE.json"

    # Set initial hard-cut stop: -7% from fill price. Initial stop state = hard-cut.
    local hard_cut fill_price_int
    fill_price_int=$(python3 -c "print(int(float('${fill_price}')))" 2>/dev/null || echo "${fill_price%%.*}")
    hard_cut=$(( fill_price_int * 93 / 100 ))
    info "Setting initial hard-cut stop at IDR ${hard_cut} (-7% from ${fill_price})"
    _stops_write "${sym}" "${fill_price}" "${hard_cut}" "hard-cut" "null" "${fill_price}"
    cmd_set_stop "${sym}" "7"

  else
    require_live_env
    # TODO: replace order fields with your broker's actual buy order schema
    local order_body
    order_body="$(cat <<JSON
{
  "symbol": "${sym}",
  "side": "buy",
  "qty": ${shares},
  "order_type": "market",
  "time_in_force": "day"
}
JSON
)"
    local response
    response="$(_broker_api POST "/accounts/${BROKER_ACCOUNT_ID}/orders" "${order_body}")"
    echo "${response}"
    # Place a real GTC trailing stop at 10% after fill confirmation.
    # TODO: for async fill events, poll for fill status before placing stop.
    info "Placing 10% trailing stop (live)"
    cmd_set_stop "${sym}" "10"
  fi
}

# ---- subcommand: sell SYMBOL SHARES ---------------------------------------

cmd_sell() {
  local sym="${1:-}"
  local shares="${2:-}"
  [[ -n "${sym}" ]]    || die "Usage: broker.sh sell SYMBOL SHARES"
  [[ -n "${shares}" ]] || die "Usage: broker.sh sell SYMBOL SHARES"

  if [[ "${TRADING_MODE}" == "paper" ]]; then
    info "PAPER MODE — simulating sell of ${shares} shares of ${sym}"
    local order_id="PAPER-SELL-$(date +%s)"
    local sell_price
    sell_price="$(cmd_quote "${sym}" 2>/dev/null \
      | python3 -c "import json,sys; d=json.load(sys.stdin); print(d.get('last_price') or d.get('last') or 5000)" 2>/dev/null \
      || echo 5000)"

    cat <<JSON
{"paper_mode":true,"status":"filled","order_id":"${order_id}","symbol":"${sym}","shares":${shares},"fill_price":${sell_price},"side":"sell"}
JSON

    # Update PAPER-STATE.json — close position, add proceeds to cash, record realised P&L
    python3 -c "
import json, os
path = '${PAPER_STATE}'
d = {}
if os.path.exists(path):
    try: d = json.load(open(path))
    except Exception: pass
sell_price = ${sell_price}
shares_to_sell = ${shares}
positions = d.get('positions', [])
new_positions = []
realised = d.get('realised_pnl', 0)
cash = d.get('cash', 0)
for pos in positions:
    if pos['ticker'] == '${sym}':
        ep = pos['entry_price']
        sh = pos['shares']
        if sh <= shares_to_sell:
            realised += (sell_price - ep) * sh
            cash += sell_price * sh
            d.get('closed_trades', []).append({**pos, 'exit_price': sell_price, 'pnl_idr': (sell_price-ep)*sh})
        else:
            new_positions.append({**pos, 'shares': sh - shares_to_sell})
            realised += (sell_price - ep) * shares_to_sell
            cash += sell_price * shares_to_sell
    else:
        new_positions.append(pos)
d['positions'] = new_positions
d['realised_pnl'] = realised
d['cash'] = cash
d['updated'] = '$(TZ=Asia/Jakarta date +%Y-%m-%d 2>/dev/null || date +%Y-%m-%d)'
with open(path, 'w') as f: json.dump(d, f, indent=2)
print('PAPER-STATE.json updated: ${sym} closed at', sell_price)
" 2>/dev/null || warn "Could not update PAPER-STATE.json on sell"

    # Remove stop entry from STOPS.json
    python3 -c "
import json, os
path = '${STOPS_FILE}'
if os.path.exists(path):
    d = json.load(open(path))
    d.pop('${sym}', None)
    json.dump(d, open(path,'w'), indent=2)
    print('STOPS.json: ${sym} removed')
" 2>/dev/null || true

  else
    require_live_env
    # TODO: replace with your broker's actual sell order schema
    local order_body
    order_body="$(cat <<JSON
{
  "symbol": "${sym}",
  "side": "sell",
  "qty": ${shares},
  "order_type": "market",
  "time_in_force": "day"
}
JSON
)"
    _broker_api POST "/accounts/${BROKER_ACCOUNT_ID}/orders" "${order_body}"
  fi
}

# ---- subcommand: set-stop SYMBOL PCT --------------------------------------

cmd_set_stop() {
  local sym="${1:-}"
  local pct="${2:-}"
  [[ -n "${sym}" ]] || die "Usage: broker.sh set-stop SYMBOL PCT"
  [[ -n "${pct}" ]] || die "Usage: broker.sh set-stop SYMBOL PCT"

  # Validate pct is a positive number
  if ! printf '%s' "${pct}" | grep -qE '^[0-9]+([.][0-9]+)?$'; then
    die "set-stop: PCT must be a positive number (e.g. 7 for 7%)"
  fi

  # NO-WIDENING GUARD: refuse to increase trail_pct above current value in STOPS.json
  if [[ -f "${STOPS_FILE}" ]]; then
    local current_trail
    current_trail="$(python3 -c "
import json, sys
try:
    d = json.load(open('${STOPS_FILE}'))
    pos = d.get('${sym}', {})
    t = pos.get('trail_pct')
    print(t if t is not None else 'null')
except Exception:
    print('null')
" 2>/dev/null || echo "null")"

    if [[ "${current_trail}" != "null" ]]; then
      if python3 -c "
import sys
current = float('${current_trail}')
proposed = float('${pct}')
sys.exit(0 if proposed <= current else 1)
" 2>/dev/null; then
        : # proposed is tighter or equal — allow it
      else
        die "set-stop: REJECTED — proposed trail ${pct}% is wider than current ${current_trail}%. Never widen a stop."
      fi
    fi
  fi

  # Get current price for absolute stop calculation
  local last_price
  last_price="$(cmd_quote "${sym}" 2>/dev/null \
    | python3 -c "import json,sys; d=json.load(sys.stdin); print(d.get('last_price') or d.get('last') or 0)" 2>/dev/null \
    || echo 0)"

  # Read entry from STOPS.json (needed for state tracking)
  local entry_price hwm
  entry_price="$(python3 -c "
import json, os
d = json.load(open('${STOPS_FILE}')) if os.path.exists('${STOPS_FILE}') else {}
print(d.get('${sym}', {}).get('entry', ${last_price}))
" 2>/dev/null || echo "${last_price}")"
  hwm="$(python3 -c "
import json, os
d = json.load(open('${STOPS_FILE}')) if os.path.exists('${STOPS_FILE}') else {}
print(d.get('${sym}', {}).get('high_water_price', ${last_price}))
" 2>/dev/null || echo "${last_price}")"

  # Update hwm if price has moved up
  if [[ "${last_price}" -gt "${hwm}" ]] 2>/dev/null; then
    hwm="${last_price}"
  fi

  # Determine stop state: hard-cut (pct=7, initial) vs trailing
  local stop_state="trailing"
  local trail_val="${pct}"
  if [[ "${pct}" == "7" ]]; then
    # Only initial buy placement uses 7% as hard-cut; once in trailing the tighten fn handles this
    stop_state="hard-cut"
    trail_val="null"
  fi

  # Compute absolute stop price
  local stop_price
  stop_price="$(python3 -c "
ref = ${hwm} if '${stop_state}' == 'trailing' else ${entry_price}
print(int(ref * (100 - ${pct}) / 100))
" 2>/dev/null || echo 0)"

  if [[ "${TRADING_MODE}" == "paper" ]]; then
    info "PAPER MODE — setting ${stop_state} stop for ${sym} at ${pct}% (IDR ${stop_price})"
    _stops_write "${sym}" "${entry_price}" "${stop_price}" "${stop_state}" "${trail_val}" "${hwm}"
    cat <<JSON
{"paper_mode":true,"status":"stop_set","symbol":"${sym}","stop_price":${stop_price},"trailing_pct":${pct},"stop_state":"${stop_state}","order_type":"trailing_stop","time_in_force":"gtc"}
JSON
  else
    require_live_env
    # Get current price to compute absolute stop price level.
    local quote_json last_price stop_price
    quote_json="$(_broker_api GET "/market/quotes/${sym}")"
    # TODO: adjust jq path to match your broker's actual quote response schema
    last_price="$(echo "${quote_json}" | jq -r '.last_price // .last // .close // empty')"
    [[ -n "${last_price}" && "${last_price}" != "null" ]] \
      || die "set-stop: could not parse last_price from broker quote for ${sym}"

    stop_price="$(printf 'scale=0; %s * (100 - %s) / 100\n' "${last_price}" "${pct}" | bc)"

    # TODO: replace with your broker's trailing-stop / GTC stop order schema
    local order_body
    order_body="$(cat <<JSON
{
  "symbol": "${sym}",
  "side": "sell",
  "qty": "all",
  "order_type": "trailing_stop",
  "trailing_percent": ${pct},
  "stop_price": ${stop_price},
  "time_in_force": "gtc"
}
JSON
)"
    _broker_api POST "/accounts/${BROKER_ACCOUNT_ID}/orders" "${order_body}"
  fi
}

# ---- subcommand: positions ------------------------------------------------

cmd_positions() {
  if [[ "${TRADING_MODE}" == "paper" ]]; then
    _paper_positions
  else
    _broker_api GET "/accounts/${BROKER_ACCOUNT_ID}/positions"
  fi
}

# ---- subcommand: cash -----------------------------------------------------

cmd_cash() {
  if [[ "${TRADING_MODE}" == "paper" ]]; then
    _paper_cash
  else
    local portfolio_json
    portfolio_json="$(_broker_api GET "/accounts/${BROKER_ACCOUNT_ID}/portfolio")"
    # TODO: adjust jq path to match your broker's portfolio response schema
    echo "${portfolio_json}" | jq -r '.cash // .buying_power // .available_cash // empty'
  fi
}

# ---- subcommand: tighten-if-triggered SYMBOL ---------------------------------
# Called by the midday routine for every open position.
# Reads entry + stop state from memory/STOPS.json; fetches live price.
# Applies stop-state transitions:
#   hard-cut → trailing (10%)  once return >= +7%
#   trailing (10%) → (7%)      once return >= +15%
#   trailing (7%)  → (5%)      once return >= +20%
# Never widens. Never tightens below 5%. Writes new state to STOPS.json.

cmd_tighten_if_triggered() {
  local sym="${1:-}"
  [[ -n "${sym}" ]] || die "Usage: broker.sh tighten-if-triggered SYMBOL"

  if [[ ! -f "${STOPS_FILE}" ]]; then
    die "memory/STOPS.json not found. Run 'broker.sh buy' first to initialise stops."
  fi

  local entry current_stop stop_state trail_pct hwm
  read -r entry current_stop stop_state trail_pct hwm <<< "$(python3 -c "
import json
try:
    d = json.load(open('${STOPS_FILE}'))
    pos = d.get('${sym}', {})
    print(
        pos.get('entry', 0),
        pos.get('current_stop', 0),
        pos.get('state', 'hard-cut'),
        pos.get('trail_pct') if pos.get('trail_pct') is not None else 'null',
        pos.get('high_water_price', pos.get('entry', 0))
    )
except Exception:
    print(0, 0, 'hard-cut', 'null', 0)
" 2>/dev/null || echo "0 0 hard-cut null 0")"

  if [[ "${entry:-0}" -le 0 ]]; then
    die "No entry record for ${sym} in memory/STOPS.json."
  fi

  local last_price
  last_price="$(cmd_quote "${sym}" 2>/dev/null \
    | python3 -c "import json,sys; d=json.load(sys.stdin); print(int(d.get('last_price') or d.get('last') or ${entry}))" 2>/dev/null \
    || echo "${entry}")"

  # Update high-water mark
  if [[ "${last_price}" -gt "${hwm}" ]] 2>/dev/null; then
    hwm="${last_price}"
  fi

  local return_pct
  return_pct="$(python3 -c "print(round(($last_price - $entry) / $entry * 100, 2))" 2>/dev/null || echo 0)"

  info "${sym}: entry=${entry}, last=${last_price}, return=${return_pct}%, stop_state=${stop_state}, hwm=${hwm}"

  if [[ "${stop_state}" == "hard-cut" ]]; then
    # Check if we should transition to trailing
    if python3 -c "import sys; sys.exit(0 if float('${return_pct}') >= 7.0 else 1)" 2>/dev/null; then
      info "${sym}: +${return_pct}% >= +7% threshold — transitioning hard-cut → 10% trailing"
      _stops_write "${sym}" "${entry}" "$(( hwm * 90 / 100 ))" "trailing" "10" "${hwm}"
      cmd_set_stop "${sym}" "10"
      echo "{\"symbol\":\"${sym}\",\"action\":\"transitioned_to_trailing\",\"trail_pct\":10,\"return_pct\":${return_pct},\"new_stop\":$(( hwm * 90 / 100 ))}"
    else
      info "${sym}: return=${return_pct}% < +7%, staying in hard-cut state"
      echo "{\"symbol\":\"${sym}\",\"action\":\"no_change\",\"stop_state\":\"hard-cut\",\"return_pct\":${return_pct}}"
    fi
    return 0
  fi

  # Trailing state — determine target trail_pct
  local target_pct="${trail_pct}"
  if python3 -c "import sys; sys.exit(0 if float('${return_pct}') >= 20.0 else 1)" 2>/dev/null; then
    target_pct=5
  elif python3 -c "import sys; sys.exit(0 if float('${return_pct}') >= 15.0 else 1)" 2>/dev/null; then
    target_pct=7
  fi

  if [[ "${target_pct}" == "${trail_pct}" ]]; then
    info "${sym}: trail_pct=${trail_pct}% unchanged (return=${return_pct}%)"
    echo "{\"symbol\":\"${sym}\",\"action\":\"no_change\",\"trail_pct\":${trail_pct},\"return_pct\":${return_pct}}"
    return 0
  fi

  local new_stop
  new_stop=$(( hwm * (100 - target_pct) / 100 ))

  if [[ "${new_stop}" -le "${current_stop}" ]] 2>/dev/null; then
    info "${sym}: new stop IDR ${new_stop} would be <= current IDR ${current_stop} — skipping"
    echo "{\"symbol\":\"${sym}\",\"action\":\"no_change\",\"reason\":\"new_stop_not_higher\",\"new_stop\":${new_stop},\"current_stop\":${current_stop}}"
    return 0
  fi

  info "${sym}: tightening trail_pct ${trail_pct}% → ${target_pct}% (return=${return_pct}%, new_stop=IDR ${new_stop})"
  _stops_write "${sym}" "${entry}" "${new_stop}" "trailing" "${target_pct}" "${hwm}"
  cmd_set_stop "${sym}" "${target_pct}"
  echo "{\"symbol\":\"${sym}\",\"action\":\"tightened\",\"old_trail_pct\":${trail_pct},\"new_trail_pct\":${target_pct},\"new_stop\":${new_stop},\"return_pct\":${return_pct}}"
}

# ---- help -----------------------------------------------------------------

show_help() {
  cat <<HELP
broker.sh — IDX Broker API Wrapper

Usage:
  broker.sh portfolio                       Print equity, cash, positions (JSON)
  broker.sh quote SYMBOL                    Last price, avg daily volume, lot size
  broker.sh buy SYMBOL SHARES               Buy (15-gate checked); sets -7% hard-cut stop
  broker.sh sell SYMBOL SHARES              Sell SHARES of SYMBOL at market
  broker.sh set-stop SYMBOL PCT             Set trailing stop at PCT% below price (never widens)
  broker.sh tighten-if-triggered SYMBOL     Apply stop-state transitions if return thresholds hit
  broker.sh positions                       List open positions only
  broker.sh cash                            Print available cash (IDR integer)
  broker.sh -h | --help                     Show this help

Stop state machine:
  hard-cut  (-7% from entry)   →  trailing (10%)  once return >= +7%
  trailing  (10% from hwm)     →  trailing (7%)   once return >= +15%
  trailing  (7% from hwm)      →  trailing (5%)   once return >= +20%

Current mode: ${TRADING_MODE}
  (set TRADING_MODE=live to send real orders; default is paper)
HELP
}

# ---- dispatch -------------------------------------------------------------

SUBCOMMAND="${1:-}"
shift || true

case "${SUBCOMMAND}" in
  portfolio)                   cmd_portfolio ;;
  quote)                       cmd_quote "$@" ;;
  buy)                         cmd_buy "$@" ;;
  sell)                        cmd_sell "$@" ;;
  set-stop)                    cmd_set_stop "$@" ;;
  tighten-if-triggered)        cmd_tighten_if_triggered "$@" ;;
  positions)                   cmd_positions ;;
  cash)                        cmd_cash ;;
  -h|--help|help)              show_help ;;
  "")
    warn "No subcommand given."
    show_help >&2
    exit 1
    ;;
  *)
    die "Unknown subcommand: '${SUBCOMMAND}'. Run: broker.sh --help"
    ;;
esac
