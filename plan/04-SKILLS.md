# Step 4 — Skills (What the Agent Can Do)

> *Skills are the tools the agent uses to do its job. Each skill is a focused capability.*

---

## What Are Skills?

A skill is a specific thing the agent knows how to do. Not a vague "strategy module" — a concrete action it can take during a session.

The agent doesn't need 50 skills. It needs a handful of sharp ones that map to its actual job: research IDX stocks, assess macro conditions, write theses, manage a portfolio.

---

## Core Skills

Each skill is called during one of the six routines defined in 05-ROUTINES.md. Output is logged to one of the five memory files in `memory/`.

### Skill 1: Research a Company

**When:** Pre-market research routine  
**Called by:** Which routine? Pre-market session  
**What it does:** Deep fundamental analysis of a single IDX stock  
**Reads:** yfinance data, web search for news/filings, existing RESEARCH-LOG.md  
**Writes:** Appends findings to RESEARCH-LOG.md under dated "Company Analysis" section

The agent works through the Investment Checklist:

1. Business quality — what does the company do, revenue/margin/ROE trends
2. Valuation — P/E, P/B, dividend yield vs sector and own history
3. Catalyst — what will cause re-rating? When?
4. Risk — what could go wrong?
5. Conviction — HIGH / MEDIUM / LOW / AVOID

Output format: Timestamped entry in RESEARCH-LOG.md with ticker, thesis statement, checklist results, and conviction level.

**Tools used:**
- `yfinance` — price data, fundamentals, financial statements
- Claude web search — news, regulatory filings, analyst commentary
- File append — logged to RESEARCH-LOG.md

### Skill 2: Assess Macro Regime

**When:** Pre-market research routine (morning analysis)  
**Called by:** Pre-market session  
**What it does:** Reads macro data and writes regime assessment  
**Reads:** yfinance for IHSG/IDR/commodities, web search for BI rate/policy news  
**Writes:** "Macro" section of today's RESEARCH-LOG.md entry

Morning research covers:
- IHSG level, trend (above/below 200-day MA)
- IDR/USD direction
- Bank Indonesia rate signals
- Coal, palm oil, nickel prices (as percentage change)
- Foreign fund flows (net buy/sell)
- Global risk appetite (VIX, MSCI EM outlook)

Regime classification output:
- **CONSTRUCTIVE** — macro supports equity positions, full position sizing
- **CAUTIOUS** — mixed signals, reduce position sizes by 25%
- **DEFENSIVE** — adverse conditions, half positions, no new entries

**Tools used:**
- `yfinance` — IHSG close, IDR/USD rate, commodity futures
- Claude web search — BI announcements, macroeconomic news
- File append — logged to RESEARCH-LOG.md

### Skill 3: Screen Watchlist

**When:** Pre-market research routine  
**Called by:** Pre-market session (after macro assessment)  
**What it does:** Quick scan of 30+ IDX stocks to find candidates worth a deep dive  
**Reads:** yfinance batch data, RESEARCH-LOG.md for past screens  
**Writes:** "Screen Results" section of today's RESEARCH-LOG.md entry

Quick filters:
- P/E below sector median?
- Revenue growing YoY?
- Any recent news catalyst?
- Volume above average?
- Not already in portfolio?

Output: 3-5 tickers worth a deeper look, with one-line rationale each. Logged with timestamp and date in RESEARCH-LOG.md.

**Tools used:**
- `yfinance` — batch fundamentals, price/volume data
- Claude web search — news catalyst scan
- File append — logged to RESEARCH-LOG.md

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

We use Claude's native capabilities and yfinance. No external AI services.

| Need | Tool | How |
|------|------|-----|
| Price data | yfinance | `yf.Ticker("BBCA.JK")` |
| Fundamentals | yfinance | `.financials`, `.balance_sheet`, `.cashflow`, `.info` |
| News | Claude web search | Built-in, no API key needed |
| Regulatory filings | Claude web search | Search IDX, OJK websites |
| Macro data | yfinance + web search | Commodity futures, BI rate announcements |
| Company analysis | Claude reasoning | The agent IS the analyst |

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
