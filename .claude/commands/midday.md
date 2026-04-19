Run the midday scan workflow manually. Same as the scheduled 11:30 WIB routine.

**Usage:** `/midday`

This command is identical to `routines/midday.md`. Run it when:
- The scheduled routine didn't fire
- You want to check positions manually mid-session
- Testing stop-tightening logic locally

---

Follow all steps in `routines/midday.md` exactly.

Env check first:
```bash
source .env 2>/dev/null || echo "No .env — export vars manually"
for v in BROKER_API_KEY BROKER_API_SECRET BROKER_ACCOUNT_ID BROKER_ENDPOINT; do
  [[ -n "${!v:-}" ]] && echo "$v: set" || echo "$v: MISSING"
done
```
