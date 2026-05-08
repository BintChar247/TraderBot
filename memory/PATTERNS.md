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

### Coal complex — synchronized Q1 2026 earnings rerate

- **Observed:** ITMG Q4 2025 print +114.10% EPS surprise (per Apr 20 entry research); ADRO Q1 2026 net +67.07% YoY / rev +23.4% (per May 8 entry research); PTBA Q1 2026 +105% YoY; BUMI Q1 2026 +34.6%; HRUM Q1 2026 rev +14.67% — all confirmed prior to ADRO entry on 2026-05-08.
- **Pattern:** When Newcastle thermal coal sustains above the $130/t thesis floor (observed range $130–$140 across Q1 2026), Indonesian coal-complex Q1 prints are converging to the upside in synchrony. Five of five coal Q1 prints positive. This is sector-level operating leverage off the realized-price uplift, not idiosyncratic name-by-name beats.
- **Confidence:** LOW (5 ticker observations, single Q1 cycle — pattern needs cross-quarter confirmation before MEDIUM)
- **Trading implication:** Sector-level conviction in coal can run higher than individual-name conviction during a synchronized rerate cycle, allowing pair-wise / basket sizing. Specifically: a second coal name added to a held coal name need not require fresh idiosyncratic catalyst — the sector thesis (Newcastle floor + Q1 cycle) carries meaningful weight. Sector concentration cap (35–40% per gate-check Check 13) becomes the binding constraint, not conviction. Caveat: under EM-OUTFLOW regime, keep aggregate coal exposure ≤25% of equity until the regime label flips.
- **Counter-evidence:** None observed yet. The thesis breaks if (a) Newcastle sustains <$125/t for 5+ days, (b) any single Q1 print misses materially (none have so far), (c) China demand signal rolls over.
