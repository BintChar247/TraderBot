#!/usr/bin/env bash
# scripts/market-data.sh — IDX Market Data Wrapper
#
# Tiered fallback for every data type:
#   quote / intraday   GoAPI (real-time) → yfinance (delayed ~15 min)
#   history            yfinance
#   fundamentals       Sectors.app → yfinance
#   index              yfinance (^JKSE)
#   commodity          prints agent instruction to use WebSearch directly
#
# Usage:
#   market-data.sh quote BBCA              Last price (GoAPI → yfinance)
#   market-data.sh intraday BBCA 1m        Intraday bars (GoAPI → yfinance fallback)
#   market-data.sh history BBCA 30d        Daily OHLCV for last N days
#   market-data.sh fundamentals BBCA       Financial ratios + statements
#   market-data.sh index JKSE              IHSG index level
#   market-data.sh commodity coal          Commodity price (agent must use WebSearch)
#   market-data.sh -h | --help             Show this help
#
# Environment:
#   GOAPI_API_KEY      GoAPI.io real-time feed (optional — falls back to yfinance)
#   SECTORS_API_KEY    Sectors.app fundamentals (optional — falls back to yfinance)
#   PYTHON             Python interpreter (default: auto-detected .venv or python3)

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# ---- load .env if present (local dev only) ----------------------------------

if [[ -f "${REPO_ROOT}/.env" ]]; then
  # shellcheck disable=SC1091
  set -o allexport
  source "${REPO_ROOT}/.env"
  set +o allexport
fi

# ---- configuration ----------------------------------------------------------

GOAPI_API_KEY="${GOAPI_API_KEY:-}"
SECTORS_API_KEY="${SECTORS_API_KEY:-}"
GOAPI_BASE="https://api.goapi.io/v1/stock/idx"
SECTORS_BASE="https://api.sectors.app/v1"

# ---- python resolver --------------------------------------------------------
# Prefer the repo venv (Python 3.12); fall back to system python3.

_find_python() {
  if [[ -x "${REPO_ROOT}/.venv/bin/python" ]]; then
    echo "${REPO_ROOT}/.venv/bin/python"
  elif command -v python3 &>/dev/null; then
    echo "python3"
  else
    echo "python"
  fi
}

PYTHON="${PYTHON:-$(_find_python)}"
YFINANCE_HELPER="${SCRIPT_DIR}/yfinance_helper.py"

# ---- helpers ----------------------------------------------------------------

die()  { echo "ERROR: $*" >&2; exit 1; }
warn() { echo "WARN:  $*" >&2; }
info() { echo "INFO:  $*" >&2; }

# Normalize IDX ticker: strip .JK suffix, uppercase (bash 3 compatible)
_clean_sym() { echo "${1}" | tr '[:lower:]' '[:upper:]' | sed 's/\.JK$//'; }

# GoAPI.io real-time quote endpoint.
# Returns empty string if key missing or request fails.
_goapi_quote() {
  local sym
  sym="$(_clean_sym "$1")"
  [[ -n "${GOAPI_API_KEY}" ]] || return 0   # no key → fall through

  local url="${GOAPI_BASE}/price?ticker=${sym}&apikey=${GOAPI_API_KEY}"
  local resp
  resp="$(curl --silent --show-error --fail --max-time 8 "${url}" 2>/dev/null)" || return 0
  # GoAPI wraps data in {"data": {...}}; normalise to flat JSON
  # TODO: verify exact field names against GoAPI docs when key is wired
  if echo "${resp}" | grep -q '"last"'; then
    echo "${resp}" | python3 -c "
import json,sys
d=json.load(sys.stdin)
inner=d.get('data',d)
print(json.dumps({
  'source':'goapi',
  'symbol':'${sym}',
  'last': inner.get('last') or inner.get('close') or inner.get('price'),
  'prev_close': inner.get('prev_close') or inner.get('reference_price'),
  'day_change_pct': inner.get('change_pct') or inner.get('pct_change'),
  'volume': inner.get('volume') or inner.get('vol'),
  'ts': inner.get('timestamp') or inner.get('date'),
},indent=2))
"
    return 0
  fi
  return 0  # non-fatal; fall to yfinance
}

# Sectors.app fundamentals endpoint.
# Returns empty string if key missing or request fails.
_sectors_fundamentals() {
  local sym
  sym="$(_clean_sym "$1")"
  [[ -n "${SECTORS_API_KEY}" ]] || return 0

  local url="${SECTORS_BASE}/company/report/${sym}/?apikey=${SECTORS_API_KEY}"
  local resp
  resp="$(curl --silent --show-error --fail --max-time 12 "${url}" 2>/dev/null)" || return 0
  if echo "${resp}" | grep -q '"symbol"'; then
    # TODO: normalise field names once Sectors.app schema is confirmed
    echo "${resp}" | python3 -c "
import json,sys
d=json.load(sys.stdin)
print(json.dumps({'source':'sectors.app','data':d},indent=2))
"
    return 0
  fi
  return 0
}

# ---- subcommand: quote SYMBOL -----------------------------------------------

cmd_quote() {
  local sym="${1:-}"
  [[ -n "${sym}" ]] || die "Usage: market-data.sh quote SYMBOL"

  # Tier 1: GoAPI real-time
  local result
  result="$(_goapi_quote "${sym}")"
  if [[ -n "${result}" ]]; then
    echo "${result}"
    return 0
  fi

  # Tier 2: yfinance (delayed)
  info "GoAPI unavailable or key not set — falling back to yfinance"
  "${PYTHON}" "${YFINANCE_HELPER}" quote "${sym}"
}

# ---- subcommand: intraday SYMBOL INTERVAL -----------------------------------
# INTERVAL: 1m | 5m | 15m | 30m | 1h  (GoAPI supports 1m; yfinance supports 1m–1h)

cmd_intraday() {
  local sym="${1:-}"
  local interval="${2:-1m}"
  [[ -n "${sym}" ]] || die "Usage: market-data.sh intraday SYMBOL INTERVAL"

  if [[ -n "${GOAPI_API_KEY}" ]]; then
    local clean_sym
    clean_sym="$(_clean_sym "${sym}")"
    local url="${GOAPI_BASE}/intraday?ticker=${clean_sym}&interval=${interval}&apikey=${GOAPI_API_KEY}"
    local resp
    resp="$(curl --silent --show-error --fail --max-time 15 "${url}" 2>/dev/null)" || true
    if [[ -n "${resp}" ]] && echo "${resp}" | grep -q '"data"'; then
      echo "${resp}"
      return 0
    fi
    warn "GoAPI intraday request failed — falling back to yfinance"
  else
    info "GOAPI_API_KEY not set — using yfinance for intraday"
  fi

  # yfinance fallback: price-history with 1d period uses intraday intervals for recent data
  # yfinance_helper doesn't have intraday natively; emit 1-day bars as proxy
  warn "yfinance intraday: returning today's minute bars (requires yfinance >= 0.2)"
  "${PYTHON}" - <<PYEOF
import yfinance as yf, json, sys

sym = "$(_clean_sym "${sym}").JK"
interval = "${interval}"
# yfinance interval strings: 1m 2m 5m 15m 30m 60m 90m 1h 1d
tf = yf.Ticker(sym)
hist = tf.history(period="1d", interval=interval, auto_adjust=False)
if hist.empty:
    print(json.dumps({"symbol": sym, "interval": interval, "error": "no intraday data"}))
    sys.exit(0)
bars = []
for ts, row in hist.iterrows():
    bars.append({
        "ts": str(ts),
        "open": float(row["Open"]),
        "high": float(row["High"]),
        "low":  float(row["Low"]),
        "close": float(row["Close"]),
        "volume": int(row["Volume"]) if row["Volume"] == row["Volume"] else None,
    })
print(json.dumps({"source": "yfinance", "symbol": sym, "interval": interval,
                  "count": len(bars), "bars": bars}, indent=2))
PYEOF
}

# ---- subcommand: history SYMBOL PERIOD --------------------------------------
# PERIOD: 30d | 90d | 1y | etc.  yfinance period strings

cmd_history() {
  local sym="${1:-}"
  local period="${2:-30d}"
  [[ -n "${sym}" ]] || die "Usage: market-data.sh history SYMBOL PERIOD"

  # Normalise: remove trailing 'd' to get day count for yfinance_helper
  local days
  days="${period%d}"
  if [[ "${days}" =~ ^[0-9]+$ ]]; then
    "${PYTHON}" "${YFINANCE_HELPER}" price-history "${sym}" --days "${days}"
  else
    # Non-integer period (e.g. 1y) — call yfinance directly
    "${PYTHON}" - <<PYEOF
import yfinance as yf, json, sys

sym = "$(_clean_sym "${sym}").JK"
period = "${period}"
t = yf.Ticker(sym)
hist = t.history(period=period, auto_adjust=False)
if hist.empty:
    print(json.dumps({"symbol": sym, "period": period, "error": "no data"}))
    sys.exit(0)
bars = []
for idx, row in hist.iterrows():
    bars.append({
        "date": str(idx.date()),
        "open": float(row["Open"]),
        "high": float(row["High"]),
        "low":  float(row["Low"]),
        "close": float(row["Close"]),
        "volume": int(row["Volume"]) if row["Volume"] == row["Volume"] else None,
    })
print(json.dumps({"source": "yfinance", "symbol": sym, "period": period,
                  "count": len(bars), "bars": bars}, indent=2))
PYEOF
  fi
}

# ---- subcommand: fundamentals SYMBOL ----------------------------------------

cmd_fundamentals() {
  local sym="${1:-}"
  [[ -n "${sym}" ]] || die "Usage: market-data.sh fundamentals SYMBOL"

  # Tier 1: Sectors.app
  local result
  result="$(_sectors_fundamentals "${sym}")"
  if [[ -n "${result}" ]]; then
    echo "${result}"
    return 0
  fi

  # Tier 2: yfinance
  info "Sectors.app unavailable or key not set — falling back to yfinance"
  "${PYTHON}" "${YFINANCE_HELPER}" fundamentals "${sym}"
}

# ---- subcommand: index INDEX_CODE -------------------------------------------
# INDEX_CODE: JKSE | LQ45 | IDX30 | KOMPAS100  (Yahoo Finance ^JKSE etc.)

cmd_index() {
  local idx="${1:-JKSE}"
  [[ -n "${idx}" ]] || die "Usage: market-data.sh index INDEX_CODE"

  # Map common IDX names to Yahoo Finance symbols
  local idx_upper yf_sym
  idx_upper="$(echo "${idx}" | tr '[:lower:]' '[:upper:]')"
  case "${idx_upper}" in
    JKSE|IHSG)   yf_sym="^JKSE" ;;
    LQ45)        yf_sym="^LQ45" ;;
    IDX30)       yf_sym="^IDX30" ;;
    KOMPAS100)   yf_sym="^KOMPAS100" ;;
    *)           yf_sym="^${idx_upper}" ;;
  esac

  "${PYTHON}" - <<PYEOF
import yfinance as yf, json, sys
from datetime import datetime

sym = "${yf_sym}"
t = yf.Ticker(sym)
hist = t.history(period="2d", auto_adjust=False)
if hist.empty:
    print(json.dumps({"index": "${idx}", "yf_symbol": sym, "error": "no data"}))
    sys.exit(0)
last  = hist.iloc[-1]
prev  = hist.iloc[-2] if len(hist) >= 2 else last
close = float(last["Close"])
prev_close = float(prev["Close"])
chg_pct = (close - prev_close) / prev_close * 100.0 if prev_close else None
print(json.dumps({
    "source": "yfinance",
    "index": "${idx}",
    "yf_symbol": sym,
    "level": close,
    "prev_close": prev_close,
    "day_change_pct": round(chg_pct, 3) if chg_pct else None,
    "ts": str(hist.index[-1]),
}, indent=2))
PYEOF
}

# ---- subcommand: commodity NAME ---------------------------------------------
# Commodity prices require live web search — cannot be reliably fetched via
# GoAPI or yfinance. The agent MUST use its WebSearch tool directly.
# This subcommand prints the recommended search queries and exits.

cmd_commodity() {
  local name="${1:-}"
  [[ -n "${name}" ]] || die "Usage: market-data.sh commodity NAME (e.g. coal, nickel, cpo)"

  cat <<MSG
[market-data] commodity prices are not available via GoAPI or yfinance.
Use the WebSearch tool with these queries:
  "${name} price today USD"
  "Newcastle ${name} benchmark price $(date +%Y-%m-%d)"
  "LME ${name} price today"
Then parse the price from the web result and proceed.
MSG
  exit 0
}

# ---- help -------------------------------------------------------------------

show_help() {
  cat <<HELP
market-data.sh — IDX Market Data Wrapper (GoAPI → yfinance → Sectors.app)

Usage:
  market-data.sh quote SYMBOL              Last price (GoAPI → yfinance)
  market-data.sh intraday SYMBOL INTERVAL  Intraday bars (1m 5m 15m 30m 1h)
  market-data.sh history SYMBOL PERIOD     Daily OHLCV (e.g. 30d, 90d, 1y)
  market-data.sh fundamentals SYMBOL       Ratios + statements (Sectors.app → yfinance)
  market-data.sh index INDEX_CODE          Index level (JKSE, LQ45, IDX30)
  market-data.sh commodity NAME            Prints WebSearch queries (agent must search)
  market-data.sh -h | --help               Show this help

Fallback chain:
  Real-time quotes  : GoAPI (GOAPI_API_KEY)   → yfinance (delayed ~15 min)
  Intraday bars     : GoAPI                   → yfinance
  Fundamentals      : Sectors.app (SECTORS_API_KEY) → yfinance
  Index             : yfinance (^JKSE etc.)
  Commodity prices  : WebSearch (agent uses native tool — no script fallback)

Environment:
  GOAPI_API_KEY      GoAPI.io real-time feed key (optional)
  SECTORS_API_KEY    Sectors.app fundamentals key (optional)
  PYTHON             Python interpreter override (default: .venv/bin/python)
HELP
}

# ---- dispatch ---------------------------------------------------------------

SUBCOMMAND="${1:-}"
shift || true

case "${SUBCOMMAND}" in
  quote)        cmd_quote "$@" ;;
  intraday)     cmd_intraday "$@" ;;
  history)      cmd_history "$@" ;;
  fundamentals) cmd_fundamentals "$@" ;;
  index)        cmd_index "$@" ;;
  commodity)    cmd_commodity "$@" ;;
  -h|--help|help) show_help ;;
  "")
    warn "No subcommand given."
    show_help >&2
    exit 1
    ;;
  *)
    die "Unknown subcommand: '${SUBCOMMAND}'. Run: market-data.sh --help"
    ;;
esac
