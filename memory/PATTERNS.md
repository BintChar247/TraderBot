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

- **Observed:** 2026-05-25 (Mon: IHSG +1.90% relief bounce / KLBF flat — lagged broad tape), 2026-05-26 (Tue: IHSG −0.98% pullback / KLBF held flat — outperformed broad tape), 2026-05-29 (Fri: MSCI rebalance day, IHSG bounced sesi I + corrected intraday / KLBF reached +9.52% on safe-lower mark vs entry — defensive sector bid sustained); 2026-06-02 (Tue: IHSG +1.49% sesi I gap-reopen bounce / KLBF Yahoo single-source 1,135 +4.61% intraday but cluster non-convergence blocks confirmation); 2026-06-03 (Wed: IHSG −5.30% sesi I panic-flush on Moody's Danantara outlook + IDR 17,920 + MSCI overhang / KLBF safe-lower carry frozen 1,035 unchanged = held flat on extreme risk-off day; cluster TradingView/Google 745 cross-source 2-source low non-confirmed); 2026-06-04 (Thu: IHSG −3.48% sesi I continuation to 5.5-yr low 5,644 + KLBF ex-div IDR 20 / safe-lower 1,035 carry held); 2026-06-05 (Fri: IHSG −0.73% sesi I + IDR 18,033 cascade kill-switch breach sustained close / KLBF safe-lower 1,035 carry held)
- **Pattern:** Under DEFENSIVE regime with active macro stress (BI +50bp hike May 21; IDR record-territory escalating to 18,033 cascade-breach Jun 5; MSCI rebalance flow; Moody's Danantara outlook negative), healthcare-defensive holdings exhibit a bilateral asymmetry: they lag broad-tape on relief-bounce sessions (cyclicals lead) and outperform on risk-off sessions (defensive bid). Net effect over a mixed-tape week is positive relative strength because the down-day protection > up-day give-back. Pattern now confirmed across TWO distinct weeks (Week 6 mixed tape + Week 7 panic-flush week including −5%/-3.5% consecutive days + IDR cascade breach).
- **Confidence:** MEDIUM (7 observations across 2 distinct weeks under DEFENSIVE-INTENSIFIED regime; data-infra cluster non-convergence caveats Week 7 readings to "safe-lower frozen carry" rather than fresh confirmed tape; promote to HIGH on a third distinct week or with a second healthcare-defensive name confirmation).
- **Trading implication:** Use healthcare-defensive names as core holdings during DEFENSIVE regime, accepting that they will lag broad-tape relief bounces. Do NOT abandon the position because of single-day lag-on-green — the down-day asymmetric protection is the source of the relative strength. The Week 7 vindication (cumulative trial alpha +6.39pp through a -7.10% IHSG week on 94.55% cash + KLBF healthcare-defensive carry) is the strongest single-week evidence to date that defensive-regime + cash-heavy + healthcare-defensive carry is the trial's edge-generation mechanism. Watch closely for thesis break only if KLBF turns RED on a red tape (loses defensive premium = pattern broken).
- **Counter-evidence:** None observed. Caveat: Week 7 carry interpretation is ambiguous on data-infrastructure outage — safe-lower 1,035 is the procedural mark but the persistent TradingView/Google 745 cluster-low Wed/Thu (2-source, sub-threshold) raises the possibility KLBF actually traded materially lower intraday. If Mon Jun 8 cluster narrows ≤2% with mark ≤931, the trailing GTC fires and the real-tape outcome will reveal whether the pattern truly held under extreme stress or whether it was a data-quality artifact. Pattern breaks if (a) KLBF cluster confirms ≤931 Mon Jun 8 and stop fires (loses defensive premium pattern under cascade-breach week), (b) sector relative strength reverses (healthcare turns into a laggard regardless of tape direction), or (c) regime label flips back to RISK-NEUTRAL/RISK-ON and defensive premium evaporates.

### Coal complex — synchronized Q1 2026 earnings rerate

### Coal complex — synchronized Q1 2026 earnings rerate

- **Observed:** ITMG Q4 2025 print +114.10% EPS surprise (per Apr 20 entry research); ADRO Q1 2026 net +67.07% YoY / rev +23.4% (per May 8 entry research); PTBA Q1 2026 +105% YoY; BUMI Q1 2026 +34.6%; HRUM Q1 2026 rev +14.67% — all confirmed prior to ADRO entry on 2026-05-08.
- **Pattern:** When Newcastle thermal coal sustains above the $130/t thesis floor (observed range $130–$140 across Q1 2026), Indonesian coal-complex Q1 prints are converging to the upside in synchrony. Five of five coal Q1 prints positive. This is sector-level operating leverage off the realized-price uplift, not idiosyncratic name-by-name beats.
- **Confidence:** LOW (5 ticker observations, single Q1 cycle — pattern needs cross-quarter confirmation before MEDIUM)
- **Trading implication:** Sector-level conviction in coal can run higher than individual-name conviction during a synchronized rerate cycle, allowing pair-wise / basket sizing. Specifically: a second coal name added to a held coal name need not require fresh idiosyncratic catalyst — the sector thesis (Newcastle floor + Q1 cycle) carries meaningful weight. Sector concentration cap (35–40% per gate-check Check 13) becomes the binding constraint, not conviction. Caveat: under EM-OUTFLOW regime, keep aggregate coal exposure ≤25% of equity until the regime label flips.
- **Counter-evidence:** None observed yet. The thesis breaks if (a) Newcastle sustains <$125/t for 5+ days, (b) any single Q1 print misses materially (none have so far), (c) China demand signal rolls over.
