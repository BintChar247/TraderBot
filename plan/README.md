# IDX AI Trading Agent — Master Plan

> **Goal:** Beat IHSG in a 2-week paper trading trial (Apr 20 - May 2, 2026).
> Autonomous fundamental swing trader for IDX using Claude Opus.
>
> **PAPER TRADE ONLY.** No real money. Simulation benchmark against IHSG.
> Starting capital: IDR 300,000,000 (simulated). NOT financial advice.

---

## The Core Insight: Files Are the Brain

Every time a routine fires, the agent wakes up knowing **nothing**. It's stateless.

**Files. The files ARE the brain.**

Every routine reads files to load its memory, does its one job, then writes files to save what it learned. Get this right and the agent compounds knowledge for months. Get it wrong and it forgets everything every morning.

The files aren't just memory — they're **personality and discipline**. A human trader has scar tissue from getting burned. The agent has WEEKLY-REVIEW.md. The line that says "18 trades = worst week" is what stops the next routine from overtrading. Lose the files, lose the lessons. **The files ARE the edge.**

Five core files: `TRADING-STRATEGY.md`, `TRADE-LOG.md`, `RESEARCH-LOG.md`, `WEEKLY-REVIEW.md`, `PROJECT-CONTEXT.md`. All committed to `main`. Git is the memory layer — free versioning, diffs, rollback, and human-readable audit trail.

---

## The Steps

```
Step 1: STRATEGY      → Write the approach on paper. No code yet.
Step 2: SCAFFOLD      → Repo layout, CLAUDE.md, wrapper scripts, prompt template.
Step 3: GUARDRAILS    → Before ANY trading logic. In prompts AND in broker.sh.
Step 4: SKILLS        → What the agent can do (research, screen, trade, journal).
Step 5: ROUTINES      → 5 scheduled triggers aligned to IDX hours (WIB).
Step 6: PERFORMANCE   → Journaling, benchmarking vs IHSG, compounding lessons.
```

---

## The Five Triggers (IDX hours, WIB)

| # | Trigger | Cron (UTC) | WIB | Job | Notify? |
|---|---------|-----------|-----|-----|---------|
| 1 | IDX — Pre-market research | `0 0 * * 1-5` | 7:00am | Research catalysts, draft trade ideas | Silent unless urgent |
| 2 | IDX — Market-open execution | `15 2 * * 1-5` | 9:15am | Execute planned trades + set stops | Only if trade placed |
| 3 | IDX — Midday scan | `30 4 * * 1-5` | 11:30am | Cut -7% losers, tighten stops on winners | Only if action taken |
| 4 | IDX — Daily summary (EOD) | `15 8 * * 1-5` | 3:15pm | EOD snapshot + daily recap | Always (one message) |
| 5 | IDX — Weekly review | `0 9 * * 5` | 4:00pm Fri | Week recap + lessons + strategy update | Always (one message) |

All run on `claude-opus`, 30+ min apart so two never corrupt a file.

---

## The Five Memory Files

| File | Purpose | Write Cadence |
|------|---------|---------------|
| `memory/TRADING-STRATEGY.md` | The rulebook. Every workflow reads this first. | Only updated Friday if a rule proves out/fails |
| `memory/TRADE-LOG.md` | Every trade + daily EOD snapshot | Every trade, every EOD |
| `memory/RESEARCH-LOG.md` | One dated entry per day | Every pre-market, optional midday addendum |
| `memory/WEEKLY-REVIEW.md` | Friday recaps with letter grade | Weekly |
| `memory/PROJECT-CONTEXT.md` | Static background, mission, platform | Rarely updated |

---

## Repository Layout

```
idx-trader/
├── CLAUDE.md                    # Agent rulebook (auto-loaded every session)
├── README.md                    # Human-facing quickstart
├── env.template                 # Template for local .env file
├── .gitignore                   # Must exclude .env
├── .claude/
│   └── commands/                # Ad-hoc slash commands for local use
│       ├── portfolio.md
│       ├── trade.md
│       ├── pre-market.md
│       ├── market-open.md
│       ├── midday.md
│       ├── daily-summary.md
│       └── weekly-review.md
├── routines/                    # Cloud routine prompts (the prod path)
│   ├── pre-market.md
│   ├── market-open.md
│   ├── midday.md
│   ├── daily-summary.md
│   └── weekly-review.md
├── scripts/                     # API wrappers (the only way to touch the outside world)
│   ├── broker.sh                # Paper broker (simulated trades, real prices)
│   ├── market-data.sh           # Real-time data (GoAPI → yfinance → Sectors.app)
│   └── notify.sh                # Notification wrapper (Telegram/email)
├── dashboard/                   # Performance visualization
│   ├── index.html               # Interactive dashboard (open in browser)
│   └── data.json                # Updated by daily-summary routine
└── memory/                      # Agent's persistent state (committed to main)
    ├── TRADING-STRATEGY.md
    ├── TRADE-LOG.md
    ├── RESEARCH-LOG.md
    ├── WEEKLY-REVIEW.md
    └── PROJECT-CONTEXT.md
```

---

## Key Guardrails

| Rule | Value | Why |
|------|-------|-----|
| Trading mode | **Paper by default** | Real trading is an explicit toggle |
| Max positions | **5-6** | Concentration is the point |
| Max per position | **20% of equity** | Meaningful but not reckless |
| Max trades | **3 per week** | Forces selectivity — the agent is eager |
| Trailing stop | **10% GTC** | Real order, never mental. Tighten to 7% at +15%, 5% at +20% |
| Cut losers | **-7%** | Manual sell, no hoping, no averaging down |
| Daily loss cap | **-2%** | Halts all trading automatically |
| Weekly loss cap | **-5%** | Reduces position sizes by 50% |
| Max drawdown | **-15%** | Close everything, alert Michael |
| Options/leverage | **Never** | Long-only equities |
| Sector exit | **2 failed trades** | Exit entire sector after 2 consecutive losses |
| Research tools | Claude web search + yfinance | No Perplexity, no external AI |

---

## The Buy-Side Gate (9 checks before every order)

1. Total positions after fill <= 6
2. Trades this week <= 3
3. Position cost <= 20% of equity
4. Position cost <= available cash
5. Catalyst documented in today's research log
6. Instrument is a stock (not option)
7. Stock avg daily volume > 500,000 shares
8. Stock in lot-size multiple of 100
9. Price hasn't moved >3% from planned entry

**If any check fails, the trade is skipped and the reason is logged.**

---

## What Kind of Trader?

| This Agent | NOT This Agent |
|---|---|
| Fundamental swing trader | Day trader / scalper |
| Holds days to weeks | Holds minutes to hours |
| Buys on undervaluation + catalyst | Buys on EMA crossover |
| Sells when thesis breaks | Sells on RSI signal |
| 3 trades per week max | Trades every session |
| Edge: depth of analysis + discipline | Edge: speed |

Opus scores 64.4% on agentic financial analysis — top-of-class at digesting filings and writing coherent fundamentals-driven theses. That maps to swing/position trading, NOT day trading.

---

## The Plan Files

| Step | File | What It Is |
|------|------|-----------|
| **1** | [00-STRATEGY-ON-PAPER.md](00-STRATEGY-ON-PAPER.md) | The strategy in plain language. Buy-side gate, sell-side rules, sector playbook. |
| **--** | [01-MEMORY-ARCHITECTURE.md](01-MEMORY-ARCHITECTURE.md) | How the file-based brain works. Five files, read/write contracts, git as memory. |
| **2** | [02-SCAFFOLD.md](02-SCAFFOLD.md) | Repo layout, CLAUDE.md, wrapper scripts, prompt scaffold template, env.template. |
| **3** | [03-GUARDRAILS.md](03-GUARDRAILS.md) | Two layers: prompt-level + code-level. Paper mode default. The agent is eager. |
| **4** | [04-SKILLS.md](04-SKILLS.md) | Six skills: research, macro, screen, trade plan, position review, journal. |
| **5** | [05-ROUTINES.md](05-ROUTINES.md) | Five routines: pre-market, open, midday, EOD, weekly review. Ad-hoc commands. |
| **6** | [06-PERFORMANCE.md](06-PERFORMANCE.md) | Journaling, benchmarking vs IHSG, lessons-learned compounding over months. |
| **7** | [07-EXECUTION-PLAN.md](07-EXECUTION-PLAN.md) | Model selection (Haiku/Sonnet/Opus), parallelism, build phases, cost estimates. |
| **8** | [08-PAPER-TRIAL.md](08-PAPER-TRIAL.md) | 2-week paper trial plan. Schedule, simulated broker, dashboard, success criteria. |
| **9** | [09-VERIFICATION.md](09-VERIFICATION.md) | 65-point pre-flight checklist. Every check has a command, expected output, and fix. |

---

## Paper Trial: April 20 - May 2, 2026

This is a **paper-only simulation**. No real broker. No real money.

- **Starting capital:** IDR 300,000,000 (simulated)
- **Broker:** Paper mode — yfinance prices, trades logged to TRADE-LOG.md
- **Dashboard:** `dashboard/index.html` — updated daily by EOD routine
- **Success:** Beat IHSG over 10 trading days while following all rules

### Day 0 Setup (Today)

1. Seed memory files (TRADING-STRATEGY, TRADE-LOG with Day 0 baseline, PROJECT-CONTEXT)
2. Write scripts/broker.sh (paper mode, yfinance-backed)
3. Write routine prompts and slash commands
4. Build dashboard
5. Set up 5 cloud routines with cron schedules
6. Smoke test: `/portfolio` shows IDR 300M, 0 positions
7. Everything ready before Monday 07:00 WIB

**Trading starts Monday, April 20, 2026.**
