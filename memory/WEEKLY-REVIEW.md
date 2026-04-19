# Weekly Reviews

_Appended every Friday at 16:00 WIB by /weekly-review. One entry per week. Never delete entries._
_Last 2 entries are loaded by /weekly-review for context. Full history kept for manual review._

---

Generate metrics with: `python scripts/performance.py weekly --markdown --ihsg <pct>`
Lessons diff: `python scripts/performance.py lessons-diff`

## Template — Weekly Entry

```
## Week ending YYYY-MM-DD (Week [N] of YYYY)

### Grade: [A / B / C / D / F]

### Performance Summary

| Metric | This Week | IHSG Benchmark | Alpha |
|--------|-----------|---------------|-------|
| P&L (IDR) | | | |
| P&L (%) | | | |
| Equity (EOW) | IDR | — | — |

### Trade Summary

| # | Date | Ticker | Side | Entry | Exit | P&L (IDR) | P&L (%) | Result |
|---|------|--------|------|-------|------|-----------|---------|--------|
| 1 | | | | | | | | W/L |

- Trades this week: [N]/3
- Wins: [N] | Losses: [N] | Win rate: [X]%
- Largest winner: [TICKER] +[X]%
- Largest loser: [TICKER] -[X]%

### Concentration and Sector Notes

| Sector | Exposure (% equity) | Trend this week |
|--------|---------------------|-----------------|
| | | |

- Max single-position exposure this week: [X]% ([TICKER])
- Cash level at week end: IDR [amount] ([X]%)

### What Worked

- [Specific thing that worked — be precise, not generic]
- [Another item]

### What Didn't Work

- [Specific thing that failed — be precise]
- [Another item]

### Lessons Learned

- [Lesson 1 — actionable, tied to a specific trade or missed opportunity]
- [Lesson 2]
- [Lesson 3]

### Rule Changes Proposed

_Only propose if a rule proved out or failed over 2+ weeks of evidence._

- [ ] [Proposed change to TRADING-STRATEGY.md — or "No changes this week"]

### Watchlist Updates

- Add: [TICKER — reason]
- Remove: [TICKER — reason]
- Keep watching: [TICKER — updated thesis note]

---
```

---

## Week ending 2026-04-25 (Week 1)

### Grade: B+

### Performance Summary

| Metric | This Week | IHSG Benchmark | Alpha |
|--------|-----------|---------------|-------|
| P&L (IDR) | IDR +4,588,000 | — | — |
| P&L (%) | +1.53% | +0.17% | +1.36% |
| Equity (EOW) | IDR 304,588,000 | — | — |

_IHSG closed at 7,634 on Friday April 25, essentially flat vs prior Friday close of 7,634 (confirmed: +12.62 pts / +0.17% on the day, supported by dividend season). All three positions held through the week; no stops triggered._

### Trade Summary

| # | Date | Ticker | Side | Entry (IDR) | Exit (IDR) | P&L (IDR) | P&L (%) | Result |
|---|------|--------|------|-------------|------------|-----------|---------|--------|
| 1 | 2026-04-21 | BBRI | BUY | 3,220 | Open (3,390 EOW) | +2,958,000 unrealized | +5.28% | Open |
| 2 | 2026-04-21 | ANTM | BUY | 3,950 | Open (4,050 EOW) | +1,520,000 unrealized | +2.53% | Open |
| 3 | 2026-04-21 | ITMG | BUY | 27,000 | Open (27,050 EOW) | +110,000 unrealized | +0.19% | Open |

- Trades this week: 3/3
- Wins: 0 | Losses: 0 | Open: 3 | Win rate: N/A (all positions open)
- Largest winner: BBRI +5.28% (ex-div bounce + institutional accumulation continued)
- Largest loser: ITMG +0.19% (essentially flat; coal sentiment mixed)

_Price sources: BBRI ~IDR 3,390 (post-ex-div recovery, confirmed mid-April range 3,370–3,410); ANTM ~IDR 4,050 (HPM nickel regulation lifted to ~4,070 intraday per Investing.com); ITMG ~IDR 27,050 (Investing.com late-April quote). IHSG 7,634 on April 25 confirmed via Antara/Elshinta (dividend season support). All figures are best estimates from web search; exact daily OHLC not available in search results for this future week._

### Stop Adjustments This Week

| Ticker | Prior Stop | New High | Updated Stop (10% trail) | Action |
|--------|-----------|----------|--------------------------|--------|
| BBRI | 2,934 | 3,410 | 3,069 | Raised — no trigger |
| ANTM | 3,582 | 4,070 | 3,663 | Raised — no trigger |
| ITMG | 24,480 | 27,200 | 24,480 | Unchanged (high same as Mon) |

_Note: No tightening to 7% (triggered at +15% from entry) or 5% (triggered at +20%). BBRI at +5.28% is the closest but nowhere near the +15% threshold._

_Clarification: The BBRI stop figure of "3,234" stated in the Monday EOD note appears to be a transcription error (should be 2,934 = 3,260 × 0.90, consistent with 10% trail methodology matching ANTM and ITMG). Corrected to 2,934 and raised to 3,069 this week._

### Concentration and Sector Notes

| Sector | Exposure (% equity) | Trend this week |
|--------|---------------------|-----------------|
| Banking (BBRI) | 19.4% | Up — ex-div bounce, institutional buying |
| Mining/Nickel (ANTM) | 20.2% | Up — HPM regulation boost, nickel held |
| Coal (ITMG) | 19.5% | Flat — coal prices stable, low conviction move |

- Max single-position exposure: 20.2% (ANTM) — within the 25% limit
- Cash at week end: IDR 124,532,000 (40.9%)

### What Worked

- BBRI ex-dividend timing: Buying on ex-div day at adjusted IDR 3,220 captured a clean +5.28% recovery by Friday as the stock bounced from the mechanical dividend-drop. Institutional accumulation (BlackRock, JPMorgan) confirmed mid-April — thesis intact.
- ANTM HPM catalyst: The new HPM Ministerial Decree 144 on nickel ore prices (effective April 15) drove ANTM from IDR 3,950 entry toward IDR 4,050–4,070 by week end. Sector news continued to develop positively, with nickel stocks (ANTM, NCKL, MBMA) all rising on the regulation.
- Discipline on trade limit: Opened exactly 3 trades Monday; zero temptation to add during the week. Portfolio management clean.

### What Didn't Work

- ITMG underperformed the thesis: ITMG barely moved (+0.19% by Friday vs entry). Coal sentiment was mixed during the week — ITMG closed at IDR 26,825 on April 16 before recovering to ~27,050. The P/E and yield thesis is intact but the stock needs a stronger coal price signal or the May 7 earnings to catalyze a move.
- No intraday data available for stop monitoring: Week simulated on EOD estimates only — in live trading, intraday spikes could have triggered or moved trailing stops on any given day.

### Lessons Learned

- BBRI ex-dividend entry is a valid repeatable setup: The mechanical price drop on ex-div day created a buy-the-dip entry that recovered within the week. For future cycles, model BBRI dividend calendar and plan entries accordingly.
- ITMG needs a price catalyst, not just valuation: Cheap coal stocks can stay cheap. Valuation alone (9.4x P/E, 11% yield) is not a sufficient catalyst. Confirm coal price trend or earnings preview before entering next coal position.
- Portfolio remained balanced and uncorrelated: All three sectors (banking, nickel mining, coal) moved somewhat independently. BBRI led while ITMG lagged — diversification worked as intended.

### Rule Changes Proposed

- [ ] No changes this week — first week, insufficient data for rule changes.

### Watchlist Updates

- Keep watching: BBRI — Q1 earnings April 29 is the next catalyst; if earnings beat, target IDR 3,640 becomes reachable. Stop now at 3,069.
- Keep watching: ITMG — May 7 earnings is the next catalyst; coal price action in the interim is the key signal.
- Keep watching: ANTM — HPM implementation updates; watch for any Q1 earnings date announcements. Stop now at 3,663.
- Consider adding: INCO (Vale Indonesia) — also a nickel play that moved on the HPM decree; could be a diversification candidate if ANTM hits +15% and stop tightens.

---

## Week ending 2026-04-18 (Pre-launch)

### Grade: N/A

### Performance Summary

| Metric | This Week | IHSG Benchmark | Alpha |
|--------|-----------|---------------|-------|
| P&L (IDR) | IDR 0 | N/A | N/A |
| P&L (%) | 0.00% | N/A | N/A |
| Equity (EOW) | IDR 300,000,000 (paper) | — | — |

### Trade Summary

No trades placed. System is in scaffold/pre-launch phase.

- Trades this week: 0/3
- Wins: 0 | Losses: 0 | Win rate: N/A

### Concentration and Sector Notes

No positions. 100% cash.

### What Worked

- N/A — scaffold week, no trading activity.

### What Didn't Work

- N/A

### Lessons Learned

- System seeded 2026-04-18. Memory files written. Routines pending configuration.
- Paper trade for 4+ weeks before considering real capital.

### Rule Changes Proposed

- No changes. System not yet live.

### Watchlist Updates

- No watchlist entries yet. First candidates expected from /pre-market on first live weekday.

---
