# Step 2 — Scaffold (Repository Layout)

> *There is no separate Python bot process. Claude is the bot.*
> *Every scheduled run is a fresh LLM invocation reading a well-defined prompt.*

---

## Three Design Properties

1. **Stateless runs** — each firing is independent, failures self-heal on the next tick
2. **Git as memory** — every piece of state is a markdown file committed to main. Free versioning, diffs, rollback, and human-readable audit trail.
3. **Hard rules as gates** — strategy discipline is enforced programmatically before every order, not left to interpretation

---

## Repository Layout

Adapted from the guide. Replace Alpaca scripts with IDX broker, Perplexity with Claude web search.

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
│   ├── README.md
│   ├── pre-market.md
│   ├── market-open.md
│   ├── midday.md
│   ├── daily-summary.md
│   └── weekly-review.md
├── scripts/                     # API wrappers (the only way to touch the outside world)
│   ├── broker.sh                # Paper broker (simulated trades, real prices)
│   ├── market-data.sh           # Real-time data (GoAPI → yfinance → Sectors.app fallback)
│   └── notify.sh                # Notification wrapper (Telegram/email/etc)
└── memory/                      # Agent's persistent state (committed to main)
    ├── TRADING-STRATEGY.md
    ├── TRADE-LOG.md
    ├── RESEARCH-LOG.md
    ├── WEEKLY-REVIEW.md
    └── PROJECT-CONTEXT.md       # Static background, mission, platform
```

### Two Execution Modes

- **Local mode**: invoke slash commands like `/pre-market` manually inside Claude Code. Credentials come from a local `.env` file. Good for testing and ad-hoc runs.
- **Cloud mode**: Claude's cloud routines fire each `routines/*.md` prompt on a cron. Credentials come from the routine's environment variables. **No `.env` file.** This is the production path.

---

## The Five Memory Files

All committed to `main`. These are the agent's ONLY state between runs.

| File | Purpose | Write Cadence |
|------|---------|---------------|
| TRADING-STRATEGY.md | The rulebook. Every workflow reads this first. | Only updated Friday if a rule proves out/fails |
| TRADE-LOG.md | Every trade + daily EOD snapshot | Every trade, every EOD |
| RESEARCH-LOG.md | One dated entry per day | Every pre-market, optional midday addendum |
| WEEKLY-REVIEW.md | Friday recaps with letter grade | Weekly |
| PROJECT-CONTEXT.md | Static background, mission, platform | Rarely updated |

---

## Wrapper Scripts

All external API calls flow through bash scripts in `scripts/`. The agent never calls curl directly. This keeps auth handling in one place, standardizes error messages, and makes the prompts shorter.

### `scripts/broker.sh` — IDX Trading

Replaces the guide's `alpaca.sh`. Wraps the IDX broker API (Interactive Brokers, or local Indonesian broker — TBD).

```bash
# Subcommands (same pattern as the guide's alpaca.sh):
bash scripts/broker.sh account       # equity, cash, buying_power
bash scripts/broker.sh positions     # all open positions w/ unrealized P&L
bash scripts/broker.sh position SYM  # single position
bash scripts/broker.sh quote SYM     # latest bid/ask
bash scripts/broker.sh orders        # open orders
bash scripts/broker.sh order '<json>' # POST new order
bash scripts/broker.sh cancel ID     # cancel order
bash scripts/broker.sh close SYM     # sell entire position
```

**IDX adaptations:**
- Ticker format: plain IDX codes (BBCA, ADRO), not US symbols
- Order sizes in lots of 100 shares
- IDX tick size rules built into the wrapper
- T+2 settlement awareness
- Market hours: 09:00–15:00 WIB with lunch break

### `scripts/notify.sh` — Notifications

Replaces the guide's `clickup.sh`. Sends notifications via Telegram, email, or preferred channel.

```bash
bash scripts/notify.sh "<markdown message>"
```

Graceful fallback: if credentials are missing, appends the message to a local `DAILY-SUMMARY.md` file and exits 0. The agent never crashes on missing notification credentials.

### Research — Claude Web Search (No Perplexity)

The guide uses `scripts/perplexity.sh` for research. We replace this with Claude's native web search. No wrapper script needed — the agent uses its built-in WebSearch tool directly.

Queries adapted for IDX:
- "IHSG index level today" (replaces "S&P futures premarket")
- "Newcastle coal price today" (replaces "WTI and Brent oil")
- "IDR USD exchange rate today"
- "Bank Indonesia interest rate decision"
- "Top IDX stock catalysts today {DATE}"
- "Indonesia economic calendar CPI PDB data"
- "IDX sector momentum LQ45"
- News on any currently-held ticker

---

## CLAUDE.md — The Agent Rulebook

Auto-loaded every session. Adapted from Appendix A of the guide:

```markdown
# Trading Bot Agent Instructions

You are an autonomous AI trading bot managing a PAPER IDX account.
Your goal is to beat IHSG over the challenge window. You are aggressive
but disciplined. Stocks only — no options, ever. Communicate ultra-concise:
short bullets, no fluff.

## Read-Me-First (every session)
Open these in order before doing anything:
- memory/TRADING-STRATEGY.md  — Your rulebook. Never violate.
- memory/TRADE-LOG.md         — Tail for open positions, entries, stops.
- memory/RESEARCH-LOG.md      — Today's research before any trade.
- memory/PROJECT-CONTEXT.md   — Overall mission and context.
- memory/WEEKLY-REVIEW.md     — Friday afternoons; template for new entries.

## Daily Workflows
Defined in .claude/commands/ (local) and routines/ (cloud). Five scheduled
runs per trading day plus two ad-hoc helpers.

## Strategy Hard Rules (quick reference)
- NO OPTIONS — ever.
- Max 5-6 open positions.
- Max 20% per position.
- Max 3 new trades per week.
- 75-85% capital deployed.
- 10% trailing stop on every position as a real GTC order. Never mental.
- Cut losers at -7% manually.
- Tighten trail to 7% at +15%, to 5% at +20%.
- Never within 3% of current price. Never move a stop down.
- Follow sector momentum. Exit a sector after 2 failed trades.
- Patience > activity.

## API Wrappers
Use bash scripts/broker.sh, scripts/notify.sh.
Never curl these APIs directly.
For research: use native WebSearch tool (not Perplexity).

## Communication Style
Ultra concise. No preamble. Short bullets. Match existing memory file
formats exactly — don't reinvent tables.
```

---

## The Prompt Scaffold Template

Every cloud routine prompt follows the same 8-section shape. Three invariants make it robust:

1. **Environment check first.** Fails fast with a clear message instead of cryptic curl errors.
2. **Persistence warning is loud.** Without the reminder, Claude skips the final push ~10% of runs.
3. **Rebase on conflict, never force-push.** Guarantees you never overwrite another run's memory.

```
[PERSONA LINE — who the agent is, the mission, one-line core rule]

You are running the [WORKFLOW NAME] workflow. Resolve today's date via:
DATE=$(date +%Y-%m-%d).

IMPORTANT — ENVIRONMENT VARIABLES:
- Every API key is ALREADY exported as a process env var:
  [list all required vars for this workflow]
- There is NO .env file in this repo and you MUST NOT create, write,
  or source one.
- If a wrapper prints "KEY not set in environment" -> STOP, send one
  notification alert naming which var is missing, then exit. Do NOT try
  to create a .env as a workaround.
- Verify env vars BEFORE any wrapper call:
  for v in VAR1 VAR2 ...; do
    [[ -n "${!v:-}" ]] && echo "$v: set" || echo "$v: MISSING"
  done

IMPORTANT — PERSISTENCE:
- This workspace is a fresh clone. File changes VANISH unless you
  commit and push to main. You MUST commit and push at the end.

STEP 1 — Read memory: [which files, what to look for]
STEP 2 — Pull live state: [which wrapper calls]
STEP 3..N-2 — Do the work; write memory as you go
STEP N-1 — Notification (conditional per workflow)
STEP N — COMMIT AND PUSH (mandatory):
  git add memory/<files touched>
  git commit -m "<tag> $DATE"
  git push origin main
  On push failure from divergence:
    git pull --rebase origin main
  then push again. Never force-push.
```

---

## Why Git as Memory Works

- Schedules are hours apart. No race conditions between routines.
- Memory writes are append-only dated sections. Merge conflicts are effectively impossible.
- Each run reads from committed main. Nothing held in-memory across firings.
- Rollback is `git revert`. Audit is `git log`. Diff is `git diff`. Free observability.

## Where Git as Memory Would Break

- Two routines scheduled seconds apart (don't do that — 30 min minimum gap)
- Someone editing memory files manually during a scheduled run (don't do that)
- Partial mid-run failure. A broker order could go through with no trade log entry. Mitigation: the next run reads live positions from the broker and reconciles.

---

## env.template

```bash
# Market Data — Real-Time (GoAPI.io — free tier)
GOAPI_API_KEY=your_goapi_key_here

# Market Data — Fundamentals (Sectors.app — free tier)
SECTORS_API_KEY=your_sectors_key_here

# yfinance needs no API key — used as fallback

# Notifications (Telegram or similar)
NOTIFY_BOT_TOKEN=your_telegram_bot_token_here
NOTIFY_CHAT_ID=your_telegram_chat_id_here

# Mode — PAPER ONLY for the 2-week trial
TRADING_MODE=paper
```

**In cloud routines:** set these as environment variables on the routine itself, NOT in a `.env` file. A `.env` committed to the repo would leak secrets immediately.

---

## Replication Checklist

Steps to stand up your own instance, in order:

- [ ] Create a new private GitHub repo
- [ ] Build the directory structure above
- [ ] Write `scripts/broker.sh` for your IDX broker
- [ ] Write `scripts/notify.sh` for your notification channel
- [ ] Copy the CLAUDE.md starter above
- [ ] Copy env.template. **Do NOT commit a real `.env`.**
- [ ] Add `.env` to `.gitignore`
- [ ] Copy the seven slash commands into `.claude/commands/`
- [ ] Copy the five routine prompts into `routines/`
- [ ] Seed the five memory files (see Memory Architecture doc)
- [ ] **Local smoke test**: copy env.template to .env, fill in credentials, open repo in Claude Code, run `/portfolio`. You should see account and positions print cleanly.
- [ ] Install the Claude GitHub App on your repo
- [ ] Create the first cloud routine (pre-market) per cloud setup instructions
- [ ] Hit "Run now" and watch the logs. Verify research log entry written, committed, pushed.
- [ ] If that works, create the other four routines with the same pattern
- [ ] Seed TRADE-LOG.md with a Day 0 EOD snapshot so Day 1's daily-summary has a baseline
- [ ] Monitor the first week closely. Read every commit the agent makes.

---

## Deliverables

- [ ] Create GitHub repo with full directory structure
- [ ] Write CLAUDE.md
- [ ] Write scripts/broker.sh (IDX broker wrapper)
- [ ] Write scripts/notify.sh (notification wrapper)
- [ ] Write env.template
- [ ] Seed 5 memory files in memory/
- [ ] Write 7 slash commands in .claude/commands/
- [ ] Write 5 routine prompts in routines/
- [ ] Local smoke test: /portfolio works
- [ ] First cloud routine runs successfully
