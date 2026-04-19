# Skill: Trade Plan

Purpose: turn a candidate from today's RESEARCH-LOG.md into an actionable order. Produces the exact `scripts/broker.sh buy` invocation, plus the 9-gate pre-check output.

All rules (hard rules, buy-side gate, entry checklist, sell-side rules) live in `memory/TRADING-STRATEGY.md`. Read that file — do not restate the rules, enforce them.

## When to call

Called from the market-open routine, once per candidate being considered for execution. Input: a single ticker from today's RESEARCH-LOG.md `Candidate Trades` section, plus current broker state (cash, open positions, trades-this-week).

## Procedure

1. Read the research entry for the ticker (thesis, catalyst, proposed entry/stop/target).
2. Pull current quote:

   ```
   python scripts/yfinance_helper.py quote <TICKER>
   ```

3. Compute position size:
   - Regime (from macro.md): CONSTRUCTIVE = 100%, CAUTIOUS = 75%, DEFENSIVE = 0% new entries.
   - Target cost = min(20% of equity, regime-adjusted) — hard cap from TRADING-STRATEGY.md.
   - Round to IDX lot size (100-share multiples).
4. Produce the Entry Checklist (from TRADING-STRATEGY.md). Every item must be answered in plain language. If any can't be, **skip the trade**:
   - Specific catalyst today?
   - Is the sector in momentum?
   - Stop level (7-10% below entry)?
   - Target (minimum 2:1 R/R)?
5. Run the 9-gate buy-side pre-check. Produce a pass/fail line per gate. Any single FAIL => skip.
6. If all 9 pass, emit the broker invocation.

## Output format

```
### Trade Plan — TICKER

Entry Checklist:
  1. Catalyst:       <one line>
  2. Sector momentum:<one line>
  3. Stop level:     IDR xxx (-x.x% from entry)
  4. Target + R/R:   IDR xxx (+y.y%, R/R = 2.4:1)

9-Gate Pre-Check:
  [PASS] 1. Positions after fill: 4/6
  [PASS] 2. Weekly trades including this: 2/3
  [PASS] 3. Cost 18.4% of equity (cap 20%)
  [PASS] 4. Cost IDR 55.2M <= cash IDR 82M
  [PASS] 5. Catalyst documented in RESEARCH-LOG.md 2026-04-18
  [PASS] 6. Instrument is a stock
  [PASS] 7. Avg daily volume 42M > 500k
  [PASS] 8. Qty 8000 is lot-multiple of 100
  [PASS] 9. Current IDR 2640 within 3% of planned 2620

Execution:
  scripts/broker.sh buy --symbol ADRO --qty 8000 --limit 2640 --stop 2380 --tag "coal-catalyst-Q1"

Thesis (for TRADE-LOG):
  <2 sentences copied from RESEARCH-LOG.md candidate>
```

## Rules

- If ANY gate fails, do not emit the `scripts/broker.sh buy` line. Instead emit:
  `SKIP: gate N failed — <reason>`
- Stops are placed as GTC orders per hard rules. The invocation includes `--stop`.
- Never chase (gate 9): if price has moved > 3% from planned entry, skip.
- Do not invent tickers. Only plan trades for candidates present in today's RESEARCH-LOG.md.
- Do not write to TRADE-LOG.md here — the market-open routine does that after execution confirms.
