# Memory Architecture — Files Are the Brain

> *The trading strategy is a piece of the work. The memory architecture is HUGE.*

---

## The Mental Model

Every time a routine fires at 7am, the agent wakes up knowing **nothing**. It's stateless. No memory of yesterday's trades, last week's lessons, or the thesis it wrote 3 hours ago.

So how do you make a stateless agent act disciplined, remember its rules, and learn over time?

**Files. The files ARE the brain.**

Every routine reads files to load its memory, does its one job, then writes files to save what it learned. The next routine picks up by reading those same files.

Get this right and the agent compounds knowledge for months. Get it wrong and it forgets everything every morning.

---

## The Hidden Lesson

The files aren't just memory. They're the agent's **personality and discipline**.

A human trader has gut feel. Scar tissue. Emotional memory of getting burned.

The agent has none of that. The WEEKLY-REVIEW.md line that says "18 trades = worst week, 0 trades next week" is what stops the next routine from overtrading. The TRADING-STRATEGY.md line that says "NO OPTIONS" is the scar tissue from a real loss.

**Lose the files, lose the lessons. The files ARE the edge.**

This is why the memory architecture matters more than the trading logic. The strategies can be rewritten in a day. The accumulated lessons in WEEKLY-REVIEW.md took months of real market experience to earn.

---

## Five Files. That's the Brain.

The guide's actual implementation uses five core memory files, plus CLAUDE.md for identity. Simple beats complex.

```
idx-trader/
├── CLAUDE.md                    # Identity + hard rules (read-only for the agent)
├── memory/
│   ├── TRADING-STRATEGY.md      # The strategy. Read every session. Updated only by /weekly-review.
│   ├── RESEARCH-LOG.md          # Running research log. Appended daily by /pre-market.
│   ├── TRADE-LOG.md             # Every trade + EOD snapshots. The source of truth.
│   ├── WEEKLY-REVIEW.md         # Weekly reflections + lessons. Grows forever.
│   └── PROJECT-CONTEXT.md       # Static background, mission, platform info. Rarely updated.
└── .env                         # Secrets (broker keys, mode toggle)
```

### Why Five Files, Not Twenty

The original plan had `state/portfolio.json`, `state/open-theses.md`, `state/market-regime.md`, `state/risk-status.json`, `research/companies/BBCA.md`, `journals/2026-04-20/morning-plan.md`, etc. — a 20-file directory tree.

The guide's approach is radically simpler:

- **TRADE-LOG.md** replaces: portfolio.json, open-theses.md, risk-status.json, and all journal entries. It's a single running log of everything that happened — trades, EOD snapshots, P&L, stops.
- **RESEARCH-LOG.md** replaces: all company research files, screens, and morning plans. Each day's research is appended as one entry.
- **WEEKLY-REVIEW.md** replaces: lessons-learned.md and weekly journal files. Each week's review is appended.
- **TRADING-STRATEGY.md** replaces: strategy.md, risk-rules.md, and sector-playbook.md. One file for the complete strategy.
- **PROJECT-CONTEXT.md** replaces: mission.md, platform-specs.md, and onboarding docs. Static background: broker name, market (IDX), timezone (WIB), account size, mission statement. Rarely updated.

Fewer files = less context budget wasted on file management, less risk of corruption, simpler read/write contracts.

---

## What Each File Contains

### TRADING-STRATEGY.md

```markdown
# Trading Strategy — IDX Fundamental Swing

## Goal
Beat IHSG through fundamental analysis of IDX-listed companies.

## What Triggers a BUY
1. Undervalued (P/E below sector, growth above)
2. Clear catalyst (earnings, BI rate, commodity tailwind)
3. Macro alignment (IDR trend, sector rotation)
4. Technical confirmation (not in freefall)
5. Can write thesis in 2 sentences

## What Triggers a SELL
1. Thesis invalidated
2. Price target reached
3. Better opportunity (rotate capital)
4. Stop loss hit (-10% from entry)
5. Time stop (30 days, no catalyst)

## Risk Rules
- Paper mode default
- Max 5% per position
- Max 3 trades per week
- Daily loss cap: -2% → stop trading
- Cut losers at -7% midday
- 10% trailing stops on winners
- No options, no leverage, long only

## Sector Playbook
{sector views — updated by /weekly-review}

## Watchlist
{current tickers to track}
```

This file is read by every routine. It's the source of truth for how the agent should behave. Only `/weekly-review` can modify it, and only with clear justification in WEEKLY-REVIEW.md.

### RESEARCH-LOG.md

```markdown
# Research Log

## 2026-04-21 (Monday)

### Macro
- IHSG prev close: 7,245 (+0.3%)
- IDR/USD: 15,890 (stable)
- Coal: $132/ton (Newcastle)
- Foreign flow: net buy IDR 420B

### Screen Results
- ADRO: P/E 4.2x, coal tailwind, dividend 12%. Worth deep dive.
- BBNI: NIM expanding, BI may cut. Already in portfolio.
- CTRA: Pre-sales up 15% QoQ. Property sector turning.

### Deep Dive: ADRO
{Full Investment Checklist analysis}
Conviction: HIGH
Thesis: "ADRO at 4x earnings with coal above $130. 12% yield provides floor."

### Trade Ideas
- BUY ADRO at IDR 2,850 (5% position, stop at IDR 2,650)

---

## 2026-04-22 (Tuesday)

### Macro
...
```

Appended daily. Each entry is dated. The `/market-open` routine reads today's entry to decide what to execute.

### PROJECT-CONTEXT.md

```markdown
# Project Context — IDX Trading Agent

## Mission
Beat IHSG through fundamental analysis of IDX-listed companies, targeting alpha in a paper account. Moderate risk, patient execution, discipline > activity.

## Platform Details
- **Broker**: IDX (Interactive Brokers or local Indonesian broker — TBD)
- **Market**: Indonesian Stock Exchange (IDX)
- **Timezone**: WIB (Western Indonesia Time, UTC+7)
- **Account Size**: IDR 300M (paper)
- **Account Type**: Paper / live (toggle via TRADING_MODE env var)

## Core Rules (Hard Limits)
- Stocks only — no options, no leverage, long only
- Max 5-6 open positions
- Max 20% per position
- Max 3 trades per week
- 10% trailing stop on every position
- Cut losers at -7%
- Patience > activity

## Watchlist
[Sector rotation priorities and current watchlist tickers]

## External References
- IHSG index: https://www.idx.co.id
- Market data: Use broker API + WebSearch
- News: IDX news wires, analyst reports
```

Seeded once at project launch. Rarely changes. Read by `/pre-market` and `/weekly-review` for context.

### TRADE-LOG.md

```markdown
# Trade Log

## Active Positions
| Ticker | Entry Date | Entry Price | Shares | Stop | Thesis |
|--------|-----------|-------------|--------|------|--------|
| BBCA | 2026-04-15 | 9,200 | 500 | 8,280 | NIM expansion + BI rate cut cycle |
| ADRO | 2026-04-21 | 2,850 | 5,000 | 2,650 | Coal supercycle, 12% dividend yield |

## Trade History

### 2026-04-21 09:22 WIB — BUY ADRO
- 5,000 shares at IDR 2,850
- Position: IDR 14.25M (4.8% of portfolio)
- Stop: IDR 2,650 (-7%)
- Trailing stop: 10% from high
- Thesis: Coal above $130/ton, 4x earnings, 12% dividend yield

### 2026-04-21 11:35 WIB — Midday check
- BBCA: +1.2%, thesis intact, stop untouched
- ADRO: +0.4%, thesis intact, no action

---

## EOD Snapshots

### 2026-04-21 EOD
- Portfolio: IDR 298.5M (+1.1%)
- IHSG: +0.3%
- Alpha: +0.8%
- Cash: IDR 135M (45%)
- Trades this week: 1/3
- Daily P&L: +IDR 3.2M
- Positions: BBCA (+3.8% unrealized), ADRO (+0.4% unrealized)
```

This is the single source of truth for everything trade-related. Active positions table at the top (always current), trade history below (append-only), EOD snapshots at the bottom (append-only).

### WEEKLY-REVIEW.md

```markdown
# Weekly Reviews

## Week 17 (2026-04-21 to 2026-04-25)

### Performance
- Weekly P&L: +2.3% (IDR 6.8M)
- IHSG: +0.9%
- Weekly alpha: +1.4%
- Trades: 2/3 used
- Win rate: 2/2 (100%)

### What Worked
- ADRO thesis correct — coal held above $130, stock rallied 4% in 3 days
- Waited for entry instead of chasing Monday morning gap

### What Didn't Work
- Missed CTRA entry — hesitated on property thesis, stock ran 6%

### Lessons
- Coal stocks: the commodity price IS the thesis. Don't overcomplicate.
- Property: when pre-sales data is good, act faster. The data IS the catalyst.
- Patience on ADRO entry was right — buying at plan price, not chasing.

### Strategy Update
- No changes to TRADING-STRATEGY.md this week
- Added CTRA to watchlist for next week

---

## Week 18 (2026-04-28 to 2026-05-02)
...
```

---

## Git as Memory (Why It Works)

All five memory files are committed to `main`. Between runs, they are the ONLY persistent state. Git provides:

- **Versioning**: every change is diffs in `git log`. Roll back any run with `git revert <commit>`.
- **Audit trail**: `git log` shows who wrote what, when. No guessing what changed.
- **Merge safety**: append-only dated sections prevent merge conflicts. Routines are spaced 30+ minutes apart — no race conditions.
- **Rollback**: `git revert` recovers from bad trades or stuck state. Free undo.

The scaffolding prompt enforces this: every routine ends with a mandatory `git add`, `git commit`, and `git push origin main`. If you push fails due to divergence (another run pushed between clone and pull), the prompt automatically rebases and retries. Never force-push.

---

## Where Git as Memory Would Break

Be aware of these edge cases:

1. **Two routines scheduled seconds apart** — Race conditions on file writes. Don't do this. Maintain 30+ minute gaps minimum.
2. **Manual edits during a scheduled run** — You edit TRADE-LOG.md while `/midday` is writing to it. The push fails, or you overwrite the agent's work. Solution: never edit memory files while a routine is running. Check the routine's status first.
3. **Partial mid-run failure** — Broker accepts an order and fills it, but the agent crashes before appending to TRADE-LOG.md. Result: a phantom position with no log entry. Mitigation: the next run reads live positions from the broker, compares them to TRADE-LOG.md, and reconciles. The daily-summary routine always does this check.

---

## Read/Write Contract Summary

```
/pre-market    READS: TRADING-STRATEGY.md, tail of TRADE-LOG.md,
               tail of RESEARCH-LOG.md, PROJECT-CONTEXT.md (for context)
               WRITES: RESEARCH-LOG.md (append today's entry)

/market-open   READS: TRADING-STRATEGY.md, today's entry in RESEARCH-LOG.md,
               tail of TRADE-LOG.md
               WRITES: TRADE-LOG.md (append trades)
               FALLBACK: runs pre-market steps inline if today's research missing

/midday        READS: TRADING-STRATEGY.md (tail), TRADE-LOG.md (today),
               RESEARCH-LOG.md (today)
               WRITES: TRADE-LOG.md (append actions)
               OPTIONAL: RESEARCH-LOG.md addendum

/daily-summary READS: TRADE-LOG.md (yesterday's close for delta calculation),
               live account state from broker
               WRITES: TRADE-LOG.md (append EOD snapshot)

/weekly-review READS: TRADE-LOG.md (full week), RESEARCH-LOG.md (full week),
               WEEKLY-REVIEW.md (existing template), TRADING-STRATEGY.md
               WRITES: WEEKLY-REVIEW.md (append this week's review)
               OPTIONAL: TRADING-STRATEGY.md (if rule proved/failed over 2+ weeks)
```

---

## Context Budget

The agent has a finite context window. With five files, budget management is straightforward:

| File | What to Load | Tokens (est.) | Change Frequency |
|------|-------------|---|---|
| CLAUDE.md | Always, full file | ~500 | Never (read-only) |
| TRADING-STRATEGY.md | Always, full file | ~1,000 | Weekly only |
| PROJECT-CONTEXT.md | Full file (small, rarely changes) | ~500 | Rarely |
| TRADE-LOG.md | Tail (last 5 days + active positions table) | ~2,000 | Daily |
| RESEARCH-LOG.md | Today's entry (or last 2 days for /market-open) | ~1,500 | Daily |
| WEEKLY-REVIEW.md | Last 2 reviews (for /weekly-review context) | ~1,000 | Weekly |

**Total: ~6,500 tokens of file context.** Leaves plenty of room for reasoning.

For `/weekly-review`, load more of TRADE-LOG.md (full week) and RESEARCH-LOG.md (full week) — this session uses more context but runs only once per week.

---

## File Growth Management

Over months, TRADE-LOG.md and RESEARCH-LOG.md will grow large. Strategy:

- **TRADE-LOG.md**: Keep the active positions table at the top (always current). Archive entries older than 90 days to `archive/trade-log-{quarter}.md`. Routines only read the tail.
- **RESEARCH-LOG.md**: Archive entries older than 30 days. Recent research is most relevant.
- **WEEKLY-REVIEW.md**: Never archive. This is long-term memory. But routines only read the last 2-4 entries. The rest is there for manual review.
- **TRADING-STRATEGY.md**: Stays small. Only updated by weekly review.

---

## Deliverables

- [ ] Create the 5 core memory files with initial templates in memory/
- [ ] Write CLAUDE.md with agent identity + hard rules
- [ ] Seed PROJECT-CONTEXT.md with static background: broker, market, timezone, account size, mission
- [ ] Implement the read/write contract for each routine
- [ ] Implement context loader that reads the right tail/section of each file
- [ ] Implement append logic (new entries go at the bottom, dated)
- [ ] Implement the "active positions" table updater in TRADE-LOG.md
- [ ] Test: simulate a full day — verify each routine reads and writes correctly
- [ ] Test: /market-open falls back gracefully when RESEARCH-LOG.md has no today entry
- [ ] Test: TRADE-LOG.md stays readable after 30 days of appends
- [ ] Test: PROJECT-CONTEXT.md loads without error on /pre-market and /weekly-review
