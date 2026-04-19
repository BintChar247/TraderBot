# Step 8 — Two-Week Paper Trial

> *No real money. No broker account needed. The bot researches real markets, makes real decisions, tracks simulated trades against real prices. Pure benchmark.*

---

## Trial Parameters

| Parameter | Value |
|-----------|-------|
| **Mode** | Paper only — no real broker, no real orders |
| **Start date** | Monday, 20 April 2026 |
| **End date** | Friday, 1 May 2026 (10 trading days) |
| **Starting capital** | IDR 300,000,000 (simulated) |
| **Benchmark** | IHSG (^JKSE) |
| **Goal** | Beat IHSG over 2 weeks while following all rules |
| **Universe** | IDX equities — LQ45 + watchlist |
| **Trading mode env var** | `TRADING_MODE=paper` (hardcoded, no live option) |

---

## What "Paper Trade" Means in This System

There is no real broker connection. The bot:

1. **Researches real markets** — uses live IHSG data, real commodity prices, real news via web search and yfinance
2. **Makes real decisions** — applies the full buy-side gate, writes real theses, picks real tickers
3. **Simulates execution** — records "filled" trades at the current market price from yfinance
4. **Tracks simulated P&L** — uses real closing prices each day to mark positions to market
5. **Enforces all rules** — trailing stops, -7% cuts, 3 trades/week, everything

The only thing that doesn't happen is an actual order hitting the exchange.

### How the Simulated Broker Works

`scripts/broker.sh` in paper mode:

| Command | What It Does (Paper Mode) |
|---------|---------------------------|
| `account` | Reads `memory/TRADE-LOG.md` for latest portfolio snapshot. Returns simulated equity, cash, buying power. |
| `positions` | Reads active positions table from `memory/TRADE-LOG.md`. Fetches current prices via yfinance to compute unrealized P&L. |
| `quote SYM` | Fetches real-time quote from yfinance (`SYM.JK`). Real market data. |
| `orders` | Returns open simulated orders from `memory/TRADE-LOG.md`. |
| `order '<json>'` | Logs the trade to `memory/TRADE-LOG.md` at current yfinance price. No actual execution. |
| `close SYM` | Logs the exit at current yfinance price. Updates positions. |

All state lives in `memory/TRADE-LOG.md`. No external broker API needed.

---

## Trial Schedule

### Week 1: April 20-24, 2026

| Day | Date | Routines | What Happens |
|-----|------|----------|--------------|
| Mon | Apr 20 | Pre-market, Market-open, Midday, EOD | Day 1. First research, possibly first trades. |
| Tue | Apr 21 | Pre-market, Market-open, Midday, EOD | Review Day 1 positions. New research. |
| Wed | Apr 22 | Pre-market, Market-open, Midday, EOD | Midweek check. May hit 3-trade weekly limit. |
| Thu | Apr 23 | Pre-market, Market-open, Midday, EOD | Likely no new trades (limit reached). Monitor. |
| Fri | Apr 24 | Pre-market, Market-open, Midday, EOD, **Weekly Review** | Week 1 recap. Letter grade. Lessons. |

### Week 2: April 28 - May 1, 2026

| Day | Date | Routines | What Happens |
|-----|------|----------|--------------|
| Mon | Apr 28 | Pre-market, Market-open, Midday, EOD | Week 2 starts. Apply Week 1 lessons. |
| Tue | Apr 29 | Pre-market, Market-open, Midday, EOD | Continue. |
| Wed | Apr 30 | Pre-market, Market-open, Midday, EOD | Midweek. |
| Thu | May 1 | **May Day — IDX closed** | No trading. |
| Fri | May 2 | Pre-market, Market-open, Midday, EOD, **Final Review** | Trial ends. Final scorecard. |

*Note: May 1 is a public holiday (International Workers' Day). IDX is closed. The bot should detect this and skip. If Apr 25 (Fri of Week 1) is also a holiday, the weekly review shifts to Thursday.*

---

## Day 0 Setup (Today — Sunday, April 19)

Everything needed before the first routine fires Monday morning:

### Files to Create

```
idx-trader/
├── CLAUDE.md                         # Agent rulebook
├── .gitignore                        # Exclude .env
├── env.template                      # Reference only
├── scripts/
│   ├── broker.sh                     # Paper broker (yfinance-backed)
│   └── notify.sh                     # Notification wrapper
├── memory/
│   ├── TRADING-STRATEGY.md           # Rulebook (from 00-STRATEGY)
│   ├── TRADE-LOG.md                  # Seeded with Day 0 snapshot
│   ├── RESEARCH-LOG.md               # Empty, ready for Monday
│   ├── WEEKLY-REVIEW.md              # Empty template
│   └── PROJECT-CONTEXT.md            # Trial parameters
├── routines/
│   ├── pre-market.md                 # Cloud routine prompt
│   ├── market-open.md
│   ├── midday.md
│   ├── daily-summary.md
│   └── weekly-review.md
├── .claude/
│   └── commands/                     # Local slash commands
│       ├── portfolio.md
│       ├── trade.md
│       └── ... (5 more)
└── dashboard/
    ├── index.html                    # Performance dashboard
    └── data.json                     # Updated by daily-summary
```

### Day 0 Seed: TRADE-LOG.md

```markdown
# Trade Log

## Active Positions
| Ticker | Entry Date | Entry Price | Shares | Cost (IDR) | Stop | Thesis |
|--------|-----------|-------------|--------|------------|------|--------|
| — | — | — | — | — | — | No positions yet |

## Trade History
(No trades yet — trial starts Monday Apr 20)

---

## EOD Snapshots

### 2026-04-19 — Day 0 (Pre-Trial Baseline)
- **Portfolio value:** IDR 300,000,000
- **Cash:** IDR 300,000,000 (100%)
- **Positions:** 0
- **IHSG close:** (record Sunday reference level)
- **Trades this week:** 0/3
- **Daily P&L:** —
- **Cumulative P&L:** 0.00%
- **Cumulative IHSG:** 0.00%
- **Alpha:** 0.00%
```

### Day 0 Seed: PROJECT-CONTEXT.md

```markdown
# Project Context

## Mission
Two-week paper trading trial. Beat IHSG using fundamental swing trading on IDX.
No real money. Simulation only.

## Trial Window
- Start: Monday, 20 April 2026
- End: Friday, 2 May 2026
- Trading days: 10 (excluding holidays)

## Account
- Starting capital: IDR 300,000,000 (simulated)
- Broker: Paper mode (yfinance prices, no real execution)
- Currency: IDR

## Market
- Exchange: Indonesia Stock Exchange (IDX)
- Hours: 09:00-15:00 WIB (UTC+7), lunch break 11:30-13:30
- Settlement: T+2 (simulated)
- Lot size: 100 shares
- Benchmark: IHSG (^JKSE)

## Timezone
- All times in WIB (Asia/Jakarta, UTC+7)

## Tools
- Research: Claude web search + yfinance
- Execution: Paper broker (scripts/broker.sh in paper mode)
- Notifications: scripts/notify.sh
- No external AI services
```

---

## Dashboard

The performance dashboard is an interactive HTML page at `dashboard/index.html`. It visualizes the bot's paper trading results.

### What the Dashboard Shows

1. **Header:** Trial dates, days elapsed, starting capital
2. **Scorecard:** Current portfolio value, total return %, IHSG return %, alpha
3. **Equity curve:** Line chart — portfolio value vs IHSG (rebased to 100) over the trial
4. **Daily P&L bar chart:** Green/red bars for each trading day
5. **Open positions table:** Ticker, entry, current price, unrealized P&L, stop level
6. **Trade history:** All simulated trades with entry/exit, P&L, thesis
7. **Stats:** Win rate, avg winner/loser, profit factor, max drawdown, trades used vs limit

### How the Dashboard Gets Updated

The daily-summary routine writes `dashboard/data.json` at EOD:

```json
{
  "trial": {
    "start_date": "2026-04-20",
    "end_date": "2026-05-02",
    "starting_capital": 300000000,
    "currency": "IDR"
  },
  "daily": [
    {
      "date": "2026-04-20",
      "portfolio_value": 300000000,
      "cash": 285000000,
      "daily_pnl_pct": 0.0,
      "cumulative_pnl_pct": 0.0,
      "ihsg_close": 7245.0,
      "ihsg_cumulative_pct": 0.0,
      "alpha_pct": 0.0,
      "positions_count": 1,
      "trades_today": 1,
      "trades_this_week": 1
    }
  ],
  "positions": [
    {
      "ticker": "BBCA",
      "entry_date": "2026-04-20",
      "entry_price": 9200,
      "shares": 500,
      "current_price": 9250,
      "unrealized_pnl_pct": 0.54,
      "stop_level": 8280,
      "thesis": "NIM expansion + BI rate cut cycle"
    }
  ],
  "trades": [
    {
      "date": "2026-04-20",
      "ticker": "BBCA",
      "side": "buy",
      "shares": 500,
      "price": 9200,
      "cost": 4600000,
      "thesis": "NIM expansion + BI rate cut cycle",
      "status": "open"
    }
  ],
  "stats": {
    "total_return_pct": 0.0,
    "ihsg_return_pct": 0.0,
    "alpha_pct": 0.0,
    "win_rate": 0.0,
    "total_trades": 1,
    "winning_trades": 0,
    "losing_trades": 0,
    "avg_winner_pct": 0.0,
    "avg_loser_pct": 0.0,
    "profit_factor": 0.0,
    "max_drawdown_pct": 0.0,
    "best_trade": null,
    "worst_trade": null
  }
}
```

The dashboard reads this JSON and renders everything client-side. No server needed — just open the HTML file in a browser.

---

## Success Criteria

After 2 weeks, the trial is a success if:

| Criteria | Target | How Measured |
|----------|--------|--------------|
| **Beat IHSG** | Positive alpha over trial | Cumulative return - IHSG return |
| **Rule compliance** | Zero rule violations | No trade that failed a gate check, no missing stops |
| **Trade discipline** | <= 3 trades/week every week | Trade count in TRADE-LOG |
| **Research quality** | Every trade has documented catalyst | RESEARCH-LOG entries |
| **Loss management** | No single position lost > -10% | TRADE-LOG exit prices |
| **Consistency** | All 5 routines ran every trading day | Git commit history |
| **Learning** | Week 2 lessons reference Week 1 | WEEKLY-REVIEW.md |

### What Happens After the Trial

| Outcome | Next Step |
|---------|-----------|
| Beat IHSG + rules followed | Consider extending to 4 weeks, then evaluate live |
| Beat IHSG but rules broken | Fix the rule violations. Run another 2 weeks. |
| Underperformed but rules followed | Review strategy. Adjust TRADING-STRATEGY.md. Run again. |
| Underperformed + rules broken | Major rethink. The system isn't ready. |

---

## Differences from Full Production Plan

| Aspect | Paper Trial | Production |
|--------|-------------|------------|
| Broker | Simulated (yfinance prices) | Real IDX broker API |
| Money | IDR 300M simulated | Real capital |
| Orders | Logged to TRADE-LOG.md only | Sent to exchange |
| Stops | Tracked in memory, checked at midday | Real GTC orders on exchange |
| Slippage | None (fills at yfinance price) | Real market impact |
| Settlement | Ignored (instant) | T+2 IDX rules |
| Notifications | Optional (can be silent) | Active (Telegram) |
| Dashboard | Updated daily | Updated daily |
| Duration | 2 weeks fixed | Ongoing |

### Limitations to Be Aware Of

Paper trading has blind spots:

1. **No slippage** — real orders move the market, especially on smaller IDX stocks
2. **No partial fills** — paper assumes full fill at quoted price
3. **No T+2 constraints** — paper ignores settlement; real trading limits buying power
4. **Trailing stops are honor-system** — in paper mode, the midday scan checks stop levels manually. Real GTC orders execute automatically even if the bot is offline
5. **Volume impact unknown** — paper can "buy" any amount; real orders in thin stocks may not fill

These are acceptable for a 2-week benchmark. Just know that real performance will differ.

---

## Deliverables (Day 0 — Today)

- [ ] Create repo with full directory structure
- [ ] Write CLAUDE.md
- [ ] Write scripts/broker.sh (paper mode — yfinance backed)
- [ ] Write scripts/notify.sh
- [ ] Seed memory/TRADING-STRATEGY.md
- [ ] Seed memory/TRADE-LOG.md (Day 0 snapshot, IDR 300M)
- [ ] Seed memory/RESEARCH-LOG.md (empty)
- [ ] Seed memory/WEEKLY-REVIEW.md (empty template)
- [ ] Seed memory/PROJECT-CONTEXT.md (trial parameters)
- [ ] Write 5 routine prompts in routines/
- [ ] Write 7 slash commands in .claude/commands/
- [ ] Build dashboard/index.html
- [ ] Write dashboard/data.json (Day 0 seed)
- [ ] Set up 5 cloud routines with cron schedules
- [ ] Smoke test: run /portfolio — should show IDR 300M, 0 positions
- [ ] Verify: pre-market routine runs, writes RESEARCH-LOG, commits
- [ ] Everything ready before market open Monday 09:00 WIB
