# Behavioural Patterns Log

_Observed ticker and sector patterns built from repeated data points. Only add a pattern after 2+ confirming observations._
_Purpose: give the bot institutional memory about how IDX instruments actually behave._

---

## Template — Pattern Entry

```
### [TICKER | SECTOR] — [Pattern name]

- **Observed:** [dates of observations]
- **Pattern:** [what happens and under what conditions]
- **Confidence:** LOW (2 obs) | MEDIUM (3-4 obs) | HIGH (5+ obs)
- **Trading implication:** [how to use this — entry timing, stop width, position sizing]
- **Counter-evidence:** [any observations that contradict this]
```

---

## Macro Patterns

### IHSG risk-off days drag banking names more than coal

- **Observed:** 2026-04-23 (IHSG −0.69%), 2026-04-24 (IHSG −1.49%)
- **Pattern:** On IHSG risk-off sessions during EM-OUTFLOW regime, banking names (BBRI) sold off with or beyond the index while commodity names (ITMG) held or outperformed.
- **Confidence:** LOW (2 obs, same week)
- **Trading implication:** Prefer commodity catalysts over bank catalysts during EM-OUTFLOW regime unless bank has a same-day idiosyncratic catalyst. Require IHSG flat-or-green before entering a bank name in this regime.
- **Counter-evidence:** None yet — pattern needs out-of-week confirmation before promoting to MEDIUM.

## Ticker Patterns

_(None yet — 2+ observations required)_

## Sector Patterns

### Healthcare (KLBF) — defensive lag-on-green / outperform-on-red under DEFENSIVE regime

- **Observed:** 2026-05-25 (Mon: IHSG +1.90% relief bounce / KLBF flat — lagged broad tape), 2026-05-26 (Tue: IHSG −0.98% pullback / KLBF held flat — outperformed broad tape), 2026-05-29 (Fri: MSCI rebalance day, IHSG bounced sesi I + corrected intraday / KLBF reached +9.52% on safe-lower mark vs entry — defensive sector bid sustained); 2026-06-02 (Tue: IHSG +1.49% sesi I gap-reopen bounce / KLBF Yahoo single-source 1,135 +4.61% intraday but cluster non-convergence blocks confirmation); 2026-06-03 (Wed: IHSG −5.30% sesi I panic-flush on Moody's Danantara outlook + IDR 17,920 + MSCI overhang / KLBF safe-lower carry frozen 1,035 unchanged = held flat on extreme risk-off day; cluster TradingView/Google 745 cross-source 2-source low non-confirmed); 2026-06-04 (Thu: IHSG −3.48% sesi I continuation to 5.5-yr low 5,644 + KLBF ex-div IDR 20 / safe-lower 1,035 carry held); 2026-06-05 (Fri: IHSG −0.73% sesi I + IDR 18,033 cascade kill-switch breach sustained close / KLBF safe-lower 1,035 carry held); **2026-06-08 (Mon Week 8: IHSG −4.53% panic-flush continuation to 6-yr low / KLBF safe-lower 1,035 carry held = cumulative alpha new trial high +27.37% single-day +3.37pp expansion); 2026-06-09 (Tue: IHSG +3.04% relief bounce post BI off-cycle +25bp / KLBF safe-lower 1,035 carry held = single-day alpha compression −2.16pp); 2026-06-10 (Wed: IHSG +5.03% continuation relief / KLBF carry held = alpha compression −3.70pp); 2026-06-11 (Thu: IHSG −1.55% mean-reversion / KLBF carry held = alpha expansion +1.21pp); 2026-06-12 (Fri Week 8 close: IHSG +4.38% continuation re-tops 6,000 / KLBF carry held = alpha compression −3.33pp)**
- **Pattern:** Under DEFENSIVE regime with active macro stress (BI +50bp hike May 21 + Tue Jun 9 +25bp off-cycle to 5.50%; IDR record-territory escalating to 18,033 cascade-breach Jun 5 then de-escalating to sub-18,000 4-sessions-sustained Jun 12; MSCI rebalance flow; Moody's Danantara outlook negative), healthcare-defensive holdings exhibit a bilateral asymmetry: they lag broad-tape on relief-bounce sessions (cyclicals lead) and outperform on risk-off sessions (defensive bid). Net effect across a full cascade-then-recovery cycle (Weeks 7-8 combined): +7.10% alpha (Wk 7 IHSG −7.10%) + (−6.17%) alpha (Wk 8 IHSG +6.17%) = +0.93% net alpha across the 2-week cycle. **Pattern now confirmed across THREE distinct weeks (Week 6 mixed tape + Week 7 panic-flush week + Week 8 cascade-then-recovery week with both modalities present simultaneously).**
- **Confidence:** HIGH (12 observations across 3 distinct weeks under DEFENSIVE-INTENSIFIED regime spanning a full cascade-then-recovery cycle; data-infra cluster non-convergence caveats Weeks 7-8 readings to "safe-lower frozen carry" rather than fresh confirmed tape; Week 8 provided clean A/B bilateral confirmation in a single week — Mon panic + Tue-Fri relief).
- **Trading implication:** Use healthcare-defensive names as core holdings during DEFENSIVE regime, accepting that they will lag broad-tape relief bounces. Do NOT abandon the position because of single-day lag-on-green — the down-day asymmetric protection is the source of the relative strength. Across a full cascade-then-recovery cycle (Wks 7-8), net alpha was mildly positive (+0.93%) on a flat-portfolio book — the structural edge is path-validated. **NEW Week 8 corollary: when cumulative alpha hits a single-day trial-high during a panic-flush continuation (Wk 8 Mon +27.37%), expect 3-5 sessions of compression on the subsequent relief bounce; do NOT trim/close the defensive carry on relief days unless cluster confirms ≤stop-floor with multi-source convergence.** Watch closely for thesis break only if KLBF turns RED on a red tape (loses defensive premium = pattern broken).
- **Counter-evidence:** None observed across 12 observations. Caveat (now 3-week persistent): carry interpretation remains ambiguous on data-infrastructure outage — safe-lower 1,035 is the procedural mark but the persistent TradingView/Google 745 cluster-low across multiple sessions (2-source, sub-≥3-source threshold) raises the possibility KLBF actually traded materially lower intraday. Cluster non-convergence Day 50-54 (Week 8) extended Day 41-45 (Week 7) into a 14-consecutive-session block. If Mon Jun 15 cluster narrows ≤2% with mark ≤931, the trailing GTC fires and the real-tape outcome will reveal whether the pattern truly held under 3 weeks of stress or whether it was a data-quality artifact. Pattern breaks if (a) KLBF cluster confirms ≤931 Mon Jun 15 and stop fires, (b) sector relative strength reverses, or (c) regime label flips back to RISK-NEUTRAL/RISK-ON.

### Coal complex — synchronized Q1 2026 earnings rerate

### Coal complex — synchronized Q1 2026 earnings rerate

- **Observed:** ITMG Q4 2025 print +114.10% EPS surprise (per Apr 20 entry research); ADRO Q1 2026 net +67.07% YoY / rev +23.4% (per May 8 entry research); PTBA Q1 2026 +105% YoY; BUMI Q1 2026 +34.6%; HRUM Q1 2026 rev +14.67% — all confirmed prior to ADRO entry on 2026-05-08.
- **Pattern:** When Newcastle thermal coal sustains above the $130/t thesis floor (observed range $130–$140 across Q1 2026), Indonesian coal-complex Q1 prints are converging to the upside in synchrony. Five of five coal Q1 prints positive. This is sector-level operating leverage off the realized-price uplift, not idiosyncratic name-by-name beats.
- **Confidence:** LOW (5 ticker observations, single Q1 cycle — pattern needs cross-quarter confirmation before MEDIUM)
- **Trading implication:** Sector-level conviction in coal can run higher than individual-name conviction during a synchronized rerate cycle, allowing pair-wise / basket sizing. Specifically: a second coal name added to a held coal name need not require fresh idiosyncratic catalyst — the sector thesis (Newcastle floor + Q1 cycle) carries meaningful weight. Sector concentration cap (35–40% per gate-check Check 13) becomes the binding constraint, not conviction. Caveat: under EM-OUTFLOW regime, keep aggregate coal exposure ≤25% of equity until the regime label flips.
- **Counter-evidence:** None observed yet. The thesis breaks if (a) Newcastle sustains <$125/t for 5+ days, (b) any single Q1 print misses materially (none have so far), (c) China demand signal rolls over.
