#!/usr/bin/env bash
# scripts/gate-check.sh — Buy-Side Gate: 9 checks enforced in code
#
# Usage:
#   gate-check.sh SYMBOL SHARES
#
# Returns 0 if all 9 checks pass. Returns 1 on the first failure.
# Each check prints "CHECK N: PASS" or "CHECK N: FAIL: <reason>".
# Final line on success: "ALL 9 GATES PASSED"
#
# This script is called by broker.sh buy before ANY order is placed.
# It is the code-level (Layer 2) enforcement of the buy-side gate.
# Layer 1 is the agent's prompt-level validation in TRADING-STRATEGY.md.
# Both layers must agree; this one cannot be bypassed.
#
# Environment (same as broker.sh):
#   TRADING_MODE, BROKER_API_KEY, BROKER_API_SECRET, BROKER_ACCOUNT_ID

set -euo pipefail

# ---- args -----------------------------------------------------------------

SYMBOL="${1:-}"
SHARES="${2:-}"

if [[ -z "${SYMBOL}" || -z "${SHARES}" ]]; then
  echo "Usage: gate-check.sh SYMBOL SHARES" >&2
  exit 1
fi

# ---- locate repo root and broker script -----------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
BROKER="${SCRIPT_DIR}/broker.sh"

# Load .env if present (local dev)
if [[ -f "${REPO_ROOT}/.env" ]]; then
  # shellcheck disable=SC1091
  set -o allexport
  source "${REPO_ROOT}/.env"
  set +o allexport
fi

TRADING_MODE="${TRADING_MODE:-paper}"

# ---- helpers --------------------------------------------------------------

FAILED=0

pass() { echo "CHECK $1: PASS"; }
fail() {
  echo "CHECK $1: FAIL: $2"
  FAILED=1
}

# ---- today's date in WIB format used in RESEARCH-LOG.md -------------------

# WIB = UTC+7; derive date robustly without relying on TZ env
TODAY_DATE="$(TZ=Asia/Jakarta date +%Y-%m-%d 2>/dev/null || date -u -v+7H +%Y-%m-%d 2>/dev/null || date +%Y-%m-%d)"

# ---- ISO week number for weekly trade count --------------------------------

CURRENT_ISO_WEEK="$(TZ=Asia/Jakarta date +%Y-W%V 2>/dev/null || date +%Y-W%V)"

# ---- CHECK 1: total positions after fill <= 6 -----------------------------
# Reads current open position count from broker.sh positions.

echo "--- Gate check for ${SYMBOL} x${SHARES} ---"

positions_json="$("${BROKER}" positions 2>/dev/null || echo '{"positions":[]}')"

# Count positions: try jq first, fall back to grep+wc
if command -v jq &>/dev/null; then
  pos_count="$(echo "${positions_json}" | jq '.positions | length' 2>/dev/null || echo 0)"
else
  # TODO: robust parser — for now approximate with grep on "symbol" fields
  pos_count="$(echo "${positions_json}" | grep -c '"symbol"' 2>/dev/null || echo 0)"
fi

# After this fill there will be pos_count + 1 positions
positions_after=$(( pos_count + 1 ))

if (( positions_after <= 6 )); then
  pass 1
else
  fail 1 "Total positions after fill would be ${positions_after} (max 6). Current: ${pos_count}."
fi

[[ "${FAILED}" -eq 0 ]] || exit 1

# ---- CHECK 2: trades this week <= 3 ----------------------------------------
# Parses memory/TRADE-LOG.md for trade entries in the current ISO week.
# Convention: each trade row in TRADE-LOG.md starts with a date like 2026-04-18.
# TODO: replace this grep+wc with a robust parser once log format is stable.

TRADE_LOG="${REPO_ROOT}/memory/TRADE-LOG.md"
trades_this_week=0

if [[ -f "${TRADE_LOG}" ]]; then
  # Extract ISO week from each dated BUY/SELL line and count those matching current week
  # Matches lines starting with a date (YYYY-MM-DD) that contain BUY or SELL (case-insensitive)
  # TODO: robust parser — current approach is a grep+date heuristic
  while IFS= read -r line; do
    line_date="$(echo "${line}" | grep -oE '^[0-9]{4}-[0-9]{2}-[0-9]{2}' || true)"
    if [[ -n "${line_date}" ]]; then
      line_week="$(date -j -f "%Y-%m-%d" "${line_date}" +%Y-W%V 2>/dev/null \
        || date -d "${line_date}" +%Y-W%V 2>/dev/null \
        || echo "")"
      if [[ "${line_week}" == "${CURRENT_ISO_WEEK}" ]]; then
        trades_this_week=$(( trades_this_week + 1 ))
      fi
    fi
  done < <(grep -iE '^\s*[|]?\s*[0-9]{4}-[0-9]{2}-[0-9]{2}.*\b(BUY|SELL)\b' "${TRADE_LOG}" 2>/dev/null || true)
fi

# Including this prospective trade: trades_this_week + 1 must be <= 3
trades_after=$(( trades_this_week + 1 ))

if (( trades_after <= 3 )); then
  pass 2
else
  fail 2 "Trades this week would be ${trades_after} (max 3). Already logged: ${trades_this_week} trade(s) this week (${CURRENT_ISO_WEEK})."
fi

[[ "${FAILED}" -eq 0 ]] || exit 1

# ---- CHECK 3: position cost <= 20% of account equity ----------------------

portfolio_json="$("${BROKER}" portfolio 2>/dev/null || echo '{"equity":0}')"
quote_json="$("${BROKER}" quote "${SYMBOL}" 2>/dev/null || echo '{"last_price":0}')"

if command -v jq &>/dev/null; then
  equity="$(echo "${portfolio_json}" | jq -r '.equity // 0')"
  last_price="$(echo "${quote_json}" | jq -r '.last_price // .last // .ask // 0')"
else
  # Minimal fallback without jq — TODO: wire to jq or python3 in production
  equity="$(echo "${portfolio_json}" | grep -oE '"equity":[0-9]+' | grep -oE '[0-9]+' || echo 0)"
  last_price="$(echo "${quote_json}" | grep -oE '"last_price":[0-9]+' | grep -oE '[0-9]+' || echo 0)"
fi

# Arithmetic in integer IDR (avoid floating point in bash)
# position_cost = last_price * SHARES
# max_allowed   = equity * 20 / 100
if [[ "${equity}" -gt 0 && "${last_price}" -gt 0 ]] 2>/dev/null; then
  position_cost=$(( last_price * SHARES ))
  max_allowed=$(( equity * 20 / 100 ))
  if (( position_cost <= max_allowed )); then
    pass 3
  else
    fail 3 "Position cost IDR ${position_cost} exceeds 20% of equity (max IDR ${max_allowed}, equity IDR ${equity})."
  fi
else
  # TODO: if equity or price are unavailable, fail safe rather than skip
  fail 3 "Could not determine equity (${equity}) or last_price (${last_price}). Failing safe."
fi

[[ "${FAILED}" -eq 0 ]] || exit 1

# ---- CHECK 4: position cost <= available cash -----------------------------

cash="$("${BROKER}" cash 2>/dev/null || echo "0")"

# Strip any non-numeric characters (e.g. trailing newline)
cash="$(echo "${cash}" | tr -d '[:space:]')"

if [[ "${cash}" -gt 0 && "${last_price}" -gt 0 ]] 2>/dev/null; then
  position_cost=$(( last_price * SHARES ))
  if (( position_cost <= cash )); then
    pass 4
  else
    fail 4 "Position cost IDR ${position_cost} exceeds available cash IDR ${cash}."
  fi
else
  fail 4 "Could not determine available cash (${cash}). Failing safe."
fi

[[ "${FAILED}" -eq 0 ]] || exit 1

# ---- CHECK 5: catalyst documented in today's RESEARCH-LOG.md --------------
# Two conditions must both be true:
#   a) There is a heading for today's WIB date in RESEARCH-LOG.md
#   b) The SYMBOL appears in the section under that heading

RESEARCH_LOG="${REPO_ROOT}/memory/RESEARCH-LOG.md"

catalyst_ok=0
if [[ -f "${RESEARCH_LOG}" ]]; then
  # Check that today's date heading exists
  if grep -qF "${TODAY_DATE}" "${RESEARCH_LOG}" 2>/dev/null; then
    # Check that SYMBOL appears somewhere after today's heading.
    # We look for both conditions in the same file; a simple check:
    # find the first occurrence of today's date heading, then check that
    # SYMBOL appears on a subsequent line before the next heading.
    # TODO: replace with a structured parser once log format is confirmed stable.
    if awk "
      /^#.*${TODAY_DATE}/ { in_section=1; next }
      /^#/ { if (in_section) in_section=0 }
      in_section && /${SYMBOL}/ { found=1; exit }
      END { exit !found }
    " "${RESEARCH_LOG}" 2>/dev/null; then
      catalyst_ok=1
    fi
  fi
fi

if [[ "${catalyst_ok}" -eq 1 ]]; then
  pass 5
else
  fail 5 "No catalyst documented for ${SYMBOL} in today's (${TODAY_DATE}) RESEARCH-LOG.md entry. Write the thesis first."
fi

[[ "${FAILED}" -eq 0 ]] || exit 1

# ---- CHECK 6: instrument is a stock (IDX equity) --------------------------
# IDX stock codes are exactly 4 uppercase letters (e.g. BBCA, ADRO, TLKM).
# Reject anything with a dash, trailing -W (warrant), -R (right), or digit.
# No options. Ever.

if echo "${SYMBOL}" | grep -qE '^[A-Z]{4}$'; then
  pass 6
else
  fail 6 "Symbol '${SYMBOL}' does not look like an IDX stock ticker (4 uppercase letters). Options, warrants (-W), rights (-R), ETFs, and non-standard tickers are rejected."
fi

[[ "${FAILED}" -eq 0 ]] || exit 1

# ---- CHECK 7: avg daily volume > 500,000 shares ---------------------------

if command -v jq &>/dev/null; then
  avg_vol="$(echo "${quote_json}" | jq -r '.avg_daily_volume // .volume // 0')"
else
  avg_vol="$(echo "${quote_json}" | grep -oE '"avg_daily_volume":[0-9]+' | grep -oE '[0-9]+' || echo 0)"
fi

avg_vol="$(echo "${avg_vol}" | tr -d '[:space:]')"

if [[ "${avg_vol}" -gt 500000 ]] 2>/dev/null; then
  pass 7
else
  fail 7 "Avg daily volume for ${SYMBOL} is ${avg_vol} shares, below the 500,000 minimum. Stock is too illiquid."
fi

[[ "${FAILED}" -eq 0 ]] || exit 1

# ---- CHECK 8: SHARES is a positive multiple of 100 (IDX lot size) ----------

if [[ "${SHARES}" -gt 0 ]] 2>/dev/null && (( SHARES % 100 == 0 )); then
  pass 8
else
  fail 8 "SHARES=${SHARES} is not a positive multiple of 100. IDX requires trades in lots of 100 shares."
fi

[[ "${FAILED}" -eq 0 ]] || exit 1

# ---- CHECK 9: current price <= 3% above planned entry price ---------------
# The planned entry price is read from the latest RESEARCH-LOG.md entry for
# SYMBOL under today's heading. If no planned entry is found, we fail safe.
# Format expected in the log: "entry: 5200" or "planned entry: 5200" (case-insensitive).
# TODO: formalise the RESEARCH-LOG entry format so this parser is reliable.

planned_entry=0
if [[ -f "${RESEARCH_LOG}" ]]; then
  planned_entry="$(awk "
    /^#.*${TODAY_DATE}/ { in_section=1; next }
    /^#/ { if (in_section) in_section=0 }
    in_section && /${SYMBOL}/ { found_sym=1 }
    found_sym && /[Pp]lanned[[:space:]]+[Ee]ntry[[:space:]]*:[[:space:]]*[0-9]+/ {
      match(\$0, /[0-9]+/)
      print substr(\$0, RSTART, RLENGTH)
      exit
    }
    found_sym && /[Ee]ntry[[:space:]]*:[[:space:]]*[0-9]+/ {
      match(\$0, /[0-9]+/)
      print substr(\$0, RSTART, RLENGTH)
      exit
    }
  " "${RESEARCH_LOG}" 2>/dev/null || echo 0)"
fi

planned_entry="$(echo "${planned_entry}" | tr -d '[:space:]')"

if [[ "${planned_entry}" -le 0 ]] 2>/dev/null; then
  fail 9 "No planned entry price found for ${SYMBOL} in today's (${TODAY_DATE}) RESEARCH-LOG.md. Document the entry price before trading."
else
  # Calculate max acceptable price = planned_entry * 103 / 100
  max_chasing_price=$(( planned_entry * 103 / 100 ))

  if [[ "${last_price}" -le "${max_chasing_price}" ]] 2>/dev/null; then
    price_pct_diff=$(( (last_price - planned_entry) * 100 / planned_entry ))
    pass 9
    echo "  (last_price=${last_price}, planned_entry=${planned_entry}, diff=${price_pct_diff}%)"
  else
    price_pct_diff=$(( (last_price - planned_entry) * 100 / planned_entry ))
    fail 9 "Current price ${last_price} is ${price_pct_diff}% above planned entry ${planned_entry} (max 3%). Do not chase. Re-evaluate."
  fi
fi

[[ "${FAILED}" -eq 0 ]] || exit 1

# ---- All gates passed -----------------------------------------------------

echo "ALL 9 GATES PASSED"
exit 0
