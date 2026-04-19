# Step 9 — Setup Verification (Pre-Flight Checklist)

> *Run this before the first routine fires Monday morning. Every check has a command, an expected result, and a pass/fail. No ambiguity.*

---

## How to Use This Checklist

Work through each section in order. Each check has:

- **Test:** the exact command or action to run
- **Expect:** what a passing result looks like
- **If it fails:** what to fix

Mark each check as you go: `[x]` = pass, `[ ]` = not yet run, `[!]` = failed (add notes).

**All checks must pass before the first pre-market routine fires at 07:00 WIB Monday, April 20.**

---

## Section 1: Strategy (Step 0)

Verifies that the trading rules are documented, complete, and internally consistent.

### 1.1 — TRADING-STRATEGY.md exists and is seeded

- [ ] **Test:** `cat memory/TRADING-STRATEGY.md | head -5`
- [ ] **Expect:** File exists, starts with `# Trading Strategy` or equivalent heading
- [ ] **If fails:** Seed from plan/00-STRATEGY-ON-PAPER.md

### 1.2 — Hard rules are present

- [ ] **Test:** `grep -c "Max.*position" memory/TRADING-STRATEGY.md`
- [ ] **Expect:** At least 3 matches (max positions, max per position, max trades/week)
- [ ] **If fails:** Copy the Hard Rules section from plan/00-STRATEGY-ON-PAPER.md

### 1.3 — Buy-side gate has all 9 checks

- [ ] **Test:** `grep -c "CHECK\|Gate\|gate\|position.*6\|trades.*3\|20%\|catalyst\|volume.*500\|lot.*100\|3%.*entry" memory/TRADING-STRATEGY.md`
- [ ] **Expect:** Multiple matches covering: positions <= 6, trades/week <= 3, 20% max, cash check, catalyst documented, stock not option, volume > 500K, lot size 100, no chasing > 3%
- [ ] **If fails:** Add the complete buy-side gate from plan/00-STRATEGY-ON-PAPER.md

### 1.4 — Sell-side rules are present

- [ ] **Test:** `grep -E "\-7%|trailing.stop|tighten" memory/TRADING-STRATEGY.md`
- [ ] **Expect:** Matches for -7% cut, 10% trailing stop, tighten rules (+15% -> 7%, +20% -> 5%)
- [ ] **If fails:** Add sell-side rules from plan/00-STRATEGY-ON-PAPER.md

### 1.5 — Sector playbook is present

- [ ] **Test:** `grep -c "BBCA\|ADRO\|TLKM\|Banking\|Coal\|Nickel" memory/TRADING-STRATEGY.md`
- [ ] **Expect:** >= 3 matches — the major IDX sectors and tickers are referenced
- [ ] **If fails:** Add the Sector Playbook table from plan/00-STRATEGY-ON-PAPER.md

---

## Section 2: Memory Architecture (Step 1)

Verifies that all 5 memory files exist with correct initial content.

### 2.1 — All 5 memory files exist

- [ ] **Test:** `ls memory/`
- [ ] **Expect:** Exactly these 5 files:
  ```
  TRADING-STRATEGY.md
  TRADE-LOG.md
  RESEARCH-LOG.md
  WEEKLY-REVIEW.md
  PROJECT-CONTEXT.md
  ```
- [ ] **If fails:** Create missing files. Templates are in plan/01-MEMORY-ARCHITECTURE.md and plan/08-PAPER-TRIAL.md

### 2.2 — TRADE-LOG.md has Day 0 baseline

- [ ] **Test:** `grep "300,000,000\|300000000" memory/TRADE-LOG.md`
- [ ] **Expect:** Day 0 EOD snapshot showing IDR 300,000,000 starting capital
- [ ] **If fails:** Seed with Day 0 template from plan/08-PAPER-TRIAL.md

### 2.3 — TRADE-LOG.md has Active Positions table header

- [ ] **Test:** `grep "Active Positions" memory/TRADE-LOG.md`
- [ ] **Expect:** Table header exists (even if empty — "No positions yet")
- [ ] **If fails:** Add the Active Positions table template

### 2.4 — RESEARCH-LOG.md is ready for appending

- [ ] **Test:** `cat memory/RESEARCH-LOG.md | head -3`
- [ ] **Expect:** File exists with a `# Research Log` heading. May be empty otherwise.
- [ ] **If fails:** Create with just `# Research Log` heading

### 2.5 — WEEKLY-REVIEW.md has template structure

- [ ] **Test:** `cat memory/WEEKLY-REVIEW.md | head -3`
- [ ] **Expect:** File exists with `# Weekly Reviews` heading
- [ ] **If fails:** Create with heading and template structure from plan/01-MEMORY-ARCHITECTURE.md

### 2.6 — PROJECT-CONTEXT.md has trial parameters

- [ ] **Test:** `grep -E "paper|300.*000.*000|April 20|May 2" memory/PROJECT-CONTEXT.md`
- [ ] **Expect:** Matches for paper mode, IDR 300M, trial dates
- [ ] **If fails:** Seed from plan/08-PAPER-TRIAL.md Day 0 section

### 2.7 — No stale files from old architecture

- [ ] **Test:** `ls memory/ | grep -v "TRADING-STRATEGY\|TRADE-LOG\|RESEARCH-LOG\|WEEKLY-REVIEW\|PROJECT-CONTEXT"`
- [ ] **Expect:** No output (no extra files). Legacy files like BENCHMARK-LOG.md or LESSONS-LEARNED.md should be removed or archived.
- [ ] **If fails:** Remove or archive stale files. The 5-file architecture is canonical.

---

## Section 3: Scaffold (Step 2)

Verifies the repo structure, CLAUDE.md, env, and .gitignore.

### 3.1 — CLAUDE.md exists and has correct trial scope

- [ ] **Test:** `grep -E "paper.*trial|Paper Mode|PAPER MODE" CLAUDE.md`
- [ ] **Expect:** Matches confirming paper-only mode, 2-week trial
- [ ] **If fails:** Update CLAUDE.md from the version in plan/02-SCAFFOLD.md

### 3.2 — CLAUDE.md references all 3 wrapper scripts

- [ ] **Test:** `grep -c "broker.sh\|market-data.sh\|notify.sh" CLAUDE.md`
- [ ] **Expect:** >= 3 (all three scripts are referenced)
- [ ] **If fails:** Add the API Wrappers section to CLAUDE.md

### 3.3 — CLAUDE.md has the scheduled routines table

- [ ] **Test:** `grep "07:00\|09:15\|11:30\|15:15\|16:00" CLAUDE.md`
- [ ] **Expect:** All 5 WIB times present in the routines table
- [ ] **If fails:** Add the routines table from plan/05-ROUTINES.md

### 3.4 — env.template exists (never .env committed)

- [ ] **Test:** `ls env.template && echo "OK"`
- [ ] **Expect:** `OK`
- [ ] **Test:** `git ls-files .env 2>/dev/null | wc -l`
- [ ] **Expect:** `0` (no .env tracked by git)
- [ ] **If fails:** Create env.template. If .env is tracked, remove it: `git rm --cached .env`

### 3.5 — .gitignore excludes .env

- [ ] **Test:** `grep "\.env" .gitignore`
- [ ] **Expect:** `.env` is listed
- [ ] **If fails:** `echo ".env" >> .gitignore`

### 3.6 — Directory structure is correct

- [ ] **Test:** `ls -d scripts/ memory/ routines/ dashboard/ plan/`
- [ ] **Expect:** All 5 directories exist
- [ ] **If fails:** `mkdir -p scripts memory routines dashboard plan`

---

## Section 4: Wrapper Scripts (Steps 2-3)

Verifies that all scripts exist, are executable, and respond to basic invocations.

### 4.1 — broker.sh exists and is executable

- [ ] **Test:** `test -x scripts/broker.sh && echo "OK"`
- [ ] **Expect:** `OK`
- [ ] **If fails:** `chmod +x scripts/broker.sh`

### 4.2 — broker.sh shows help

- [ ] **Test:** `bash scripts/broker.sh --help 2>&1 | head -5`
- [ ] **Expect:** Usage text mentioning `portfolio`, `quote`, `buy`, `sell`
- [ ] **If fails:** Script is broken — check for syntax errors

### 4.3 — broker.sh portfolio runs in paper mode

- [ ] **Test:** `TRADING_MODE=paper bash scripts/broker.sh portfolio`
- [ ] **Expect:** JSON or formatted output showing equity ~IDR 300M, cash, positions (may be empty)
- [ ] **If fails:** Debug the paper-mode portfolio reader

### 4.4 — broker.sh quote fetches a real price

- [ ] **Test:** `bash scripts/broker.sh quote BBCA`
- [ ] **Expect:** A price in IDR (e.g., 9000-10000 range), volume, lot size
- [ ] **If fails:** Check that yfinance/GoAPI is reachable and `BBCA.JK` resolves

### 4.5 — market-data.sh exists and is executable

- [ ] **Test:** `test -x scripts/market-data.sh && echo "OK"`
- [ ] **Expect:** `OK`
- [ ] **If fails:** `chmod +x scripts/market-data.sh`

### 4.6 — market-data.sh quote works (tiered fallback)

- [ ] **Test:** `bash scripts/market-data.sh quote BBCA`
- [ ] **Expect:** Price data returned. Log line shows which source was used (GoAPI or yfinance).
- [ ] **If fails:** Check GOAPI_API_KEY env var. If GoAPI fails, verify yfinance fallback works.

### 4.7 — market-data.sh index works

- [ ] **Test:** `bash scripts/market-data.sh index JKSE`
- [ ] **Expect:** IHSG level (should be in the 6000-8000 range as of April 2026)
- [ ] **If fails:** Check yfinance can resolve `^JKSE`

### 4.8 — market-data.sh history works

- [ ] **Test:** `bash scripts/market-data.sh history BBCA 5d`
- [ ] **Expect:** 5 rows of OHLCV data with dates, open, high, low, close, volume
- [ ] **If fails:** Check yfinance installation: `pip install yfinance`

### 4.9 — market-data.sh fundamentals works

- [ ] **Test:** `bash scripts/market-data.sh fundamentals BBCA`
- [ ] **Expect:** Financial ratios (P/E, P/B, etc.) or statement data
- [ ] **If fails:** Check SECTORS_API_KEY. If Sectors.app fails, verify yfinance fallback.

### 4.10 — notify.sh exists and is executable

- [ ] **Test:** `test -x scripts/notify.sh && echo "OK"`
- [ ] **Expect:** `OK`
- [ ] **If fails:** `chmod +x scripts/notify.sh`

### 4.11 — notify.sh sends a test message (or graceful fallback)

- [ ] **Test:** `bash scripts/notify.sh send "Setup verification test — $(date)"`
- [ ] **Expect:** Either "Sent via telegram" or "Notification logged (no channel configured)" — never a crash
- [ ] **If fails:** Check TELEGRAM_BOT_TOKEN and TELEGRAM_CHAT_ID. Script must exit 0 even without credentials.

### 4.12 — gate-check.sh exists and is executable

- [ ] **Test:** `test -x scripts/gate-check.sh && echo "OK"`
- [ ] **Expect:** `OK`
- [ ] **If fails:** `chmod +x scripts/gate-check.sh`

### 4.13 — gate-check.sh runs all 9 checks

- [ ] **Test:** `TRADING_MODE=paper bash scripts/gate-check.sh BBCA 500 2>&1`
- [ ] **Expect:** Output showing CHECK 1 through CHECK 9, each PASS or FAIL with a reason. Final line: "ALL 9 GATES PASSED" or "GATE FAILED: ..."
- [ ] **If fails:** Debug the gate logic. Cross-reference with plan/03-GUARDRAILS.md

### 4.14 — Python helper exists

- [ ] **Test:** `python3 scripts/yfinance_helper.py --test 2>&1 | head -3`
- [ ] **Expect:** No import errors. Prints a test quote or version info.
- [ ] **If fails:** `pip install yfinance --break-system-packages`

---

## Section 5: Guardrails (Step 3)

Verifies that both layers of guardrails are active and correct.

### 5.1 — Paper mode is the default everywhere

- [ ] **Test:** `grep "paper" env.template CLAUDE.md memory/TRADING-STRATEGY.md | wc -l`
- [ ] **Expect:** >= 3 (paper mode referenced in env, CLAUDE.md, and strategy)
- [ ] **If fails:** Ensure all three files default to paper mode

### 5.2 — broker.sh enforces paper mode

- [ ] **Test:** `TRADING_MODE=paper bash scripts/broker.sh buy BBCA 500 2>&1 | grep -i "paper\|simulated"`
- [ ] **Expect:** Output confirming trade is simulated, not sent to exchange
- [ ] **If fails:** Check the paper-mode branch in broker.sh

### 5.3 — gate-check blocks oversized position

- [ ] **Test:** Simulate buying 100% of equity in one position — the gate should reject
- [ ] **Expect:** "CHECK 3: FAIL" — position exceeds 20% of equity
- [ ] **If fails:** Fix gate check #3 logic

### 5.4 — Loss limits are documented

- [ ] **Test:** `grep -E "\-2%|\-5%|\-15%" memory/TRADING-STRATEGY.md`
- [ ] **Expect:** All three loss caps present: -2% daily, -5% weekly, -15% max drawdown
- [ ] **If fails:** Add loss limits to TRADING-STRATEGY.md

---

## Section 6: Skills (Step 4)

Verifies that the skill definitions exist and reference correct files.

### 6.1 — Skills directory exists

- [ ] **Test:** `ls skills/`
- [ ] **Expect:** 6 skill files: research.md, macro.md, screen.md, trade-plan.md, position-review.md, journal.md
- [ ] **If fails:** Create from plan/04-SKILLS.md

### 6.2 — Skills reference correct memory paths

- [ ] **Test:** `grep -r "state/\|research/companies\|journals/" skills/ | wc -l`
- [ ] **Expect:** `0` — no references to old file paths. All should reference `memory/` files.
- [ ] **If fails:** Update skill files to use memory/ paths

### 6.3 — Research skill uses correct tools

- [ ] **Test:** `grep -E "yfinance|WebSearch|web.search|GoAPI|Sectors" skills/research.md`
- [ ] **Expect:** References to yfinance and/or WebSearch (no Perplexity, no external AI)
- [ ] **If fails:** Update to use Claude web search + yfinance + market-data.sh

---

## Section 7: Routines (Step 5)

Verifies that all 5 routine prompts and slash commands exist and are properly structured.

### 7.1 — All 5 routine prompts exist

- [ ] **Test:** `ls routines/pre-market.md routines/market-open.md routines/midday.md routines/daily-summary.md routines/weekly-review.md`
- [ ] **Expect:** All 5 files listed, no errors
- [ ] **If fails:** Create from plan/05-ROUTINES.md

### 7.2 — Each routine has the environment check

- [ ] **Test:** `grep -l "ENVIRONMENT\|env.*var\|MISSING.*halt" routines/*.md | wc -l`
- [ ] **Expect:** `5` — every routine starts with an env var verification step
- [ ] **If fails:** Add the environment check section from plan/02-SCAFFOLD.md prompt template

### 7.3 — Each routine has the persistence step

- [ ] **Test:** `grep -l "git.*push\|COMMIT AND PUSH\|commit.*push" routines/*.md | wc -l`
- [ ] **Expect:** `5` — every routine ends with git add, commit, push
- [ ] **If fails:** Add the mandatory persistence step

### 7.4 — Routine cron schedules don't overlap

- [ ] **Verify manually:**
  ```
  Pre-market:    0 0 * * 1-5   → 07:00 WIB
  Market-open:  15 2 * * 1-5   → 09:15 WIB (135 min gap)
  Midday:       30 4 * * 1-5   → 11:30 WIB (135 min gap)
  Daily-summary:15 8 * * 1-5   → 15:15 WIB (225 min gap)
  Weekly-review: 0 9 * * 5     → 16:00 WIB Fri (45 min gap)
  ```
- [ ] **Expect:** All gaps >= 30 minutes. No two routines fire simultaneously.

### 7.5 — Pre-market routine reads correct files

- [ ] **Test:** `grep -E "TRADING-STRATEGY|TRADE-LOG|RESEARCH-LOG" routines/pre-market.md`
- [ ] **Expect:** Reads all three files at the start
- [ ] **If fails:** Fix Step 1 of pre-market routine

### 7.6 — Daily-summary updates dashboard data

- [ ] **Test:** `grep -E "data\.json\|dashboard" routines/daily-summary.md`
- [ ] **Expect:** Routine writes to `dashboard/data.json` at EOD
- [ ] **If fails:** Add dashboard update step to daily-summary routine

### 7.7 — Slash commands exist (if using local mode)

- [ ] **Test:** `ls .claude/commands/ 2>/dev/null | wc -l`
- [ ] **Expect:** >= 2 command files (at minimum: portfolio.md, trade.md)
- [ ] **If fails:** Create from plan/05-ROUTINES.md ad-hoc commands section. Optional for cloud-only setup.

---

## Section 8: Execution & Model Config (Step 7)

Verifies the model assignments and parallel execution plan.

### 8.1 — Routine model assignments are documented

- [ ] **Test:** `grep -E "Opus|Sonnet|Haiku" plan/07-EXECUTION-PLAN.md | head -10`
- [ ] **Expect:** Model assignments for all 5 routines
- [ ] **If fails:** See plan/07-EXECUTION-PLAN.md

### 8.2 — Cloud routines are configured with correct models

- [ ] **Verify in Claude Cloud Routines UI:**
  - Pre-market: claude-opus
  - Market-open: claude-sonnet (or claude-opus for first 2 weeks)
  - Midday: claude-sonnet (or claude-opus for first 2 weeks)
  - Daily-summary: claude-haiku (or claude-sonnet for first 2 weeks)
  - Weekly-review: claude-opus
- [ ] **If fails:** Update the model in each routine's cloud configuration

### 8.3 — Environment variables set on cloud routines

- [ ] **Verify each cloud routine has these env vars:**
  ```
  GOAPI_API_KEY=<your key>
  SECTORS_API_KEY=<your key>
  TRADING_MODE=paper
  TELEGRAM_BOT_TOKEN=<your token>  (optional)
  TELEGRAM_CHAT_ID=<your chat id>  (optional)
  ```
- [ ] **If fails:** Add missing env vars in the routine config UI

### 8.4 — GitHub App installed on repo

- [ ] **Test:** Check repo Settings > Integrations > Claude GitHub App
- [ ] **Expect:** Claude GitHub App has access to this repo
- [ ] **If fails:** Install from Claude Cloud Routines setup flow

### 8.5 — Unrestricted branch pushes enabled

- [ ] **Test:** In the cloud routine environment settings, check "Allow unrestricted branch pushes"
- [ ] **Expect:** Enabled
- [ ] **If fails:** Enable it. Without this, `git push` silently fails — the #1 setup issue.

---

## Section 9: Paper Trial (Step 8)

Verifies the 2-week trial is configured correctly.

### 9.1 — Trial dates are correct everywhere

- [ ] **Test:** `grep -r "April 20\|Apr 20\|2026-04-20" CLAUDE.md memory/PROJECT-CONTEXT.md dashboard/data.json`
- [ ] **Expect:** Start date April 20, 2026 in all three files
- [ ] **If fails:** Update to correct trial start date

### 9.2 — Starting capital is consistent

- [ ] **Test:** `grep -r "300.000.000\|300000000\|300,000,000" CLAUDE.md memory/ dashboard/data.json`
- [ ] **Expect:** IDR 300,000,000 in CLAUDE.md, TRADE-LOG.md (Day 0 snapshot), PROJECT-CONTEXT.md, and data.json
- [ ] **If fails:** Correct the mismatched values

### 9.3 — TRADE-LOG.md Day 0 snapshot has IHSG baseline

- [ ] **Test:** `grep -E "IHSG|JKSE" memory/TRADE-LOG.md`
- [ ] **Expect:** IHSG closing level recorded for Day 0 (needed to compute alpha from Day 1)
- [ ] **If fails:** Fetch today's IHSG close: `bash scripts/market-data.sh index JKSE` and add to Day 0

### 9.4 — No live trading possible

- [ ] **Test:** `grep -c "live" env.template CLAUDE.md memory/TRADING-STRATEGY.md`
- [ ] **Expect:** "live" appears only in context of "paper mode is default" or "no live mode" — never as the active setting
- [ ] **If fails:** Ensure TRADING_MODE=paper is hardcoded for the trial

---

## Section 10: Dashboard

Verifies the dashboard renders correctly and is connected to data.

### 10.1 — Dashboard files exist

- [ ] **Test:** `ls dashboard/index.html dashboard/data.json`
- [ ] **Expect:** Both files exist
- [ ] **If fails:** Create from plan/08-PAPER-TRIAL.md templates

### 10.2 — data.json has valid structure

- [ ] **Test:** `python3 -c "import json; d=json.load(open('dashboard/data.json')); print('trial:', d['trial']['start_date']); print('daily entries:', len(d['daily'])); print('stats keys:', list(d['stats'].keys())[:5])"`
- [ ] **Expect:**
  ```
  trial: 2026-04-20
  daily entries: 1
  stats keys: ['total_return_pct', 'ihsg_return_pct', 'alpha_pct', 'win_rate_pct', 'total_trades']
  ```
- [ ] **If fails:** Fix data.json structure. Reference plan/08-PAPER-TRIAL.md for the schema.

### 10.3 — data.json Day 0 values are correct

- [ ] **Test:** `python3 -c "import json; d=json.load(open('dashboard/data.json')); e=d['daily'][0]; print('date:', e['date']); print('value:', e['portfolio_value']); print('positions:', e['positions_count'])"`
- [ ] **Expect:**
  ```
  date: 2026-04-19
  value: 300000000
  positions: 0
  ```
- [ ] **If fails:** Reset data.json to Day 0 seed values

### 10.4 — Dashboard opens in browser

- [ ] **Test:** Open `dashboard/index.html` in a browser
- [ ] **Expect:** Dark-themed dashboard showing:
  - Header: "IDX Paper Trading Dashboard"
  - Status badge: "Starts Tomorrow" (or "Live Paper Trading" after Apr 20)
  - Scorecard: Portfolio IDR 300M, 0.00% return, 0 positions
  - Charts: placeholder text "Equity curve will appear after Day 1"
  - Empty positions table: "No open positions"
  - Empty trades table: "No trades yet"
  - Rules compliance: green dot, "zero violations"
- [ ] **If fails:** Check browser console for JavaScript errors

### 10.5 — Dashboard reads data.json correctly

- [ ] **Test:** Modify data.json temporarily — change `portfolio_value` to 305000000, reload dashboard
- [ ] **Expect:** Portfolio Value card updates to "IDR 305,000,000"
- [ ] **Restore:** Revert data.json to 300000000
- [ ] **If fails:** Check that `fetch('./data.json')` is resolving (may need to serve via localhost if CORS blocks file:// access)

### 10.6 — Dashboard handles multi-day data

- [ ] **Test:** Add a second entry to the `daily` array in data.json:
  ```json
  {
    "date": "2026-04-20", "day_label": "Day 1",
    "portfolio_value": 302500000, "cash": 270000000,
    "daily_pnl_idr": 2500000, "daily_pnl_pct": 0.83,
    "cumulative_pnl_pct": 0.83,
    "ihsg_close": 6570, "ihsg_cumulative_pct": 0.31,
    "alpha_pct": 0.52,
    "positions_count": 2, "trades_today": 2, "trades_this_week": 2
  }
  ```
  Reload dashboard.
- [ ] **Expect:** Equity curve chart renders with 2 data points. Daily P&L bar chart shows one green bar. Scorecard updates.
- [ ] **Restore:** Remove the test entry, revert to Day 0 only.
- [ ] **If fails:** Debug the chart rendering in index.html

---

## Section 11: End-to-End Smoke Tests

Run these after all individual checks pass. These simulate real routine behavior.

### 11.1 — Simulate pre-market run

- [ ] **Test:** Invoke the pre-market workflow manually (either `/pre-market` in Claude Code, or "Run now" on the cloud routine)
- [ ] **Expect:**
  1. Agent reads TRADING-STRATEGY.md, TRADE-LOG.md, RESEARCH-LOG.md
  2. Agent runs web searches for IHSG, coal, IDR/USD, IDX catalysts
  3. Agent appends a dated entry to RESEARCH-LOG.md
  4. Agent commits and pushes: `git log -1` shows "pre-market 2026-04-20" or similar
- [ ] **If fails:** Check routine prompt, env vars, and git push permissions

### 11.2 — Simulate daily-summary run (most critical)

- [ ] **Test:** Invoke the daily-summary workflow manually
- [ ] **Expect:**
  1. Agent reads TRADE-LOG.md for previous close
  2. Agent calls `scripts/broker.sh portfolio` for current state
  3. Agent computes day P&L and cumulative P&L
  4. Agent appends EOD snapshot to TRADE-LOG.md
  5. Agent writes updated `dashboard/data.json`
  6. Agent sends notification (or logs if no channel configured)
  7. Agent commits and pushes: `git log -1` shows "daily-summary 2026-04-20"
- [ ] **If fails:** This is the most important routine — fix it before anything else

### 11.3 — Verify git push works from cloud

- [ ] **Test:** "Run now" on any cloud routine. Then check: `git log origin/main -1`
- [ ] **Expect:** A new commit from the cloud run, pushed to main
- [ ] **If fails:** Check: (1) Claude GitHub App installed, (2) unrestricted branch pushes enabled, (3) routine has correct env vars

### 11.4 — Verify dashboard updates after EOD

- [ ] **Test:** After the daily-summary smoke test, open `dashboard/index.html`
- [ ] **Expect:** Dashboard shows updated data from the EOD snapshot (not still showing Day 0 zeros)
- [ ] **If fails:** Check that daily-summary routine writes to `dashboard/data.json` and commits it

---

## Section 12: Final Readiness

### 12.1 — Summary scorecard

Fill this in after running all checks:

| Section | Checks | Passed | Status |
|---------|--------|--------|--------|
| 1. Strategy | 5 | /5 | |
| 2. Memory Architecture | 7 | /7 | |
| 3. Scaffold | 6 | /6 | |
| 4. Wrapper Scripts | 14 | /14 | |
| 5. Guardrails | 4 | /4 | |
| 6. Skills | 3 | /3 | |
| 7. Routines | 7 | /7 | |
| 8. Execution & Model Config | 5 | /5 | |
| 9. Paper Trial | 4 | /4 | |
| 10. Dashboard | 6 | /6 | |
| 11. End-to-End | 4 | /4 | |
| **Total** | **65** | **/65** | |

### 12.2 — Launch readiness

- [ ] All 65 checks pass
- [ ] Git repo is clean: `git status` shows nothing uncommitted
- [ ] Cloud routines are scheduled and active
- [ ] data.json has Day 0 baseline with correct IHSG close
- [ ] TRADE-LOG.md has Day 0 baseline with IDR 300M
- [ ] First routine (pre-market) fires at 07:00 WIB Monday, April 20
- [ ] Michael has dashboard URL bookmarked

### 12.3 — First-day monitoring plan

Monday, April 20 — watch these:

| Time (WIB) | Routine | What to Check |
|------------|---------|---------------|
| 07:00 | Pre-market | `git log -1` — new commit? RESEARCH-LOG.md has today's entry? |
| 09:15 | Market-open | Any trades placed? TRADE-LOG.md updated? |
| 11:30 | Midday | Position checks logged? Any stops tightened? |
| 15:15 | Daily-summary | EOD snapshot in TRADE-LOG.md? data.json updated? Notification received? |
| 15:30 | Dashboard | Open dashboard — does it show Day 1 data? |

If pre-market fails, stop and fix before market-open fires. The cascade depends on it.

---

## Quick-Fix Reference

| Problem | Most Likely Cause | Fix |
|---------|-------------------|-----|
| Script not found | Not executable | `chmod +x scripts/*.sh` |
| yfinance import error | Not installed | `pip install yfinance --break-system-packages` |
| GoAPI returns nothing | Missing API key | Set `GOAPI_API_KEY` env var |
| git push fails | Unrestricted pushes off | Enable in cloud routine settings |
| No notification | Missing Telegram tokens | Set `TELEGRAM_BOT_TOKEN` + `TELEGRAM_CHAT_ID` |
| Dashboard blank | data.json malformed | Validate: `python3 -m json.tool dashboard/data.json` |
| "Repository not accessible" | GitHub App missing | Install Claude GitHub App on repo |
| Agent creates .env file | Prompt was paraphrased | Re-paste exact prompt from routines/*.md |
| Pre-market has no research | WebSearch blocked or down | Check network access; routine will retry next day |
| TRADE-LOG.md empty | Day 0 not seeded | Seed with Day 0 template from plan/08-PAPER-TRIAL.md |
