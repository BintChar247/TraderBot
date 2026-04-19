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

_paper_portfolio() {
  cat <<'JSON'
{
  "mode": "paper",
  "account_id": "PAPER_ACCT",
  "equity": 10000000000,
  "cash": 10000000000,
  "buying_power": 10000000000,
  "currency": "IDR",
  "positions": [],
  "note": "Paper account — IDR 10,000,000,000 equity. No real orders placed."
}
JSON
}

_paper_positions() {
  cat <<'JSON'
{
  "mode": "paper",
  "positions": [],
  "count": 0,
  "note": "No paper positions held yet."
}
JSON
}

_paper_quote() {
  local sym="$1"
  # TODO: wire to real IDX market data (yfinance .JK suffix, broker market data, etc.)
  cat <<JSON
{
  "mode": "paper",
  "symbol": "${sym}",
  "last_price": 5000,
  "avg_daily_volume": 1500000,
  "lot_size": 100,
  "currency": "IDR",
  "note": "Stub quote — TODO: wire to real IDX market data feed."
}
JSON
}

_paper_cash() {
  # TODO: wire to real paper account state if tracking paper P&L across sessions
  echo "10000000000"
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
    cat <<JSON
{"paper_mode":true,"status":"filled","order_id":"${order_id}","symbol":"${sym}","shares":${shares},"side":"buy"}
JSON
    # Place a paper trailing stop at 10% below fill price (IDX rule: every position)
    info "Placing 10% trailing stop (paper)"
    cmd_set_stop "${sym}" "10"
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
    cat <<JSON
{"paper_mode":true,"status":"filled","order_id":"${order_id}","symbol":"${sym}","shares":${shares},"side":"sell"}
JSON
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
    die "set-stop: PCT must be a positive number (e.g. 10 for 10%)"
  fi

  if [[ "${TRADING_MODE}" == "paper" ]]; then
    info "PAPER MODE — simulating trailing stop for ${sym} at ${pct}% below current price"
    cat <<JSON
{"paper_mode":true,"status":"stop_set","symbol":"${sym}","trailing_pct":${pct},"order_type":"trailing_stop","time_in_force":"gtc"}
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

# ---- help -----------------------------------------------------------------

show_help() {
  cat <<HELP
broker.sh — IDX Broker API Wrapper

Usage:
  broker.sh portfolio              Print equity, cash, positions (JSON)
  broker.sh quote SYMBOL           Last price, avg daily volume, lot size
  broker.sh buy SYMBOL SHARES      Buy (9-gate checked); places 10% trailing stop
  broker.sh sell SYMBOL SHARES     Sell SHARES of SYMBOL at market
  broker.sh set-stop SYMBOL PCT    Set/replace trailing stop at PCT% below price
  broker.sh positions              List open positions only
  broker.sh cash                   Print available cash (IDR integer)
  broker.sh -h | --help            Show this help

Current mode: ${TRADING_MODE}
  (set TRADING_MODE=live to send real orders; default is paper)

Environment variables:
  TRADING_MODE        paper | live  (required; defaults to paper)
  BROKER_API_KEY      API key (required in live mode)
  BROKER_API_SECRET   API secret — used for HMAC request signing (live mode)
  BROKER_ACCOUNT_ID   Account ID (required in live mode)
  BROKER_BASE_URL     Broker REST base URL (default: https://api.example-broker.id/v1)
HELP
}

# ---- dispatch -------------------------------------------------------------

SUBCOMMAND="${1:-}"
shift || true

case "${SUBCOMMAND}" in
  portfolio)          cmd_portfolio ;;
  quote)              cmd_quote "$@" ;;
  buy)                cmd_buy "$@" ;;
  sell)               cmd_sell "$@" ;;
  set-stop)           cmd_set_stop "$@" ;;
  positions)          cmd_positions ;;
  cash)               cmd_cash ;;
  -h|--help|help)     show_help ;;
  "")
    warn "No subcommand given."
    show_help >&2
    exit 1
    ;;
  *)
    die "Unknown subcommand: '${SUBCOMMAND}'. Run: broker.sh --help"
    ;;
esac
