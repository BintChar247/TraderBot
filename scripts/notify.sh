#!/usr/bin/env bash
# scripts/notify.sh — Notification Wrapper
#
# This is the ONLY way the agent sends notifications. Never post to Telegram
# or email directly. All channel routing and fallback logic lives here.
#
# Usage:
#   notify.sh send "message"
#   notify.sh send --channel=telegram "message"
#   notify.sh send --channel=email    "message"
#   notify.sh send --channel=none     "message"
#   notify.sh -h | --help
#
# Environment:
#   NOTIFY_CHANNEL        Default channel: telegram | email | none
#                         (default: telegram)
#   TELEGRAM_BOT_TOKEN    Token from @BotFather (required for telegram)
#   TELEGRAM_CHAT_ID      Your chat or group ID (required for telegram)
#
# Guarantees:
#   - Always safe to call. If credentials are missing or send fails, the
#     message is printed to stdout so nothing is ever silently dropped.
#   - Never crashes the calling routine. All error paths exit 0 after
#     emitting a warning to stderr and the message to stdout.

set -euo pipefail

# ---- locate repo root (for .env loading and fallback file path) -----------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Load .env if present (local dev)
if [[ -f "${REPO_ROOT}/.env" ]]; then
  # shellcheck disable=SC1091
  set -o allexport
  source "${REPO_ROOT}/.env"
  set +o allexport
fi

# ---- helpers --------------------------------------------------------------

warn() { echo "WARN:  $*" >&2; }

timestamp_wib() {
  TZ=Asia/Jakarta date "+%Y-%m-%d %H:%M WIB" 2>/dev/null \
    || date -u "+%Y-%m-%d %H:%M UTC (WIB unavailable)"
}

# print_message: always print the message to stdout as the safe fallback
print_message() {
  local msg="$1"
  echo "[notify] ${msg}"
}

# fallback_to_stdout MSG [REASON]
# Emit a warning to stderr then print the message to stdout.
# This guarantees the message is never silently dropped when a channel fails.
fallback_to_stdout() {
  local msg="$1"
  local reason="${2:-channel unavailable}"
  warn "Notification channel failed (${reason}). Message printed to stdout instead."
  print_message "${msg}"
}

# ---- telegram send --------------------------------------------------------

send_telegram() {
  local msg="$1"
  local token="${TELEGRAM_BOT_TOKEN:-}"
  local chat_id="${TELEGRAM_CHAT_ID:-}"

  if [[ -z "${token}" ]]; then
    warn "TELEGRAM_BOT_TOKEN is not set."
    return 1
  fi
  if [[ -z "${chat_id}" ]]; then
    warn "TELEGRAM_CHAT_ID is not set."
    return 1
  fi

  # Build JSON payload; escape the message safely via printf %s in the here-string.
  # For production use with arbitrary text, pipe through jq --arg or python3 json.dumps.
  # TODO: replace with jq-based JSON building once jq is confirmed available.
  local escaped_msg
  escaped_msg="$(printf '%s' "${msg}" \
    | sed 's/\\/\\\\/g; s/"/\\"/g; s/'"$(printf '\t')"'/\\t/g' \
    | tr -d '\000-\037')"
  # Preserve newlines as literal \n in the JSON string
  escaped_msg="$(printf '%s' "${escaped_msg}" | awk '{printf "%s\\n", $0}' | sed 's/\\n$//')"

  local response http_code
  response="$(curl \
    --silent \
    --show-error \
    --write-out "\n__HTTP_CODE__:%{http_code}" \
    -X POST \
    "https://api.telegram.org/bot${token}/sendMessage" \
    -H "Content-Type: application/json" \
    -d "{\"chat_id\":\"${chat_id}\",\"text\":\"${escaped_msg}\",\"parse_mode\":\"Markdown\"}" \
    2>&1 || true)"

  http_code="$(echo "${response}" | grep -oE '__HTTP_CODE__:[0-9]+' | cut -d: -f2 || echo 0)"
  local body
  body="$(echo "${response}" | sed 's/__HTTP_CODE__:[0-9]*$//')"

  if [[ "${http_code}" == "200" ]]; then
    echo "Notification sent via Telegram (chat_id=${chat_id})."
    return 0
  else
    warn "Telegram API returned HTTP ${http_code}. Response: ${body}"
    return 1
  fi
}

# ---- email send (stub) ----------------------------------------------------

send_email() {
  local msg="$1"
  # TODO: implement email sending via sendmail, msmtp, or an email API (SendGrid, etc.)
  # For now this is a documented stub that fails gracefully.
  warn "Email channel is not yet implemented (stub). TODO: wire to sendmail or an email API."
  return 1
}

# ---- help -----------------------------------------------------------------

show_help() {
  cat <<HELP
notify.sh — Notification Wrapper

Usage:
  notify.sh send "message"
  notify.sh send --channel=telegram "message"
  notify.sh send --channel=email    "message"
  notify.sh send --channel=none     "message"   (stdout only, no network call)
  notify.sh -h | --help

Channels:
  telegram  Send via Telegram Bot API (default)
            Requires: TELEGRAM_BOT_TOKEN, TELEGRAM_CHAT_ID
  email     Send via email (TODO: not yet implemented)
  none      Print to stdout only (silent/local mode)

Behavior:
  Always safe to call. If config is missing or the send fails, the message
  is printed to stdout and a warning is emitted to stderr. Nothing is ever
  silently dropped.

Environment variables:
  NOTIFY_CHANNEL       Default channel (telegram | email | none)
  TELEGRAM_BOT_TOKEN   Telegram bot token from @BotFather
  TELEGRAM_CHAT_ID     Telegram chat or group ID
HELP
}

# ---- dispatch -------------------------------------------------------------

SUBCOMMAND="${1:-}"
shift || true

case "${SUBCOMMAND}" in
  send)
    # Parse optional --channel=X flag before the message
    CHANNEL="${NOTIFY_CHANNEL:-telegram}"
    MESSAGE=""

    for arg in "$@"; do
      case "${arg}" in
        --channel=*)
          CHANNEL="${arg#--channel=}"
          ;;
        *)
          # First non-flag argument is the message
          if [[ -z "${MESSAGE}" ]]; then
            MESSAGE="${arg}"
          fi
          ;;
      esac
    done

    if [[ -z "${MESSAGE}" ]]; then
      echo "ERROR: No message provided. Usage: notify.sh send [--channel=X] \"message\"" >&2
      exit 1
    fi

    TIMESTAMP="$(timestamp_wib)"

    case "${CHANNEL}" in
      telegram)
        if ! send_telegram "${MESSAGE}"; then
          fallback_to_stdout "${MESSAGE}" "Telegram send failed"
        fi
        ;;
      email)
        if ! send_email "${MESSAGE}"; then
          fallback_to_stdout "${MESSAGE}" "email send failed (stub)"
        fi
        ;;
      none)
        # Silent/local mode: print to stdout only, no network call
        print_message "${MESSAGE}"
        ;;
      *)
        warn "Unknown channel '${CHANNEL}'. Valid: telegram | email | none."
        fallback_to_stdout "${MESSAGE}" "unknown channel '${CHANNEL}'"
        ;;
    esac
    ;;

  -h|--help|help)
    show_help
    ;;
  "")
    warn "No subcommand given."
    show_help >&2
    exit 1
    ;;
  *)
    echo "ERROR: Unknown subcommand '${SUBCOMMAND}'. Run: notify.sh --help" >&2
    exit 1
    ;;
esac

exit 0
