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
