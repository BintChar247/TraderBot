# Skill: Position Review

Purpose: midday and EOD triage of every open position against the sell-side rules.

Canonical sell-side rules live in `memory/TRADING-STRATEGY.md` "The Sell-Side Rules". Read them — this skill enforces them, does not redefine them.

## When to call

- Midday scan (11:30 WIB) — act on hard-cut and tighten-stop signals.
- Daily summary (15:15 WIB) — cleaner pass at close.
- Opportunistically when news breaks a thesis.

## Inputs

- Open positions from `memory/TRADE-LOG.md` (symbol, entry price, entry date, thesis, current stop, sector).
- Live quotes:

  ```
  python scripts/yfinance_helper.py quote <TICKER>
  ```

- Recent sector trade outcomes (TRADE-LOG.md) — for the 2-consecutive-losers rule.

## Procedure

For each open position, compute:
- `pnl_pct` = (current - entry) / entry
- `thesis_intact` — yes/no, based on whether the original catalyst/driver is still valid (may require a quick web search if news broke)
- `sector_recent_fails` — count of closed trades in the same sector with loss, most recent first

Apply rules in this order (first match wins):

1. `pnl_pct <= -7%` → **CLOSE** (hard cut, no averaging).
2. `thesis_intact == no` → **CLOSE** (thesis broken).
3. Sector has 2 consecutive failed trades → **CLOSE ALL in sector** (sector exit).
4. `pnl_pct >= +20%` and current stop looser than 5% → **TIGHTEN STOP to 5%**.
5. `pnl_pct >= +15%` and current stop looser than 7% → **TIGHTEN STOP to 7%**.
6. Otherwise → **HOLD**.

Never tighten a stop to within 3% of current price. Never move a stop down (both from TRADING-STRATEGY.md).

## Output — decisions table (append to TRADE-LOG.md)

```
### Position Review — {date_wib} {hh:mm WIB}

| Symbol | Entry | Current | P&L %  | Thesis | Sector | Action            | Reason                                   |
|--------|-------|---------|--------|--------|--------|-------------------|------------------------------------------|
| ADRO   | 2620  | 2490    | -5.0%  | intact | coal   | HOLD              | Above -7%, thesis ok, below trim tiers   |
| BBCA   | 9200  | 10820   | +17.6% | intact | bank   | TIGHTEN STOP 7%   | Crossed +15%, stop was 10%               |
| INCO   | 4100  | 3805    | -7.2%  | intact | nickel | CLOSE             | Hard cut at -7%                          |
| CTRA   | 1180  | 1155    | -2.1%  | broken | prop   | CLOSE             | BI pushed cut out, thesis invalidated    |

Sector exits triggered: none
Orders to place:
  scripts/broker.sh sell --symbol INCO --qty <n> --reason "-7% hard cut"
  scripts/broker.sh sell --symbol CTRA --qty <n> --reason "thesis broken"
  scripts/broker.sh modify-stop --symbol BBCA --stop <price>
```

## Rules

- One row per open position. No position omitted.
- Actions are `HOLD`, `CLOSE`, `TRIM`, `TIGHTEN STOP X%`, or `SECTOR EXIT`.
- If thesis needs re-reading, fetch the original entry from TRADE-LOG.md — do not invent.
- Emit the exact `scripts/broker.sh` invocations for each non-HOLD action so the routine can execute them.
- After execution, the routine (not this skill) appends fills to TRADE-LOG.md.
