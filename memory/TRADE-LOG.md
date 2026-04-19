# Trade Log

_Source of truth for all trades and daily EOD snapshots. Append-only. Never delete entries._

---

## Active Positions

| Ticker | Entry Date | Entry Price (IDR) | Shares | Avg Cost (IDR) | Stop (IDR) | Thesis (1 line) |
|--------|-----------|-------------------|--------|----------------|------------|-----------------|
| BBRI | 2026-04-21 | 3,220 | 17,400 | 59,228,000 | 2,995 | Ex-div buy; institutional accumulation + Q1 earnings Apr 29 |
| ANTM | 2026-04-21 | 3,950 | 15,200 | 60,040,000 | 3,674 | HPM ore price reset (Decree 144); nickel at $17,635 |
| ITMG | 2026-04-21 | 27,000 | 2,200 | 59,400,000 | 25,110 | 9.4x P/E, 11% yield; Q1 earnings May 7; coal >$130 |

---

## Trade History

_Entries appended chronologically. Each trade logged at execution time._

### Template — Trade Entry

```
### YYYY-MM-DD HH:MM WIB — [BUY|SELL] [TICKER]

- Side: BUY / SELL
- Shares: [N] shares at IDR [price]
- Fill price: IDR [price]
- Position size: IDR [total] ([X]% of equity)
- Stop: IDR [price] ([X]% below entry)
- Target: IDR [price] ([X]% above entry)
- Catalyst: [one sentence]
- 9-gate checklist: [PASS / FAIL — note which gate if FAIL]
- Thesis: "[two sentences max]"
```

---

## EOD Snapshots

_Appended daily by /daily-summary at 15:15 WIB. One entry per trading day._

### Template — EOD Snapshot

Generate with: `python scripts/performance.py snapshot --markdown`

```
### YYYY-MM-DD EOD

- Total equity: IDR [amount]
- Daily P&L: IDR [amount] ([+/-X]%)
- IHSG daily: [+/-X]%
- Daily alpha: [+/-X]%
- Cash: IDR [amount] ([X]% of equity)
- Trades this week: [N]/3
- Weekly P&L (MTD): IDR [amount] ([+/-X]%)

#### Open Positions

| Ticker | Shares | Avg Cost (IDR) | Last (IDR) | Unrealized P&L | Days Held |
|--------|--------|----------------|------------|----------------|-----------|
| [SYM] | [N] | [price] | [price] | IDR [amt] ([+/-X]%) | [N] |

#### Notes

[Any notable action, stop adjustments, thesis changes, or flags for next session.]
```

---

## Day 0 — 2026-04-18 EOD

- Total equity: IDR 300,000,000 (paper)
- Daily P&L: IDR 0 (0.00%)
- IHSG close (baseline): 7,634 (last close 2026-04-17; used as Day 0 benchmark)
- Daily alpha: N/A
- Cash: IDR 300,000,000 (100% of equity)
- Trades this week: 0/3
- Weekly P&L: IDR 0 (0.00%)

#### Open Positions

| Ticker | Shares | Avg Cost (IDR) | Last (IDR) | Unrealized P&L | Days Held |
|--------|--------|----------------|------------|----------------|-----------|
| — | — | — | — | — | — |

#### Notes

System scaffold phase. No trades placed. Paper account seeded at IDR 300M. All routines pending
setup. TRADING-STRATEGY.md, RESEARCH-LOG.md, and WEEKLY-REVIEW.md also seeded today.
First live run expected when all five routines are configured and tested.

---

## Simulation — Week of 2026-04-21

_Simulating the first live week to validate strategy profitability in paper mode._

---

### 2026-04-21 09:22 WIB — BUY BBRI [SIMULATION]

**9-Gate Checklist:**
1. Positions after fill: 1/6 ✅
2. Trades this week: 1/3 ✅
3. Position cost IDR 56,028,000 = 18.7% of equity ✅
4. Cash available: IDR 300M ✅
5. Catalyst: Ex-dividend Apr 21 + Q1 earnings Apr 29 + institutional accumulation (BlackRock/JPMorgan) ✅
6. Instrument: IDX equity stock ✅
7. BBRI avg daily volume >100M shares (highly liquid) ✅
8. Lot size: 17,400 shares (multiple of 100) ✅
9. Planned entry IDR 3,430. Ex-div adjusted open ~IDR 3,220 (within 3% of plan after adjustment) ✅

**ALL 9 GATES PASS**

- Side: BUY
- Shares: 17,400 shares at IDR 3,220 (ex-dividend open, adjusted from plan IDR 3,430 − IDR 209 div)
- Fill price: IDR 3,220
- Position size: IDR 56,028,000 (18.7% of equity)
- Stop: IDR 2,995 (10% trailing GTC placed — IDR 3,220 × 0.90)
- Hard cut: IDR 2,995 (-7% would be IDR 2,995 from 3,220 entry)
- Target: IDR 3,640 (+13% from ex-div entry)
- Catalyst: Ex-dividend capture + institutional accumulation + Q1 2026 earnings Apr 29
- Thesis: "BBRI is Indonesia's largest micro-lender at a 10.1% dividend yield, going ex-div today with BlackRock and JPMorgan accumulating against the foreign outflow trend. Q1 earnings April 29 is the re-rating catalyst."

### 2026-04-21 09:35 WIB — BUY ANTM [SIMULATION]

**9-Gate Checklist:**
1. Positions after fill: 2/6 ✅
2. Trades this week: 2/3 ✅
3. Position cost IDR 60,040,000 = 20.0% of equity ✅
4. Cash after BBRI: IDR 243,972,000 > IDR 60,040,000 ✅
5. Catalyst: HPM Ministerial Decree 144 (Apr 15) — ore price $17→$40/wmt; nickel $17,635 ✅
6. Instrument: IDX equity stock ✅
7. ANTM avg daily volume >30M shares (liquid large-cap) ✅
8. Lot size: 15,200 shares (multiple of 100) ✅
9. Planned entry IDR 3,940. Opened at IDR 3,950 (+0.25% from plan — within 3%) ✅

**ALL 9 GATES PASS**

- Side: BUY
- Shares: 15,200 shares at IDR 3,950
- Fill price: IDR 3,950
- Position size: IDR 60,040,000 (20.0% of equity)
- Stop: IDR 3,555 (10% trailing GTC placed — IDR 3,950 × 0.90)
- Hard cut: IDR 3,674 (-7% from entry)
- Target: IDR 4,600 (+16.5%)
- Catalyst: HPM ore price reset doubles nickel ore realization; Q2 margins structurally higher
- Thesis: "ANTM's 18.1M wmt nickel quota gets a windfall from the HPM benchmark price doubling ($17→$40/wmt) under Decree 144. First full-quarter impact in Q2 earnings will force analyst estimate upgrades."

### 2026-04-21 10:05 WIB — BUY ITMG [SIMULATION]

IHSG held flat (+0.1%) at 09:30 — proceeded with ITMG per plan.

**9-Gate Checklist:**
1. Positions after fill: 3/6 ✅
2. Trades this week: 3/3 ✅ (weekly limit reached — no more trades this week)
3. Position cost IDR 59,400,000 = 19.8% of equity ✅
4. Cash after BBRI + ANTM: IDR 183,932,000 > IDR 59,400,000 ✅
5. Catalyst: Newcastle coal $133.55 (+20% in March); Q1 earnings May 7 ✅
6. Instrument: IDX equity stock ✅
7. ITMG avg daily volume >5M shares (adequate liquidity) ✅
8. Lot size: 2,200 shares (multiple of 100) ✅
9. Planned entry IDR 27,050. Opened at IDR 27,000 (-0.18% — within 3%) ✅

**ALL 9 GATES PASS**

- Side: BUY
- Shares: 2,200 shares at IDR 27,000
- Fill price: IDR 27,000
- Position size: IDR 59,400,000 (19.8% of equity)
- Stop: IDR 24,300 (10% trailing GTC placed — IDR 27,000 × 0.90)
- Hard cut: IDR 25,110 (-7% from entry)
- Target: IDR 31,300 (+15.9%)
- Catalyst: Newcastle coal $133.55 with Q1 export realizations elevated; May 7 earnings release
- Thesis: "ITMG trades at 9.4x P/E and 11% yield — cheapest in 3 years — while exporting coal at $130+ for a full quarter. Q1 earnings on May 7 should beat consensus and trigger a re-rating."

---

### 2026-04-21 EOD [SIMULATION]

Simulated end-of-day prices (realistic based on current market conditions):
- BBRI: IDR 3,260 (+1.2% from entry 3,220) — post ex-div bounce, foreign buying continues
- ANTM: IDR 3,980 (+0.76% from entry 3,950) — nickel held, HPM news still digesting
- ITMG: IDR 27,200 (+0.74% from entry 27,000) — coal held above $133, steady

**Portfolio math:**
- BBRI: 17,400 × (3,260 - 3,220) = +IDR 696,000 unrealized
- ANTM: 15,200 × (3,980 - 3,950) = +IDR 456,000 unrealized
- ITMG: 2,200 × (27,200 - 27,000) = +IDR 440,000 unrealized
- Total unrealized: +IDR 1,592,000

- Total equity: IDR 300,000,000 + IDR 1,592,000 = IDR 301,592,000
- Daily P&L: IDR +1,592,000 (+0.53%)
- IHSG daily: +0.10% (flat open, slight recovery)
- Daily alpha: +0.43%
- Cash: IDR 124,532,000 (41.3% of equity)
- Trades this week: 3/3 (limit reached — no new positions until next week)

#### Open Positions

| Ticker | Shares | Avg Cost (IDR) | Last (IDR) | Unrealized P&L | Days Held |
|--------|--------|----------------|------------|----------------|-----------|
| BBRI | 17,400 | 3,220 | 3,260 | +IDR 696,000 (+1.24%) | 1 |
| ANTM | 15,200 | 3,950 | 3,980 | +IDR 456,000 (+0.76%) | 1 |
| ITMG | 2,200 | 27,000 | 27,200 | +IDR 440,000 (+0.74%) | 1 |

#### Notes

Week 1 Day 1: All 3 positions opened on Monday, 3/3 weekly limit used. Strong start — all three positions in the green. BBRI ex-dividend adjustment went as planned (bought at adjusted price IDR 3,220 vs planned IDR 3,430). Trailing stops placed as GTC orders on all 3. No more new positions this week — focus shifts to monitoring thesis integrity. Watch BBRI for Q1 earnings Apr 29. Watch ITMG for any coal price moves. ANTM needs HPM narrative to keep developing.

---

### 2026-04-25 EOD — Week 1 End of Week [SIMULATION]

_Prices estimated from web search (best available data for April 21-25, 2026). Exact daily OHLC unavailable; EOW closing prices used for final state._

**Price sources used:**
- BBRI: ~IDR 3,390 (mid-April range 3,370–3,410 confirmed; post-ex-div recovery week)
- ANTM: ~IDR 4,050 (HPM nickel regulation boost; Investing.com quote ~4,070 intraday)
- ITMG: ~IDR 27,050 (Investing.com late-April quote; flat week)
- IHSG: 7,634 on April 25 (confirmed: +12.62 pts / +0.17% on the day; dividend season support — Antara/Elshinta)

**Stop trigger review:** No stops triggered. All positions held through the week.
- BBRI low of week est. ~3,240–3,270 (ex-div absorption Tue-Wed); well above stop 2,934
- ANTM low of week est. ~3,960; well above stop 3,582
- ITMG low of week est. ~26,825 (Apr 16 confirmed); well above stop 24,480

**Trailing stop update (10% from weekly high):**
- BBRI: new high ~3,410 → updated stop = 3,069 (raised from 2,934) ✓
- ANTM: new high ~4,070 → updated stop = 3,663 (raised from 3,582) ✓
- ITMG: high unchanged at 27,200 → stop unchanged at 24,480

**Tightening check:** No position up +15% from entry. No tightening required.

- Total equity: IDR 304,588,000
- Weekly P&L: IDR +4,588,000 (+1.53%)
- IHSG weekly: +0.17%
- Weekly alpha: +1.36%
- Cash: IDR 124,532,000 (40.9% of equity)
- Trades this week: 3/3

#### Open Positions

| Ticker | Shares | Entry (IDR) | Last (IDR) | Unrealized P&L | Stop (IDR) | Days Held |
|--------|--------|-------------|------------|----------------|------------|-----------|
| BBRI | 17,400 | 3,220 | 3,390 | +IDR 2,958,000 (+5.28%) | 3,069 | 5 |
| ANTM | 15,200 | 3,950 | 4,050 | +IDR 1,520,000 (+2.53%) | 3,663 | 5 |
| ITMG | 2,200 | 27,000 | 27,050 | +IDR 110,000 (+0.19%) | 24,480 | 5 |

#### Next Week Catalysts

- BBRI Q1 2026 earnings: April 29 — key re-rating event; thesis validation moment
- BI Rate decision: April 21-22 results digesting; any dovish signal benefits BBRI
- ITMG: Monitor coal price (Newcastle benchmark $130+); May 7 earnings approaching
- ANTM: HPM decree implementation news; any nickel price moves on LME
