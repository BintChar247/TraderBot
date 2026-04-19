# Routines — Cloud Prompts

These files are the production prompts for the five scheduled cloud routines.
Each runs as an independent Claude agent on a cron schedule.

## Schedule (WIB / UTC)

| Routine | WIB | Cron (UTC) | Days |
|---------|-----|-----------|------|
| pre-market.md | 07:00 | `0 0 * * 1-5` | Mon-Fri |
| market-open.md | 09:15 | `15 2 * * 1-5` | Mon-Fri |
| midday.md | 11:30 | `30 4 * * 1-5` | Mon-Fri |
| daily-summary.md | 15:15 | `15 8 * * 1-5` | Mon-Fri |
| weekly-review.md | 16:00 | `0 9 * * 5` | Fri only |

Minimum 30-minute gap between routines. Never two running concurrently.

## Setup Checklist (one-time per routine)

- [ ] Paste the `.md` content into the routine's prompt field
- [ ] Set all required env vars on the routine (NOT in a .env file):
      `BROKER_API_KEY`, `BROKER_API_SECRET`, `BROKER_ACCOUNT_ID`,
      `TELEGRAM_BOT_TOKEN`, `TELEGRAM_CHAT_ID`, `TRADING_MODE=paper`
- [ ] Enable "Allow unrestricted branch pushes" (required for git push to work)
- [ ] Hit "Run now" and verify the memory file was committed and pushed

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| `git push` fails | Enable "Allow unrestricted branch pushes" in routine settings |
| "KEY not set" | Add the env var to the routine's configuration |
| Agent creates .env file | Re-paste the prompt exactly — it was paraphrased |
| Yesterday's trades missing | Check `git log origin/main` — prior run didn't push |
| Push fails "fetch first" | Handled automatically by `git pull --rebase` in the prompt |
