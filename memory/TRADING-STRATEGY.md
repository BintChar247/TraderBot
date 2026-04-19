# Trading Strategy — Canonical Rulebook

**Purpose:** This is the production rulebook for the IDX trading agent. Every routine reads this file first, before any research, scan, or order placement. It is the single source of truth for hard rules, the buy-side gate, sell-side rules, the entry checklist, the sector playbook, and the morning research queries.

**Last reviewed:** 2026-04-18

**Status:** Canonical. Routines read this file first. If a rule here conflicts with anything elsewhere, this file wins. Changes go through explicit review and a new last-reviewed date.

---

## Hard Rules (non-negotiable)

These are scar tissue from real losses. They are not suggestions.

- No options. Ever. Stocks only.
- Maximum 5-6 open positions at a time.
- Maximum 20% of equity per position.
- Maximum 3 new positions (trades) per week.
- Target 75-85% of capital deployed.
- Every position gets a 10% trailing stop placed as a real GTC order. Never mental.
- Cut any losing position at -7% from entry. Manual sell. No hoping, no averaging down.
- Tighten the trailing stop to 7% when a position is up +15%. Tighten to 5% when up +20%.
- Never tighten a stop to within 3% of current price. Never move a stop down.
- Exit an entire sector after 2 consecutive failed trades in that sector.
- Follow sector momentum. Don't force a thesis if the whole sector is rolling over.
- Patience beats activity. A week with zero trades can be the right answer.
- **Daily loss cap:** if portfolio is down -2% on the day, halt all new trades for that day.
- **Weekly loss cap:** if portfolio is down -5% for the week, reduce all new position sizes by 50%.
- **Max drawdown:** if portfolio falls -15% from its peak, close everything, send alert, wait for human review.

---

## The Buy-Side Gate (9 checks)

Before placing any buy order, **every single one** of these checks must pass. If any fail, the trade is skipped and the reason is logged. Checks 1-6 are the core gate; checks 7-9 are the IDX-specific additions.

1. Total positions after this fill will be no more than 6.
2. Total trades placed this week (including this one) is no more than 3.
3. Position cost is no more than 20% of account equity.
4. Position cost is no more than available cash.
5. A specific catalyst is documented in today's research log entry.
6. The instrument is a stock (not an option, not anything else).
7. Stock average daily volume > 500,000 shares (liquidity check).
8. Stock is in a lot-size multiple of 100.
9. Price hasn't moved >3% from planned entry (no chasing).

---

## The Sell-Side Rules

Evaluated at the midday scan and opportunistically whenever new information arrives.

- If unrealized loss is **-7% or worse**, close immediately.
- If the thesis has broken (catalyst invalidated, sector rolling over, news event), close, even if not yet at -7%.
- If position is up **+20% or more**, tighten trailing stop to 5%.
- If position is up **+15% or more**, tighten trailing stop to 7%.
- If a sector has **two consecutive failed trades**, exit all positions in that sector.

---

## Entry Checklist

The agent documents **all of these** before placing any trade. If any item cannot be answered in plain language, the trade is skipped.

1. What is the specific catalyst today?
2. Is the sector in momentum?
3. What is the stop level (7-10% below entry)?
4. What is the target (minimum 2:1 risk/reward)?

---

## What Triggers a BUY on IDX

1. **Undervalued fundamental story** — P/E or P/B below sector median, growth above.
2. **Catalyst identified** — earnings beat, BI rate cut, commodity tailwind, regulatory change, insider buying.
3. **Macro alignment** — the trade fits IDR trend, commodity cycle, foreign flow direction.
4. **Sector in momentum** — don't fight a sector that's rolling over.
5. **Thesis in 2 sentences** — if you can't write it, don't buy.

---

## Sector Playbook

| Sector | When to Be Long | Key Drivers |
|--------|-----------------|-------------|
| **Banking** (BBCA, BBRI, BMRI, BBNI) | BI cutting rates, loan growth up | Interest rates, credit growth, NPL |
| **Coal** (ADRO, ITMG, PTBA) | Coal >$100/ton | Newcastle coal, China demand |
| **Nickel** (ANTM, INCO, MDKA) | EV demand, nickel rising | LME nickel, smelter policy |
| **Telco** (TLKM, EXCL, ISAT) | Data revenue growing | ARPU, subscriber growth |
| **Consumer** (UNVR, ICBP, INDF) | IDR stable, inflation low | CPI, IDR/USD, Ramadan |
| **Property** (BSDE, CTRA, SMRA) | BI cutting rates | Interest rates, pre-sales |

---

## Morning Research Queries (IDX)

These replace US-centric queries in any upstream guide. The morning routine runs these (or their equivalents) before any scanning or order placement.

| Generic / US Query | IDX Adaptation |
|--------------------|----------------|
| "WTI and Brent oil price" | "Newcastle coal price, palm oil price, nickel price today" |
| "S&P 500 futures premarket" | "IHSG index level, Asia markets overnight" |
| "VIX level today" | "IDR USD exchange rate today" |
| "Top stock market catalysts today" | "Top IDX stock catalysts today, Bank Indonesia news" |
| "Earnings reports today" | "IDX earnings releases today, corporate actions" |
| "Economic calendar today CPI PPI FOMC" | "Indonesia economic calendar CPI PDB BI rate decision" |
| "S&P 500 sector momentum YTD" | "IDX sector momentum LQ45 index composition" |

---

## Why This Works (the edge, and why discipline beats FOMO)

When the rules feel inconvenient — when a stock is running and the gate says no, when a week ends with zero trades, when the -7% cut looks premature — re-read this section before overriding anything.

1. **Depth** — Opus digests more filings and data than a human analyst in the same time.
2. **Discipline** — follows rules, no FOMO, no revenge trading, no emotional attachment.
3. **Consistency** — runs every session, every day.
4. **Learning** — compounds knowledge through WEEKLY-REVIEW.md over months.

The edge is not speed. The edge is that this rulebook gets followed on day 400 the same way it gets followed on day 4.
