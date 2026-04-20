#!/usr/bin/env bash
# Append a run entry to dashboard/data.json activity_log.
# Usage:
#   bash scripts/log-activity.sh \
#     --routine  <pre-market|market-open|midday|daily-summary|weekly-review> \
#     --status   <success|warning|error> \
#     --summary  "<one-line outcome>" \
#     --duration <seconds>               \   # optional
#     --actions  '<json-array>'           \   # optional, e.g. '[{"type":"buy","ticker":"ITMG","detail":"3200 shares @ 4050"}]'
#     --commit   <sha>                    \   # optional
#     --commit-msg "<message>"                # optional

set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
DATA_FILE="$REPO_ROOT/dashboard/data.json"

if [[ ! -f "$DATA_FILE" ]]; then
  echo "ERROR: $DATA_FILE not found" >&2
  exit 1
fi

if ! command -v python3 &>/dev/null; then
  echo "ERROR: python3 required" >&2
  exit 1
fi

# Parse args
ROUTINE="" STATUS="" SUMMARY="" DURATION="" ACTIONS="[]" COMMIT_HASH="" COMMIT_MSG=""
while [[ $# -gt 0 ]]; do
  case "$1" in
    --routine)    ROUTINE="$2";    shift 2 ;;
    --status)     STATUS="$2";     shift 2 ;;
    --summary)    SUMMARY="$2";    shift 2 ;;
    --duration)   DURATION="$2";   shift 2 ;;
    --actions)    ACTIONS="$2";    shift 2 ;;
    --commit)     COMMIT_HASH="$2";shift 2 ;;
    --commit-msg) COMMIT_MSG="$2"; shift 2 ;;
    *) echo "Unknown arg: $1" >&2; exit 1 ;;
  esac
done

[[ -z "$ROUTINE" ]] && { echo "ERROR: --routine required" >&2; exit 1; }
[[ -z "$STATUS"  ]] && { echo "ERROR: --status required"  >&2; exit 1; }
[[ -z "$SUMMARY" ]] && { echo "ERROR: --summary required" >&2; exit 1; }

TS=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

python3 - <<PYEOF
import json, sys

data_file = "$DATA_FILE"
with open(data_file) as f:
    data = json.load(f)

if "activity_log" not in data:
    data["activity_log"] = []

entry = {
    "timestamp": "$TS",
    "routine":   "$ROUTINE",
    "status":    "$STATUS",
    "summary":   "$SUMMARY",
}

duration_s = "$DURATION"
if duration_s:
    try:
        entry["duration_s"] = int(duration_s)
    except ValueError:
        pass

try:
    actions = json.loads(r"""$ACTIONS""")
    if actions:
        entry["actions"] = actions
except Exception:
    pass

commit_hash = "$COMMIT_HASH"
commit_msg  = "$COMMIT_MSG"
if commit_hash:
    entry["commit_hash"]    = commit_hash
    entry["commit_message"] = commit_msg

data["activity_log"].append(entry)

# Keep at most 200 entries (newest last)
data["activity_log"] = data["activity_log"][-200:]

with open(data_file, "w") as f:
    json.dump(data, f, indent=2)

print(f"activity_log: appended entry for {entry['routine']} ({entry['status']})")
PYEOF
