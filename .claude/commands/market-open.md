Run the market-open execution workflow manually. Same as the scheduled 09:15 WIB routine.

**Usage:** `/market-open`

This command is identical to `routines/market-open.md`. Run it when:
- The scheduled routine didn't fire
- You want to execute trades manually after reviewing research
- Testing order flow locally

Prerequisite: today's entry must exist in `memory/RESEARCH-LOG.md`. If not, run `/pre-market` first.

---

Follow all steps in `routines/market-open.md` exactly.

Env check first:
```bash
source .env 2>/dev/null || echo "No .env — export vars manually"
for v in BROKER_API_KEY BROKER_API_SECRET BROKER_ACCOUNT_ID BROKER_ENDPOINT; do
  [[ -n "${!v:-}" ]] && echo "$v: set" || echo "$v: MISSING"
done
```
