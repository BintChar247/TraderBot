Run the weekly review workflow manually. Same as the scheduled Friday 16:00 WIB routine.

**Usage:** `/weekly-review`

This command is identical to `routines/weekly-review.md`. Run it when:
- The scheduled routine didn't fire on Friday
- You want to run a mid-week performance check (for learning; don't update strategy mid-week)
- Testing the weekly review format locally

Strategy rule changes should only happen on Friday after a full week of data.
If running mid-week, write the review but don't update TRADING-STRATEGY.md.

---

Follow all steps in `routines/weekly-review.md` exactly.

Env check first:
```bash
source .env 2>/dev/null || echo "No .env — export vars manually"
for v in BROKER_API_KEY BROKER_API_SECRET BROKER_ACCOUNT_ID BROKER_ENDPOINT; do
  [[ -n "${!v:-}" ]] && echo "$v: set" || echo "$v: MISSING"
done
```
