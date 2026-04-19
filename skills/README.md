# Skills — Index

Prompt fragments slotted into the scheduled routines. Each skill is a focused capability, not a full prompt. Canonical rules live in `memory/TRADING-STRATEGY.md` — skills reference, never restate.

## Skill -> Routine matrix

| Skill             | File                 | pre-market | market-open | midday | daily-summary | weekly-review |
|-------------------|----------------------|:----------:|:-----------:|:------:|:-------------:|:-------------:|
| Macro readout     | `macro.md`           |     X      |             |        |               |               |
| Sector screen     | `screen.md`          |     X      |             |        |               |               |
| Research          | `research.md`        |     X      |      (fallback) |    |               |               |
| Trade plan        | `trade-plan.md`      |            |      X      |        |               |               |
| Position review   | `position-review.md` |            |             |   X    |       X       |       X       |
| Journal           | `journal.md`         |            |      X      |   X    |       X       |       X       |

`(fallback)` = market-open runs research inline if today's RESEARCH-LOG.md entry is missing (per PROJECT-CONTEXT.md read/write contract).

## Helper

- `scripts/yfinance_helper.py` — quote, fundamentals, sector screen, price history, liquidity. IDX symbols auto-`.JK`. Scaffold; swap for real IDX feed later.

## Output targets

- Research, macro, screen -> `memory/RESEARCH-LOG.md`
- Trade plan, position review, journal (trade/action/EOD) -> `memory/TRADE-LOG.md`
- Journal (weekly grade) -> `memory/WEEKLY-REVIEW.md`

All writes append. Never rewrite history.
