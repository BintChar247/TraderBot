# Skill: Journal

Purpose: the append-only journaling skill. Used by market-open (trade entries), midday (position actions), daily-summary (EOD snapshot), and weekly-review (weekly grade).

Canonical principle: **the files ARE the edge** (see `memory/PROJECT-CONTEXT.md`). This skill never rewrites history. Always append. Dates in WIB.

## When to call

| Routine         | Entry kind            | Target file         |
|-----------------|-----------------------|---------------------|
| market-open     | Trade Entry           | TRADE-LOG.md        |
| midday          | Position Action       | TRADE-LOG.md        |
| daily-summary   | EOD Snapshot          | TRADE-LOG.md        |
| weekly-review   | Weekly Grade          | WEEKLY-REVIEW.md    |

## Formats

### 1. Trade Entry (TRADE-LOG.md)

```
## {YYYY-MM-DD WIB} — Trade Entry: BUY TICKER

- Fill: qty {n} @ IDR {price} (total IDR {cost})
- Stop (GTC): IDR {stop} ({-x.x}% from entry)
- Target: IDR {target} ({+y.y}%, R/R {r}:1)
- Sector: {sector}
- Thesis (2 sentences): {...}
- Catalyst: {specific + dated}
- Invalidation: {what would break the thesis}
- Research ref: RESEARCH-LOG.md {date} candidate #{n}
- Broker tag: {tag passed to broker.sh}
```

### 2. Position Action (TRADE-LOG.md)

```
## {YYYY-MM-DD hh:mm WIB} — Position Action: {ACTION} TICKER

- Rule fired: {e.g. "-7% hard cut" | "+15% trail tighten" | "thesis broken" | "sector exit"}
- Before: entry IDR {e}, current IDR {c}, P&L {pct}%
- After: {closed @ IDR x | stop moved from y to z | trimmed n shares}
- Thesis status: {intact | broken — one-line reason}
```

### 3. EOD Snapshot (TRADE-LOG.md, once per trading day)

```
## {YYYY-MM-DD WIB} — EOD Snapshot

Account:
- Equity: IDR {e} ({+/-x.x}% day, {+/-y.y}% WTD)
- Cash: IDR {c} ({d}% of equity)
- Deployed: {p}% (target 75-85%)

Open positions ({n}/6):
| Symbol | Qty | Entry | Last | P&L % | Stop | Sector | Days held |
|--------|-----|-------|------|-------|------|--------|-----------|
| ...    | ... | ...   | ...  | ...   | ...  | ...    | ...       |

Today's closes:
- {TICKER}: {reason, realized P&L}

Trades this week: {k}/3

Notes:
- {one-to-three terse lines: what worked, what didn't, what to watch tomorrow}
```

### 4. Weekly Grade (WEEKLY-REVIEW.md)

```
## Week of {YYYY-MM-DD} — Weekly Review

Grade: {A | B | C | D | F}

Numbers:
- Start equity: IDR {s}
- End equity: IDR {e} ({+/-x.x}%)
- IHSG week: {+/-y.y}% — alpha: {z}pp
- Trades: {opened} open / {closed} closed / {win} win / {loss} loss
- Best: {ticker, P&L}
- Worst: {ticker, P&L}

What worked:
- {bullet}

What broke:
- {bullet — name the rule, name the trade}

Lessons (future-self actionable):
- {bullet}

Rule changes proposed (if any, 2+ weeks of evidence):
- {bullet, or "none"}

Next week plan:
- Sectors to watch: {...}
- Risks: {...}
```

## Rules

- **Never rewrite** an existing entry. Append only. If a past entry needs correction, append a correction note referencing the original date.
- All dates are WIB. Timestamps `hh:mm WIB` in 24h.
- Be specific (tickers, prices, IDR amounts). Vague journals are useless six months later.
- Be honest — mistakes are inputs to the next week's edge (see PROJECT-CONTEXT.md "files are the brain").
- Trade Entry and Position Action are written only **after** the broker fill confirms.
- No emojis.
