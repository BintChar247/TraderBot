Read-only portfolio snapshot. No orders. No file writes. No state changes.

**Usage:** `/portfolio`

---

STEP 1 — Pull live account state:
```bash
bash scripts/broker.sh portfolio
bash scripts/broker.sh positions
```

STEP 2 — Read the Active Positions table from memory/TRADE-LOG.md for context.

STEP 3 — Print a clean summary in this format:

```
=== Portfolio Snapshot [DATE TIME WIB] ===

Account
  Mode:         [paper | live]
  Equity:       IDR [amount]
  Cash:         IDR [amount] ([X]%)
  Buying Power: IDR [amount]

Open Positions ([N]/6)
  TICKER   Shares   Cost (IDR)   Last (IDR)   Unrealized P&L   Stop (IDR)   Days Held
  ------   ------   ----------   ----------   --------------   ----------   ---------
  [rows]

Trailing Stops (from positions output or broker log)
  TICKER  trail_pct  approx_stop_IDR

Flags
  [Flag any position without a visible stop order]
  [Flag any stop below entry — this would be a violation]
  [Flag if weekly trade count is at 3/3 — no new trades allowed]
  [Flag if daily P&L is at or below -2% — trading halted for today]
```

STEP 4 — No commit, no notification, no file writes. This command is read-only.
