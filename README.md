# idx-trader

An autonomous fundamental swing trader for the Indonesia Stock Exchange (IDX), powered by Claude
Opus/Sonnet/Haiku via Claude Code cloud routines. The agent wakes up five times per trading day,
reads its file-based memory, does one job, commits what it learned, and goes back to sleep.

**NOT FINANCIAL ADVICE.** This is an experimental AI system. Use it to learn, not to risk capital
you cannot afford to lose. Past paper performance does not predict live results.

**Paper mode by default.** The agent will never place real orders unless `TRADING_MODE=live` is
explicitly set in the environment. When in doubt, leave it on paper.

---

## Setup

### 1. Clone and configure

```bash
git clone <your-repo-url> idx-trader
cd idx-trader
cp env.template .env
# Open .env and fill in your broker credentials and notification tokens.
# Leave TRADING_MODE=paper until you have validated the system thoroughly.
```

### 2. Verify the environment

```bash
# Confirm the required vars are present
for v in BROKER_API_KEY BROKER_API_SECRET BROKER_ACCOUNT_ID; do
  [[ -n "${!v:-}" ]] && echo "$v: set" || echo "$v: MISSING"
done
```

### 3. Smoke test (local)

Open this repo in Claude Code and run the `/portfolio` slash command. You should see account
equity, cash, and open positions printed cleanly. If the broker wrapper returns an error, check
your credentials and `BROKER_API_KEY` before proceeding.

### 4. Seed memory files

Memory files under `memory/` must be seeded before the first routine runs. See
`plan/01-MEMORY-ARCHITECTURE.md` for templates and initial content.

### 5. Paper trade first

Run all five routines in paper mode for at least four weeks before enabling live trading.
Monitor every commit the agent makes. Read `memory/TRADE-LOG.md` and `memory/RESEARCH-LOG.md`
daily to verify the agent is reasoning as expected.

---

## Repo Layout

```
idx-trader/
├── CLAUDE.md                    # Agent rulebook — auto-loaded every session
├── README.md                    # This file
├── env.template                 # Copy to .env and fill in credentials
├── .gitignore
├── .claude/
│   └── commands/                # Slash commands for local/ad-hoc use
│       ├── portfolio.md
│       ├── trade.md
│       ├── pre-market.md
│       ├── market-open.md
│       ├── midday.md
│       ├── daily-summary.md
│       └── weekly-review.md
├── routines/                    # Cloud routine prompts (production path)
│   ├── pre-market.md
│   ├── market-open.md
│   ├── midday.md
│   ├── daily-summary.md
│   └── weekly-review.md
├── scripts/                     # External API wrappers — the ONLY way to touch APIs
│   ├── broker.sh                # IDX broker wrapper
│   └── notify.sh                # Notification wrapper (Telegram / email)
└── memory/                      # Agent's persistent state — committed to main
    ├── TRADING-STRATEGY.md
    ├── TRADE-LOG.md
    ├── RESEARCH-LOG.md
    ├── WEEKLY-REVIEW.md
    └── PROJECT-CONTEXT.md
```

---

## The Five Routines

| # | Routine | WIB time | Days | Job |
|---|---------|----------|------|-----|
| 1 | Pre-market research | 07:00 | Mon–Fri | Research catalysts, draft trade ideas, update RESEARCH-LOG |
| 2 | Market-open execution | 09:15 | Mon–Fri | Execute planned trades, set GTC trailing stops |
| 3 | Midday scan | 11:30 | Mon–Fri | Cut -7% losers, tighten stops on winners |
| 4 | Daily summary (EOD) | 15:15 | Mon–Fri | EOD snapshot, daily recap, commit TRADE-LOG |
| 5 | Weekly review | 16:00 | Fri only | Week recap, letter grade, update WEEKLY-REVIEW |

---

## Safety Rails

| Rail | Value |
|------|-------|
| Default mode | Paper — no real orders without `TRADING_MODE=live` |
| Max open positions | 6 |
| Max position size | 20% of equity |
| Max new trades per week | 3 |
| Trailing stop | 10% GTC real order on every position |
| Hard cut | -7% from entry |
| Daily loss cap | -2% — halts all trading |
| Weekly loss cap | -5% — reduces new position sizes by 50% |
| Max drawdown | -15% — closes everything, alerts owner |
| Options / leverage | Never |
| Sector exit | After 2 consecutive losses in a sector |

All broker calls go through `scripts/broker.sh`. All notifications go through `scripts/notify.sh`.
The agent never calls external APIs directly.

---

## Further Reading

- `plan/00-STRATEGY-ON-PAPER.md` — the trading strategy in plain language
- `plan/01-MEMORY-ARCHITECTURE.md` — how the file-based brain works
- `plan/02-SCAFFOLD.md` — repo design decisions and prompt scaffold template
- `plan/03-GUARDRAILS.md` — two-layer guardrail design (prompt + code)
- `plan/04-SKILLS.md` — research, screening, trade planning, journaling skills
- `plan/05-ROUTINES.md` — full routine prompt specifications
- `plan/06-PERFORMANCE.md` — benchmarking vs IHSG, lessons-learned compounding
