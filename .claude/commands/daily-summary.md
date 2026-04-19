Run the daily summary (EOD) workflow manually. Same as the scheduled 15:15 WIB routine.

**Usage:** `/daily-summary`

This command is identical to `routines/daily-summary.md`. Run it when:
- The scheduled routine didn't fire
- You want an EOD snapshot on demand
- Testing the snapshot format

Important: the EOD snapshot commit is critical for tomorrow's P&L delta calculation.
Do not skip the commit at the end.

---

Follow all steps in `routines/daily-summary.md` exactly.

Env check first:
```bash
source .env 2>/dev/null || echo "No .env — export vars manually"
for v in BROKER_API_KEY BROKER_API_SECRET BROKER_ACCOUNT_ID BROKER_ENDPOINT; do
  [[ -n "${!v:-}" ]] && echo "$v: set" || echo "$v: MISSING"
done
```
