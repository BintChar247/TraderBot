# Step 3 — Guardrails

> *Do this BEFORE any trading logic. No exceptions.*
> *Guardrails live in two places: prompts AND the broker wrapper.*

---

## Why This Comes First

The agent is eager. Left unchecked, it trades every session. It will find reasons to buy, reasons to sell, reasons to adjust. It's an analytical machine that wants to use its analysis.

Your job is to constrain it. Put guardrails in BEFORE you give it the ability to trade.

Guardrails live in **two places** because one isn't enough:

1. **In the prompt** (CLAUDE.md + TRADING-STRATEGY.md) — the agent reads these rules every session and validates the buy-side gate before placing any order
2. **In the broker wrapper** (broker.sh) — hard limits in the script that enforce rules even if the agent tries to reason past them

---

## TRADING-STRATEGY.md Risk Section — The Prompt-Level Guardrails

These rules live in the Risk Rules section of TRADING-STRATEGY.md. Read by the agent every session. Only `/weekly-review` can modify TRADING-STRATEGY.md, and only with clear justification.

```markdown
# Risk Rules

These rules cannot be overridden. They are non-negotiable.
If you are unsure whether a trade violates a rule, DO NOT trade.

## Trading Mode
- DEFAULT: Paper mode. All trades are simulated.
- Live mode is an EXPLICIT toggle by Michael. You cannot switch it yourself.
- If you are unsure which mode you're in, STOP and ask.

## Position Limits
- Max 20% of portfolio in any single stock
- Max 25% of portfolio in any single sector
- Max 5-6 concurrent positions (hard limit: 6)

## Stop Loss Rules
- 10% trailing stop on every position as real GTC order
- Cut losers at -7% manually (do not wait for stop)
- Tighten trail to 7% at +15%, to 5% at +20%
- Never within 3% of current price. Never move a stop down.

## Trade Frequency
- Max 3 trades per WEEK (not per day, not per session)
- This forces you to be selective. Not every signal deserves capital.
- Count rebalances and trims as trades too.

## Sector Rules
- Exit sector after 2 consecutive failed trades in that sector
- Wait at least 1 week before re-entering

## Loss Limits
- Daily loss cap: -2% of portfolio
- If daily P&L hits -2%, STOP ALL TRADING for the rest of the day
- This is automatic. No exceptions. No "but the thesis is good."
- Weekly loss cap: -5% of portfolio → reduce all position sizes by 50%
- Max drawdown: -15% from peak → close all positions, alert Michael

## What You MUST NOT Do
- No options. Ever.
- No leverage. Ever.
- No shorting. Long only.
- No penny stocks (price below IDR 100)
- No stocks with average daily volume below 500,000 shares
- No chasing — if price moved >3% from your planned entry, re-evaluate
- No revenge trading — if you just took a loss, wait until next session
- No FOMO — if you missed an entry, let it go

## What You MUST Do
- Read strategy.md FIRST, every session
- Write the thesis BEFORE placing any trade
- Set a trailing stop for every position (10% initial, tightens as documented above)
- Journal every decision (including decisions NOT to trade)
- Do NOT send notifications/summaries unless you actually traded

## The Buy-Side Gate: 9 Checks Before Every Trade

Before calling broker.sh to place an order, you MUST validate all 9 checks. If ANY check fails, skip the trade and log the reason in your journal.

1. Total positions after fill will be <= 6 (hard limit)
2. Trades this week (including this one) <= 3
3. Position cost will be <= 20% of current portfolio equity
4. Position cost is <= available cash (can afford it)
5. Catalyst is documented in today's research log
6. Instrument is a stock (not an option, not a sector ETF)
7. Stock average daily volume > 500,000 shares
8. Lot size is a multiple of 100
9. Price hasn't moved more than 3% from planned entry price

## The Eagerness Check
Before every trade, ask yourself:
1. Does this meet ALL criteria in strategy.md?
2. Do all 9 buy-side gate checks pass?
3. Am I within my weekly trade limit?
4. Am I within my daily loss cap?
5. Is this genuinely the best use of capital, or am I trading for the sake of trading?
6. Would I be comfortable explaining this trade to Michael?

If any answer is "no" or "maybe," DO NOT TRADE.
```

---

## Broker Wrapper Enforcement — Code-Level Guardrails

The `broker.sh` wrapper script is the final enforcement layer. Before executing any order, it validates order parameters against the hard limits:

- Mode check: Is TRADING_MODE set to "live" or "paper"?
- Position size check: Will this position exceed 20% of portfolio equity?
- Position count check: Will total positions exceed 6 after this fill?
- Trade frequency check: Have we already placed 3 trades this week?
- Daily loss cap check: Is daily P&L already at or below -2%?
- Available cash check: Do we have enough liquidity for this position?
- Lot size check: Is the quantity a multiple of 100?

If any check fails, broker.sh rejects the order, logs the reason, and returns an error. The agent sees this and cannot proceed without violating the wrapper.

**Key principle:** The wrapper enforces limits. The prompt teaches the agent to be thoughtful. Together, they make it impossible to trade recklessly.

---

## The Paper Mode Default

This is critical. The `.env` file defaults to `TRADING_MODE=paper`. The broker.sh script defaults to `paper` if the env var is missing. The CLAUDE.md says paper mode.

**Three layers of protection:**
1. `.env` says paper
2. broker.sh defaults to paper
3. CLAUDE.md says paper

To go live, Michael must:
1. Change `.env` to `TRADING_MODE=live`
2. Update CLAUDE.md to say live mode
3. Have the agent acknowledge the mode change in its journal

This is intentionally friction-heavy. You should feel the weight of going live.

---

## Constraining the Eager Agent

The guide is explicit: the agent is eager. Here's how to handle it:

| Agent Tendency | Guardrail |
|---------------|-----------|
| Wants to trade every session | Max 3 trades per WEEK |
| Finds reasons to buy everything | Must write thesis first, must pass all 9 buy-side gate checks |
| Rationalizes holding losers | 10% trailing stop per position, tightened per rules |
| Sends noise updates | "Do NOT send notifications unless you traded" |
| Overcomplicates analysis | Strategy.md keeps it focused on fundamentals |
| Ignores its own rules when excited | Broker wrapper can't be talked past |

---

## Two-Layer Philosophy

1. **Prompt-level guardrails** — Every session, the agent reads TRADING-STRATEGY.md and validates all 9 buy-side gate checks in its reasoning before calling broker.sh. The agent's own discipline is the first line of defense.

2. **Broker-level guardrails** — Even if the agent's reasoning is flawed, the broker.sh wrapper enforces hard limits on position size, position count, trade frequency, loss caps, and available cash. No order passes without passing the wrapper.

This redundancy is intentional. One layer fails sometimes. Two layers fail together almost never.

---

## Deliverables

- [x] TRADING-STRATEGY.md Risk Section matches strategy.md (all hard rules included)
- [x] Buy-side gate: 9 explicit checks documented
- [x] broker.sh validates all guardrails before order execution
- [x] Ensure `.env` defaults to `TRADING_MODE=paper`
- [x] CLAUDE.md references risk rules every session
- [x] Agent validates all 9 gates before calling broker.sh
- [x] Memory files are in memory/ (not state/, research/companies/, etc.)
- [x] Position sizes corrected: 20% (not 5%), max positions: 6 (not 15)
