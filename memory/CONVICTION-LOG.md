# Conviction Calibration Log

_Tracks whether HIGH / MEDIUM / LOW conviction calls actually outperform over time._
_If HIGH conviction calls don't beat MEDIUM calls, the research process is miscalibrated._

---

## Summary Table (updated weekly)

| Period | HIGH calls | HIGH win rate | MEDIUM calls | MEDIUM win rate | LOW calls | LOW win rate | Calibration |
|--------|-----------|--------------|-------------|----------------|----------|-------------|-------------|
| Week 1 (2026-04-20 → 04-24) | 1 (ITMG) | Open (+2.40% unrealized) | 1 (BBRI) | Open (−5.52% unrealized) | 0 | — | Directionally on track — HIGH green, MEDIUM red — but no closed trades yet to confirm |
| Week 3 (2026-05-04 → 05-08) | 0 | — | 1 (ADRO) | Open (−1.96% same-day) | 0 | — | One new MEDIUM open. ITMG (HIGH from Week 1) still open at −0.29% pre-Q1-print outcome |
| Week 6 (2026-05-25 → 05-29) | 0 | — | 0 | — | 0 | — | No new entries (0/3 slot used); no closes. KLBF (HIGH from Week 5) carries open at +9.52%, first +7% threshold crossed → stop-state transitioned to 10% trailing. Calibration data still deferred until first close. |
| Cumulative (closed only) | 0 closed | — | 1 closed (BBRI) | 0% (1 loss, −8.28%) | 0 | — | Insufficient sample. 1 closed trade across all conviction tiers — calibration cannot be assessed yet. |

---

## Individual Call Log

| Date | Ticker | Conviction | Entry | Exit | P&L % | Correct? | Notes |
|------|--------|-----------|-------|------|--------|---------|-------|
| 2026-04-20 | ITMG | HIGH | 26,075 | Open | −0.29% (May 8 mark) | Pending | Q1 EPS 114% beat catalyst; coal $131–$140/t; pre-Q1 2026 print, position carrying through binary |
| 2026-04-23 | BBRI | MEDIUM | 3,260 | 2,990 (2026-05-01) | −8.28% (realized) | NO | Q1 earnings T−6 at entry; macro-shock dominated post-earnings drift; hard-cut fired (procedural lag — see MISTAKES.md) |
| 2026-05-08 | ADRO | MEDIUM | 2,550 | Open | −1.96% (same-day) | Pending | Coal Q1 +67.07% YoY catalyst already landed; data-infra block kept conviction at MEDIUM (not HIGH) despite real catalyst — ADV verified by WebSearch override only |

---

## Calibration Notes

_(Weekly review appends observations here when patterns emerge)_

### 2026-04-24 — Week 1

- Too early to judge calibration. Both Week-1 positions remain open into Week 2.
- Observation: the one HIGH call (ITMG, catalyst already landed) is green; the one MEDIUM call (BBRI, catalyst 6 days out at entry) is red. This fits the prior: catalyst distance and conviction should correlate. Watch whether this pattern persists in Weeks 2–3.

### 2026-05-08 — Week 3

- Sample is still too small for statistical inference (1 closed trade across all tiers).
- The one closed call (BBRI MEDIUM) hard-cut at −8.28% — directionally consistent with the Week 1 hypothesis that MEDIUM-conviction far-catalyst trades are the highest-failure setup.
- ADRO open at MEDIUM despite a strong real catalyst (+67.07% Q1 already printed). Conviction was held to MEDIUM specifically because (a) data-infra block forced WebSearch-override ADV verification rather than primary-source GoAPI/yfinance, and (b) sector pair-trade rather than standalone idiosyncratic. The conviction-rating discipline correctly noted the infra constraint — a HIGH call requires both fundamentals AND data integrity.
- ITMG (HIGH, Week 1) still open into Q1 print outcome — calibration outcome on this call deferred until print-driven move resolves (Mon May 11).
- After 3 weeks: HIGH = 1 open; MEDIUM = 2 (1 closed loss, 1 open). Cannot conclude HIGH calls outperform MEDIUM yet — needs ≥3 closes per tier.

### 2026-05-29 — Week 6

- No new entries this week; full 0/3 slot allocation preserved. No closes either — KLBF (HIGH from Week 5) carries open at +9.52% with first state-machine transition fired (hard-cut → 10% trailing) Fri midday.
- KLBF's continued strength under DEFENSIVE regime + MSCI rebalance event is directionally consistent with the HIGH conviction call (defensive sector + buyback program + Q1 +10% revenue growth + healthcare leadership pattern bilaterally validated on green Mon / red Tue tape this week).
- Cumulative tally unchanged: 1 closed call total (BBRI MEDIUM, hard-cut −8.28%). Need ≥3 closes per tier before any statistical inference. KLBF outcome will be the next data point — currently advancing the HIGH thesis but unrealized.
- Calibration note: trial period showed all 3 hard-cut casualties (BBRI MEDIUM, ITMG HIGH on Q1 miss, ADRO MEDIUM) clustered in regime-shift windows — pure conviction tier may matter less than regime fit. Track over more closes.
