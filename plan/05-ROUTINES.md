# Step 5 — Routines (The Five Workflows in Detail)

> *Every workflow follows the same 8-step scaffold. The differences are what each one reads and writes.*

---

## Trigger Table (Asia/Jakarta timezone)

| # | Trigger Name | Cron (WIB) | Cron (UTC) | Purpose | Writes to | Notify? |
|---|-------------|-----------|-----------|---------|-----------|---------|
| 1 | IDX — Pre-market research | 7:00am | `0 0 * * 1-5` | Research catalysts, draft trade ideas | RESEARCH-LOG.md | Silent unless urgent |
| 2 | IDX — Market-open execution | 9:15am | `15 2 * * 1-5` | Execute planned trades + set stops | TRADE-LOG.md | Only if trade placed |
| 3 | IDX — Midday scan | 11:30am | `30 4 * * 1-5` | Cut -7% losers, tighten stops on winners | TRADE-LOG.md | Only if action taken |
| 4 | IDX — Daily summary (EOD) | 3:15pm | `15 8 * * 1-5` | EOD snapshot + daily recap | TRADE-LOG.md | Always (one message) |
| 5 | IDX — Weekly review | 4:00pm Fri | `0 9 * * 5` | Week recap + lessons | WEEKLY-REVIEW.md | Always (one message) |

All run on `claude-opus`. Spaced 30+ min apart. Never force-push.

---

## Pre-Market Workflow (07:00 WIB, before the open)

```
STEP 1 — Read memory for context:
  - memory/TRADING-STRATEGY.md
  - tail of memory/TRADE-LOG.md
  - tail of memory/RESEARCH-LOG.md

STEP 2 — Pull live account state:
  bash scripts/broker.sh account
  bash scripts/broker.sh positions
  bash scripts/broker.sh orders

STEP 3 — Research market context via WebSearch. Run queries:
  - "Newcastle coal price today"
  - "IHSG index level today, Asian markets overnight"
  - "IDR USD exchange rate today"
  - "Top IDX stock catalysts today $DATE"
  - "Indonesia earnings releases today before market open"
  - "Indonesia economic calendar CPI BI rate decision"
  - "IDX LQ45 sector momentum"
  - News on any currently-held ticker
  (Use Claude native WebSearch, not Perplexity)

STEP 4 — Write a dated entry to memory/RESEARCH-LOG.md:
  - Account snapshot (equity, cash, buying power)
  - Market context (commodities, IHSG, IDR, today's releases)
  - 2-3 actionable trade ideas WITH catalyst + entry/stop/target
  - Risk factors for the day
  - Decision: trade or HOLD (default HOLD — patience > activity)

STEP 5 — Notification: silent unless urgent.
  (Urgent = held position already below -7%, thesis broke overnight,
   major macro event like BI emergency rate change)

STEP 6 — COMMIT AND PUSH (mandatory):
  git add memory/RESEARCH-LOG.md
  git commit -m "pre-market research $DATE"
  git push origin main
```

---

## Market-Open Workflow (09:15 WIB, shortly after the bell)

```
STEP 1 — Read memory for today's plan:
  - memory/TRADING-STRATEGY.md
  - TODAY's entry in memory/RESEARCH-LOG.md
    (if missing, run pre-market STEPS 1-3 inline first.
     Never trade without documented research.)
  - tail of memory/TRADE-LOG.md (for weekly trade count)

STEP 2 — Re-validate with live data:
  bash scripts/broker.sh account
  bash scripts/broker.sh positions
  bash scripts/broker.sh quote <each planned ticker>

STEP 3 — Hard-check rules BEFORE every order. Skip any trade that fails:
  - Total positions after trade <= 6
  - Trades this week <= 3
  - Position cost <= 20% of equity
  - Catalyst documented in today's RESEARCH-LOG
  - Stock volume > 500K daily average
  - Price hasn't moved >3% from planned entry

STEP 4 — Execute the buys:
  bash scripts/broker.sh order '{"symbol":"BBCA","qty":"500",
    "side":"buy","type":"market","time_in_force":"day"}'
  Wait for fill confirmation before placing the stop.

STEP 5 — Immediately place 10% trailing stop for each new position:
  bash scripts/broker.sh order '{"symbol":"BBCA","qty":"500",
    "side":"sell","type":"trailing_stop","trail_percent":"10",
    "time_in_force":"gtc"}'
  (Adapt order format to IDX broker's API)

STEP 6 — Append each trade to memory/TRADE-LOG.md:
  Date, ticker, side, shares, entry price, stop level, thesis, target, R:R.

STEP 7 — Notification: only if a trade was actually placed.

STEP 8 — COMMIT AND PUSH (mandatory):
  git add memory/TRADE-LOG.md
  git commit -m "market-open $DATE"
  git push origin main
  (Skip commit if no trades fired)
```

---

## Midday Workflow (11:30 WIB, lunch break)

```
STEP 1 — Read memory:
  - memory/TRADING-STRATEGY.md (the tail)
  - memory/TRADE-LOG.md (today's trades)
  - memory/RESEARCH-LOG.md (today's entry)

STEP 2 — Pull positions and open orders:
  bash scripts/broker.sh positions
  bash scripts/broker.sh orders

STEP 3 — For any position with unrealized P&L <= -7%:
  Close the position. Cancel its trailing stop order.
  Log the exit with realized P&L and the reason.

STEP 4 — For winners:
  - If up +20% or more: cancel old trailing stop, place new with trail 5%.
  - If up +15% or more: cancel old trailing stop, place new with trail 7%.
  - Respect the 3%-of-current-price guardrail.

STEP 5 — Thesis check: for each remaining position:
  - Review price action and any midday news
  - If a thesis broke intraday, cut the position even if not at -7% yet

STEP 6 — Optional: intraday research via WebSearch if something is
  moving sharply with no obvious cause.

STEP 7 — Notification: only if action was taken (sell, stop tightened, thesis exit).

STEP 8 — COMMIT AND PUSH:
  git add memory/TRADE-LOG.md
  (optional: git add memory/RESEARCH-LOG.md if addendum written)
  git commit -m "midday $DATE"
  git push origin main
  (Skip commit if nothing changed)
```

---

## Daily-Summary Workflow (15:15 WIB, after the close)

```
STEP 1 — Find yesterday's closing equity in the trade log
  (needed for day-over-day P&L math).

STEP 2 — Pull today's final account state:
  bash scripts/broker.sh account
  bash scripts/broker.sh positions
  bash scripts/broker.sh orders

STEP 3 — Compute:
  - Day P&L in IDR and percent
  - Phase-to-date cumulative P&L
  - Trades today
  - Running trade count for the week

STEP 4 — Append a dated EOD snapshot section to memory/TRADE-LOG.md:
  - Positions table with current prices, unrealized P&L, stop levels
  - Plain-language notes paragraph (what happened, thesis status)

STEP 5 — Send one notification message, always, even on no-trade days.
  Keep it under 15 lines:
  bash scripts/notify.sh "<daily summary message>"

STEP 6 — COMMIT AND PUSH (mandatory — this commit is critical):
  git add memory/TRADE-LOG.md
  git commit -m "daily-summary $DATE"
  git push origin main
  Tomorrow's daily-summary P&L calculation depends on this persisting.
```

---

## Weekly-Review Workflow (Friday 16:00 WIB)

```
STEP 1 — Read the full week:
  - memory/TRADE-LOG.md (all entries this week)
  - memory/RESEARCH-LOG.md (all entries this week)
  - memory/WEEKLY-REVIEW.md (existing template for format)
  - memory/TRADING-STRATEGY.md

STEP 2 — Pull Friday close account state:
  bash scripts/broker.sh account
  bash scripts/broker.sh positions

STEP 3 — Compute:
  - Starting portfolio (Monday open) → ending portfolio
  - Week return in IDR and percent
  - IHSG week return (via WebSearch)
  - W/L/open trade counts, win rate, best trade, worst trade, profit factor

STEP 4 — Append a full review section to memory/WEEKLY-REVIEW.md:
  - Stats table
  - Open positions at week end
  - 3-5 "what worked" bullets
  - 3-5 "what didn't work" bullets
  - Key lessons
  - Adjustments for next week
  - Overall letter grade A-F

STEP 5 — If a rule has proven itself for 2+ weeks or failed badly,
  also update memory/TRADING-STRATEGY.md in the same commit.
  Call out the change in the review.

STEP 6 — Send one notification message with headline numbers, always.

STEP 7 — COMMIT AND PUSH:
  git add memory/WEEKLY-REVIEW.md
  (git add memory/TRADING-STRATEGY.md if changed)
  git commit -m "weekly-review $DATE"
  git push origin main
```

---

## Ad-Hoc Commands

### `/portfolio`

Read-only snapshot. Calls account, positions, orders. Prints a clean summary. No state changes, no orders, no file writes. Only commentary allowed: flag if a position has no stop, or a stop is below current price.

### `/trade`

Manual trade helper. Takes SYMBOL SHARES SIDE as arguments. Runs the full buy-side gate from the strategy. Prints the order JSON and validation results. Asks for y/n confirmation before placing. On confirm, executes the buy and immediately places the 10% trailing stop. Logs to the trade log. Sends a notification. Refuses any trade that fails a rule check.

---

## Notification Philosophy

Most bots are chatty. This one is not.

- **Pre-market**: silent unless something is genuinely urgent
- **Market-open**: only if a trade was placed
- **Midday**: only if action was taken (a sell, a stop tightened, a thesis exit)
- **Daily-summary**: always sends, one message, under 15 lines
- **Weekly-review**: always sends, one message, headline numbers

The cost of a missed notification is low (you can always check the portfolio ad-hoc). The cost of a chatty bot is high (you stop reading the messages, and then you miss the one that mattered).

---

## Cloud Routine Setup (one-time)

### What happens on each scheduled run:
1. The cron fires in the timezone you set on the routine
2. Claude's cloud spins up a new container
3. It clones your GitHub repo at main, so it sees the latest memory
4. It injects the environment variables you configured
5. It starts Claude with the prompt you pasted into the routine
6. Claude does the work: reads memory, calls wrappers, writes memory
7. Claude **must run `git commit` and `git push origin main` before exiting.** Otherwise everything it did evaporates.
8. The container is destroyed

### Prerequisites (one-time):
1. **Install Claude GitHub App** on your repo (least privilege — only the trading repo)
2. **Enable "Allow unrestricted branch pushes"** in the routine's environment settings. Without this, `git push` silently fails. This is the #1 reason first-time setups break.
3. **Set environment variables on the routine** (NOT in a .env file)

### First-Run Troubleshooting

| Symptom | Cause | Fix |
|---------|-------|-----|
| "Repository not accessible" | Claude GitHub App not installed | Install it, grant access |
| `git push` fails with proxy error | "Allow unrestricted branch pushes" is off | Enable it |
| API key not set | Env var missing from routine env | Add it in routine config |
| Agent creates a .env file | Prompt was paraphrased | Re-paste prompt from routines/*.md exactly |
| Yesterday's trades missing | Previous run didn't commit+push | Check `git log origin/main` |
| Push fails "fetch first" | Another run pushed between clone and push | Prompt handles this with `git pull --rebase` |

---

## Deliverables

- [ ] Write all 5 routine prompts in `routines/` (adapted for IDX + WIB)
- [ ] Write all 7 slash commands in `.claude/commands/`
- [ ] Set up 5 cloud routines with correct cron schedules
- [ ] Configure env vars on each routine
- [ ] Enable unrestricted branch pushes
- [ ] Test: "Run now" on pre-market, verify research log committed
- [ ] Test: "Run now" on daily-summary, verify EOD snapshot committed
- [ ] Test: notification fires for daily-summary
- [ ] Verify 30-min spacing between all routines
