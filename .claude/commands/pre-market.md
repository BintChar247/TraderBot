Run the pre-market research workflow manually. Same as the scheduled 07:00 WIB routine.

**Usage:** `/pre-market`

This command is identical to `routines/pre-market.md`. Run it when:
- The scheduled routine didn't fire (holiday, missed cron)
- You want to run research outside normal hours
- Testing the routine locally before deploying to cloud

---

Follow all steps in `routines/pre-market.md` exactly.

Key difference from cloud: credentials come from your local `.env` file, not routine env vars.
Verify env is loaded before running:
```bash
source .env 2>/dev/null || echo "No .env found — export vars manually"
for v in BROKER_API_KEY BROKER_API_SECRET BROKER_ACCOUNT_ID BROKER_ENDPOINT; do
  [[ -n "${!v:-}" ]] && echo "$v: set" || echo "$v: MISSING"
done
```

Then follow all STEPS 1–6 from `routines/pre-market.md`.
