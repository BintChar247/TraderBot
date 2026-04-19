# Project Context — IDX Trading Agent

_Last updated: 2026-04-18_

---

## Mission

Beat IHSG through disciplined fundamental swing trading on the Indonesian Stock Exchange (IDX).
Target alpha in a paper account first. Discipline and selectivity over activity.

---

## Platform

| Field | Value |
|-------|-------|
| Agent model | Claude Opus |
| Broker interface | `scripts/broker.sh` (IDX broker API wrapper) |
| Notification | `scripts/notify.sh` (Telegram/email) |
| Market | Indonesian Stock Exchange (IDX) |
| Timezone | WIB — Western Indonesia Time (Asia/Jakarta, UTC+7) |
| User | Michael |
| Default mode | **Paper trading** (toggle via `TRADING_MODE` env var) |
| Account size | IDR 10 Milyar (paper) |

---

## Repository Layout

```
idx-trader/
├── CLAUDE.md                    # Agent identity + hard rules (read-only for agent)
├── README.md                    # Human-facing quickstart
├── env.template                 # Template for local .env file
├── .gitignore                   # Excludes .env
├── .claude/
│   └── commands/                # Ad-hoc slash commands
│       ├── portfolio.md
│       ├── trade.md
│       ├── pre-market.md
│       ├── market-open.md
│       ├── midday.md
│       ├── daily-summary.md
│       └── weekly-review.md
├── routines/                    # Cloud routine prompts (production)
│   ├── pre-market.md
│   ├── market-open.md
│   ├── midday.md
│   ├── daily-summary.md
│   └── weekly-review.md
├── scripts/
│   ├── broker.sh                # IDX broker API wrapper
│   └── notify.sh                # Notification wrapper
└── memory/                      # Agent persistent state — committed to main
    ├── TRADING-STRATEGY.md
    ├── TRADE-LOG.md
    ├── RESEARCH-LOG.md
    ├── WEEKLY-REVIEW.md
    └── PROJECT-CONTEXT.md
```

---

## The Five Memory Files

| File | Purpose | Write Cadence |
|------|---------|---------------|
| `TRADING-STRATEGY.md` | Rulebook: buy/sell gates, risk rules, sector playbook. Read every session. | Weekly only (Friday review) |
| `TRADE-LOG.md` | Every trade + daily EOD snapshot. Source of truth for positions and P&L. | Every trade, every EOD |
| `RESEARCH-LOG.md` | One dated entry per day: macro, sectors, top candidates, theses. | Daily (pre-market), optional midday addendum |
| `WEEKLY-REVIEW.md` | Friday recaps with letter grade, lessons, rule changes. Long-term memory. | Weekly |
| `PROJECT-CONTEXT.md` | Static background, mission, platform, repo layout. This file. | Rarely |

---

## The Five Scheduled Routines

| # | Name | WIB Time | Cron (UTC) | Days | Job |
|---|------|----------|-----------|------|-----|
| 1 | Pre-market research | 07:00 | `0 0 * * 1-5` | Mon-Fri | Research catalysts, draft trade ideas, append RESEARCH-LOG.md |
| 2 | Market-open execution | 09:15 | `15 2 * * 1-5` | Mon-Fri | Execute planned trades, set stops, append TRADE-LOG.md |
| 3 | Midday scan | 11:30 | `30 4 * * 1-5` | Mon-Fri | Cut -7% losers, tighten stops on winners, append TRADE-LOG.md |
| 4 | Daily summary (EOD) | 15:15 | `15 8 * * 1-5` | Mon-Fri | EOD snapshot, reconcile broker vs log, append TRADE-LOG.md |
| 5 | Weekly review | 16:00 Fri | `0 9 * * 5` | Fri | Week recap + lessons + optional TRADING-STRATEGY.md update |

All routines run on `claude-opus`. Minimum 30-minute gaps between routines — never two running concurrently.

---

## Files Are the Brain

The agent is stateless. Every routine wakes up knowing nothing. Memory lives entirely in these five files.

Every routine reads files to load context, does its one job, then writes files to persist what it learned. The next routine picks up by reading those same files.

The files are not just memory — they are the agent's personality and discipline. The scar tissue from bad trades lives in WEEKLY-REVIEW.md. The hard rules live in TRADING-STRATEGY.md. Lose the files, lose the lessons. **The files ARE the edge.**

---

## Read/Write Contract Summary

```
/pre-market    READS:  TRADING-STRATEGY.md, tail of TRADE-LOG.md,
                       tail of RESEARCH-LOG.md, PROJECT-CONTEXT.md
               WRITES: RESEARCH-LOG.md (append today's entry)

/market-open   READS:  TRADING-STRATEGY.md, today's RESEARCH-LOG.md entry,
                       tail of TRADE-LOG.md
               WRITES: TRADE-LOG.md (append trades)
               FALLBACK: runs pre-market steps inline if today's entry missing

/midday        READS:  TRADING-STRATEGY.md (tail), TRADE-LOG.md (today),
                       RESEARCH-LOG.md (today)
               WRITES: TRADE-LOG.md (append actions)
               OPTIONAL: RESEARCH-LOG.md addendum

/daily-summary READS:  TRADE-LOG.md (yesterday's close for delta),
                       live account state from broker
               WRITES: TRADE-LOG.md (append EOD snapshot)

/weekly-review READS:  TRADE-LOG.md (full week), RESEARCH-LOG.md (full week),
                       WEEKLY-REVIEW.md (existing), TRADING-STRATEGY.md
               WRITES: WEEKLY-REVIEW.md (append this week's review)
               OPTIONAL: TRADING-STRATEGY.md (rule proved/failed over 2+ weeks)
```

---

## Key Guardrails Reference

| Rule | Value |
|------|-------|
| Trading mode | Paper by default — real is an explicit toggle |
| Max open positions | 5-6 |
| Max per position | 20% of equity |
| Max trades per week | 3 |
| Trailing stop | 10% GTC — tighten to 7% at +15%, 5% at +20% |
| Cut losers | -7% — no averaging down |
| Daily loss cap | -2% → halt all trading |
| Weekly loss cap | -5% → reduce position sizes by 50% |
| Max drawdown | -15% → close everything, alert Michael |
| Instruments | Long-only equities. No options, no leverage. |
