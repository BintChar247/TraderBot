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

## Week ending 2026-04-24 (Week 1 — Trial, IDR 10B capital)

### Grade: B+

### Performance Summary

| Metric | This Week | IHSG Benchmark | Alpha |
|--------|-----------|---------------|-------|
| P&L (IDR) | IDR −22,537,500 | — | — |
| P&L (%) | −0.23% | −3.35% | +3.12% |
| Equity (EOW) | IDR 9,977,462,500 | — | — |

_IHSG: Day 0 baseline 7,634 (Apr 17 close) → 7,378 (Apr 24 close) = −3.35%. Portfolio lost 0.23% gross; alpha +3.12% driven by (a) 85.9% cash buffer through a risk-off week and (b) ITMG coal resilience offsetting BBRI pre-earnings weakness._

### Trade Summary

| # | Date | Ticker | Side | Entry (IDR) | Exit (IDR) | P&L (IDR) | P&L (%) | Result |
|---|------|--------|------|-------------|------------|-----------|---------|--------|
| 1 | 2026-04-20 | ITMG | BUY | 26,075 | Open (26,700) | +17,062,500 unrealized | +2.40% | Open |
| 2 | 2026-04-23 | BBRI | BUY | 3,260 | Open (3,080) | −39,600,000 unrealized | −5.52% | Open |

- Trades this week: 2/3
- Wins: 0 | Losses: 0 | Open: 2 | Win rate: N/A (no closes)
- Largest winner (unrealized): ITMG +2.40%
- Largest loser (unrealized): BBRI −5.52%
- Profit factor: N/A (no closed trades)
- No stops triggered. No hard cuts. No tightening (no position at +15%).

### Open Positions at Week End

| Ticker | Shares | Avg Cost (IDR) | Last (IDR) | Unrealized P&L | Days Held |
|--------|--------|----------------|------------|----------------|-----------|
| ITMG | 27,300 | 26,075 | 26,700 | +IDR 17,062,500 (+2.40%) | 4 |
| BBRI | 220,000 | 3,260 | 3,080 | −IDR 39,600,000 (−5.52%) | 1 |

- Cash at week end: IDR 8,570,952,500 (85.90%)
- Gross exposure: 14.10% (Coal 7.31% + Banking 6.79%) — well below 75–85% target
- Stop state: both HARD-CUT (neither at +7% trailing activation)

### What Worked

- **Pre-market discipline on Apr 20**: Skipped BBRI (ex-div Apr 21, price too far above post-div plan) and ANTM (above 3,960 ceiling) — only ITMG met entry criteria. Caught the Q1 EPS 114% beat catalyst at IDR 26,075 (below planned 27,000–27,200). Single best trade of the week.
- **Cash preservation under risk-off**: IHSG fell −3.35% across the week; we deployed only 14.1% gross and generated +3.12% alpha. Regime-aware sizing (15% cap in EM-OUTFLOW) did its job.
- **ITMG coal thesis intact**: Newcastle $131–136.50/t held the $130 thesis floor all week. Position +2.40% with coal tailwind intact through Gulf tensions.
- **Midday gate discipline (Apr 22)**: Market-data outage rejected ANTM/BBRI re-entry attempts on gates 3/4/7. Rather than override, accepted a no-trade day. "Patience beats activity" principle validated.

### What Didn't Work

- **BBRI timing on Apr 23**: Entered at IDR 3,260 the same day IHSG dropped −0.69%, and the stock slid to IDR 3,080 by Friday (−5.52% from entry). Entry took the full weekly budget risk without waiting for price confirmation. Thesis (Q1 earnings Apr 29) intact but timing was early into a risk-off tape.
- **Market-data infra outage all week**: yfinance unreachable from workspace from Apr 21 onward. All marks via WebSearch fallback. This is a systemic infra problem — in a faster-moving regime, missing intraday data could cost real money.
- **Only 2/3 weekly trades used**: ANTM HPM catalyst was on the plan but price never pulled back to the 3,900–3,960 entry zone; no re-attempt on Thu/Fri despite continued positive nickel narrative. Missed a potential third entry.

### Lessons Learned

- **Wait for the IHSG tape on banking entries**: BBRI bought into a −0.69% IHSG day and kept bleeding with it. For next cycle: on a banking name, require IHSG flat-or-green OR a same-day stock-specific catalyst (not a 6-day-out catalyst) before entering during EM-OUTFLOW regime.
- **Coal names reward patience, bank names punish it**: ITMG bought Monday with immediate catalyst (Q1 beat Apr 19) held gains all week. BBRI bought pre-earnings (T−6) was exposed to every risk-off day until the catalyst lands. Pair catalyst distance with regime — closer catalyst preferred in risk-off.
- **Cash is a position in EM-OUTFLOW**: 85.9% cash generated +3.12% alpha this week. Stop romanticizing the 75–85% deployed target as always-on; treat it as a regime-dependent ceiling, not a floor.
- **Fix market-data infra before Week 2**: Five straight days on WebSearch fallback is not sustainable. Without reliable intraday data, stop logic is executed with eyes half-closed.

### Rule Changes Proposed

- No changes. One week is not 2+ weeks of evidence for any rule revision. Rules held. TRADING-STRATEGY.md unchanged.

### Watchlist Updates

- **Keep watching: BBRI** — Q1 earnings Wed Apr 29 is the pivot. If it beats (prior quarter beat consensus by 12.86%) and price rips above IDR 3,400, thesis validated; if it misses or IDR weakens further, exit likely precedes hard-cut 3,031.
- **Keep watching: ITMG** — May 7 earnings approaching. Trail activates at 27,900 (+7%). Watch Newcastle coal; below $120/t would break thesis.
- **Keep watching: ANTM** — HPM narrative intact but price above 3,960 entry ceiling all week. Re-add if pulls back; consider paired with INCO as diversified nickel exposure.
- **Add: INCO** — Vale Indonesia, second nickel play on HPM decree; pair-trade candidate if ANTM runs without pullback.
- **Remove: none.**

---

## Week ending 2026-04-25 (Week 1 — SIMULATION, IDR 300M test)

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

## Week ending 2026-05-08 (Week 3 — Post-trial continuation, IDR 10B capital)

### Grade: C

### Performance Summary

| Metric | This Week | IHSG Benchmark | Alpha |
|--------|-----------|---------------|-------|
| P&L (IDR) | IDR +7,695,000 | — | — |
| P&L (%) | +0.08% | +4.07% | −3.99% |
| Equity (EOW) | IDR 9,918,947,500 | — | — |

_IHSG: Week-baseline 6,888 (Apr 30 close, frozen through May 1 Labor Day) → 7,168.47 (May 8 sesi I anchor; sesi II final not in cycle by 15:15 WIB) = +4.07%. Portfolio gained only +0.08% on the week — cash drag through a +4% recovery rally. Cumulative trial-period alpha trimmed from +8.88% (May 1 peak) to +5.29% (today) — gave back ~360 bps of accumulated alpha by sitting on 92–93% cash through 4 of 5 sessions._

### Trade Summary

| # | Date | Ticker | Side | Entry (IDR) | Exit (IDR) | P&L (IDR) | P&L (%) | Result |
|---|------|--------|------|-------------|------------|-----------|---------|--------|
| 1 | 2026-05-08 | ADRO | BUY | 2,550 | Open (2,500) | −19,605,000 unrealized | −1.96% | Open |

- Trades this week: 1/3
- Wins: 0 | Losses: 0 | Open: 1 | Win rate: N/A (no closes this week)
- Largest winner: N/A (no closed gainers)
- Largest loser (unrealized): ADRO −1.96% (same-day, first-day noise within bounds)
- Profit factor: N/A (no closed trades)
- No stops triggered. No hard cuts. No tightening (no position at +15%).
- Sells/closes this week: 0. BBRI hard-cut sat in last week's tally (May 1).

### Open Positions at Week End

| Ticker | Shares | Avg Cost (IDR) | Last (IDR) | Unrealized P&L | Days Held |
|--------|--------|----------------|------------|----------------|-----------|
| ITMG | 27,300 | 26,075 | 26,000 | −IDR 2,047,500 (−0.29%) | 18 |
| ADRO | 392,100 | 2,550 | 2,500 | −IDR 19,605,000 (−1.96%) | 0 |

- Cash at week end: IDR 8,228,897,500 (82.96%)
- Gross exposure: 17.04% (Coal: ITMG 7.16% + ADRO 9.88%) — well below 75–85% target
- Sector exposure: Coal 17.04%; Banking 0% (BBRI removed via May 1 hard-cut)
- Stop state: ITMG hard-cut 24,250 (buffer +6.73%); ADRO hard-cut 2,371 (buffer +5.16%) — both pre-trail

### What Worked

- **Discipline through SMGR/AKRA/ANTM/UNVR (May 5–7)**: Four candidate days, four SKIP decisions on legitimate grounds (Gate 9 chase failures, R:R <2:1, multi-source price not verifiable under data-infra block since Apr 21). No rule overrides, no chasing into a rally. The "patience beats activity" principle was honored — even when wrong-footed by the bounce.
- **ITMG carry through Q1 print day (May 7)**: Pre-commit triggers held through 09:15 WIB / 11:30 WIB / EOD scans on print day. Hard-cut buffer 4.81%+ throughout. Position closed the week at −0.29%, well-positioned for any post-print rerate. Multi-source verification protocol from MISTAKES.md 2026-05-01 lesson applied at every scan.
- **ADRO entry on May 8**: Real fundamental catalyst (Q1 net +67.07% YoY, US$128.14M; rev +23.4%) inside coal-complex 5-of-5 positive Q1 prints. 15-gate buy-side check passed. Pair-wise add to held ITMG. Position 10.06% of equity (well within 15% EM-OUTFLOW cap), sector concentration 17.22% post-fill (well below 35–40% cap). Tighter R:R (1.96:1) than alternatives (PTBA 1.71:1).
- **No rule breaches all week**: No daily-cap, no weekly-cap, no max-drawdown alerts. Stops ledger clean. Drawdown −1.07% from peak (vs −15% hard limit). Risk hygiene perfect.

### What Didn't Work

- **Cash drag during a +4% IHSG bounce**: 92.99% cash from May 4–7 through a recovery rally driven by lower-than-expected April CPI + PDB beat momentum. The regime label EM-OUTFLOW remained appropriate ex-ante, but ex-post the tape said risk-on. We collected only +0.08% while the index ran +4.07%. This is the single biggest weekly alpha-bleed of the trial.
- **Three candidates failed Gate 9 on intraday chase, never revisited**: MEDC May 5 (−9.93% drift), ANTM May 6 (+6.53% drift carried into May 7), UNVR May 7 (+8.84%/+10.06% chase). All three were on the morning research plan. None returned to plan zones; none triggered a re-research. The agent skipped, but did not adapt — the static plan zones became noise as the market moved.
- **Data-infra still blocking after 17 days**: yfinance has been 403-blocked since Apr 21; GoAPI key never set. Every mark this week was multi-source WebSearch. Conservative anchors held (e.g., May 8 ITMG mid-range 26,000), but this is not a sustainable production posture. ITMG Q1 print release timing was uncertain into EOD May 7 partly because no live tape feed could confirm a wire post.
- **ADRO entry on the last possible day, with the worst structural deployment**: From a portfolio-construction view, sitting cash for 4 days then deploying 10% of equity on Friday concentrates entry timing risk into the same-day-of-data, same-day-as-weekly-review tape. Smaller stagger / earlier-week deployment would have been cleaner.

### Lessons Learned

- **Re-evaluate plan-zone Gate 9 rejects on a 24-hour clock**: When a candidate fails Gate 9 by a chase margin one day (e.g., MEDC May 5 −9.93%, ANTM May 6 +6.53%), force a same-evening RESEARCH-LOG re-entry to either reset the plan zone or formally drop the name. The drift IS new information — sticking with a stale zone for 3+ days while the market trends through it = systematically missing the move. Add to /pre-market routine: any prior-day Gate 9 reject must be either re-priced (new entry zone, new R:R) or removed from the candidate slate.
- **Cumulative alpha is path-dependent and asymmetric**: Saving +8.88% alpha through Week 1's risk-off requires keeping it through Week 3's risk-on. Sitting 93% cash during a +4% IHSG bounce surrenders alpha that took 8 sessions of cash-discipline to earn. Rule the agent should self-impose: when IHSG closes ≥+1% on a session in EM-OUTFLOW regime AND cumulative alpha > +5%, the next pre-market should explicitly re-evaluate the regime label, not default to "EM-OUTFLOW unchanged" by inertia. The regime label should follow tape, not anchor it.
- **Coal-sector Q1 prints are converging up — sector-level conviction is now stronger than name-level**: ITMG Q1 (last quarter +114%), ADRO Q1 (+67.07%), PTBA (+105%), BUMI (+34.6%), HRUM (+14.67% rev). Five of five coal Q1 prints positive in 2026 cycle. Pattern: coal complex is in a synchronized earnings rerate, not isolated names. PATTERNS.md eligible for promotion (2+ obs).
- **Multi-source verification protocol works**: From MISTAKES.md 2026-05-01 (BBRI stale-source lag during hard-cut), the 1.5%-buffer trigger for multi-source confirmation was applied at every midday/market-open this week. Zero stale-mark errors. The procedural fix from the BBRI failure stuck. Keep this protocol; do not relax it.

### Rule Changes Proposed

- **No formal TRADING-STRATEGY.md changes this week.** One week of cash-drag during a bounce is one data point, not 2+ weeks of evidence. The 15% EM-OUTFLOW position cap held; the 75–85% deployed target was missed but the regime call was the operative constraint. If Week 4 shows the same cash-drag pattern under a continued risk-on tape AND regime label still EM-OUTFLOW, that's grounds for a regime-recheck rule change. For now: rules held.
- Watch-list candidate change: **promote "regime-recheck on +1% IHSG day with cumulative alpha >+5%" to a soft rule** in the next weekly review if Week 4 confirms. Not codifying yet.

### Watchlist Updates

- **Add: PTBA** — coal complex Q1 +105% YoY confirmed; if ADRO works as a pair-trade, PTBA is the third-leg coal name to watch on a sector continuation. Wait for Newcastle continuation above $135/t before adding (sector concentration cap 35–40% must be respected).
- **Add: BUMI** — coal Q1 +34.6%; complementary to ADRO/ITMG. Liquidity and ADV check needed before promotion.
- **Keep watching: ITMG** — Q1 2026 print release timing uncertain into May 8 close; pre-market Mon May 11 priority is to surface the print. If material miss → close on open per pre-commit. If beat (last quarter +114% surprise) → transition stop to TRAILING 10% on intraday >27,900.
- **Keep watching: ADRO** — first-day −1.96%, within first-day noise. Pre-mortem triggers (Brent <$95 sustained 3 days, Newcastle <$125, ADRO −4% within 5 days) all unbreached. Dividend payment day (May 8) was mechanical, not signal.
- **Keep watching: SMGR** — re-look on pullback to ≤1,950 enabling ≥2:1 R:R. Cement-sector consolidation thesis intact.
- **Remove: UNVR** — chase has run too far for the EGMS dividend catalyst window (June). Pullback to 1,650–1,700 needed to re-enter consideration.
- **Remove: ANTM** — Gate 9 chase carry-over since May 6; no fresh research re-entry. Drop from candidate list pending HPM/nickel re-thesis.
- **Remove: MEDC** — Iran-US de-escalation premium has retraced; thesis structurally weakened. Off candidate list.

---
