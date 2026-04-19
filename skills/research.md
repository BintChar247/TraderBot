# Skill: Research (pre-market)

Purpose: deep pre-market research on IDX catalysts. Output is appended to `memory/RESEARCH-LOG.md` as a single dated entry.

Canonical rules live in `memory/TRADING-STRATEGY.md`. Do not restate them here — read them.

## Inputs

- `date_wib` — today's date in Asia/Jakarta (YYYY-MM-DD, WIB).
- Macro snapshot hooks — results from the `macro.md` skill run earlier in the routine:
  - Newcastle coal price, palm oil (CPO), LME nickel
  - IDR/USD rate
  - IHSG level + 200-day MA position
  - Asia overnight (Nikkei, HSI, KOSPI)
- Tail of `memory/RESEARCH-LOG.md` (last 2 entries for continuity).
- Tail of `memory/TRADE-LOG.md` (current open positions — don't re-propose what's already held).

## Tools used

- **WebSearch** (native Claude tool) — for macro data, IDX catalysts, news
- **`scripts/market-data.sh`** — for OHLCV history, quotes, fundamentals (yfinance / GoAPI / Sectors.app)
- **`scripts/yfinance_helper.py`** — via market-data.sh; no direct calls

## Web search queries to issue

Run these verbatim (adapt the IDX adaptations in the TRADING-STRATEGY.md "Morning Research Queries" table). Issue at least six:

1. `Newcastle coal price today, palm oil price, nickel price LME`
2. `IHSG index level today, Asia markets overnight Nikkei HSI KOSPI`
3. `IDR USD exchange rate today, Bank Indonesia news`
4. `Top IDX stock catalysts today, Bank Indonesia rate decision`
5. `IDX earnings releases today, corporate actions RUPS dividend`
6. `Indonesia economic calendar CPI PDB BI rate this week`
7. `IDX sector momentum LQ45, foreign net buy sell IDX today`
8. Targeted per-sector query if a macro tailwind appears (e.g. `Indonesia coal exports China demand 2026`, `BBCA BBRI loan growth guidance Q1 2026`).

Record which queries fired and any non-obvious hit in the entry.

## Output schema (append to RESEARCH-LOG.md)

```
## {date_wib} — Pre-market Research

### Macro
<YAML block from macro.md skill>

### Screen Results
<from screen.md skill, if sector tailwind present; otherwise "No sector scan today">

### Candidate Trades

1. TICKER — Sector
   - Thesis (2 sentences, falsifiable):
   - Catalyst (specific, dated):
   - Proposed entry / stop / target:
   - R/R:
   - Why not skip:

2. ... (same structure)

3. ... (same structure)

### Notes
- Queries fired: ...
- Open positions that influenced selection: ...
```

## Rules

- Produce exactly 3 candidate trades unless the filter is dry.
- Each candidate must:
  - name a **specific** catalyst (earnings date, BI decision, commodity print, insider filing) — not "sector looks good"
  - state proposed entry, stop (7-10% below entry per TRADING-STRATEGY.md), target (>=2:1 R/R)
  - include a "why not skip" line answering: why bother with this one vs. holding cash?
- Do **not** re-propose tickers already in open positions (read TRADE-LOG.md tail).
- Do **not** size the trade here — that's the trade-plan skill's job.
- If no candidate clears the buy-side gate filter (catalyst + sector momentum + liquidity + fits macro), write:

  `No trades today — catalyst dry`

  and STOP. Do not pad the list. Overtrading is the sin. Patience beats activity (TRADING-STRATEGY.md hard rules).

## Handoff

The market-open routine reads today's `Candidate Trades` section. Only candidates the trade-plan skill later promotes get executed.
