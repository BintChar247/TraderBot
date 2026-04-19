# Benchmark Log

_Append-only daily tracking of agent equity vs IHSG. One row per trading day. Never delete rows._

Run `python scripts/performance.py benchmark --symbol=IHSG --days=1` to fetch IHSG daily return.

---

## Log

| Date (WIB) | IHSG Close | IHSG Day % | Agent Equity (IDR) | Agent Day % | Alpha (Day) | Cumulative Alpha |
|------------|-----------|------------|-------------------|------------|-------------|-----------------|
| 2026-04-18 | — | — | 300,000,000 | — | — | 0.00% |

_Day 0: scaffold phase. Paper account seeded at IDR 300M. IHSG not yet tracked. Baseline set._

---

## Template — Daily Row

```
| YYYY-MM-DD | [close] | [+/-X]% | [equity] | [+/-X]% | [+/-X]% | [+/-X]% |
```

Generate with: `python scripts/performance.py benchmark --symbol=IHSG --days=1 --markdown`
