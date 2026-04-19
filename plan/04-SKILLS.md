# Step 4 — Skills (What the Agent Can Do)

> *Skills are the tools the agent uses to do its job. Each skill is a focused capability.*

---

## What Are Skills?

A skill is a specific thing the agent knows how to do. Not a vague "strategy module" — a concrete action it can take during a session.

The agent doesn't need 50 skills. It needs a handful of sharp ones that map to its actual job: research IDX stocks, assess macro conditions, write theses, manage a portfolio.

---

## Core Skills

Each skill is called during one of the six routines defined in 05-ROUTINES.md. Output is logged to one of the five memory files in `memory/`.

### Skill 1: Research a Company (Tier 4 — Deep Fundamentals)

**When:** Pre-market research routine  
**Called by:** Pre-market session, for top 2-3 trade candidates  
**What it does:** Institutional-grade fundamental analysis of a single IDX stock  
**Reads:** market-data.sh (fundamentals + 30d history), WebSearch for news/filings/analyst views, RESEARCH-LOG.md  
**Writes:** "Top 3 Candidates" section of today's RESEARCH-LOG.md entry

The agent works through a structured checklist for each candidate:

**Fundamental case:**
1. Business quality — what does the company do, revenue/margin/ROE trends
2. Valuation — P/E, P/B vs sector averages and own history
3. Catalyst — specific event with expected date, why now
4. Analyst view — latest target price, recent upgrades/downgrades
5. Insider activity — recent director/commissioner share transactions
6. Conviction — HIGH / MEDIUM / LOW / AVOID

**Technical context** (Tier 6 — not a technical system, but informs entry timing):
- Current price vs 50-day and 200-day MA
- Volume vs 20-day average
- Nearest support (recent swing low) and resistance (recent swing high)
- Trend: uptrend / downtrend / range-bound

**Trade plan:**
- Entry price, stop level (% risk), target price (% upside)
- R:R ratio, position size (IDR and % of equity)

Output format: Structured candidate entry in RESEARCH-LOG.md with fundamental case, technical context, and concrete trade plan.

**Tools used:**
- `bash scripts/market-data.sh fundamentals [TICKER]` — financial statements, ratios
- `bash scripts/market-data.sh history [TICKER] 30d` — recent price/volume data
- `bash scripts/market-data.sh history [TICKER] 60d` — for technical context (MA, support/resistance)
- Claude WebSearch — `"[TICKER] earnings results"`, `"[TICKER] analyst target price"`, `"[TICKER] insider transaction"`
- File append — logged to RESEARCH-LOG.md

### Skill 2: Assess Macro Regime (Tiers 1-3 — Global, Flow, Calendar)

**When:** Pre-market research routine (morning analysis)  
**Called by:** Pre-market session  
**What it does:** Institutional-grade macro assessment across 3 tiers with ~22 data points  
**Reads:** market-data.sh (IHSG, commodities), WebSearch for overnight markets/flow/calendar  
**Writes:** Global Overnight, Macro Snapshot, Flow & Positioning, Corporate Calendar, Sector Momentum, and Macro Regime Assessment sections of today's RESEARCH-LOG.md entry

**Tier 1 — Global Overnight & Macro Snapshot** (~12 queries):
- US markets: S&P 500, Nasdaq, Dow close and change
- Asia: Shanghai Composite, Hang Seng, Nikkei 225 close
- IHSG level (via `bash scripts/market-data.sh index JKSE`)
- IDR/USD exchange rate
- Bond yields: US 10Y Treasury, Indonesia 10Y SUN, Indo-US spread
- VIX volatility index
- Commodities: Newcastle coal, CPO palm oil, LME nickel, Brent crude
  (via `bash scripts/market-data.sh commodity coal/nickel` + WebSearch)

**Tier 2 — Flow & Positioning** (~4 queries):
- KSEI foreign investor net buy/sell (IDR billions)
- Foreign flow streak (consecutive days net buy or sell)
- Top broker net buy/sell activity
- BI foreign reserves, MSCI EM Indonesia weight changes

**Tier 3 — Corporate Calendar & Events** (~6 queries):
- IDX earnings releases schedule this week
- Corporate actions: ex-dividend, rights issues, stock splits
- OJK new regulations and announcements
- Bank Indonesia rate decision schedule
- Indonesia economic data releases (CPI, GDP, trade balance)

**Regime classification output:**
- **CONSTRUCTIVE** — macro supports equity positions, full position sizing
- **CAUTIOUS** — mixed signals, reduce position sizes by 25%
- **DEFENSIVE** — adverse conditions, half positions, no new entries

Includes reasoning (2-3 sentences) and what would change the regime.

**Tools used:**
- `bash scripts/market-data.sh index JKSE` — IHSG level
- `bash scripts/market-data.sh commodity coal` / `nickel` — commodity prices
- Claude WebSearch — ~22 queries across overnight markets, FX, bonds, flow data, corporate calendar
- File append — logged to RESEARCH-LOG.md

### Skill 3: Scan Sentiment & News (Tier 5)

**When:** Pre-market research routine  
**Called by:** Pre-market session (after macro + flow assessment)  
**What it does:** Scans market-wide sentiment, unusual activity, and position-specific news  
**Reads:** WebSearch for catalysts/news, TRADE-LOG.md for held positions  
**Writes:** Sentiment findings feed into Sector Momentum table and Held Position Updates in RESEARCH-LOG.md

Queries (~5):
- Top IDX stock catalysts today
- Indonesia stock market news today
- Per held position: `"[TICKER] IDX news today"`
- IDX unusual volume stocks / saham volume tidak biasa
- IDX broker accumulation/distribution patterns

Also feeds into watchlist screening — stocks flagged for unusual volume or broker accumulation become deep-dive candidates (Skill 1).

**Tools used:**
- Claude WebSearch — ~5 targeted queries
- File append — findings integrated into RESEARCH-LOG.md sections

### Skill 4: Write Trade Plan

**When:** Pre-market → market-open handoff (morning plan finalization)  
**Called by:** Pre-market session (end) reads by market-open routine  
**What it does:** Translates research into specific trade ideas ready for execution  
**Reads:** RESEARCH-LOG.md (company analysis, macro, screen results), TRADING-STRATEGY.md (rules), TRADE-LOG.md (current portfolio)  
**Writes:** "Trade Ideas" section of RESEARCH-LOG.md

A trade plan entry includes:
- Ticker and direction (BUY/SELL)
- Thesis (2 sentences, falsifiable)
- Entry price target
- Position size (% of portfolio, respecting 20% max per position)
- Stop loss level
- Take-profit target
- What would invalidate the thesis

Market-open routine reads RESEARCH-LOG.md trade ideas and decides what to actually execute via broker.sh.

**Tools used:**
- File read — RESEARCH-LOG.md, TRADING-STRATEGY.md, TRADE-LOG.md
- File append — logged to RESEARCH-LOG.md

### Skill 5: Review Positions

**When:** Midday scan, EOD summary, weekly review routines  
**Called by:** These three routines  
**What it does:** Checks each open position against its original thesis, applies sell-side rules  
**Reads:** TRADE-LOG.md (open positions, entry theses), current market data via yfinance  
**Writes:** Appends position review notes to TRADE-LOG.md with action recommendations

For each position, asks:
- Is the thesis still intact?
- Has the catalyst played out?
- Is the stop loss close to triggering?
- Has anything changed fundamentally?
- Apply sell-side rules: -7% hard stop, trailing stop tightening, thesis check
- Verdict: HOLD / ADD / TRIM / EXIT

Output: Dated entry in TRADE-LOG.md with position symbol, current price, thesis status, and action recommendation.

**Tools used:**
- `yfinance` — current prices, intraday moves
- File read/append — TRADE-LOG.md

### Skill 6: Journal & Reflect

**When:** EOD summary (every trading day) and weekly review (Saturday)  
**Called by:** Daily summary and weekly review routines  
**What it does:** Documents what happened and extracts lessons  
**Reads:** Today's trades, morning plan from RESEARCH-LOG.md, portfolio changes in TRADE-LOG.md  
**Writes:** EOD snapshots to TRADE-LOG.md, weekly lessons to WEEKLY-REVIEW.md, strategy updates (if earned) to TRADING-STRATEGY.md

EOD journal entry (TRADE-LOG.md):
- What trades executed today, why
- Positions closed, rationale
- Win/loss summary
- Key learnings for tomorrow

Weekly review entry (WEEKLY-REVIEW.md):
- Week summary: wins, losses, themes
- Process breakdowns or wins
- Specific lessons to apply next week
- If a strategy rule was validated/broken, update it

This is where the agent compounds knowledge. Entries must be:
- Specific (name tickers, prices, dates, actual P&L)
- Honest (acknowledge mistakes and false signals)
- Actionable (write lessons future-self can apply)

**Tools used:**
- File read — TRADE-LOG.md, RESEARCH-LOG.md
- File append — TRADE-LOG.md, WEEKLY-REVIEW.md, TRADING-STRATEGY.md

---

## What the Agent Uses for Research (Not Perplexity)

We use Claude's native capabilities and a 3-tier real-time data stack. No external AI services.

| Need | Tool | Command / How |
|------|------|---------------|
| Real-time price | GoAPI.io (primary) | `bash scripts/market-data.sh quote BBCA` |
| Intraday bars | yfinance (secondary) | `bash scripts/market-data.sh intraday BBCA 1m` |
| Daily OHLCV | yfinance | `bash scripts/market-data.sh history BBCA 30d` |
| Fundamentals | Sectors.app → yfinance | `bash scripts/market-data.sh fundamentals BBCA` |
| IHSG index | GoAPI → yfinance ^JKSE | `bash scripts/market-data.sh index JKSE` |
| Commodities | WebSearch | `bash scripts/market-data.sh commodity coal` |
| News & catalysts | Claude WebSearch | Built-in, ~30 queries in pre-market |
| Flow data (KSEI) | Claude WebSearch | Foreign net buy/sell, broker activity |
| Corporate calendar | Claude WebSearch | Earnings, ex-div, OJK filings |
| Macro / BI rate | Claude WebSearch | Bond yields, rate decisions, CPI |
| Company analysis | Claude reasoning | The agent IS the analyst |

**Data priority:** GoAPI (near real-time, free tier) → yfinance (~15 min delayed, free) → Sectors.app (fundamentals, free tier) → WebSearch (news/macro, built-in).

All data calls go through `scripts/market-data.sh` which handles fallback automatically. Never call APIs directly with curl or Python.

No external AI services. No Perplexity. No ChatGPT. Claude does its own thinking.

---

## Skills the Agent Does NOT Have

Being explicit about what the agent can't do prevents confusion:

- **No options trading** — can't price or trade options
- **No shorting** — long only on IDX
- **No leverage** — no margin
- **No real-time streaming** — uses polling/scheduled sessions, not websocket feeds
- **No other AI models** — no GPT, no Perplexity, no external LLMs

---

## Memory File Reference

All skill output flows into these five files in `memory/`:

1. **TRADING-STRATEGY.md** — Updated when a rule is validated or broken. Stores conviction rules, position sizing rules, macro regime thresholds, and stop-loss rules.
2. **TRADE-LOG.md** — Append-only log of executions, position reviews, and EOD summaries. Includes open position tracking and thesis statements.
3. **RESEARCH-LOG.md** — Append-only log of company analyses, macro assessments, screen results, and trade ideas. The input to decision-making.
4. **WEEKLY-REVIEW.md** — Append-only log of weekly reflections, process observations, and lessons learned.
5. **PROJECT-CONTEXT.md** — Project metadata, rules, portfolio constraints, and reference data.

Each entry is dated and timestamped for traceability.

---

## Deliverables

- [ ] Implement company research skill (yfinance + web search → RESEARCH-LOG.md entry)
- [ ] Implement macro regime assessment skill (daily morning macro section in RESEARCH-LOG.md)
- [ ] Implement watchlist screen skill (screen results section in RESEARCH-LOG.md)
- [ ] Implement trade plan writer skill (trade ideas section in RESEARCH-LOG.md, for market-open routine to execute)
- [ ] Implement position review skill (position checks logged to TRADE-LOG.md with action recommendations)
- [ ] Implement journal/reflection skill (EOD entries in TRADE-LOG.md, weekly entries in WEEKLY-REVIEW.md, strategy updates in TRADING-STRATEGY.md)
- [ ] Test: research BBCA end-to-end, verify RESEARCH-LOG.md entry quality
- [ ] Test: macro assessment produces valid regime classification and is written to RESEARCH-LOG.md
- [ ] Test: watchlist screen returns reasonable candidates and appears in RESEARCH-LOG.md
- [ ] Test: position review skill checks thesis integrity and logs to TRADE-LOG.md
