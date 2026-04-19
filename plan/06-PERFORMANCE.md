# Performance, Journaling & Compounding Knowledge

> *The agent gets smarter every week — but only if it writes down what it learned.*

---

## The Learning Loop

```
Week 1:  Agent trades on TRADING-STRATEGY.md alone
         → Journals every day in TRADE-LOG.md → Saturday: writes WEEKLY-REVIEW.md

Week 2:  Agent reads WEEKLY-REVIEW.md Monday morning
         → Avoids last week's mistakes → Adds more lessons Saturday

Month 2: WEEKLY-REVIEW.md has 8 weeks of wisdom
         → Agent is measurably better than Week 1

Month 6: Agent has deep institutional knowledge about IDX
         → Sector patterns, seasonal effects, macro sensitivities
         → No retail trader maintains this level of documentation
```

This only works if journal quality is high and lessons are specific + actionable.

---

## Journal Quality Standards

Every journal entry must be:

1. **Specific** — "BBCA RSI dropped to 28 on high volume after BI held rates" not "BBCA looks oversold"
2. **Data-backed** — every claim cites a number
3. **Thesis-driven** — every trade has a clear thesis that can be proven wrong
4. **Honest** — acknowledge mistakes, don't rationalize losers
5. **Actionable** — write lessons as "do X" not "be more careful"

---

## WEEKLY-REVIEW.md — The Long-Term Memory

This file grows every week. It's read every morning. It's the agent's most valuable asset.

Each weekly review contains:

```markdown
# Trading Review — Week of 2026-04-21

## Stats Table
| Metric | Value |
|--------|-------|
| Starting Portfolio | IDR 150,000,000 |
| Ending Portfolio | IDR 153,200,000 |
| Week Return | IDR +3,200,000 / +2.13% |
| IHSG Week Return | +0.87% |
| Alpha | +1.26% |
| Trades Taken | 2 |
| Win Rate | 66.7% (2W, 1L) |
| Best Trade | ADRO: +1.8% |
| Worst Trade | TLKM: -1.2% |
| Profit Factor | 2.40 |

## Open Positions (as of week end)
| Ticker | Qty | Entry | Current | Unrealized P&L | Stop Loss |
|--------|-----|-------|---------|-----------------|-----------|
| ADRO | 500 | 13,200 | 13,438 | +119,000 | 12,900 |
| BBCA | 300 | 15,800 | 15,650 | -45,000 | 15,200 |

## What Worked
- ADRO thesis validated: coal price spike was real catalyst, not emotion
- RSI oversold on BBCA after foreign outflows: good entry signal
- Using IHSG correlation as macro filter: helped avoid false breakouts

## What Didn't Work
- TLKM "cheap valuation" trade: thesis was too vague. Learned: need catalyst
- Over-trading on Monday: hit 3-trade limit by Tuesday, had to sit out Thu/Fri
- Not reading research log before trading: missed key crude price news

## Key Lessons
- Commodity thesis must be specific: commodity price IS the thesis for commodity stocks
- Valuation alone is not a thesis. Need a specific trigger (catalyst)
- Banking sector is correlated with foreign flows; diversifying within banking doesn't reduce concentration risk

## Adjustments for Next Week
- Before any entry: read that day's RESEARCH-LOG.md entry
- For commodity stocks: check commodity price trend first, then check thesis
- Limit to 2 trades per day to avoid impulsive entries
- If thesis intact and stop not hit, hold through 5% dips

## Grade
A- (strong week, but over-traded early)
```

**Rules:**
- Be specific (name tickers, dates, prices)
- Be actionable (future-agent can apply this)
- Never delete old reviews
- If a lesson was wrong, add a correction in the next week's review
- TRADING-STRATEGY.md is only updated if a rule has proven itself for 2+ weeks or failed badly

---

## Performance Metrics

### Benchmarking

- **Primary:** IHSG (Jakarta Composite, `^JKSE`) — the agent's weekly return vs IHSG
- **Secondary:** LQ45 — closer to agent's universe
- **Risk-free:** BI 7-day reverse repo rate (~6% annualized)
- **Alpha** = Agent return − IHSG return. Target: positive, consistently.

### Daily Tracking

Each EOD routine appends an EOD snapshot to TRADE-LOG.md:

```markdown
## 2026-04-20 EOD

**Daily Summary**
- Portfolio: IDR 151,500,000
- Daily P&L: IDR +150,000 / +0.10%
- Phase-to-date P&L: IDR +1,500,000 / +1.0%
- Trades today: 1
- Running week count: 2

**Positions**
| Ticker | Qty | Entry | Current | Unrealized P&L | Stop Loss |
|--------|-----|-------|---------|-----------------|-----------|
| ADRO | 500 | 13,200 | 13,340 | +70,000 | 12,900 |
| BBCA | 300 | 15,800 | 15,720 | -24,000 | 15,200 |

**Cash Position:** IDR 52,000,000 (35%)

**Notes:** ADRO broke above 13,300 on coal price rally. BBCA held after BI comments.
```

### Weekly Scorecard (in WEEKLY-REVIEW.md)

- Starting and ending portfolio values (in IDR)
- Week return in IDR and percent
- IHSG week return (via WebSearch)
- Alpha generated
- Win/Loss/Open counts
- Win rate (% of trades profitable)
- Best and worst trade
- Profit factor
- Biggest lesson of the week
- Grade A-F for the week's decision-making

---

## The Compounding Effect

| Timeline | What the Agent Knows |
|----------|---------------------|
| **Day 1** | TRADING-STRATEGY.md only |
| **Week 1** | + 5 days of TRADE-LOG.md observations + first WEEKLY-REVIEW.md |
| **Month 1** | + 10-20 research log entries + sector patterns + ~4 weekly reviews with 15+ lessons |
| **Month 3** | + seasonal patterns + macro cycle experience + deep sector knowledge + 12 weekly scorecards |
| **Month 6** | Institutional-grade IDX knowledge: earnings calendars, flow patterns, commodity sensitivity by ticker, BI rate impact by sector, seasonal trade setups |

This is the real edge. The agent builds it automatically, every day, through TRADE-LOG.md and RESEARCH-LOG.md.

---

## Core Files

### TRADE-LOG.md
- Daily EOD snapshots (portfolio value, P&L, positions table, cash %)
- Trade summaries (entry/exit, thesis, result)
- Running count of trades per week
- Daily cash and position tracking

### WEEKLY-REVIEW.md
- Weekly stats table
- Open positions at week end
- What worked / what didn't work (3-5 bullets each)
- Key lessons from the week
- Adjustments for next week
- Letter grade A-F
- Cumulative metrics (month-to-date, phase-to-date)

### TRADING-STRATEGY.md
- The rulebook: which sectors, position sizes, entry/exit rules
- Updated only when a rule has proven itself for 2+ weeks or failed badly
- Changes are committed in the same commit as the weekly review that prompted them

### RESEARCH-LOG.md
- Daily research entries (macro news, earnings, sector research, commodity prices)
- Read by agent each morning before trading
- Informs daily thesis generation and risk adjustments

---

## Deliverables

- [ ] Daily EOD routine: append to TRADE-LOG.md with portfolio value, P&L, positions, cash %
- [ ] Daily-summary workflow: compute day P&L, phase-to-date P&L, trade count, running week count
- [ ] Weekly review workflow: compute stats table, compare to IHSG (via WebSearch), write weekly scorecard to WEEKLY-REVIEW.md
- [ ] Monday morning: agent reads WEEKLY-REVIEW.md and RESEARCH-LOG.md before trading
- [ ] Weekly strategy update: after 2+ weeks of repeated lessons, update TRADING-STRATEGY.md
- [ ] After 4 weeks of paper trading: review cumulative alpha and performance before live trading
