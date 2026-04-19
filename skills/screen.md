# Skill: Sector Screen

Purpose: screen one IDX sector for candidates when the macro readout flags a tailwind. Not a market-wide scan — targeted.

Sector definitions and drivers live in `memory/TRADING-STRATEGY.md` "Sector Playbook". Read it to pick the sector.

## When to call

Called from the pre-market research routine *after* `macro.md`, *only if* the macro readout surfaces a sector tailwind, e.g.:

- Coal > $100/ton and firm → screen coal
- BI cutting or easing bias → screen banking, property
- Nickel rising, EV/smelter news → screen nickel
- IDR stable, inflation cool → screen consumer
- Data ARPU news → screen telco

If no macro tailwind, skip this skill.

## Procedure

1. Pick the sector from the playbook table (`banking`, `coal`, `nickel`, `telco`, `consumer`, `property`).
2. Run the quantitative screen:

   ```
   python scripts/yfinance_helper.py screen --sector <name> --table
   ```

   This returns each sector symbol's P/E, P/B, dividend yield, 20-day avg volume, 1-month price change.

3. Apply quantitative filter. A candidate must:
   - P/E below sector median **or** P/B below sector median
   - 20-day avg volume > 500,000 shares (matches buy-side gate check #7)
   - Not already in open positions (read TRADE-LOG.md tail)
4. For each quantitative pass (expect 2-5), do a qualitative catalyst check:
   - Web search: `<ticker> news earnings catalyst` and `<ticker> IDX filing insider`
   - Keep only names with a **specific dated catalyst** (earnings, RUPS, insider buy, commodity contract, regulatory).
5. Rank survivors by strength of catalyst × valuation gap.

## Output schema (goes into the Screen Results section of RESEARCH-LOG.md)

```
### Screen Results — {sector}

Macro trigger: <one line, e.g. "Newcastle coal $128, foreign buying coal 3 days">

| Ticker | P/E | P/B | Div% | AvgVol20d | 1M % | Catalyst | Keep? |
|--------|-----|-----|------|-----------|------|----------|-------|
| ADRO   | 4.2 | 0.9 | 8.1  | 42M       | +6   | Q1 earnings Apr 25 | Y |
| ITMG   | 5.8 | 1.4 | 9.3  | 11M       | +2   | None dated        | N |
| PTBA   | 6.1 | 1.6 | 7.2  | 18M       | +4   | Interim div May 3 | Y |

Candidates forwarded to research: ADRO, PTBA
```

## Rules

- Quantitative filter alone is not enough. A cheap stock without a catalyst is not a trade.
- Qualitative filter alone is not enough. A great story at a rich P/E waits.
- Liquidity is a hard cut — < 500k shares/day is out, no exceptions.
- Forward at most 3 tickers to the research skill. If more than 3 survive, keep the 3 with the strongest catalyst.
- If zero survive, write `No sector candidates — screen dry` and stop.
