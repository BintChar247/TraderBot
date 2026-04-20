#!/usr/bin/env bash
# scripts/gate-check.sh — Buy-Side Gate: 15 checks enforced in code
#
# Usage:
#   gate-check.sh SYMBOL SHARES
#
# Returns 0 if all 15 checks pass. Returns 1 on any failure.
# All failures are ACCUMULATED and printed at the end — no exit-on-first.
# Portfolio caps (Checks 10-12) are evaluated first; if any fail, trade is
# rejected immediately before trade-level checks to preserve determinism.
#
# Changes from original 9-gate version:
#   - Check 3:  reads max_position_pct from memory/RISK-STATE.json (regime-adaptive)
#   - Check 6:  accepts 4-5 char tickers (MTEL, GOTO, etc.)
#   - Checks 10-12: daily P&L cap, weekly P&L cap, peak drawdown cap
#   - Check 13: sector concentration cap (≤40% per sector)
#   - Check 14: ADV participation cap (order ≤10% of avg daily volume)
#   - Check 15: risk budget cap (≤75bps per idea)
#   - WIB date uses python3 for cross-platform TZ determinism
#   - Check 5 awk handles heading styles: "## 2026-04-19" or "## 2026-04-19 — Pre-Market"

set -euo pipefail

# ---- args -----------------------------------------------------------------

SYMBOL="${1:-}"
SHARES="${2:-}"

if [[ -z "${SYMBOL}" || -z "${SHARES}" ]]; then
  echo "Usage: gate-check.sh SYMBOL SHARES" >&2
  exit 1
fi

# ---- locate repo root -----------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
BROKER="${SCRIPT_DIR}/broker.sh"

if [[ -f "${REPO_ROOT}/.env" ]]; then
  # shellcheck disable=SC1091
  set -o allexport
  source "${REPO_ROOT}/.env"
  set +o allexport
fi

TRADING_MODE="${TRADING_MODE:-paper}"

# ---- helpers ---------------------------------------------------------------

FAILURES=()     # accumulated list; never cleared

pass() {
  local note="${2:-}"
  echo "CHECK $1: PASS${note:+ ($note)}"
}

fail() {
  echo "CHECK $1: FAIL: $2"
  FAILURES+=("Gate $1: $2")
}

# ---- WIB date/week — python3 for cross-platform TZ determinism ------------

TODAY_DATE="$(python3 -c "
from datetime import datetime, timezone, timedelta
print(datetime.now(timezone(timedelta(hours=7))).strftime('%Y-%m-%d'))
" 2>/dev/null \
  || TZ=Asia/Jakarta date +%Y-%m-%d 2>/dev/null \
  || date +%Y-%m-%d)"

CURRENT_ISO_WEEK="$(python3 -c "
from datetime import datetime, timezone, timedelta
print(datetime.now(timezone(timedelta(hours=7))).strftime('%Y-W%V'))
" 2>/dev/null \
  || TZ=Asia/Jakarta date +%Y-W%V 2>/dev/null \
  || date +%Y-W%V)"

# ---- RISK-STATE.json — regime-adaptive caps --------------------------------

RISK_STATE="${REPO_ROOT}/memory/RISK-STATE.json"
TRADING_HALTED=false
MAX_POS_PCT=20        # default 20%; file may lower to 15% in EM-outflow regime
DAILY_PNL_PCT=0
WEEKLY_PNL_PCT=0
DRAWDOWN_FROM_PEAK_PCT=0

if [[ -f "${RISK_STATE}" ]]; then
  read -r TRADING_HALTED MAX_POS_PCT DAILY_PNL_PCT WEEKLY_PNL_PCT DRAWDOWN_FROM_PEAK_PCT <<< \
    "$(python3 -c "
import json, sys
try:
    d = json.load(open('${RISK_STATE}'))
    halted  = str(d.get('trading_halted', False)).lower()
    max_pos = int(float(d.get('max_position_pct', 0.20)) * 100)
    daily   = float(d.get('daily_pnl_pct', 0))
    weekly  = float(d.get('weekly_pnl_pct', 0))
    dd      = float(d.get('drawdown_from_peak_pct', 0))
    print(halted, max_pos, daily, weekly, dd)
except Exception:
    print('false 20 0 0 0')
" 2>/dev/null || echo "false 20 0 0 0")"
fi

echo "--- Gate check for ${SYMBOL} x${SHARES} (${TODAY_DATE}, mode=${TRADING_MODE}) ---"
echo "    Regime: max_pos=${MAX_POS_PCT}%, daily_pnl=${DAILY_PNL_PCT}%, weekly_pnl=${WEEKLY_PNL_PCT}%, dd_peak=${DRAWDOWN_FROM_PEAK_PCT}%"
echo ""

if [[ "${TRADING_HALTED}" == "true" ]]; then
  echo "CHECK 0: FAIL: Trading is halted (RISK-STATE.json trading_halted=true). Clear halt before placing orders."
  exit 1
fi

# ---- SECTOR MAP (IDX equities) — bash 3.2 compatible ----------------------

_sector_for() {
  local s="${1:-}"
  case "${s}" in
    BBCA|BBRI|BMRI|BBNI|BNGA|NISP|BBTN) echo "Banking" ;;
    ADRO|ITMG|PTBA|BUMI|HRUM)           echo "Coal" ;;
    MEDC|PGAS)                          echo "Energy" ;;
    ANTM|INCO|MDKA|NCKL|MBMA)          echo "Nickel" ;;
    TINS)                               echo "Metals" ;;
    UNVR|ICBP|INDF|MYOR|SIDO)          echo "Consumer" ;;
    TLKM|EXCL|ISAT|MTEL|TOWR)          echo "Telco" ;;
    BSDE|CTRA|SMRA|PWON)               echo "Property" ;;
    GOTO|BUKA|EMTK)                    echo "Tech" ;;
    ASII|UNTR|AKRA)                    echo "Industrial" ;;
    AALI|LSIP)                         echo "Plantation" ;;
    JSMR|WSKT|PTPP|ADHI)              echo "Infrastructure" ;;
    KLBF|MIKA)                         echo "Healthcare" ;;
    SMGR|INTP)                         echo "Cement" ;;
    *)                                  echo "Unknown" ;;
  esac
}
SYMBOL_SECTOR="$(_sector_for "${SYMBOL}")"

# ═══════════════════════════════════════════════════════════════════════════
# PORTFOLIO-LEVEL CAPS — Checks 10-12
# Evaluated first. A failure here rejects the trade immediately.
# ═══════════════════════════════════════════════════════════════════════════

PORTFOLIO_FAIL=0

# ---- CHECK 10: Daily loss cap (-2%) ----------------------------------------

if python3 -c "import sys; sys.exit(0 if float('${DAILY_PNL_PCT}') >= -2.0 else 1)" 2>/dev/null; then
  pass 10 "daily_pnl=${DAILY_PNL_PCT}%"
else
  fail 10 "Daily P&L ${DAILY_PNL_PCT}% has breached -2% daily loss cap. Trading halted for today. Update RISK-STATE.json at EOD."
  PORTFOLIO_FAIL=1
fi

# ---- CHECK 11: Weekly loss cap (-5%) — halves shares, does not reject ------

WEEKLY_HALVED=false
if python3 -c "import sys; sys.exit(0 if float('${WEEKLY_PNL_PCT}') >= -5.0 else 1)" 2>/dev/null; then
  pass 11 "weekly_pnl=${WEEKLY_PNL_PCT}%"
else
  echo "CHECK 11: WARN: Weekly P&L ${WEEKLY_PNL_PCT}% has breached -5%. Halving SHARES per risk rules."
  SHARES=$(( (SHARES / 200) * 100 ))  # halve and floor to nearest 100-lot
  WEEKLY_HALVED=true
  if [[ "${SHARES}" -lt 100 ]]; then
    fail 11 "After halving, SHARES=${SHARES} is below minimum lot (100). Trade rejected."
    PORTFOLIO_FAIL=1
  else
    echo "CHECK 11: PASS (halved to ${SHARES} shares)"
  fi
fi

# ---- CHECK 12: Peak drawdown (-15%) ----------------------------------------

if python3 -c "import sys; sys.exit(0 if abs(float('${DRAWDOWN_FROM_PEAK_PCT}')) <= 15.0 else 1)" 2>/dev/null; then
  pass 12 "drawdown=${DRAWDOWN_FROM_PEAK_PCT}%"
else
  fail 12 "Peak drawdown ${DRAWDOWN_FROM_PEAK_PCT}% exceeds -15% hard limit. Close all positions and await Michael review. No new trades until RISK-STATE.json is reset."
  PORTFOLIO_FAIL=1
fi

# Exit if any portfolio cap is breached
if [[ "${PORTFOLIO_FAIL}" -ne 0 ]]; then
  echo ""
  echo "=== TRADE REJECTED — PORTFOLIO CAP BREACH ==="
  printf 'FAIL: %s\n' "${FAILURES[@]}"
  exit 1
fi

# ═══════════════════════════════════════════════════════════════════════════
# SHARED DATA FETCH — used by multiple trade-level checks
# ═══════════════════════════════════════════════════════════════════════════

TRADE_LOG="${REPO_ROOT}/memory/TRADE-LOG.md"
RESEARCH_LOG="${REPO_ROOT}/memory/RESEARCH-LOG.md"
PAPER_STATE="${REPO_ROOT}/memory/PAPER-STATE.json"

positions_json="$("${BROKER}" positions 2>/dev/null || echo '{"positions":[]}')"
portfolio_json="$("${BROKER}" portfolio 2>/dev/null || echo '{"equity":10000000000}')"
quote_json="$("${BROKER}" quote "${SYMBOL}" 2>/dev/null || echo '{"last_price":0,"avg_daily_volume":0}')"

# Parse equity, price, volume via python3
read -r equity last_price avg_vol <<< "$(python3 -c "
import json, sys
try:
    p = json.loads('''${portfolio_json}''')
    q = json.loads('''${quote_json}''')
    equity     = int(p.get('equity', 10000000000))
    last_price = int(q.get('last_price') or q.get('last') or q.get('ask') or 0)
    avg_vol    = int(q.get('avg_daily_volume') or q.get('volume') or 0)
    print(equity, last_price, avg_vol)
except Exception:
    print(10000000000, 0, 0)
" 2>/dev/null || echo "10000000000 0 0")"

# Count current open positions
pos_count="$(python3 -c "
import json, sys
try:
    d = json.loads('''${positions_json}''')
    print(len(d.get('positions', [])))
except Exception:
    print(0)
" 2>/dev/null || echo 0)"

cash="$("${BROKER}" cash 2>/dev/null || echo "0")"
cash="$(echo "${cash}" | tr -d '[:space:]')"

# Pre-compute position cost (used in Checks 3, 4, 13, 15)
position_cost=0
if [[ "${last_price}" -gt 0 ]]; then
  position_cost=$(( last_price * SHARES ))
fi

# ═══════════════════════════════════════════════════════════════════════════
# TRADE-LEVEL CHECKS — 1 through 9, then 13-15
# All failures accumulated; full list printed at end.
# ═══════════════════════════════════════════════════════════════════════════

# ---- CHECK 1: total positions after fill <= 6 ------------------------------

positions_after=$(( pos_count + 1 ))
if (( positions_after <= 6 )); then
  pass 1
else
  fail 1 "Positions after fill would be ${positions_after} (max 6). Current: ${pos_count}."
fi

# ---- CHECK 2: trades this week <= 3 ----------------------------------------

trades_this_week=0
if [[ -f "${TRADE_LOG}" ]]; then
  while IFS= read -r line; do
    line_date="$(echo "${line}" | grep -oE '[0-9]{4}-[0-9]{2}-[0-9]{2}' | head -1 || true)"
    if [[ -n "${line_date}" ]]; then
      line_week="$(python3 -c "
from datetime import datetime
print(datetime.strptime('${line_date}','%Y-%m-%d').strftime('%Y-W%V'))
" 2>/dev/null || date -d "${line_date}" +%Y-W%V 2>/dev/null || echo "")"
      if [[ "${line_week}" == "${CURRENT_ISO_WEEK}" ]]; then
        trades_this_week=$(( trades_this_week + 1 ))
      fi
    fi
  done < <(grep -iE '^\s*[|]?\s*[0-9]{4}-[0-9]{2}-[0-9]{2}.*\b(BUY|SELL)\b' "${TRADE_LOG}" 2>/dev/null || true)
fi

trades_after=$(( trades_this_week + 1 ))
if (( trades_after <= 3 )); then
  pass 2
else
  fail 2 "Trades this week would be ${trades_after} (max 3). Already placed: ${trades_this_week} this week (${CURRENT_ISO_WEEK})."
fi

# ---- CHECK 3: position cost <= regime-adaptive max -------------------------

if [[ "${equity}" -gt 0 && "${last_price}" -gt 0 ]]; then
  max_allowed=$(( equity * MAX_POS_PCT / 100 ))
  if (( position_cost <= max_allowed )); then
    pass 3 "cost=IDR ${position_cost}, max=${MAX_POS_PCT}% of IDR ${equity}=IDR ${max_allowed}"
  else
    fail 3 "Position cost IDR ${position_cost} exceeds ${MAX_POS_PCT}% cap (IDR ${max_allowed}). Regime: ${MAX_POS_PCT}%."
  fi
else
  fail 3 "Cannot determine equity (${equity}) or price (${last_price}). Failing safe."
fi

# ---- CHECK 4: position cost <= available cash ------------------------------

if [[ "${cash:-0}" -gt 0 && "${position_cost}" -gt 0 ]]; then
  if (( position_cost <= cash )); then
    pass 4
  else
    fail 4 "Position cost IDR ${position_cost} exceeds available cash IDR ${cash}."
  fi
else
  fail 4 "Cannot determine cash (${cash}). Failing safe."
fi

# ---- CHECK 5: catalyst in today's RESEARCH-LOG.md --------------------------
# Handles heading formats: "## 2026-04-19", "## 2026-04-19 — Pre-Market", etc.

catalyst_ok=0
if [[ -f "${RESEARCH_LOG}" ]]; then
  if grep -qF "${TODAY_DATE}" "${RESEARCH_LOG}" 2>/dev/null; then
    if awk -v date="${TODAY_DATE}" -v sym="${SYMBOL}" '
      /^#+/ && index($0, date) > 0 { in_section=1; next }
      /^#+/ { if (in_section) in_section=0 }
      in_section && index($0, sym) > 0 { found=1; exit }
      END { exit !found }
    ' "${RESEARCH_LOG}" 2>/dev/null; then
      catalyst_ok=1
    fi
  fi
fi

if [[ "${catalyst_ok}" -eq 1 ]]; then
  pass 5
else
  fail 5 "No catalyst for ${SYMBOL} in today's (${TODAY_DATE}) RESEARCH-LOG.md. Document thesis before trading."
fi

# ---- CHECK 6: IDX equity ticker (4-5 uppercase letters) --------------------

if echo "${SYMBOL}" | grep -qE '^[A-Z]{4,5}$'; then
  pass 6
else
  fail 6 "Symbol '${SYMBOL}' is not a valid IDX ticker (4-5 uppercase letters). No options, warrants (-W), or rights (-R)."
fi

# ---- CHECK 7: avg daily volume > 500,000 -----------------------------------

if [[ "${avg_vol:-0}" -gt 500000 ]]; then
  pass 7 "avg_vol=${avg_vol}"
else
  fail 7 "Avg daily volume ${avg_vol} < 500,000 minimum. Stock too illiquid for our sizing."
fi

# ---- CHECK 8: SHARES is a positive multiple of 100 -------------------------

if [[ "${SHARES}" -gt 0 ]] 2>/dev/null && (( SHARES % 100 == 0 )); then
  pass 8
else
  fail 8 "SHARES=${SHARES} is not a positive multiple of 100. IDX requires 100-share lots."
fi

# ---- CHECK 9: current price <= 3% above planned entry ----------------------

planned_entry=0
if [[ -f "${RESEARCH_LOG}" ]]; then
  planned_entry="$(awk -v date="${TODAY_DATE}" -v sym="${SYMBOL}" '
    /^#+/ && index($0, date) > 0 { in_section=1; next }
    /^#+/ { if (in_section) in_section=0 }
    in_section && index($0, sym) > 0 { found_sym=1 }
    found_sym && tolower($0) ~ /entry[[:space:]]*:/ {
      # strip commas, then find first price-like number (4-6 digits)
      line = $0; gsub(/,/, "", line)
      if (match(line, /[1-9][0-9]{3,6}/)) {
        print substr(line, RSTART, RLENGTH); exit
      }
    }
  ' "${RESEARCH_LOG}" 2>/dev/null || echo 0)"
fi

planned_entry="$(echo "${planned_entry}" | tr -d '[:space:]')"

if [[ "${planned_entry:-0}" -le 0 ]] 2>/dev/null; then
  fail 9 "No planned entry price for ${SYMBOL} in today's (${TODAY_DATE}) RESEARCH-LOG.md."
else
  max_chasing=$(( planned_entry * 103 / 100 ))
  if [[ "${last_price}" -le "${max_chasing}" ]] 2>/dev/null; then
    price_diff=$(( (last_price - planned_entry) * 100 / planned_entry ))
    pass 9 "last=${last_price}, planned=${planned_entry}, diff=${price_diff}%"
  else
    price_diff=$(( (last_price - planned_entry) * 100 / planned_entry ))
    fail 9 "Price ${last_price} is +${price_diff}% above planned ${planned_entry} (max 3%). Do not chase."
  fi
fi

# ---- CHECK 13: sector concentration <= 40% of equity -----------------------

if [[ "${SYMBOL_SECTOR}" != "Unknown" && "${equity}" -gt 0 && "${position_cost}" -gt 0 ]]; then
  sector_deployed=0
  if [[ -f "${PAPER_STATE}" ]]; then
    sector_deployed="$(python3 -c "
import json
sector_map = {
  'BBCA':'Banking','BBRI':'Banking','BMRI':'Banking','BBNI':'Banking','BNGA':'Banking','NISP':'Banking','BBTN':'Banking',
  'ADRO':'Coal','ITMG':'Coal','PTBA':'Coal','BUMI':'Coal','HRUM':'Coal',
  'MEDC':'Energy','PGAS':'Energy',
  'ANTM':'Nickel','INCO':'Nickel','MDKA':'Nickel','NCKL':'Nickel','MBMA':'Nickel',
  'TINS':'Metals',
  'UNVR':'Consumer','ICBP':'Consumer','INDF':'Consumer','MYOR':'Consumer','SIDO':'Consumer',
  'TLKM':'Telco','EXCL':'Telco','ISAT':'Telco','MTEL':'Telco','TOWR':'Telco',
  'BSDE':'Property','CTRA':'Property','SMRA':'Property','PWON':'Property',
  'GOTO':'Tech','BUKA':'Tech','EMTK':'Tech',
  'ASII':'Industrial','UNTR':'Industrial','AKRA':'Industrial',
  'AALI':'Plantation','LSIP':'Plantation',
  'JSMR':'Infrastructure','WSKT':'Infrastructure','PTPP':'Infrastructure','ADHI':'Infrastructure',
  'KLBF':'Healthcare','MIKA':'Healthcare',
  'SMGR':'Cement','INTP':'Cement',
}
try:
    d = json.load(open('${PAPER_STATE}'))
    total = sum(
        p['shares'] * p['entry_price']
        for p in d.get('positions', [])
        if sector_map.get(p['ticker']) == '${SYMBOL_SECTOR}'
    )
    print(total)
except Exception:
    print(0)
" 2>/dev/null || echo 0)"
  fi

  total_sector=$(( sector_deployed + position_cost ))
  max_sector=$(( equity * 40 / 100 ))
  if (( total_sector <= max_sector )); then
    sector_pct=$(( total_sector * 100 / equity ))
    pass 13 "sector=${SYMBOL_SECTOR}, total=${sector_pct}% of equity (cap 40%)"
  else
    sector_pct=$(( total_sector * 100 / equity ))
    fail 13 "Sector ${SYMBOL_SECTOR} would be ${sector_pct}% of equity (max 40%). Existing IDR ${sector_deployed} + new IDR ${position_cost} = IDR ${total_sector}."
  fi
else
  pass 13 "sector=${SYMBOL_SECTOR} (not in map, concentration unchecked)"
fi

# ---- CHECK 14: ADV participation cap (order <= 10% of avg daily vol) -------

if [[ "${avg_vol:-0}" -gt 0 ]]; then
  max_adv_shares=$(( avg_vol / 10 ))
  max_adv_shares=$(( (max_adv_shares / 100) * 100 ))  # floor to nearest 100-lot
  if [[ "${SHARES}" -le "${max_adv_shares}" ]] 2>/dev/null; then
    pass 14 "shares=${SHARES} <= 10% ADV=${max_adv_shares}"
  else
    fail 14 "Order ${SHARES} shares exceeds 10% of avg daily volume (${avg_vol}; max ${max_adv_shares} shares). Split order or reduce size."
  fi
else
  pass 14 "avg_vol unavailable — ADV cap skipped"
fi

# ---- CHECK 15: risk budget <= 75bps per idea --------------------------------
# risk_idr = shares × (entry − hard_cut); hard_cut = entry × 0.93
# risk_bps = risk_idr × 10000 / equity

if [[ "${last_price:-0}" -gt 0 && "${equity:-0}" -gt 0 ]]; then
  hard_cut=$(( last_price * 93 / 100 ))
  risk_idr=$(( SHARES * (last_price - hard_cut) ))
  risk_bps=$(( risk_idr * 10000 / equity ))
  if (( risk_bps <= 75 )); then
    pass 15 "risk=IDR ${risk_idr} (${risk_bps}bps of book, max 75bps)"
  else
    fail 15 "Risk budget ${risk_bps}bps exceeds 75bps max per idea. IDR ${risk_idr} at risk on IDR ${equity} book. Reduce SHARES."
  fi
else
  pass 15 "price or equity unavailable — risk budget check skipped"
fi

# ═══════════════════════════════════════════════════════════════════════════
# FINAL RESULT
# ═══════════════════════════════════════════════════════════════════════════

echo ""
if [[ "${#FAILURES[@]}" -eq 0 ]]; then
  echo "ALL 15 GATES PASSED — proceed to order placement"
  [[ "${WEEKLY_HALVED}" == "true" ]] && echo "NOTE: SHARES was halved to ${SHARES} per weekly loss cap rule."
  exit 0
else
  echo "=== TRADE REJECTED — ${#FAILURES[@]} GATE(S) FAILED ==="
  printf 'FAIL: %s\n' "${FAILURES[@]}"
  exit 1
fi
