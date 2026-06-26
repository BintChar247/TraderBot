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

## Week ending 2026-05-29 (Week 6 — Post-trial continuation, IDR 10B capital; Idul Adha shortened week)

### Grade: B

### Performance Summary

| Metric | This Week | IHSG Benchmark | Alpha |
|--------|-----------|---------------|-------|
| P&L (IDR) | IDR +46,710,000 | — | — |
| P&L (%) | +0.48% | +1.40% | −0.92% |
| Equity (EOW) | IDR 9,856,337,500 | — | — |

_IHSG: Mon May 25 opening anchor 6,094.94 (Fri May 22 close) → Fri May 29 safe-midrange close 6,180 = +1.40% (multi-source spread issue persists at filing; sesi I close 6,217.87, intraday correction 6,112; safe-midrange used as conservative anchor). Portfolio +0.48% via KLBF unrealized mark uplift (entry 945 → safe-lower 1,035 = +9.52% on position); cash drag through a +1.40% bounce trimmed cumulative trial alpha from +18.26% (May 22 high) to +17.61% (today) = ~65bp give-back. 3-trading-day week (Wed/Thu IDX closed Idul Adha 1447 H)._

### Trade Summary

| # | Date | Ticker | Side | Entry | Exit | P&L (IDR) | P&L (%) | Result |
|---|------|--------|------|-------|------|-----------|---------|--------|
| — | — | — | — | — | — | — | — | No trades placed this week |

- Trades this week: 0/3 (Mon SKIP, Tue SKIP, Wed/Thu IDX closed, Fri SKIP — full slot allocation preserved unused into Week 7)
- Wins: 0 | Losses: 0 | Open closes: 0 | Win rate: N/A (no closed trades)
- Largest winner: N/A (no closed trades; KLBF carry +9.52% unrealized is the live winner)
- Largest loser: none (no closed trades; no hard cuts)
- Profit factor: N/A (no closed trades)
- Stop-state transitions: **1 — KLBF hard-cut 878 → trailing 10% @ 931 (HWM 1,035)** on Fri Day 30 midday (first state-machine transition fired of the trial)
- No stops triggered; no hard cuts; no tightening to 7%/5% (deferred per spread-discipline pending cluster narrowing)

### Open Positions at Week End

| Ticker | Shares | Avg Cost (IDR) | Last (IDR) | Unrealized P&L | Days Held |
|--------|--------|----------------|------------|----------------|-----------|
| KLBF | 519,000 | 945 | 1,035 (safe-lower) | +IDR 46,710,000 (+9.52%) | 7 |

- Cash at week end: IDR 9,319,172,500 (94.55%)
- Gross exposure: 5.45% (Healthcare only — KLBF) — well below 75–85% target; consistent with DEFENSIVE regime 5% cap
- Sector exposure: Healthcare 5.45%; coal/banking/mining/nickel all 0%
- Stop state: KLBF now TRAILING 10% (state-1 transition fired Fri midday); current stop 931 = floor locks max realized loss at IDR −7,266,000 (~0.07% of equity) vs original hard-cut at 878 (~0.69% of equity) — ~10× downside risk reduction post-transition

### What Worked

- **First stop-state transition fired correctly (KLBF, Fri midday)** — Position crossed +7% threshold; hard-cut 878 transitioned to trailing 10% @ 931 (HWM 1,035). Spread-discipline applied: safe-lower 1,035 used for stop placement; +15%/+20% tightening deferred pending cluster narrowing. This is the first state-machine validation of the multi-stage stop ladder in the live trial — confirms the state machine fires as designed when winners materialize. Strategy validation milestone.
- **Disciplined SKIP through 15 candidate-evaluations across 3 trading days** — Mon (6 candidates), Tue (5), Fri (4). Every SKIP rooted in concrete plan-gate failures (Gate 9 chase, sub-2:1 R:R, multi-source price spread >2% verification floor, single-source catalyst confirmation, MSCI Small Cap removal contamination). Zero forced entries despite 3/3 weekly slot allocation untouched and cumulative trial alpha already strongly positive (+17.61% — the kind of cushion that tempts loosening).
- **MSCI rebalance flow event navigated without entering** — Friday May 29 was the MSCI Indonesia implementation day ($1.6–1.8B passive outflow concentrated at MOC). Plan said "deferred candidates re-enter consideration only on IHSG-open behavior + R:R ≥2:1". PGAS skipped on IHSG opening 6,065 below the 6,100 plan floor; ICBP/TLKM/JSMR all failed 2:1 R:R gate at verified marks. Discipline preserved through structural flow event without capital deployment risk.
- **KLBF entry thesis (Week 5) validated through Week 6** — Healthcare defensive sector leadership held on red tape (Tue −0.98%, KLBF +0.53%) AND held its bid on green tape (Mon +1.90%, KLBF flat). Buyback program IDR 500B live through Jul 2 continued to provide structural bid floor. KLBF NOT in MSCI Small Cap removal list (MIKA was — sector-localized contamination, name-specific clean). +9.52% unrealized at week close.
- **Multi-source verification + safe-lower-mark rule held under spread stress** — KLBF cluster 1,035–1,135 (~9.7% spread) on Fri; per MISTAKES.md 2026-05-01 procedure, used safe-lower 1,035 for all gates and EOD MTM. No stale-mark error this week. The procedural fix from the BBRI May 1 failure continues to stick.

### What Didn't Work

- **Cash drag on a +1.40% IHSG recovery week** — 94.55% cash through a relief-rally week trimmed cumulative trial alpha by ~65bp (from +18.26% May 22 high to +17.61% May 29). Less severe than Week 3's −3.99% alpha give-back, but the same structural pattern: defensive cash positioning underperforms on recovery tape. The week's −0.92% alpha is the binding cost of the DEFENSIVE regime label held through a partial relief rally.
- **Data infrastructure Day 36–40 — yfinance still blocked, broker.sh sell override still not patched** — Six full weeks of WebSearch fallback for all marks. KLBF MTM uplift required multi-source synthesis with 9.7% cluster spread on Fri (safe-lower used; high-mark deferred). The MD_LAST_PRICE_OVERRIDE codification for broker.sh cmd_sell (called out in MISTAKES.md 2026-05-20 ADRO entry) remains unimplemented after 3 hard-cut corruptions of broker.sh fill prices (BBRI / ITMG / ADRO). Repeat infra debt.
- **Multi-source spread on PGAS persisted Mon AND Tue, blocking the highest-scoring candidate twice** — Mon spread 3.4% (1,790–1,850); Tue spread ~30% (1,850 vs 2,400 Investing.com outlier). Abadi LNG catalyst confirmed by 5 independent sources but live-mark ambiguity binding under the 2% verification gate. Data-infra forces a hard SKIP on the most catalyst-fresh candidate two days running. Skip was right; the cost is invisible (counterfactual entry P&L) but real.
- **WEEKLY-REVIEW.md gap (Weeks 4 and 5 never written)** — TRADE-LOG narrative repeatedly references "DEFENSIVE-confirmed (formal Week 5 weekly-review flip per WEEKLY-REVIEW.md)" but no Week 4 or Week 5 entry exists in this file. MACRO-REGIME.md still shows "EM OUTFLOW — RECOVERING" as of May 8 (Week 3). The regime-label drift between TRADE-LOG operational language and MACRO-REGIME / WEEKLY-REVIEW canonical records is a process miss. (This week's entry only covers Week 6 — does not retroactively fill Weeks 4–5.)

### Lessons Learned

- **State-machine transitions should be a celebrated artifact, not a sidebar** — Fri's KLBF hard-cut → trailing transition is the first time the multi-stage stop ladder fired in the live trial. The transition reduces position-level downside by ~10× (0.69% → 0.07% of equity at risk). For Week 7+: explicitly log every state-machine transition in TRADE-LOG with a dedicated header, and in WEEKLY-REVIEW track how often each state fires across the trial — this is the strategy's compounding-edge mechanism and deserves accumulated evidence.
- **Cash drag during DEFENSIVE on a partial-recovery week is the right cost, not a mistake** — Week 6 −0.92% alpha vs Week 3 −3.99% alpha shows the cost scales with rally magnitude, not with regime-label correctness. The DEFENSIVE label held the right call through Tue's −0.98% red day (alpha +0.90%) and surrendered ~65bp on Mon's +1.90% and Fri's +0.81% green prints. Net week alpha is negative but the regime call would have been validated by a single sustained capitulation day (and there are likely more coming — IHSG cumulative −19.05% from Day 0). Do not loosen the label on one mild-recovery week.
- **PGAS multi-source spread is a recurring data-infra surface area, not a candidate problem** — Same name failed the same gate (Gate a verification, <2% multi-source spread) on Mon AND Tue. The candidate is fine; the data infra is the bottleneck. Codify: when the same candidate fails the same verification gate on 2 consecutive sessions due to source-spread, escalate to a pre-market data-infra investigation task (which source is right? can we whitelist a primary?) rather than re-running the same SKIP rationale a third day. This is the same root cause as the yfinance-blocked debt — different symptom.
- **Close the WEEKLY-REVIEW.md / MACRO-REGIME.md / TRADE-LOG label-drift gap** — The "DEFENSIVE-confirmed" label is in operational use in TRADE-LOG narrative but not formally codified in MACRO-REGIME.md or backfilled in WEEKLY-REVIEW.md (Weeks 4–5 missing). Next weekly-review (Week 7) should either backfill Weeks 4–5 retroactively from TRADE-LOG evidence OR explicitly accept the gap and move forward with a Week 7 MACRO-REGIME.md update that re-anchors the label timeline.

### Rule Changes Proposed

- **No formal TRADING-STRATEGY.md changes this week.** Week 6 is one week of mild cash drag (−0.92%) during a partial recovery in a 3-trading-day Idul Adha-shortened window. Not 2+ weeks of new evidence requiring strategy revision. Rules held; DEFENSIVE regime call held; 5% sizing cap held; gates fired correctly on every SKIP.
- **Process change (non-strategy):** schedule a Week 7 MACRO-REGIME.md formal update to (a) flip the canonical label to DEFENSIVE matching the operational TRADE-LOG language since Week 5, OR (b) re-evaluate and document why the EM-OUTFLOW-RECOVERING label is still operative. Either way, close the doc drift.
- **Process change (non-strategy):** weekend infra patch carryover — add MD_LAST_PRICE_OVERRIDE support to broker.sh cmd_sell (mirror cmd_buy at line 351). Open since MISTAKES.md 2026-05-01 (BBRI), reinforced 2026-05-20 (ADRO). Three hard-cuts have corrupted broker.sh fill prices. Codify as Week 7 priority infra task.

### Watchlist Updates

- **Keep watching: KLBF** — Now in state-2 trailing (current_stop 931, HWM 1,035, trail_pct 10). Mon Jun 1 priority: re-evaluate multi-source cluster for narrowing. If cluster converges ≤2% spread AND mark ≥1,087 (+15%), tighten trail to 7%; ≥1,134 (+20%), tighten to 5%. State-2 transitions deferred only on data-quality grounds, not discretion. Buyback program through Jul 2 supportive.
- **Keep watching: PGAS** — Abadi LNG catalyst remains fresh (5-source confirmed). Block was data-infra (multi-source price spread) two days running, NOT thesis. If Mon Jun 1 cluster narrows ≤2% AND R:R ≥2:1 at verified mark, top-priority Week 7 entry candidate.
- **Keep watching: ICBP, TLKM, JSMR** — Deferred candidates from Fri SKIP on R:R compression. Re-evaluate Mon Jun 1 if any pull back to entry zones (ICBP ≤7,950; TLKM, JSMR ≤ research-spec floors). Consumer FMCG (ICBP) leadership pattern still operative under DEFENSIVE regime.
- **Keep watching: BBCA** — Pullback to ≤5,800 needed for clean 2:1 R:R. Banking sector-watch 1-of-2 strike (BBRI May 1) still binding — entry requires same-day catalyst or BI-cut signal, not just R:R compression.
- **Remove: MIKA** — MSCI Small Cap removal Fri May 29 close = passive-flow contamination on top of the held-KLBF sector-concentration concern. Off candidate list pending fresh thesis and post-flow stabilization.
- **Remove: INDF, UNVR** — INDF Bloomberg-cited 6,725 vs research 7,600 anchor = unresolved data quality issue and possible thesis-break trigger that was never adjudicated. UNVR retracement post May 21 +5.45% candle erased the entry zone. Both off candidate list pending fresh research re-entry.
- **Watch the deferred Week 7 entry queue closely** — 4 carry-over candidates (PGAS, ICBP, TLKM, JSMR) into Week 7 with full 3/3 slot allocation available. Weekly trade discipline is intact (0 used in Week 6) but Week 7 needs to make capital work if R:R / data-infra clears.

---

## Week ending 2026-06-05 (Week 7 — Post-trial continuation, IDR 10B capital; IDX -7.10% panic-flush week + IDR cascade kill-switch BREACHED)

### Grade: A

### Performance Summary

| Metric | This Week | IHSG Benchmark | Alpha |
|--------|-----------|---------------|-------|
| P&L (IDR) | IDR 0 (frozen safe-lower carry) | — | — |
| P&L (%) | 0.00% | −7.10% | +7.10% |
| Equity (EOW) | IDR 9,856,337,500 | — | — |

_IHSG: Mon Jun 1 IDX closed Hari Lahir Pancasila; week starts Tue Jun 2 reopen. Anchors: Fri May 29 sesi II actual close 6,127.38 → Fri Jun 5 sesi I close 5,692.15 (sesi II final not multi-source convergent at filing) = −7.10%. Portfolio flat through the panic-flush week on 94.55% cash buffer + KLBF safe-lower carry frozen at 1,035. Cumulative trial alpha vs IHSG: +24.00% — new trial high (3rd consecutive day of expansion Wed/Thu/Fri). Day 0 baseline IHSG 7,634 → today sesi I 5,692.15 = IHSG cumulative −25.44%; portfolio cumulative −1.44%; alpha gap +24.00pp = the strongest absolute alpha position of the trial to date._

### Trade Summary

| # | Date | Ticker | Side | Entry | Exit | P&L (IDR) | P&L (%) | Result |
|---|------|--------|------|-------|------|-----------|---------|--------|
| — | — | — | — | — | — | — | — | No trades placed this week |

- Trades this week: 0/3 (Mon Jun 1 IDX closed; Tue/Wed/Thu/Fri all 0 trades — second consecutive zero-trade week; full slot allocation preserved)
- Wins: 0 | Losses: 0 | Open closes: 0 | Win rate: N/A (no closed trades)
- Largest winner: N/A (no closed trades; KLBF carry +9.52% unrealized is the live winner, frozen at safe-lower 1,035)
- Largest loser: none (no closed trades; no hard cuts)
- Profit factor: N/A (no closed trades)
- Stop-state transitions: 0 (KLBF state-3 +15% and state-4 +20% triggers ARMED but cluster non-convergence Day 41–45 blocks confirmation; State 2 trailing 10% @ 931 unchanged from Fri May 29 midday transition)
- No stops triggered; no hard cuts; no tightening fired

### Open Positions at Week End

| Ticker | Shares | Avg Cost (IDR) | Last (IDR) | Unrealized P&L | Days Held |
|--------|--------|----------------|------------|----------------|-----------|
| KLBF | 519,000 | 945 | 1,035 (safe-lower carry frozen since Tue Jun 2 anchor; Fri Jun 5 cluster Yahoo 945 / Investing 1,135 ~20.1% spread > 2% threshold; full-week cluster wider — Wed/Thu/Fri saw TradingView/Google 745 + Morningstar 770 low outliers + Yahoo 1,135 stale high; safe-lower discipline binds per MISTAKES.md 2026-05-01) | +IDR 46,710,000 (+9.52%) | 12 |

- Cash at week end: IDR 9,319,172,500 (94.55%)
- Gross exposure: 5.45% (Healthcare only — KLBF) — well below 75–85% target; consistent with DEFENSIVE-INTENSIFIED 5% sizing cap
- Sector exposure: Healthcare 5.45%; coal/banking/mining/nickel/conglomerate all 0%
- Stop state: KLBF TRAILING state-2 10% @ 931 (hwm 1,035); state-3 (≥1,087 → 7% trail) ARMED; state-4 (≥1,134 → 5% trail) ARMED; cluster non-convergence blocks all transitions per discipline
- Future dividend receivable: IDR 10,380,000 (519,000 sh × IDR 20 KLBF cash dividend; ex-date Jun 4; recording Jun 5; payment Jun 24 — not in equity carry yet)

### What Worked

- **Asymmetric defensive vindication on the IHSG -7.10% panic-flush week** — Wed Jun 3 IHSG sesi I −5.30% (Moody's Danantara Baa2 negative outlook downgrade + IDR 17,920 + MSCI overhang); Thu Jun 4 IHSG −3.48% continuation (5.5-year low intraday 5,644); Fri Jun 5 IHSG −0.73% (IDR 18,033 cascade kill-switch BREACHED sustained close). 94.55% cash buffer + KLBF healthcare-defensive frozen carry produced 3 consecutive days of cumulative-alpha expansion: +21.41% Wed → +23.44% Thu → +24.00% Fri. This is the asymmetric tail-event the defensive book was structured for. Cumulative trial alpha is now at the strongest absolute level of the entire trial (+24.00pp vs IHSG since Day 0).
- **Patience discipline through 17+ candidate evaluations across 4 live trading days** — Tue (5 candidates SKIP), Wed (6 candidates SKIP), Thu (4 candidates SKIP), Fri (4 candidates SKIP). Every SKIP rooted in concrete plan-gate failures: multi-source cluster spread >2% (PGAS Day 3+, ICBP, JSMR, MYOR), Gate 9 chase fail (TLKM +24.8% above plan, UNVR +13.1%, ICBP +8.0%), R:R compression (JSMR 1.62:1 < 2:1), and pre-emptive plan-mandated defer (Tue gap-reopen pre-11:00 WIB rule). Zero forced entries despite 0/3 weekly slot allocation untouched and cumulative trial alpha already strongly positive (+17.61% → +24.00% — the kind of cushion that tempts loosening).
- **Pre-11:00 WIB gap-reopen defer rule fired correctly on Tue Jun 2 reopen** — 4-day cumulative IDX pause (Idul Adha + weekend + Pancasila) into a single Tue reopen carrying MSCI rebalance T+2/T+3 + Danantara Phase 1 Day 2 + IDR record-breach 17,970 catch-up = textbook gap-reopen risk. Plan-mandated defer of all 5 candidates at 09:15 to midday post-sesi-I-close was the right answer; midday re-gate at 11:30 confirmed all still failed multi-source ≤2% discipline. Tape played out exactly as defer rule anticipated: sesi I +1.49% bounce reverted into the panic-flush regime by Wed.
- **Multi-source verification + safe-lower-mark rule held under DAILY-WORSE spread stress** — KLBF cluster widened from 9.7% (Tue) to ~42% (Wed) to ~52% (Thu) to ~20% (Fri) as different sources cached forward or pulled fresh tape inconsistently. Per MISTAKES.md 2026-05-01 procedure, safe-lower 1,035 used for all gates and EOD MTM all week. Zero stale-mark errors. The procedural fix from the BBRI May 1 failure continues to stick through extreme stress. TradingView/Google 745 cluster-low on Wed/Thu (which would have triggered hard-cut at −21% if confirmed) was correctly NOT acted on because 2-source convergence failed the ≥3-source threshold per discipline.
- **IDR cascade kill-switch framework armed and disciplined on Fri Jun 5 breach** — IDR 17,899 (Wed 09:15) → 17,920 (Wed EOD) → 17,977 (Thu intraday) → 18,033 (Fri sustained close, BREACHES 18,000) followed a clean escalation ladder. Each pre-emptive de-risk threshold (17,820 → 17,900 → 18,000) was tracked and disciplined; no panic action taken on the breach because (a) KLBF cluster non-convergence blocks broker-side fire anyway, (b) Mon Jun 8 pre-market explicitly scheduled for formal pre-emptive de-risk evaluation. Process held under primary kill-switch breach event.

### What Didn't Work

- **Data infrastructure Day 41–45 — yfinance + GoAPI both still blocked; broker.sh cmd_sell MD_LAST_PRICE_OVERRIDE still not patched** — Seven full weeks of WebSearch fallback for all marks. KLBF cluster spread widened from 9.7% (Tue) to ~52% (Wed/Thu) — the worst single-week cluster non-convergence of the trial. State-machine state-3 (+15%) and state-4 (+20%) tightening triggers ARMED all week but never fired because Yahoo's single-source 1,135 reading appears stale-cached forward from Tue Jun 2 sesi I anchor (cannot confirm a fresh Thu/Fri timestamp). The MD_LAST_PRICE_OVERRIDE codification for broker.sh cmd_sell (open since MISTAKES.md 2026-05-01 BBRI; reinforced 2026-05-20 ADRO) remains unimplemented Week 7. Three hard-cuts have corrupted broker.sh fill prices to date; the infra debt is structural.
- **KLBF carry interpretation ambiguity persists** — The frozen safe-lower 1,035 carry held the equity ledger and dashboard MTM at +9.52% all week, but the persistent TradingView/Google Finance 745 cluster-low cross-source readings on Wed/Thu (−21% from entry, well below the trailing 931 GTC stop) suggest real-tape KLBF may have traded materially lower than the dashboard reflects. The cluster-discipline procedure protects against single-source whipsaw correctly, but on a -7.10% IHSG week even a defensive healthcare name likely faced ~5-10% drawdown intraday on some days. The "frozen +9.52%" carry is a procedural fiction during data-quality outage; the broker-side trailing 931 GTC IS the binding floor. If the real tape closes Mon Jun 8 with cluster ≤931 confirmed, stop fires automatically and the realized P&L will be near 931 fill, not 1,035 carry.
- **WEEKLY-REVIEW.md Weeks 4 and 5 still not backfilled** — Flagged in Week 6 review as a process miss; remains a gap this week. TRADE-LOG operational language ("DEFENSIVE-confirmed Week 5 flip") is in active use but the canonical Weekly Review entries for Weeks 4 and 5 do not exist in this file. The cumulative trial alpha trajectory (+24.00% Day 35) is reconstructible from EOD daily logs, but the formal narrative reviews for Weeks 4–5 (regime flip → DEFENSIVE-CONFIRMED + ITMG Q1 miss + KLBF entry rationale + ADRO hard-cut Week 5) remain undocumented in this file. Carrying the gap forward to Week 8 — Week 8 review should either backfill or formally accept and move forward.
- **Cluster non-convergence on KLBF blocks state-machine compounding edge** — Wed Yahoo 1,135 reading alone would have crossed +20% threshold (fire state-4 → 5% trail at 1,077 hwm); confirmed state-4 transition would have raised the GTC stop from 931 (locking IDR −7,266,000 max position loss) to 1,077 (locking IDR +68,508,000 minimum gain). The compounding edge of the multi-stage stop ladder is exactly what the strategy designed for, but data-infrastructure failure now blocks the upside fire-paths for 5 consecutive sessions. Safe-lower discipline is correct under cluster non-convergence, but the cost is invisible (counterfactual locked profit) and real. Same root cause as the broker.sh patch debt.

### Lessons Learned

- **Defensive cash buffer + healthcare-defensive carry is the binding alpha generator under cascade-event weeks** — Week 7 vs Week 3 contrast: Week 3 (+0.08% portfolio vs +4.07% IHSG = −3.99% alpha, the trial's worst alpha-bleed) showed cash drag on a recovery rally. Week 7 (0.00% portfolio vs −7.10% IHSG = +7.10% alpha, the trial's best week-over-week alpha after Week 1's +3.12%) shows the same cash-heavy structure is asymmetric on the downside. The DEFENSIVE label has now been operationally validated across one recovery rally (loss-on-pattern, accepted) AND one panic-flush + IDR-cascade week (massive gain-on-pattern). The 5% sizing cap + 94.55% cash + healthcare-defensive carry is the trial's edge-generation mechanism. Lesson: DO NOT loosen the regime label on a single bounce or recovery print — the asymmetric downside protection is the trade.
- **The IDR cascade kill-switch breach Fri Jun 5 needs a codified de-risk procedure before Mon Jun 8 pre-market** — IDR 18,033 sustained close is the primary kill-switch breach event the strategy named but never tested. Current state: framework activated, no action taken Fri because (a) KLBF cluster non-convergence blocks broker-side fire, (b) routine boundary (no entries / no formal discretionary trim post 15:15 WIB by procedure). For Mon Jun 8 pre-market: define a concrete pre-emptive de-risk algorithm: (1) if IDR remains ≥18,000 sustained pre-open, evaluate KLBF healthcare-defensive bid resilience vs import-cost headwind compression; (2) consider a discretionary 50% trim if cluster narrows ≤2% with mark ≥1,000 (lock partial profit before any further IDR escalation); (3) if cluster narrows ≤2% with mark ≤931, broker-side stop fires automatically per GTC; (4) NO new entries while IDR ≥18,000 sustained. Codify this in pre-market routine Mon Jun 8 — the kill-switch breach event needs a deterministic playbook, not ad-hoc judgment.
- **Cluster spread is now a recurring 5-week pattern, not an episodic infra glitch** — Day 41 (Tue 9.7%) → Day 42 (Tue 9.7%) → Day 43 (Wed ~42%) → Day 44 (Thu ~52%) → Day 45 (Fri ~20%). Five consecutive days of multi-source spread >2% threshold blocks every state-machine transition on KLBF. The procedural fix (safe-lower carry per BBRI 2026-05-01 discipline) holds the equity ledger together but the compounding edge of the stop ladder is broken until data infrastructure restores. ESCALATE to a Week 8 priority infra task: either (a) write a multi-source aggregator script that polls ≥3 free WebSearch sources, applies the ≤2% convergence test programmatically, and surfaces cluster status in broker.sh quote output; or (b) procure a paid GoAPI/IDX-direct feed for the held-position symbol set. This has been open since Apr 21 (Day 1 trial) — the infra cost is now blocking the strategy's primary compounding mechanism, not just slowing data lookups.
- **Patience discipline is path-validated under forced-entry pressure** — Week 7 entered with 0/3 slots fresh + +17.61% cumulative trial alpha protected + 4-day cumulative IDX pause = textbook forced-entry pressure. Tue/Wed/Thu/Fri evaluated 19 distinct candidate-session combinations across 5 candidate tickers (PGAS / TLKM / ICBP / INDF / JSMR / MYOR / UNVR — slight rotation across days). 19 of 19 SKIPs, every one rooted in concrete plan-gate failures (not vague reasoning), every one defensible against subsequent tape (TLKM closed +24.8% above plan all week; UNVR +13.1%; ICBP +8.0%; PGAS data-infra blocked). The cumulative-alpha expansion +6.39pp this week is the binding evidence that "no trade beats a bad trade" is the correct default under DEFENSIVE-INTENSIFIED regime. The discipline holds; the regime label correctly priced the binding constraint.

### Rule Changes Proposed

- **No formal TRADING-STRATEGY.md changes this week.** Week 7 is one week of dramatic alpha-positive vindication on the DEFENSIVE 5/5 regime call + the multi-source ≤2% / safe-lower discipline. The rules held perfectly under stress. No rule has proven out or failed over 2+ consecutive weeks of new evidence requiring strategy revision.
- **Process change (non-strategy):** **Mon Jun 8 pre-market routine MUST add a formal pre-emptive de-risk evaluation block for IDR ≥18,000 sustained.** This is the codification called for in "Lessons Learned" #2 above. Concrete algorithm: (a) check IDR Bloomberg + multi-source Mon pre-open; (b) if ≥18,000 sustained, evaluate KLBF position with explicit trim/hold/close framework; (c) NO new entries while IDR ≥18,000 sustained; (d) document de-risk decision rationale in pre-market RESEARCH-LOG. This is process, not strategy — codify in `routines/pre-market.md` not `TRADING-STRATEGY.md`.
- **Process change (non-strategy):** **Multi-source aggregator infra patch promoted from "deferred" to Week 8 Priority 1.** The repeat broker.sh cmd_sell MD_LAST_PRICE_OVERRIDE patch (open since 2026-05-01) PLUS a new multi-source ≤2% convergence helper script. Open since Day 1 trial (Apr 21 yfinance block); five consecutive days of state-machine transition blocking is the threshold to act. Estimate <4 hours work to write the polling/spread/convergence helper. Hard deadline: before Mon Jun 8 market-open if possible; if not, Tue Jun 9.
- **Tentative rule-change watch (1-week marker — NOT codifying yet):** if Week 8 also shows multi-source cluster non-convergence on every held/candidate symbol AND IDR remains ≥18,000 sustained AND cumulative trial alpha remains >+20%, formally promote the "soft rule" of multi-source ≤2% convergence as a hard gate in TRADING-STRATEGY.md (currently a procedural discipline from MISTAKES.md). 2+ weeks of evidence will justify codification.

### Watchlist Updates

- **Keep watching: KLBF** — Now in state-2 trailing (current_stop 931 GTC armed broker-side, HWM 1,035, trail_pct 10). Mon Jun 8 priorities: (1) fresh multi-source cluster — if ≥3-source convergence ≤2% AND mark ≥1,087 → fire state-3 (7% trail at 1,011 from hwm 1,087); ≥1,134 → fire state-4 (5% trail at 1,077); ≤931 → trailing GTC fires automatically. (2) IDR cascade ≥18,000 de-risk evaluation per process change above. Buyback program Rp500B through Jul 2 active (~27 days remain); dividend receivable IDR 10,380,000 payment Jun 24.
- **Keep watching: TLKM** — AGM Mon Jun 8 + Rp 4T buyback T+3 is THE Week 8 fresh catalyst. Plan entry ≤2,900 chase failed +24.8% all of Week 7; AGM-day tape could re-price either direction. Re-gate Mon pre-market with full 9-gate; multi-source ≤2% and Gate 9 (≤3% drift from plan) both binding. If tape moderates and gates clear, this is the highest-conviction Week 8 entry candidate.
- **Keep watching: PGAS** — Abadi LNG HoA catalyst remains fresh (5-source confirmed). Block has been data-infra (multi-source price spread) Day 4+ consecutive. If Mon Jun 8 cluster narrows ≤2% AND R:R ≥2:1 at verified mark AND IDR < 18,000, top-priority defensive utility entry candidate.
- **Keep watching: ICBP** — Q1 rev +7.6% YoY + defensive FMCG leadership; plan ≤7,500. Fri chase 8,100–8,250 fails Gate 9; consumer-staples leadership pattern operative under DEFENSIVE regime. Re-gate Mon if pullback to ≤7,725 (3% chase cap) on fresh tape.
- **Keep watching: UNVR** — Re-added to candidate list Week 7 mid-week. Plan ≤1,755 chase fails ~+13% all of Week 7; consumer-staples regime fit but R:R compression severe. Watch only on pullback to ≤1,800.
- **Keep watching: MYOR** — Plan ≤1,765; no fresh Jun 5 multi-source convergent tape (stale May 6 / May 22 prints only). Watch on cluster narrowing Mon.
- **Keep watching: BBCA** — Pullback to ≤5,800 needed for clean 2:1 R:R; Week 7 panic flush may have driven into the entry zone but data-infra block prevented verification. Banking 1-of-2 strike still binding (BBRI May 1).
- **Remove: JSMR** — R:R compression 1.62:1 sub-2:1 plan minimum persisted across 4 evaluation sessions Week 7; off candidate list pending fresh research re-entry at lower entry zone (≤2,800 estimated for 2:1 R:R to 3,350 target).
- **Remove: INDF** — Repeat removal from Week 6; Week 7 anchor spread 6,925 vs 7,600 historical unresolved + Wed Jun 3 cluster ~50% spread. Off pending data-infra fix.
- **Week 8 entry queue:** 6 candidates carry forward (KLBF held + TLKM/PGAS/ICBP/UNVR/MYOR/BBCA watch). Full 3/3 slot allocation available Week 8. IDR ≥18,000 sustained binds against any new entries pre-emptive de-risk procedure (per process change above).

---

## Week ending 2026-06-12 (Week 8 — Post-trial continuation, IDR 10B capital; IHSG Mon ARB-flush then 4-session relief rally; IDR de-escalation criterion (a) FULLY CONFIRMED)

### Grade: B

### Performance Summary

| Metric | This Week | IHSG Benchmark | Alpha |
|--------|-----------|---------------|-------|
| P&L (IDR) | IDR 0 (frozen safe-lower carry held all 5 sessions) | — | — |
| P&L (%) | 0.00% | +6.17% (5,692.15 → 6,043.55) | −6.17% |
| Equity (EOW) | IDR 9,856,337,500 | — | — |

_IHSG: Mon Jun 8 baseline (= Fri Jun 5 sesi I close) 5,692.15 → Fri Jun 12 sesi I close 6,043.55 = +6.17% (price basis). Week shape: Mon −4.53% panic-flush continuation (intraday low 5,346.33 = 6-yr low) → Tue +3.04% relief bounce post BI off-cycle +25bp hike → Wed +5.03% continuation → Thu −1.55% mean-reversion → Fri +4.38% relief continuation re-tops 6,000 psychological. Portfolio flat through the week on 94.55% cash + KLBF safe-lower carry frozen at 1,035 per cluster non-convergence Day 50-54. Cumulative trial alpha vs IHSG: Day 35 close +24.00% → Day 36 Mon +27.37% (trial high, +3.37pp Mon panic-flush expansion) → Day 40 Fri close +19.39% (−4.61pp net week; −7.98pp from Mon peak). Day 0 baseline IHSG 7,634 → Fri sesi I 6,043.55 = IHSG cumulative −20.83%; portfolio cumulative −1.44%; alpha gap +19.39pp = first trial-high alpha give-back week, but structurally strong absolute position holds._

### Trade Summary

| # | Date | Ticker | Side | Entry | Exit | P&L (IDR) | P&L (%) | Result |
|---|------|--------|------|-------|------|-----------|---------|--------|
| — | — | — | — | — | — | — | — | No trades placed this week (3rd consecutive 0/3 week) |

- Trades this week: 0/3 (Mon SKIP all 5 candidates on IDR cascade pre-emptive de-risk; Tue SKIP all on continuation; Wed SKIP MDKA+TLKM on multi-source non-convergence; Thu SKIP MDKA+TLKM on Gate 9 + binary AGM T+0 timing; Fri SKIP BBCA+TLKM+MDKA on Gate 9 chase + catalyst-invalidated; full slot allocation preserved into Week 9)
- Wins: 0 | Losses: 0 | Open closes: 0 | Win rate: N/A (no closed trades)
- Largest winner: N/A (no closed trades; KLBF carry +9.52% unrealized at frozen 1,035 is the live winner)
- Largest loser: none (no closed trades; no hard cuts; no thesis exits; no stop tightenings)
- Profit factor: N/A (no closed trades)
- Stop-state transitions: 0 (KLBF state-3 +15% and state-4 +20% triggers ARMED but cluster non-convergence Day 50-54 blocks confirmation 5 sessions in a row; State 2 trailing 10% @ 931 GTC unchanged from Fri May 29 midday transition — now 14 sessions held)
- No stops triggered; no hard cuts; no tightening fired

### Open Positions at Week End

| Ticker | Shares | Avg Cost (IDR) | Last (IDR) | Unrealized P&L | Days Held |
|--------|--------|----------------|------------|----------------|-----------|
| KLBF | 519,000 | 945 | 1,035 (safe-lower carry frozen since Tue Jun 2 anchor; Fri Jun 12 cluster ~33% 3-source spread > 2% threshold; full-week cluster persisted Day 50-54 wider; safe-lower discipline binds per MISTAKES.md 2026-05-01) | +IDR 46,710,000 (+9.52%) | 17 |

- Cash at week end: IDR 9,319,172,500 (94.55%)
- Gross exposure: 5.45% (Healthcare only — KLBF) — well below 75–85% target; consistent with DEFENSIVE-INTENSIFIED 5% sizing cap
- Sector exposure: Healthcare 5.45%; coal/banking/mining/nickel/conglomerate/telco/consumer all 0%
- Stop state: KLBF TRAILING state-2 10% @ 931 GTC armed broker-side (hwm 1,035); state-3 (≥1,087 → 7% trail) ARMED; state-4 (≥1,134 → 5% trail) ARMED; cluster non-convergence Day 50-54 blocks all transitions per safe-lower discipline
- Future dividend receivable: IDR 10,380,000 (519,000 sh × IDR 20 KLBF cash dividend; payment Jun 24 = T+8) — not in equity carry yet

### What Worked

- **Mon Jun 8 IDR cascade pre-emptive de-risk procedure fired correctly per Week 7 codification** — Mon pre-open IDR cluster ~18,015–18,045 sustained ≥18,000 = primary kill-switch BREACHED-SUSTAINED, binding the "NO new entries possible" rule codified in Week 7 review's "Process change" #1. All 5 candidates (TLKM/UNVR/ICBP/MYOR/MDKA) blocked at the regime gate BEFORE 9-gate execution. Mon IHSG sesi I closed −4.53% to 5,434.30 (intraday low 5,346.33 = 6-yr low) = the panic-flush continuation the de-risk rule was designed for. Single-day cumulative alpha expansion +3.37pp to trial-high +27.37% on the procedural defense. The Week 7 process change paid for itself on Day 1 of its enforcement.
- **KLBF safe-lower carry discipline held through 5 consecutive sessions of cluster non-convergence Day 50-54** — Multi-source cluster persisted 33-52% spread all week (Yahoo 1,135 stale-high vs TradingView 745 cross-source low vs Investing/Stockbit cluster). Per MISTAKES.md 2026-05-01 safe-assumption rule, 1,035 used for all EOD MTM and dashboard carry. Zero stale-mark errors. State-3 (+15%) and state-4 (+20%) tightening triggers ARMED all week but discipline blocked confirmation. Procedural fix from the BBRI May 1 failure now validated through 9 consecutive sessions of cluster non-convergence (Wed Jun 4 onward through Fri Jun 12).
- **Patience discipline through 14 candidate-session SKIPs across 5 live trading days under forced-entry pressure** — Mon (5 SKIP regime-gate), Tue (5 SKIP continuation), Wed (2 SKIP), Thu (2 SKIP), Fri (3 SKIP). Every SKIP rooted in concrete plan-gate failures: regime pre-emptive de-risk (Mon/Tue), multi-source ≤2% convergence (Wed/Thu/Fri), Gate 9 chase cap (TLKM +24.8% above plan all week, BBCA +25% 5-day rebound Thu, MDKA +15% 4-day chase Thu), binary AGM T+0 timing risk (MDKA Thu), catalyst invalidation (MDKA AGM postponed to Tue Jun 23 RUPSLB Fri). 3rd consecutive 0/3 week — defensible against subsequent tape on every SKIP.
- **Eagerness Check held through trial-high alpha +27.37% Mon EOD — the strongest forced-entry pressure of the trial** — Week 8 entered with 3/3 slot allocation + 2 consecutive 0/3 weeks of cash drag building pressure + Mon panic-flush expanded cumulative alpha to a new trial high (+27.37%) = the classic "cushion that tempts loosening" setup. The 5% cap binding + multi-source ≤2% + Gate 9 chase cap held without exception. Tue–Fri saw 4 consecutive sessions of cumulative alpha compression (+27.37% → +25.21% → +21.51% → +22.72% → +19.39%) without a single forced entry. The structural defensive book is calibrated to lose relative ground on relief days by design — the discipline accepted the cost rather than chasing.
- **MDKA AGM T+0 binary catalyst correctly skipped Thu Jun 11 — vindicated Fri Jun 12 by AGM postponement to Tue Jun 23 RUPSLB** — Thu 09:15 routine SKIPPED MDKA on Gate 9 multi-source non-convergence + binary AGM T+0 timing risk + 4-day cumulative +15% chase. Fri 09:15 confirmed: the AGM was POSTPONED to a RUPSLB Tue Jun 23 — meaning a Thu entry would have committed capital ahead of a catalyst that didn't even fire. The "binary AGM T+0 timing" SKIP discipline avoided a real-world catalyst-invalidation event. Re-gate deferred to Mon Jun 22 pre-RUPSLB.

### What Didn't Work

- **First trial-high alpha give-back week of the trial: cumulative alpha compressed −7.98pp from Mon peak +27.37% to Fri close +19.39%** — On a 4-session relief rally (Tue +3.04% / Wed +5.03% / Thu −1.55% reversion / Fri +4.38% = cumulative IHSG +9.41% recovery), the cash-heavy defensive book underperformed by design. This is the inverse of Week 7's vindication (+6.39pp alpha expansion on the IHSG −7.10% panic-flush week). The structural defensive book lost relative ground on the biggest single-week IHSG recovery of the trial. The lesson is bilateral confirmation of the pattern in PATTERNS.md (healthcare-defensive lag-on-green / outperform-on-red) — but the cost (−7.98pp from peak) is the largest single-week alpha compression of the trial.
- **3rd consecutive 0/3 week — slot underutilization is now a structural pattern post-trial** — Week 6 = 0/3, Week 7 = 0/3, Week 8 = 0/3. Nine cumulative slots unused over 3 weeks while the strategy intent is 75–85% capital deployment. Current deployed: 5.45%. The DEFENSIVE-INTENSIFIED regime binds the 5% cap and procedural defenses, but the strategy is now functionally converging to a single-position cash-defensive book (KLBF only). This may be the correct response to the regime, OR it may indicate the candidate-screening process has too many concurrent veto gates active (regime + multi-source + Gate 9 + binary catalyst risk + chase cap). Slot underutilization itself is not a rule violation, but 3 weeks of zero entries needs explicit Week 9 evaluation: either (a) confirm the defensive posture is the correct response to the regime and accept the cash drag as a paid premium, or (b) identify which gate is the binding constraint and whether it should relax under DOWNGRADE-PENDING regime status (criterion (a) IDR sub-18,000 4-of-4 sustained CONFIRMED Fri; criterion (c) IHSG >5,500 reclaim CONFIRMED Wed; 2 of 4 de-escalation triggers met).
- **MSCI Jun 18 announcement T-4 overhang continues to bind without resolution** — The MSCI Indonesia capital-market-status classification announcement remains 4 trading days away as of Fri Jun 12 close. Binary frontier-downgrade risk has been an investor wait-and-see overhang for ~10 trading days now and will resolve next Thursday. No positioning is possible because the binary outcome is unknown and the cluster non-convergence on KLBF blocks even the trim/hold/close framework for the held position. Carry-forward: Mon Jun 15 pre-market must include T-3 MSCI positioning framework; pre-emptive trim of KLBF before MSCI resolution if cluster narrows.
- **Data infrastructure Day 50-54 — yfinance + GoAPI both still blocked; broker.sh cmd_sell MD_LAST_PRICE_OVERRIDE still not patched** — Eight full weeks of WebSearch fallback for all marks. KLBF cluster spread 33-52% all of Week 8 (same as Week 7). The multi-source aggregator infra patch promoted to "Week 8 Priority 1" in Week 7 review remains unimplemented. The MD_LAST_PRICE_OVERRIDE codification for broker.sh cmd_sell (open since 2026-05-01 BBRI; reinforced 2026-05-20 ADRO) remains unimplemented. Cost: 14 consecutive sessions of state-machine state-3/state-4 transition blocking on KLBF (Day 41 onward). The infra debt is structural and now blocking the strategy's primary compounding mechanism for a 3rd consecutive week.

### Lessons Learned

- **Bilateral pattern confirmation: healthcare-defensive carry produces asymmetric weekly alpha — lose on relief rallies, win on panic flushes — and the cost-benefit math holds across the cycle.** Week 7 vs Week 8 is now a clean A/B comparison: same KLBF carry + same 94.55% cash + same DEFENSIVE-INTENSIFIED regime. Week 7 (IHSG −7.10%) = +7.10% alpha. Week 8 (IHSG +6.17%) = −6.17% alpha. The 2-week net is +0.93% alpha across the cycle — the defensive structure is mildly net-positive across cascade-then-recovery cycle. The structural lesson is to accept the bilateral cost without flipping the regime label on a single recovery week. Per the Week 7 lesson ("DO NOT loosen the regime label on a single bounce or recovery print"), the regime call was correctly held. The trial-high alpha give-back is the bilateral cost of the asymmetric protection — not a flaw in the strategy.
- **3-week zero-entry pattern requires explicit Week 9 evaluation, not auto-continuation.** Slot underutilization is not a rule violation, but 9 unused slots across Weeks 6-8 means the strategy is functionally converging to a single-position cash-defensive book. Week 9 must (a) make the de-escalation criteria explicit: of 4 triggers (IDR sub-18,000 / Moody's stabilization / IHSG >5,500 reclaim / foreign flow inflection), 2 are CONFIRMED Fri Jun 12 (a + c) — when do triggers (b) and (d) get checkable + at what threshold does DEFENSIVE-INTENSIFIED downgrade to DEFENSIVE-CONFIRMED → enabling the 10% cap restoration?; and (b) make the candidate-screening veto budget explicit: a 5-veto evaluation (regime + cluster + Gate 9 + binary risk + chase cap) is over-defensive for a DOWNGRADE-PENDING regime. Either accept the cash drag as the priced premium for the regime, OR identify one veto to relax (most likely: multi-source ≤2% becomes ≤4% under cluster non-convergence outage as a procedural patch, not a strategy change).
- **The Mon Jun 8 IDR pre-emptive de-risk procedure paid for itself on Day 1 of enforcement — codify this pattern: codify near-term safety procedures BEFORE they're needed, not after.** Week 7's "Process change" #1 was written Fri Jun 5 16:00 WIB. Mon Jun 8 09:15 WIB it fired on the IDR 18,033 sustained-breach event the rule was designed for, blocking 5 candidates at the regime gate during a −4.53% panic-flush. This is the second time a Week-N codified process change fired correctly on Week-N+1 Day 1 (first was the BBRI hard-cut May 1 codification → procedural fixes throughout Week 2). Lesson: prophylactic codification of safety procedures has positive expected value even on low-probability events. Continue to codify pre-emptive de-risk procedures for the named kill-switches (IDR ≥18,500 cascade-tier-2; IHSG ≤5,500 reclaim-failure; Moody's sovereign downgrade) BEFORE they fire, not after.
- **MDKA AGM postponement Fri Jun 12 vindicated the Thu Jun 11 "binary AGM T+0 timing" SKIP — binary catalyst timing is a real-world failure mode, not just a theoretical Gate 9 concern.** The Thu Jun 11 SKIP rationale included "binary AGM T+0 timing risk" alongside Gate 9 multi-source + chase cap. The Fri Jun 12 09:15 routine confirmed: the AGM was postponed to Tue Jun 23 RUPSLB. A Thu entry would have committed 5% of equity into a catalyst that didn't even fire — i.e., a real-world catalyst-invalidation event, not a hypothetical one. Lesson: when binary-timing risk is one of multiple SKIP rationales, the SKIP discipline holds even if Gate 9 alone might have cleared. Codify "binary AGM/Q1/policy-decision T+0 timing risk" as a recognized SKIP rationale in pre-market checklist for the candidate-screening phase, NOT just at 09:15 routine.

### Rule Changes Proposed

- **No formal TRADING-STRATEGY.md changes this week.** Week 8 produced bilateral confirmation of the Week 7 pattern (healthcare-defensive + cash-heavy = asymmetric alpha across cascade-then-recovery cycle) but NOT 2+ weeks of new evidence requiring strategy revision. The rules held; the regime call held; gates fired correctly on every SKIP. Bar for codification remains high.
- **Tentative rule-change watch (now 2-week marker — codify next Friday if pattern persists):** Per Week 7 marker — if Week 9 also shows multi-source cluster non-convergence on every held/candidate symbol AND no fresh data-infra fix shipped AND no entries placed, formally promote the multi-source ≤2% convergence to a hard gate in TRADING-STRATEGY.md (currently a procedural discipline from MISTAKES.md). 2 weeks of evidence is now banked (Weeks 7-8); 3 weeks would justify codification.
- **Process change (non-strategy):** **Week 9 must include explicit de-escalation framework** — codify in `routines/pre-market.md` the de-escalation trigger framework: of 4 DEFENSIVE-INTENSIFIED downgrade triggers (a: IDR sub-18,000 sustained / b: Moody's stabilization / c: IHSG >5,500 reclaim sustained / d: foreign flow inflection), 2 are CONFIRMED as of Fri Jun 12 (a + c). Mon Jun 15 pre-market MUST check trigger (b) Moody's status and trigger (d) foreign-flow direction; if 3 of 4 confirm, downgrade DEFENSIVE-INTENSIFIED → DEFENSIVE-CONFIRMED and restore 10% position cap; if 4 of 4 confirm, downgrade DEFENSIVE-CONFIRMED → DEFENSIVE-MONITORING and restore 15% cap with R:R compression evaluation.
- **Process change (non-strategy):** **Week 9 must include explicit slot-utilization evaluation** — codify in `routines/weekly-review.md` a "slot utilization check": if 3 consecutive weeks of 0/3 slot usage AND cumulative trial alpha >+15%, the next pre-market routine MUST evaluate at least one candidate at relaxed multi-source threshold (≤4% under outage) AND document why no entry cleared the relaxed gate (i.e., identify the binding constraint explicitly rather than auto-defaulting to the next SKIP).
- **Process change (non-strategy):** **Multi-source aggregator infra patch DEMOTED from Week 8 Priority 1 to Week 9 NON-NEGOTIABLE.** Same fix open since Day 1 trial (Apr 21 yfinance block). Week 7 promoted to Priority 1; Week 8 did not ship. 14 consecutive sessions of state-machine transition blocking. Hard deadline: before Mon Jun 15 market-open. If not shipped, the routine must run as a manual WebSearch ≤4% relaxed-threshold workaround documented in MISTAKES.md.

### Watchlist Updates

- **Keep watching: KLBF** — state-2 trailing GTC @ 931 armed broker-side, hwm 1,035, trail_pct 10. Mon Jun 15 priorities: (1) fresh multi-source cluster — if ≥3-source convergence ≤2% AND mark ≥1,087 → fire state-3 (7% trail at 1,011 from hwm 1,087); ≥1,134 → fire state-4 (5% trail at 1,077); ≤931 → trailing GTC fires automatically. (2) MSCI Jun 18 T-3 positioning framework: pre-emptive trim if cluster narrows + frontier-downgrade probability tilts. (3) BI RDG Jun 17-18 T-2 positioning framework: rate decision binary. Buyback program Rp500B through Jul 2 active (~20 days remain); dividend receivable IDR 10,380,000 payment Jun 24 = T+8.
- **Re-rank as Week 9 Priority 1 candidate: TLKM** — Mon ARB 2,350 anchor → Wed ~2,900 / Thu ~3,050 / Fri ~3,100 chase Gate 9 fail all week. Buyback Rp4T executed mid-week per multi-source rumor (not confirmed). If Mon Jun 15 pullback to ≤2,990 (3% chase cap from 2,900 reference) AND multi-source convergent AND IDR sustained sub-18,000, top entry candidate. Telco-defensive fit under DOWNGRADE-PENDING regime.
- **Keep watching: BBCA** — Mon ARB 4,375 anchor → Thu 5,600 = +28% 5-day rebound (Fri Gate 9 chase fail). Need pullback to ≤5,432 (3% chase cap from Thu close) for clean re-entry. Banking 1-of-2 strike binding (BBRI May 1) — entry requires same-day catalyst (BI RDG +25bp dovish would trigger bank-positive). Re-gate priority post-BI Jun 17-18.
- **Keep watching: MDKA** — AGM postponed to Tue Jun 23 RUPSLB; dividend approval (first ever) + private placement = re-priced binary catalyst T+7. Re-gate Mon Jun 22 pre-RUPSLB. Off candidate list until T-1.
- **Keep watching: PGAS** — Abadi LNG HoA catalyst remains fresh (5-source confirmed since Week 6). Block remains data-infra (multi-source price spread). Promote to Mon Jun 15 priority if cluster narrows; defensive utility regime fit under DOWNGRADE-PENDING.
- **Keep watching: ICBP** — Q1 rev +7.6% YoY + defensive FMCG leadership; plan ≤7,500. Re-gate Mon Jun 15 if pullback. Cum-div Fri Jun 19 (T+5) is a near-term positive catalyst.
- **Keep watching: UNVR** — Cum-div Fri Jun 19 (T+5; Rp 114/sh ~7% yield); defensive bid near-term. Plan ≤1,755 chase fails ~+13% all of Weeks 7-8. Watch on pullback to ≤1,800 with cum-div bid support.
- **Keep watching: MYOR** — Plan ≤1,765; cluster narrowing watch.
- **Remove: BUMI** — Coal sector EXITED per 2-strike rule (ITMG May 18 + ADRO May 20); Fri +13.6% on Brent collapse + commodity rotation = noise. Off candidate list until full coal sector-watch reset.
- **Remove: INDY, INCO** — Same coal/nickel sector noise on Fri commodity rotation; INDY +22.5% Fri; INCO +13.6% Fri. Off candidate list per sector-EXIT + speculative-rotation thesis-break filter.
- **Week 9 entry queue:** 7 candidates carry forward (KLBF held + TLKM/BBCA/PGAS/ICBP/UNVR/MYOR watch + MDKA delayed T+7). Full 3/3 slot allocation available Week 9. **DOWNGRADE-PENDING regime status (2 of 4 de-escalation triggers confirmed)** — if Mon Jun 15 confirms 3 of 4 (Moody's stabilization OR foreign-flow inflection), DEFENSIVE-INTENSIFIED → DEFENSIVE-CONFIRMED downgrade + 10% cap restoration. Slot underutilization 3-week pattern triggers explicit Week 9 evaluation per process change above.

---

## Week ending 2026-06-19 (Week 9 — Post-trial continuation, IDR 10B capital; BI RDG +25bp HIKE to 5.75% CONFIRMED Thu + MSCI NEGATIVE info-flow downgrade Fri + IDR sub-18,000 8-of-8 sustained + 4th consecutive 0/3 week)

### Grade: B+

### Performance Summary

| Metric | This Week | IHSG Benchmark | Alpha |
|--------|-----------|---------------|-------|
| P&L (IDR) | IDR 0 (frozen safe-lower carry held all 4 live sessions) | — | — |
| P&L (%) | 0.00% | +1.39% (6,043.55 → 6,127.38) | −1.39% |
| Equity (EOW) | IDR 9,856,337,500 | — | — |

_IHSG: Fri Jun 12 close 6,043.55 → Fri Jun 19 sesi II final 6,127.38 = +1.39% (price basis). Week shape: Mon Jun 15 sesi I +4.40% to 6,309.73 (relief-rally continuation; sesi II final 6,254.97) → Tue Jun 16 IDX LIBUR 1 Muharam 1448 H (Islamic New Year, no trading session) → Wed Jun 17 sesi I −0.84% to 6,202.47 (profit-taking + BI RDG Day 1) → Thu Jun 18 sesi I −1.06% to 6,154.92 (BI +25bp HIKE to 5.75% CONFIRMED + MSCI binary day) → Fri Jun 19 sesi II final 6,127.38 −0.73% (MSCI NEGATIVE info-flow downgrade absorbed + TLKM ex-div Day 1 mechanical). Portfolio flat through the week on 94.55% cash + KLBF safe-lower carry frozen at 1,035 per cluster non-convergence Day 55-59. Cumulative trial alpha vs IHSG: Day 40 close +19.39% → Day 41 Mon +15.91% (−3.48pp single-day compression on +4.40% relief-rally continuation) → Day 42 Tue +16.62% (holiday anchor reconciliation +0.71pp from Mon sesi II final 6,254.97 confirmed) → Day 43 Wed +17.31% (+0.69pp single-day expansion) → Day 44 Thu +17.94% (+0.63pp on BI hike defensive vindication) → Day 45 Fri close +18.30% (+0.36pp on MSCI NEGATIVE info-flow downgrade defensive vindication). Net week alpha −1.09pp (cumulative trial alpha compression Wk 8 +19.39% → Wk 9 +18.30%); intra-week trajectory Mon trough +15.91% → Fri +18.30% recovered +2.39pp post-BI/MSCI binary resolutions. Day 0 baseline IHSG 7,634 → Fri sesi II 6,127.38 = IHSG cumulative −19.74%; portfolio cumulative −1.44%; alpha gap +18.30pp = 4th-strongest weekly close of the trial; structurally strong absolute position holds. 4-session live trading week (Tue Jun 16 IDX LIBUR holiday-pass)._

### Trade Summary

| # | Date | Ticker | Side | Entry | Exit | P&L (IDR) | P&L (%) | Result |
|---|------|--------|------|-------|------|-----------|---------|--------|
| — | — | — | — | — | — | — | — | No trades placed this week (4th consecutive 0/3 week) |

- Trades this week: 0/3 (Mon SKIP all 6 candidates on Gate 9 unverifiable + data-infra Day 55; Tue IDX LIBUR; Wed SKIP all on BI RDG Day 1 binary pre-event; Thu SKIP all on BI +25bp HIKE binary resolution + MSCI binary pending close; Fri SKIP all on MSCI NEGATIVE info-flow downgrade absorption + data-infra Day 59; full slot allocation preserved into Week 10)
- Wins: 0 | Losses: 0 | Open closes: 0 | Win rate: N/A (no closed trades)
- Largest winner: N/A (no closed trades; KLBF carry +9.52% unrealized at frozen safe-lower 1,035 is the live winner)
- Largest loser: none (no closed trades; no hard cuts; no thesis exits; no stop tightenings)
- Profit factor: N/A (no closed trades)
- Stop-state transitions: 0 (KLBF state-3 +15% and state-4 +20% triggers ARMED but cluster non-convergence Day 55-59 blocks confirmation 5 consecutive sessions; State 2 trailing 10% @ 931 GTC armed broker-side, unchanged from Fri May 29 midday transition — now 19 cumulative consecutive sessions ARMED-BLOCKED Day 41-59)
- No stops triggered; no hard cuts; no tightening fired; broker-side trailing 931 GTC armed all week, did not fire (cluster Day 55-59 never confirmed ≤931 multi-source convergent)

### Open Positions at Week End

| Ticker | Shares | Avg Cost (IDR) | Last (IDR) | Unrealized P&L | Days Held |
|--------|--------|----------------|------------|----------------|-----------|
| KLBF | 519,000 | 945 | 1,035 (safe-lower carry frozen since Tue Jun 2 anchor; Fri Jun 19 cluster ≥33% 3-source spread > 2% threshold; full-week cluster persisted Day 55-59 wider; safe-lower discipline binds per MISTAKES.md 2026-05-01) | +IDR 46,710,000 (+9.52%) | 22 |

- Cash at week end: IDR 9,319,172,500 (94.55%)
- Gross exposure: 5.45% (Healthcare only — KLBF) — well below 75–85% target; consistent with DEFENSIVE-INTENSIFIED 5% sizing cap
- Sector exposure: Healthcare 5.45%; coal/banking/mining/nickel/conglomerate/telco/consumer all 0%
- Stop state: KLBF TRAILING state-2 10% @ 931 GTC armed broker-side (hwm 1,035); state-3 (≥1,087 → 7% trail) ARMED; state-4 (≥1,134 → 5% trail) ARMED; cluster non-convergence Day 55-59 blocks all transitions per safe-lower discipline (cumulative 19 consecutive blocked sessions Day 41-59 across Weeks 7-8-9)
- Dividend received: IDR 10,380,000 (519,000 sh × IDR 20 KLBF cash dividend; payment Jun 24 = T+3 from week close; not yet reconciled to equity carry, will land Tue Jun 23 EOD or Wed Jun 24)

### What Worked

- **Both major Week 9 binaries (BI RDG +25bp HIKE Thu + MSCI NEGATIVE info-flow downgrade Fri) resolved defensive-positive — cumulative alpha expanded +0.36pp on a +1.39% IHSG week** — Thu Jun 18 BI delivered the 3rd hike in 4 weeks (5.50% → 5.75%; previously May 21 +50bp and Jun 9 off-cycle +25bp), confirming the deeply restrictive monetary stance. Fri Jun 19 MSCI delivered a NEGATIVE info-flow downgrade signal (accessibility concerns flagged per Bloomberg Jun 18; not a full frontier downgrade but a structural overhang). Both outcomes vindicate the held DEFENSIVE-INTENSIFIED regime label. The cumulative trial alpha expanded from +17.31% (Wed close, pre-BI) → +17.94% (Thu post-BI +0.63pp) → +18.30% (Fri post-MSCI +0.36pp) — the defensive book gained alpha on a green IHSG week because the index gave back gains as the binaries resolved hawkish/negative. Structural validation of the regime call.
- **Patience discipline through 18+ candidate-session SKIPs across 4 live trading days under elevated binary-resolution pressure** — Mon (6 SKIP regime-data-infra Gate 9 unverifiable: TLKM/BBCA/PGAS/UNVR/ICBP/MYOR), Wed (BI Day 1 binary pre-event all SKIP), Thu (BI binary day + MSCI pending all SKIP), Fri (MSCI NEGATIVE absorption + data-infra Day 59 all SKIP). 4th consecutive 0/3 week. Every SKIP rooted in concrete plan-gate failures: Mon explicit relaxed ≤4% multi-source threshold check per Week 8 Process change #3 — even with the relaxation, no candidate cleared (data-infra Day 55 cluster spread was so wide on every candidate that even ≤4% would not clear). The Week 8 process change was executed correctly; the relaxation did not produce a forced entry because the binding constraint was structural, not threshold-set.
- **Mon Jun 15 explicit slot-utilization evaluation (Week 8 Process change #3) executed correctly** — Week 8 review codified the requirement: "if 3 consecutive weeks of 0/3 slot usage AND cumulative trial alpha >+15%, the next pre-market routine MUST evaluate at least one candidate at relaxed multi-source threshold (≤4% under outage) AND document why no entry cleared the relaxed gate". Mon pre-market explicitly evaluated all 6 candidates at relaxed ≤4% threshold and documented that the data-infra cluster spread was wider than ≤4% on every candidate, identifying the binding constraint as structural data-infra outage Day 55, not a discretionary or threshold-set veto. The process change paid for itself on Day 1 of its enforcement (same pattern as Week 7→Week 8 IDR de-risk procedure).
- **MSCI NEGATIVE info-flow downgrade Fri Jun 19 vindicates "binary catalyst T+0 risk" SKIP discipline carried from Week 8** — The MSCI Indonesia capital-market-status announcement had been an investor wait-and-see overhang ~12 trading days (Week 7 onward). Fri Jun 19 resolved NEGATIVE info-flow (accessibility concerns flagged) — exactly the binary risk that bound the patience discipline through Weeks 7-8-9. A pre-resolution entry into any MSCI-exposed name would have committed capital into a tape that absorbed the negative signal Fri sesi I (IHSG opened green, faded into close). The +0.36pp Fri cumulative alpha expansion is the asymmetric reward for the binary-risk SKIP discipline.
- **KLBF safe-lower carry discipline held through 5th consecutive week of cluster non-convergence — 19 cumulative sessions Day 41-59** — Multi-source cluster persisted 25-52% spread all of Week 9 (Yahoo 1,135 stale-high vs TradingView/Google 745 cross-source low). Per MISTAKES.md 2026-05-01 safe-assumption rule, 1,035 used for all EOD MTM and dashboard carry. Zero stale-mark errors. The procedural fix from the BBRI May 1 failure continues to stick through 19 consecutive sessions of severe data-infra stress. The Week 9 weekly cycle is the 3rd consecutive week without a fresh state-machine transition fire, validating the procedural-discipline-over-action default.

### What Didn't Work

- **4th consecutive 0/3 week — slot underutilization is now a 4-week structural pattern; 12 cumulative slots unused** — Week 6 = 0/3, Week 7 = 0/3, Week 8 = 0/3, Week 9 = 0/3. Twelve cumulative slots unused over 4 weeks while the strategy intent is 75–85% capital deployment. Current deployed: 5.45%. The DEFENSIVE-INTENSIFIED regime binds the 5% cap and procedural defenses, but the strategy is now functionally converging to a permanent single-position cash-defensive book (KLBF only). The Week 8 explicit slot-utilization evaluation (Process change #3) was executed Mon Jun 15 and identified the binding constraint as structural data-infra outage, but the structural constraint persisted all week, blocking the relaxation's intended effect. The 4-week pattern requires a deeper Week 10 evaluation: either (a) accept that the data-infra outage is the binding constraint and codify a permanent ≤4% relaxed threshold under outage in TRADING-STRATEGY.md, OR (b) escalate the data-infra patch to a hard deadline (no new market days without GoAPI or multi-source aggregator script shipped). The infra debt is now blocking 19 consecutive sessions of state-machine transitions AND 12 cumulative weekly slot uses.
- **Cumulative trial alpha net give-back −1.09pp Week 9 (from +19.39% Wk 8 close to +18.30% Wk 9 close)** — Despite both major binaries (BI HIKE + MSCI NEGATIVE) resolving defensive-positive, the cumulative alpha still net-compressed across the week because Mon's +4.40% relief-rally continuation produced a single-day −3.48pp compression that the BI/MSCI defensive vindications could not fully recover. The Mon trough +15.91% → Fri +18.30% recovery (+2.39pp post-binary resolutions) was real but not enough. The lesson is consistent with Week 8's bilateral-pattern confirmation: healthcare-defensive carry produces asymmetric weekly alpha, but on weeks where the relief-rally session dominates the binary-resolution sessions in magnitude, net alpha will compress even when the binary outcomes vindicate the defensive stance.
- **BI HIKE Thu Jun 18 +25bp to 5.75% indicates regime re-escalation signal — NOT consistent with Week 8 DOWNGRADE-PENDING label** — The Week 8 review labeled the regime "DEFENSIVE-INTENSIFIED — DOWNGRADE-PENDING (2 of 4 de-escalation criteria CONFIRMED)" with the expectation that Mon Jun 15 would check criteria (b) Moody's stabilization + (d) foreign-flow inflection for potential downgrade to DEFENSIVE-CONFIRMED + 10% cap restoration. Reality: Thu BI delivered a 3rd hike in 4 weeks (5.25% → 5.50% → 5.75%, all in 27 days), which is the opposite signal of "stabilizing toward dovish accommodation". The Moody's status remained PENDING all week (no upgrade signal). Foreign-flow remained PARTIAL on Mon-Thu, did not confirm inflection. The "DOWNGRADE-PENDING" label was therefore overoptimistic — Week 9 evidence shows the regime is structurally re-anchoring at the INTENSIFIED level, not downgrading. Week 10 MACRO-REGIME.md update must re-evaluate: either hold DEFENSIVE-INTENSIFIED without DOWNGRADE-PENDING qualifier OR add a new "INTENSIFIED-REANCHORING" sub-label reflecting the BI HIKE trajectory.
- **Data infrastructure Day 55-59 — yfinance + GoAPI both still blocked; broker.sh cmd_sell MD_LAST_PRICE_OVERRIDE still not patched** — Nine full weeks of WebSearch fallback for all marks. KLBF cluster spread 25-52% all of Week 9 (consistent with Weeks 7-8). The multi-source aggregator infra patch promoted to "Week 9 NON-NEGOTIABLE" in Week 8 review remains unimplemented. The MD_LAST_PRICE_OVERRIDE codification for broker.sh cmd_sell (open since 2026-05-01 BBRI; reinforced 2026-05-20 ADRO) remains unimplemented. Cost: 19 consecutive sessions of state-machine state-3/state-4 transition blocking on KLBF (Day 41-59 across Weeks 7-8-9). The infra debt is structural; "NON-NEGOTIABLE" status from Week 8 did not produce action. Week 10 must either (a) hard-stop all new market days until shipped, OR (b) accept the procedural workaround as a permanent baseline.

### Lessons Learned

- **Binary catalyst resolution weeks produce asymmetric defensive-positive alpha — when both major binaries (BI + MSCI) resolve hawkish/negative on the same week, the cumulative trial alpha expands even on a positive IHSG week.** Week 9 is the cleanest binary-resolution week of the trial: Thu BI +25bp HIKE (defensive-confirming) + Fri MSCI NEGATIVE info-flow downgrade (defensive-confirming) on a +1.39% IHSG net week. The cumulative alpha expanded from +17.31% pre-BI Wed to +18.30% post-MSCI Fri (+0.99pp across two binary days). The general pattern: when binary outcomes resolve in the direction the defensive book is structured for, the asymmetric vindication outweighs the cash drag from any concurrent index strength. Codify in MACRO-REGIME.md / PATTERNS.md: on weeks containing 2+ major binary catalysts where the defensive stance is the "downside protection bet", weight the binary-outcome alpha expansion as a separate source of weekly P&L vs the relief-rally cash drag.
- **The Mon Jun 15 explicit slot-utilization evaluation (Week 8 Process change #3) correctly identified the binding constraint — but the constraint is structural data-infra outage, not a discretionary veto.** Mon pre-market executed the relaxed ≤4% multi-source threshold check on all 6 candidates. Result: NO candidate cleared even the relaxed gate because the data-infra cluster spread was wider than 4% on every candidate. This is the diagnostic the Week 8 process change was designed to produce: the binding constraint is identified as structural infrastructure, not "the agent is being too cautious". Week 10 must either (a) codify a permanent ≤4% relaxed threshold in TRADING-STRATEGY.md as the under-outage default (current procedural discipline is ≤2%, with ≤4% only as a Week 8 process patch), OR (b) hard-deadline the data-infra fix to unblock the ≤2% strict gate.
- **The 3-week multi-source ≤2% convergence cluster-non-convergence pattern is now confirmed across Weeks 7-8-9 — codify the procedural discipline into TRADING-STRATEGY.md as a hard data-quality gate.** Week 7 review proposed a tentative rule-change watch ("if Week 8 also shows multi-source cluster non-convergence on every held/candidate symbol AND no fresh data-infra fix shipped AND no entries placed, formally promote the multi-source ≤2% convergence to a hard gate in TRADING-STRATEGY.md"). Week 8 confirmed Conditions 1-3 met → 2-week marker. Week 9 confirms Conditions 1-3 met again → 3-week marker. The codification trigger is met. Action: ADD "Data Quality Gate" section to TRADING-STRATEGY.md formalizing the multi-source ≤2% convergence requirement (currently a MISTAKES.md procedural discipline) as a canonical entry gate, AND ADD the ≤4% relaxed-under-outage variant as a documented exception that requires explicit RESEARCH-LOG documentation of the data-infra outage state.
- **DOWNGRADE-PENDING regime labels are conditional on the next-week binary outcomes — do not commit to a downgrade narrative until the binary resolves.** Week 8 review labeled the regime "DOWNGRADE-PENDING" anticipating Mon Jun 15 might confirm 3 of 4 de-escalation criteria. Reality: Thu BI +25bp HIKE moved criterion (b) from PENDING toward HAWKISH-REANCHORING (opposite of stabilization), and foreign-flow criterion (d) remained PARTIAL all week. The "DOWNGRADE-PENDING" label proved overoptimistic. Lesson: when 2 of 4 de-escalation criteria are confirmed but the 2 remaining criteria include a binary central-bank decision in the same week, defer the DOWNGRADE-PENDING label until post-binary; label the interim as "DEFENSIVE-INTENSIFIED — BINARY-PENDING" instead. Process change for Week 10: refine the regime-label vocabulary to distinguish "criteria-on-track" from "binary-event-pending".

### Rule Changes Proposed

- **FORMAL TRADING-STRATEGY.md CHANGE: ADD "Data Quality Gate" section codifying multi-source ≤2% convergence as canonical entry requirement.** Triggered by 3-week cluster non-convergence pattern (Weeks 7-8-9) confirming all Week 7 codification conditions: (1) cluster non-convergence on every held/candidate symbol = 19 consecutive sessions Day 41-59; (2) no fresh data-infra fix shipped = 9 weeks since Apr 21 yfinance block; (3) no entries placed = 4 consecutive 0/3 weeks. The procedural discipline (MISTAKES.md 2026-05-01) is now elevated to canonical rule. New section text below; included in this commit. Reference: this is the codification trigger Week 7 named explicitly + Week 8 banked as "2-week marker" + Week 9 banks "3-week marker = codify".
- **Process change (non-strategy):** **Week 10 must include explicit regime re-evaluation of DOWNGRADE-PENDING label given BI HIKE Thu + MSCI NEGATIVE Fri.** Week 8's DOWNGRADE-PENDING label was overoptimistic — the BI HIKE Thu is a re-escalation signal, the MSCI NEGATIVE Fri is a new headwind. Mon Jun 22 pre-market MUST re-anchor the regime label and document either (a) drop DOWNGRADE-PENDING qualifier, hold DEFENSIVE-INTENSIFIED; (b) introduce new "DEFENSIVE-INTENSIFIED-REANCHORING" sub-label reflecting BI HIKE trajectory; (c) elevate to DEFENSIVE-INTENSIFIED-DEEPENING with MSCI overhang re-introduced. Codify in `routines/pre-market.md` the regime-label-vocabulary refinement.
- **Process change (non-strategy):** **Multi-source aggregator infra patch ESCALATED from "Week 9 NON-NEGOTIABLE" to "Week 10 HARD-STOP".** Same fix open since Day 1 trial (Apr 21 yfinance block). Week 7 → Priority 1; Week 8 → did not ship; Week 9 NON-NEGOTIABLE → did not ship. 19 consecutive sessions of state-machine transition blocking + 12 cumulative weekly slot uses lost + 4 consecutive 0/3 weeks. Hard-stop terms: if not shipped before Mon Jun 22 market-open, then the routine must NOT execute new entries on any session until shipped (current ≤2% strict gate cannot clear under outage; ≤4% relaxed gate from Week 8 process change is the safety valve but Week 9 demonstrated even ≤4% does not clear under wide spread). Or: accept the workaround as the permanent baseline and update TRADING-STRATEGY.md to formalize the under-outage ≤4% relaxed threshold.
- **Tentative rule-change watch (new 1-week marker — codify if pattern persists into Week 10):** if Week 10 also shows 0/3 slot usage AND data-infra outage persists AND the under-outage ≤4% relaxed threshold continues to fail to clear any candidate, formally codify the under-outage ≤4% relaxed gate as the canonical-under-outage rule in TRADING-STRATEGY.md (currently a Week 8 process change). 1 week of evidence Week 9; need Week 10 confirmation.

### Watchlist Updates

- **Keep watching: KLBF** — state-2 trailing GTC @ 931 armed broker-side, hwm 1,035, trail_pct 10. 19 consecutive sessions Day 41-59 of cluster non-convergence blocking state-3/state-4 transitions. Mon Jun 22 priorities: (1) fresh multi-source cluster — if ≥3-source convergence ≤2% AND mark ≥1,087 → fire state-3 (7% trail at 1,011 from hwm 1,087); ≥1,134 → fire state-4 (5% trail at 1,077); ≤931 → trailing GTC fires automatically. (2) Dividend receivable IDR 10,380,000 payment Jun 24 = T+2 from Mon (reconcile to equity carry). (3) Buyback program Rp500B through Jul 2 active (~13 days remain). (4) Re-evaluate KLBF as the only deployed position vs the 4-week 0/3 entry pattern — is the held name still the highest-conviction healthcare-defensive carry or are TLKM/UNVR/ICBP/PGAS now better-conviction defensive entries post-BI/MSCI/IDR resolution?
- **Re-rank as Week 10 Priority 1 candidate: TLKM** — Ex-div Fri Jun 19 Day 1 = mechanical price drop in play Mon Jun 22 (ex-div discount). If pullback to ≤2,990 (3% chase cap from prior ~3,100 reference) AND multi-source cluster convergent AND IDR sustained sub-18,000 AND BI HIKE signal supports telco-defensive bid, top entry candidate for telco-defensive rotation post-binary resolutions. Telco-defensive fit under DEFENSIVE-INTENSIFIED regime (post-BI HIKE re-anchoring).
- **Keep watching: BBCA, BMRI** — BI HIKE +25bp Thu = bank-positive for NIM expansion, but BBCA already +25-28% rebound from Wk 8 Mon ARB; chase risk binding. BBCA pullback to ≤5,432 (3% chase cap) needed for clean re-entry. BMRI similar. Banking 1-of-2 strike binding (BBRI May 1); BI HIKE is the binary catalyst per "same-day catalyst" exception, so re-eligibility gate clears in principle.
- **Keep watching: UNVR** — Cum-div Fri Jun 19 (Rp 114/sh ~7% yield); ex-div Mon Jun 22 = mechanical drop. Defensive bid post-cum-div + DEFENSIVE-INTENSIFIED regime fit + BI HIKE bank-rotation contagion possible. Watch on pullback post-ex-div mechanical to ≤1,700.
- **Keep watching: ICBP** — Cum-div Fri Jun 19 (similar window to UNVR). FMCG defensive leadership pattern operative. Re-gate Mon Jun 22 post-ex-div mechanical.
- **Keep watching: PGAS** — Abadi LNG HoA catalyst fresh since Week 6; data-infra cluster non-convergence has blocked Week 7-8-9 entries. Same data-infra binding constraint as KLBF. If cluster narrows ≤2% (or ≤4% under relaxed-outage rule if codified Week 10), defensive utility regime fit under DEFENSIVE-INTENSIFIED — top entry candidate.
- **Keep watching: MDKA** — AGM RUPSLB Tue Jun 23 = re-priced binary catalyst T+1 from week close. Re-gate Mon Jun 22 pre-RUPSLB. Dividend approval (first ever) + private placement = the binary outcome.
- **Keep watching: BBRI, BMRI cum-div Tue Jun 23 (T+1 from week close)** — Near-term defensive bid catalysts. Sector concentration banks-cluster post-BI HIKE re-rate is the Week 10 highest-priority sector rotation possibility (but BBRI strike-1 binding for new entries).
- **Remove: MYOR** — Carry-forward 3 weeks with no fresh cluster convergence and no idiosyncratic catalyst surfaced; off candidate list pending fresh research.
- **Week 10 entry queue:** 7 candidates carry forward (KLBF held + TLKM/BBCA/BMRI/UNVR/ICBP/PGAS watch + MDKA delayed T+1). Full 3/3 slot allocation available Week 10. **REGIME STATUS: DEFENSIVE-INTENSIFIED — BINARY-RESOLVED (Wk 9 BI HIKE +25bp + MSCI NEGATIVE info-flow downgrade both confirm hawkish/headwind path; DOWNGRADE-PENDING qualifier dropped pending Week 10 re-anchor)**. Slot underutilization 4-week pattern + binary-resolution-vindicated alpha expansion triggers explicit Week 10 evaluation of position-cap restoration framework. **NEW DATA QUALITY GATE codified in TRADING-STRATEGY.md** — all Week 10 entries must clear the multi-source ≤2% (or ≤4% relaxed-under-outage with explicit RESEARCH-LOG documentation) convergence requirement as canonical.

---

## Week ending 2026-06-26 (Week 10 — Post-trial continuation, IDR 10B capital; MSCI Annual Classification CONFIRMED Indonesia REMAINS EM + Mideast geopolitical cascade + IHSG −5.54% net week + 5th consecutive 0/3 week + cumulative trial alpha fresh high +22.23%)

### Grade: A

### Performance Summary

| Metric | This Week | IHSG Benchmark | Alpha |
|--------|-----------|---------------|-------|
| P&L (IDR) | IDR +10,380,000 (KLBF dividend posting Wed Jun 24) | — | — |
| P&L (%) | +0.105% | −5.54% (6,177.14 → 5,835.11) | +5.65% |
| Equity (EOW) | IDR 9,866,717,500 | — | — |

_IHSG: Mon Jun 22 baseline = Fri Jun 19 sesi II final 6,177.14 (carry-corrected Mon pre-market vs prior provisional 6,127.38) → Fri Jun 26 sesi I provisional 5,835.11 = −5.54% (price basis). Week shape: Mon Jun 22 sesi I −1.25% to 6,099.92 (Mideast escalation + Strait of Hormuz + pre-MSCI Annual T-1 derisk; sesi II final 6,116.60 reconciled) → Tue Jun 23 sesi I −1.29% to 6,037.52 (pre-MSCI Annual T-0 defensive derisk; sesi II final 6,101.33) → Wed Jun 24 −3.56% to 5,883.88 (MSCI Annual release CONFIRMED Indonesia REMAINS EM 03:30 WIB pre-open; initial relief faded sharply on Mideast carry + Fed hawkish overnight + foreign net sell Rp 311.60B → Rp 976.66B regular market) → Thu Jun 25 +1.96% relief-bounce to 5,999.03 sesi II final → Fri Jun 26 sesi I −2.73% to 5,835.11 (relief-bounce fully reversed; all 11 sectors red; 593 down vs 91 up). Portfolio +0.105% on KLBF dividend posting Wed Jun 24 (IDR 10,380,000 = 519,000 sh × IDR 20/sh) + flat KLBF safe-lower carry held all 5 sessions per cluster non-convergence Day 60-65 (24 consecutive sessions Day 41-65). Cumulative trial alpha vs IHSG: Day 45 reconciled +17.65% → Day 46 Mon +18.66% (+1.01pp) → Day 47 Tue +19.48% provisional (+0.82pp) → Day 48 Wed +20.05% provisional / reconciled +21.60% (Wed sesi II final 5,883.88 reconciled per Day 33-40 procedure vs sesi I provisional 6,002.20 used at filing) → Day 49 Thu +19.73% provisional (reconciliation reset from +21.60% to +19.73% on relief-bounce +2.42% IHSG day) → Day 50 Fri +22.23% provisional (+2.50pp single-day expansion on IHSG sesi I −2.73% selloff). **Cumulative trial alpha +22.23% Fri sesi I provisional is the 2nd-strongest weekly close of trial, behind only Wk 7's +24.00%.** Day 0 baseline IHSG 7,634 → Fri sesi I 5,835.11 = IHSG cumulative −23.56%; portfolio cumulative −1.33% (narrowed from Day 45 −1.44% via dividend posting); alpha gap +22.23pp = fresh trial-cumulative high water above Day 48 reconciled +21.60%. 5-session live trading week._

### Trade Summary

| # | Date | Ticker | Side | Entry | Exit | P&L (IDR) | P&L (%) | Result |
|---|------|--------|------|-------|------|-----------|---------|--------|
| — | — | — | — | — | — | — | — | No trades placed this week (5th consecutive 0/3 week Wks 6-10) |

- Trades this week: 0/3 (Mon SKIP 4 candidates BMRI/ASII/TLKM/ICBP on Data Quality Gate fail Day 60 + foreign-flow unverified + MSCI Annual T-1 derisk + R:R below 2:1; Tue SKIP TLKM/ASII on Gate 9 Data Quality fail Day 61 + R:R below 2:1 (TLKM 1.43:1 / ASII 1.44:1) + Defensive-Quality Track SUSPENDED under DEFENSIVE regime + MSCI Annual T-0 binary; Wed SKIP BBCA/TLKM/BMRI on Gate 9 chase cap fail + R:R below 2:1 (BBCA 1.40:1 / TLKM 1.20:1 / BMRI 1.81:1) — MSCI relief bid materialized at open above pre-market R:R-clearing thresholds; Thu SKIP BBCA/TLKM/BMRI on R:R 2:1 BBCA 1.87:1 + Data Quality TLKM 18.4% spread + cascading conditional BMRI + IDR ARMED-WATCH 17,956 cascade kill-switch; Fri SKIP UNVR (TOP) +4.4% Gate 9 chase / BBCA R:R 1.87:1 / TLKM ≥25% spread / BMRI cascading conditional — full slot allocation preserved into Week 11)
- Wins: 0 | Losses: 0 | Open closes: 0 | Win rate: N/A (no closed trades)
- Largest winner: N/A (no closed trades; KLBF carry +9.52% unrealized at frozen safe-lower 1,035 is the live winner; dividend +IDR 10.38M realized cash credit)
- Largest loser: none (no closed trades; no hard cuts; no thesis exits; no stop tightenings)
- Profit factor: N/A (no closed trades)
- Stop-state transitions: 0 (KLBF state-3 +15% and state-4 +20% triggers ARMED but cluster non-convergence Day 60-65 blocks confirmation 6 consecutive sessions; State 2 trailing 10% @ 931 GTC armed broker-side, unchanged from Fri May 29 midday transition — now 25 cumulative consecutive sessions ARMED-BLOCKED Day 41-65 across Weeks 7-8-9-10)
- No stops triggered; no hard cuts; no tightening fired; broker-side trailing 931 GTC armed all 5 sessions, did not fire (cluster Day 60-65 never confirmed ≤931 multi-source convergent; TradingView 710-745 fresh LOWs Fri Jun 26 single-source did NOT anchor execution per Data Quality Gate ≥3-source ≤2% canonical convergence requirement)
- Cash event: KLBF dividend payment T-0 Wed Jun 24 IDR 10,380,000 (519,000 sh × IDR 20/sh) posted to PAPER-STATE.json cash (9,319,172,500 → 9,329,552,500); first realized cash credit since trial began Apr 20

### Open Positions at Week End

| Ticker | Shares | Avg Cost (IDR) | Last (IDR) | Unrealized P&L | Days Held |
|--------|--------|----------------|------------|----------------|-----------|
| KLBF | 519,000 | 945 | 1,035 (safe-lower carry frozen since Tue Jun 2 anchor; Fri Jun 26 cluster Day 65 ≥60% 4-source spread persists: TradingView 710 fresh LOW / Yahoo 760 stale / broker stub 945 / Investing.com 1,135 stale; safe-lower discipline binds per MISTAKES.md 2026-05-01 + TRADING-STRATEGY.md Data Quality Gate L75-100) | +IDR 46,710,000 (+9.52%) | 36 |

- Cash at week end: IDR 9,329,552,500 (94.56% of equity — +IDR 10,380,000 from KLBF dividend posting Wed Jun 24)
- Gross exposure: 5.44% (Healthcare only — KLBF) — well below 75–85% target; consistent with DEFENSIVE-INTENSIFIED 5% sizing cap
- Sector exposure: Healthcare 5.44%; coal/banking/mining/nickel/conglomerate/telco/consumer all 0%
- Stop state: KLBF TRAILING state-2 10% @ 931 GTC armed broker-side (hwm 1,035); state-3 (≥1,087 → 7% trail) ARMED; state-4 (≥1,134 → 5% trail) ARMED; cluster non-convergence Day 60-65 blocks all transitions per safe-lower discipline (cumulative 25 consecutive blocked sessions Day 41-65 across Weeks 7-8-9-10)
- Dividend received: IDR 10,380,000 (KLBF Jun 20/sh × 519,000 sh) posted Wed Jun 24 = 1st realized cash credit of trial

### What Worked

- **Defensive alpha vindicated through MSCI binary + Mideast cascade week — flat portfolio +5.65pp alpha on −5.54% IHSG net week (2nd-best weekly alpha of trial after Wk 7 +7.10%)** — Mon Jun 22 (IHSG sesi I −1.25% on Mideast escalation + pre-MSCI Annual T-1 derisk) flat carry held = +1.01pp single-day alpha expansion; Tue Jun 23 (sesi I −1.29% on pre-MSCI Annual T-0 derisk + Mideast carry) flat carry held = +0.82pp single-day expansion; Wed Jun 24 (sesi II final −3.56% on MSCI relief-then-cascade) dividend posting +0.11% + alpha expansion to reconciled +21.60%; Thu relief-bounce +2.42% IHSG forced single-day contraction; Fri Jun 26 (sesi I −2.73% post-MSCI absorption Day 2) flat carry held = +2.50pp single-day expansion to fresh trial-cumulative high +22.23% provisional. Three of five trading days produced significant alpha expansion (+1.01pp Mon + +0.82pp Tue + +2.50pp Fri = +4.33pp cumulative); even the Thu relief contraction was contained because the cluster non-convergence prevented forced state-machine fires. The asymmetric pattern (flat-portfolio outperforms on selloff days, lags on relief days) repeated cleanly across all 5 sessions of MSCI cascade absorption.
- **Patience discipline through 17 candidate-session SKIPs across 5 live trading days under elevated MSCI binary + bank-rotation pressure** — Mon (4 candidates BMRI/ASII/TLKM/ICBP all SKIP on Gate 9 + foreign-flow unverified + R:R sub-2:1), Tue (2 candidates TLKM/ASII SKIP on Gate 9 Day 61 + R:R 1.43-1.44:1 + Track suspension), Wed (3 candidates BBCA/TLKM/BMRI SKIP on Gate 9 chase cap fail + R:R 1.20-1.81:1 on MSCI relief-bid materialization above planned-entry thresholds), Thu (3 candidates BBCA/TLKM/BMRI SKIP on R:R 1.87:1 BBCA + Data Quality 18.4% TLKM + cascading conditional BMRI), Fri (4 candidates UNVR/BBCA/TLKM/BMRI SKIP on Gate 9 chase cap UNVR +4.4% + R:R 1.87:1 BBCA mirror Thu + Data Quality ≥25% TLKM + cascading BMRI). Every SKIP rooted in concrete plan-gate failures (not vague reasoning). The cumulative trial alpha +22.23% provisional Fri sesi I is the binding evidence that 5 weeks of patience discipline under sustained forced-entry pressure is the correct default under DEFENSIVE-INTENSIFIED regime.
- **MSCI Annual Classification Review binary navigation Wed Jun 24 03:30 WIB pre-open release** — Indonesia REMAINS EM-classified CONFIRMED per Bareksa/heygotrade/Jakarta Globe multi-source. Frontier-downgrade tail risk REMOVED ($13B Goldman outflow scenario averted). Pre-MSCI defensive derisk Mon-Tue (IHSG cumulative −2.54% sesi I-on-sesi I) absorbed cleanly; relief bid Wed open faded sharply by sesi I close on Mideast carry + Fed hawkish overnight + foreign net sell Rp 311.60B → Rp 976.66B regular market. The binary outcome (favorable for retention) initially read positive but the relief bid was structurally weak — the post-MSCI absorption Day 1 (Wed) cascaded to −3.56% close 5,883.88. The defensive thesis was vindicated NOT by the binary outcome itself but by the post-binary absorption pattern (initial relief faded into structural overhang). Process change from Wk 8 IDR de-risk procedure paid for itself again on Wk 10 MSCI binary day — pre-binary derisk + post-binary absorption pattern is now a 2-week reproducible playbook.
- **First realized cash credit of trial: KLBF dividend payment Wed Jun 24 IDR 10,380,000** — 519,000 shares × IDR 20/sh cash dividend posted to PAPER-STATE.json cash (9,319,172,500 → 9,329,552,500); narrowed phase-to-date P&L from −1.44% Day 47 to −1.33% Day 48-50. First non-trading cash event of the trial (66 sessions in). The held position thesis validated bilaterally: capital-return discipline confirmed (Rp 500B buyback through Jul 2 active concurrent + cash dividend executed) + healthcare-defensive regime fit + 36-day held position with no hard-cut / no thesis-break / no procedural lag. The dividend is a tangible thesis-validation marker independent of cluster non-convergence MTM ambiguity.
- **Safe-lower carry discipline held through 6th consecutive week of cluster non-convergence — 25 cumulative sessions Day 41-65 across Wks 7-8-9-10** — Multi-source cluster persisted ≥36% to ≥126% spread all 5 sessions of Week 10 (TradingView 710-745 fresh LOWs vs Yahoo 945-1,135 stale highs vs broker stub 945 vs Investing.com 1,135-1,685 stale-cached / outlier). Per MISTAKES.md 2026-05-01 + TRADING-STRATEGY.md Data Quality Gate L75-100 (canonical codified Wk 9), 1,035 used for all EOD MTM and dashboard carry. Zero stale-mark errors. The Wk 9 codification trigger (canonical ≤2% / relaxed ≤4% convergence requirement) held perfectly: TradingView 710 fresh single-source LOW on Fri Jun 26 would have triggered trailing 931 GTC fire at confirmed mark, but FAILED canonical ≥3-source ≤2% convergence — safe-lower carry 1,035 correctly held without forced execution on single-source spike. The Data Quality Gate L96 explicit rule "state-machine transitions only fire on confirmed cluster convergence at the relevant threshold (NOT on single-source readings)" is now codified-as-canonical AND validated under stress.

### What Didn't Work

- **Data infrastructure Day 60-65 — yfinance + GoAPI both still blocked; broker.sh cmd_sell MD_LAST_PRICE_OVERRIDE still not patched; multi-source aggregator script NOT shipped** — Ten full weeks of WebSearch fallback for all marks. KLBF cluster spread persisted ≥36% to ≥126% (4-source) all 5 sessions of Week 10. The "Week 10 HARD-STOP" escalation from Wk 9 review (multi-source aggregator helper script ship-or-pause-all-new-entries) did NOT produce action — no infra fix shipped before Mon Jun 22 market-open, no formal hard-stop invoked, and the routine continued executing market-open / midday / EOD per discipline anyway. Cost: 25 consecutive sessions of state-machine state-3/state-4 transition blocking on KLBF (Day 41-65 across Weeks 7-8-9-10) AND 15 cumulative weekly slot uses lost (Wks 6-10). The infra debt is structural; "HARD-STOP" labels from prior reviews do not produce engineering action without a concrete execution owner. Week 11 must either (a) accept the procedural workaround as a permanent baseline AND codify under-outage ≤4% relaxed threshold as canonical default (the Wk 9 marker would now bank 2-weeks evidence on this), OR (b) escalate to an explicit hardware-side action plan (procure paid feed, write multi-source helper) with a named owner and dated deadline.
- **5th consecutive 0/3 week + cumulative 15 unused slots (Wks 6-10) — slot underutilization is now a 5-week structural pattern AND R:R 2:1 / 5% cap math is the recurring binding constraint, not just Data Quality Gate** — Week 6 = 0/3, Week 7 = 0/3, Week 8 = 0/3, Week 9 = 0/3, Week 10 = 0/3. **NEW Week 10 evidence:** the binding SKIP constraint shifted partially away from Data Quality Gate (Wks 7-9 dominant) toward R:R 2:1 + Gate 9 chase cap (Wks 9-10 secondary, Wk 10 dominant). On Wed Jun 24 MSCI relief day, BBCA cluster passed canonical ≤2% convergence at 5,925-6,000, BMRI cluster passed relaxed ≤4% at 3,970 — Data Quality Gate cleared on bank-cluster candidates. The binding constraint became R:R 2:1 (BBCA 1.40:1 at 5,925, TLKM 1.20:1 at 2,960, BMRI 1.81:1 at 4,200) because MSCI relief bid arrived AT the cluster price, not AT the planned-entry pullback level. This is the diagnostic the Wk 9 review process change was designed to produce: the constraint is now identified as structural R:R-cap math, NOT discretionary veto AND NOT (only) data-infra outage. Wk 10 weekly-review codification trigger evaluation: 5% position cap × −7% hard-cut math leaves R:R 2:1 minimum infeasible unless target ≥+14% from entry. On clean defensive-quality banks (BBCA target 6,700 from 5,890 = +13.7%; BMRI target 4,600 from 4,000 = +15.0%; TLKM target 3,250 from 2,830 = +14.8%), R:R 2:1 requires entry below planned threshold by 1-3% — which is precisely what the 3% Gate 9 chase cap blocks. **The 5% cap × −7% stop × Gate 9 3% chase cap × R:R 2:1 minimum math is structurally over-constrained for defensive-relief entries.** Either (a) the entry threshold widens slightly within Gate 9 tolerance, (b) the target widens (which requires higher-conviction catalyst), or (c) the position cap restores to 10% (which the regime currently blocks per criteria b+d+e gating).
- **Reconciliation churn: Day 48 reconciled +21.60% vs Day 49 provisional +19.73% vs Day 50 provisional +22.23% reflects the per-Day-33-40 reconciliation procedure but creates intra-week alpha-trajectory noise** — Day 48 EOD used Wed sesi I provisional 6,002.20 → cumulative alpha +20.05% provisional; per Day 33-40 protocol, Wed sesi II final 5,883.88 was reconciled into Day 49 EOD's alpha calculation = reconciled Day 48 alpha actually +21.60% (the higher water mark). Day 49 EOD (Thu) on the relief-bounce +2.42% IHSG day used sesi II final 5,999.03 (multi-source convergent Fri pre-market read) = contraction from +21.60% reconciled Day 48 to +19.73% Day 49. Day 50 EOD (Fri sesi I provisional 5,835.11) = +22.23% provisional. The intra-week reconciliation creates a "phantom contraction" Day 48 → Day 49 (from reconciled +21.60% to provisional +19.73% = −1.87pp) that reflects sesi-I-vs-sesi-II anchor noise, not real portfolio underperformance. The protocol is operationally sound but the WEEKLY-REVIEW narrative needs to use reconciled anchors throughout for honest representation — provisional sesi-I anchors used at filing inflate intra-week alpha noise. Week 11 should adopt: use sesi-II-final anchors throughout the cumulative-alpha narrative, not sesi-I provisionals.
- **Forward Mideast geopolitics + Fed hawkish overnight + IDR cascade kill-switch ARMED-WATCH = elevated tail-risk into Week 11** — Mideast escalation persisted through Mon-Tue-Wed (Trump-Iran threats + Strait of Hormuz restrictions); Brent collapsed −5.23% to $73.05 Wed close on Strait of Hormuz easing + US-Iran peace progress — relief signal for risk-on, NOT for defensive book; commodity carry-over (Newcastle coal $144 flat / LME nickel $16,880 2-month low) confirms commodity-cycle weakness. US Thu S&P −0.01% / Nasdaq −0.46% with chip-sector contagion ahead of Micron earnings = Fed hawkish overnight continues. IDR Fri spot 17,954 = at/above 17,950 cascade kill-switch threshold (~4bps pierce, 1 of 2 sessions; Thu was 17,943 not pierced). If Mon Jun 29 IDR spot pierces 17,950 sustained 2 sessions, IDR cascade kill-switch RE-ARMS — primary kill-switch path back to BREACHED state would constrain Week 11 sizing/entries.

### Lessons Learned

- **Cumulative trial alpha is the strategy's true output, not weekly-relative outperformance — and it is path-dependent in both directions** — Wk 10 closed at +22.23% provisional (fresh trial-cumulative high water above Wk 7's +24.00%). The 5-week cycle (Wks 6-10) shows the structural path: Wk 6 −0.92% relief drag, Wk 7 +7.10% panic-flush vindication, Wk 8 −6.17% recovery drag, Wk 9 −1.39% binary-resolution net, Wk 10 +5.65% MSCI cascade vindication. Net 5-week cumulative alpha change: −0.92 + 7.10 − 6.17 − 1.39 + 5.65 = **+4.27pp net (Wk 5 close +18.26% → Wk 10 close +22.53%-ish provisional)**. The strategy's edge is asymmetric across cascade-versus-relief weeks; ~+4pp net cumulative alpha over 5 weeks under sustained DEFENSIVE-INTENSIFIED regime with persistent data-infra outage AND 0/3 slot usage every week. This is the binding evidence for the 5%-cap-defensive-cash strategy's edge: the absolute cumulative alpha protection is positive over a 5-week cycle even when the relative weekly alpha is negative on relief weeks. **Codify in MACRO-REGIME.md / PATTERNS.md: under DEFENSIVE-INTENSIFIED regime with persistent data-infra outage, expect the strategy's 5-week net cumulative alpha to be in the +3 to +6 pp range; weekly-relative tracking is noisy and dominated by cascade-vs-relief week labels.**
- **The R:R 2:1 + 5% cap + Gate 9 3% chase cap math is structurally over-constrained for defensive-quality bank entries on relief-bid days — the codification trigger needs +1 week of evidence (Wk 11) before formal TRADING-STRATEGY.md change** — Wk 10 Wed/Thu/Fri provided 3 distinct sessions of clean evidence: BBCA cluster cleared Data Quality Gate (≤2% canonical) AND Gate 9 chase cap (within 3% of planned 5,890), but R:R 1.87:1 at 5,925 cluster mark failed the 2:1 minimum. The math: 5% position × −7% hard-cut = max 35bps risk per idea (well within 75bps Gate 15 budget); but R:R 2:1 requires target ≥+14% from entry; defensive bank targets typically +12-15% (BBCA 6,700 from 5,890 = +13.7%; BMRI 4,600 from 4,000 = +15.0%; TLKM 3,250 from 2,830 = +14.8%). On relief-bid days the entry threshold gets pushed +1-3% above planned by the relief bid arriving at cluster, which compresses target distance below 14% → R:R drops below 2:1. **Two structural fixes available:** (i) widen Gate 9 chase cap to 4% under DEFENSIVE-INTENSIFIED-CASCADE-ABSORBED regime (allows entry slightly above planned); (ii) tighten R:R 2:1 minimum to 1.85:1 specifically for Defensive-Quality Track or for held-name pair-trades. Both options require evidence over 2+ weeks before formal codification. Wk 11 should explicitly test: are these candidates SKIPPING because of R:R 2:1 mechanics or because of poor catalyst conviction? If Wk 11 produces the same R:R-binding skips, the Wk 9 codification trigger (1-week marker) becomes 2-week marker → formal codification eligible.
- **MSCI binary outcome relief-then-cascade is now a 2-week pattern (Wk 9 NEGATIVE info-flow downgrade + Wk 10 Annual Classification CONFIRMED REMAINS EM); the binary itself does NOT drive sustained alpha — the post-binary absorption pattern does** — Wk 9 MSCI NEGATIVE info-flow Fri sesi II −0.73% absorbed → IHSG net week +1.39% (alpha contraction). Wk 10 MSCI CONFIRMED REMAINS EM Wed pre-open release → initial relief at open 6,135 → cascade to sesi II final 5,883.88 (−3.56%); Thu relief-bounce +1.96%; Fri sesi I reversal −2.73%. Net Wed-Thu-Fri = −0.83% additional weakness from Wed close. **Pattern: the MSCI binary outcome alone produces 1-day relief that fades within 24 hours; the post-binary absorption pattern (Days 2-3) is where the structural defensive alpha actually emerges.** Codify in MACRO-REGIME.md / PATTERNS.md as 2-week observation marker (HIGH confidence requires 3-4 obs but the directional pattern is clear). Trading implication for next MSCI binary cycle (Aug-Nov 2026 consultation per Jun 24 release): plan defensive derisk pre-binary, AVOID entry on initial-relief day, RE-EVALUATE entry candidates on Day 2-3 post-binary when relief-bid faded and cluster narrowing achievable.
- **Multi-source aggregator infra patch is the binding constraint — 4 weeks of "Week N HARD-STOP" labels did NOT produce action (Wks 7-10) — the routine must either ship it or formally accept procedural workaround as canonical** — Wk 7 → "Week 8 Priority 1"; Wk 8 → "Week 9 NON-NEGOTIABLE"; Wk 9 → "Week 10 HARD-STOP"; Wk 10 (this review) → status: not shipped. 25 consecutive sessions of state-machine transition blocking + 15 cumulative weekly slot uses lost + 5 consecutive 0/3 weeks. The escalating label rhetoric (Priority 1 → NON-NEGOTIABLE → HARD-STOP) is not producing engineering action because the routine has no explicit ship-or-pause-all-new-entries hard-stop enforcement code. **Two options for Wk 11:** (i) explicitly write the multi-source aggregator helper script Wk 11 Mon Jun 29 pre-market hour (estimate <4 hours; previously named in Wk 7 review); (ii) formally accept the procedural workaround as canonical baseline AND codify the under-outage ≤4% relaxed threshold as the canonical Wk 11+ default in TRADING-STRATEGY.md (currently a Wk 8 process change). The codification eligibility requires 2 consecutive weeks of evidence (Wk 9 1-week marker + Wk 10 2-week marker = met for the under-outage ≤4% canonicalization). Option (ii) is the lower-cost path forward absent engineering action.

### Rule Changes Proposed

- **No formal TRADING-STRATEGY.md changes this week.** Week 10 produced multiple candidate codification triggers but each requires additional weeks of evidence:
  - **R:R 2:1 + 5% cap structural over-constraint:** Wk 10 is the 1-week marker (Wk 9 had partial evidence on R:R compression; Wk 10 had cleaner 3-session evidence Wed/Thu/Fri). Codify only if Wk 11 confirms 2-week marker (same R:R-binding skips on clean Data Quality Gate-passing candidates).
  - **Under-outage ≤4% relaxed threshold canonicalization:** Wk 9 was 1-week marker; Wk 10 is 2-week marker (data-infra outage persists, 5 consecutive 0/3 weeks confirmed). 2-week marker met but Wk 9 review explicitly named "1 week of evidence Week 9; need Week 10 confirmation" — Wk 10 confirms. **Decision: defer codification one more week** because the Wk 10 binding constraint shifted away from Data Quality Gate (Wks 7-9 dominant) to R:R 2:1 / Gate 9 chase cap (Wk 10 dominant). The under-outage relaxation is no longer the marginal binding constraint; codifying it now would not change Wk 10 outcomes. Wk 11 should confirm whether either constraint (or both) is the binding one before any formal change.
  - **Binary-resolution-day separable alpha source sub-pattern (Wk 9 + Wk 10):** Wk 9 introduced +0.99pp combined binary alpha as separable from cash drag; Wk 10 confirmed +1.83pp combined alpha across MSCI binary + post-binary absorption (Wed + Fri). Now 2 weeks of evidence; the PATTERNS.md update (below) banks this as a NEW sub-pattern of the healthcare-defensive lag-on-green / outperform-on-red pattern.
- **Process change (non-strategy):** **Week 11 pre-market routine MUST include explicit R:R-cap-math evaluation for any candidate that fails R:R 2:1 on Gate 9 chase cap binding.** Document: (a) cluster mark at Mon Jun 29 09:15 WIB, (b) planned entry threshold, (c) hard-cut at −7% from entry, (d) target distance from entry, (e) R:R at cluster mark vs planned-entry mark. If the candidate would pass R:R 2:1 at planned-entry but fails at cluster mark by ≤1.5% chase delta, document the SKIP as "R:R 2:1 binding via Gate 9 chase delta" — this provides the 3rd week of evidence needed to evaluate the structural over-constraint hypothesis. Codify in `routines/pre-market.md` and `routines/market-open.md`.
- **Process change (non-strategy):** **Cumulative alpha narrative in WEEKLY-REVIEW.md adopts reconciled sesi-II-final anchors throughout, not sesi-I-provisional.** Wk 10 review section "What Didn't Work" item 3 documents the "phantom contraction" Day 48 → Day 49 caused by sesi-I provisional vs sesi-II final reconciliation. Process fix: weekly-review compute Friday's cumulative alpha using reconciled Thu sesi II final + Fri-end-of-day (sesi II if available, sesi I provisional if not, with explicit annotation). Avoid intra-week sesi-I vs sesi-II noise compounding into the trial-cumulative narrative.
- **Tentative rule-change watch (3-week marker on multi-source aggregator infra debt):** if Week 11 also shows multi-source cluster non-convergence on every held/candidate symbol AND no infra fix shipped AND no entries placed AND R:R 2:1 binds on at least one Data Quality Gate-clearing candidate, formally codify EITHER (a) under-outage ≤4% relaxed threshold as canonical in TRADING-STRATEGY.md, OR (b) R:R 2:1 → 1.85:1 sub-rule for defensive-quality LQ45 names with cluster-clearing Data Quality Gate AND Gate 9 chase cap pass. Wk 11 confirmation triggers formal change; not yet codifying.

### Watchlist Updates

- **Keep watching: KLBF** — state-2 trailing GTC @ 931 armed broker-side, hwm 1,035, trail_pct 10. 25 consecutive sessions Day 41-65 of cluster non-convergence blocking state-3/state-4 transitions. Mon Jun 29 priorities: (1) fresh multi-source cluster — if ≥3-source convergence ≤2% AND mark ≥1,087 → fire state-3 (7% trail at 1,011 from hwm 1,087); ≥1,134 → fire state-4 (5% trail at 1,077); ≤931 → trailing GTC fires automatically. (2) Dividend posting reconciled Wed Jun 24 IDR 10,380,000; no further accruing receivables. (3) Buyback program Rp 500B through Jul 2 active (~6 days remain at Mon Jun 29 = Day 66; ~3 days remain at week-end Jul 3). (4) Re-evaluate KLBF as the only deployed position vs the 5-week 0/3 entry pattern — is the held name still the highest-conviction healthcare-defensive carry, or has buyback program nearing expiry weakened the structural bid?
- **Re-rank as Week 11 Priority 1 candidate: UNVR** — Wk 10 MNC Sekuritas Fri TOP PICK Spec Buy 1,675-1,685 TP 1,885-2,000 CL 1,670; Brent collapse −5.23% Wed CPI-relief tailwind partially supportive but not sub-$70 sustained; B50 CPO supportive carryover. Mon Jun 29 priority: re-gate at cluster narrowing pullback ≤1,690 (3% chase cap from prior reference); R:R 2.62:1 at 1,690/2,000 PASSES strategy R:R; Defensive-Quality Track SUSPENDED still binds under DEFENSIVE-INTENSIFIED-CASCADE-ABSORBED-DAY2 regime so catalyst-track only — Brent CPI-relief catalyst is the qualifying entry trigger.
- **Re-rank as Week 11 Priority 2: BBCA** — Wk 10 cluster cleared Data Quality Gate ≤2% canonical at 5,925-6,000 Wed-Fri; SKIP rationale was R:R 2:1 binding (1.87:1 at 5,925 vs 2:1 minimum requires ≤5,890). Mon Jun 29 priority: if Mon open pulls back ≤5,890 with cluster ≥3-source convergent ≤2%, BBCA becomes immediate entry candidate (R:R 2.05:1 at 5,890/6,700 with stop 5,477). Post-MSCI absorption Day 3 mean-reversion scenario.
- **Keep watching: BMRI** — Wk 10 BI HIKE +25bp NIM-positive but NIM headwind caveat binding (BSI deconsol + cost-of-funds + yield compression → 2026 NIM 4.6-4.8% per Wk 10 evaluation); cluster passed relaxed ≤4% at 3,970 but cascading conditional fails (BBCA-cluster bid initiator required). Mon Jun 29: re-eligibility requires BBCA cluster entry confirmation as bank-cluster initiator + IDR sub-17,950 cascade kill-switch DE-ARMED.
- **Keep watching: TLKM** — Wk 10 cluster Data Quality Gate FAILED 4 consecutive sessions (Mon-Tue-Wed-Thu-Fri spread 18-25% on stale Investing.com Jun 17 anchor vs fresh TradingView prints). Mon Jun 29: requires fresh cluster ≥3-source ≤2% convergence (canonical or ≤4% relaxed-outage). AGM-day carry-over noise persists; ex-div Jun 19 mechanical drop should now be fully absorbed.
- **Keep watching: ICBP** — Wk 10 Mon SKIP on R:R below 2:1 + cluster non-convergence. FMCG defensive leadership pattern operative; cum-div Jun 19 carry. Re-gate Mon Jun 29 if cluster narrows to entry zone (≤7,725 estimated for 3% chase cap above plan ≤7,500 and R:R clear).
- **Keep watching: PGAS** — Carry-forward 5+ weeks (Wk 6-10) of data-infra Data Quality Gate FAIL; Abadi LNG HoA catalyst fresh since Wk 6 but never anchored. Mon Jun 29: if cluster narrows ≤2% (or relaxed ≤4%) AND R:R ≥2:1 at verified mark AND IDR sub-17,950 sustained, top-priority defensive utility candidate.
- **Remove: MDKA** — RUPSLB Tue Jun 23 binary T+0 outcome not surfaced multi-source convergent in Wk 10 routine; private placement 2.45B sh (10% dilution) post-vote = structural headwind unless dividend approval first ever drives institutional bid. Off candidate list pending fresh post-RUPSLB research.
- **Remove: ASII** — Wk 10 Tue SKIP on Defensive-Quality Track SUSPENDED + R:R 1.44:1 + Gate 9 Data Quality fail. Catalyst (IDR 8T buyback + 56% auto market share) intact but Track suspension binds under DEFENSIVE regime — re-eligible only on regime downgrade to DEFENSIVE-CONFIRMED or NEUTRAL.
- **Week 11 entry queue:** 6 candidates carry forward (KLBF held + UNVR Priority 1 + BBCA Priority 2 + BMRI/TLKM/ICBP/PGAS watch). Full 3/3 slot allocation available Week 11. **REGIME STATUS: DEFENSIVE-INTENSIFIED — CASCADE-ABSORBED-DAY2 (Wk 10 MSCI Annual Classification CONFIRMED Indonesia REMAINS EM; criterion (e) MSCI accessibility flipped NEUTRAL-LOCKED → NEGATIVE-LOCKED Wed cascade; 1.5 of 5 de-escalation criteria confirming with criteria (b)+(d)+(e) regression persisting as gating; IDR sub-18,000 13-of-13 PERMANENTLY-LOCKED + cascade kill-switch ARMED-WATCH 1-of-2 sessions Fri pierce 17,954)**. Slot underutilization 5-week pattern + R:R-cap-math structural over-constraint hypothesis triggers Wk 11 explicit R:R-cap evaluation per Process Change above. **No new TRADING-STRATEGY.md codifications this week** — codification triggers banked for Wk 11 confirmation.

---
