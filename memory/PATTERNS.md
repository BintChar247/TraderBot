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

_(None yet — 2+ observations required)_
