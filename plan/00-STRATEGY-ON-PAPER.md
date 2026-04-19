# Step 1 — Strategy on Paper

> *The logic is simple; the discipline is the hard part. These rules exist because they were learned the hard way.*

---

## Hard Rules (non-negotiable)

Adapted from the guide for IDX. These are scar tissue from real losses.

- No options. Ever. Stocks only.
- Maximum 5-6 open positions at a time.
- Maximum 20% of equity per position.
- Maximum 3 new trades per week.
- Target 75-85% of capital deployed.
- Every position gets a 10% trailing stop placed as a real GTC order. Never mental.
- Cut any losing position at -7% from entry. Manual sell. No hoping, no averaging down.
- Tighten the trailing stop to 7% when a position is up +15%. Tighten to 5% when up +20%.
- Never tighten a stop to within 3% of current price. Never move a stop down.
- Exit an entire sector after 2 consecutive failed trades in that sector.
- Follow sector momentum. Don't force a thesis if the whole sector is rolling over.
- Patience beats activity. A week with zero trades can be the right answer.

---

## The Buy-Side Gate

Before placing any buy order, **every single one** of these checks must pass. If any fail, the trade is skipped and the reason is logged.

1. Total positions after this fill will be no more than 6
2. Total trades placed this week (including this one) is no more than 3
3. Position cost is no more than 20% of account equity
4. Position cost is no more than available cash
5. A specific catalyst is documented in today's research log entry
6. The instrument is a stock (not an option, not anything else)

### IDX-Specific Gate Additions
7. Stock average daily volume > 500,000 shares (liquidity check)
8. Stock is in a lot-size multiple of 100
9. Price hasn't moved >3% from planned entry (no chasing)

---

## The Sell-Side Rules

Evaluated at the midday scan and opportunistically:

- If unrealized loss is **-7% or worse**, close immediately
- If the thesis has broken (catalyst invalidated, sector rolling over, news event), close, even if not yet at -7%
- If position is up **+20% or more**, tighten trailing stop to 5%
- If position is up **+15% or more**, tighten trailing stop to 7%
- If a sector has **two consecutive failed trades**, exit all positions in that sector

---

## Entry Checklist

The agent documents **all of these** before placing any trade:

1. What is the specific catalyst today?
2. Is the sector in momentum?
3. What is the stop level (7-10% below entry)?
4. What is the target (minimum 2:1 risk/reward)?

---

## IDX-Specific Strategy Notes

### What Triggers a BUY on IDX

1. **Undervalued fundamental story** — P/E or P/B below sector median, growth above
2. **Catalyst identified** — earnings beat, BI rate cut, commodity tailwind, regulatory change, insider buying
3. **Macro alignment** — the trade fits IDR trend, commodity cycle, foreign flow direction
4. **Sector in momentum** — don't fight a sector that's rolling over
5. **Thesis in 2 sentences** — if you can't write it, don't buy

### Sector Playbook

| Sector | When to Be Long | Key Drivers |
|--------|----------------|-------------|
| **Banking** (BBCA, BBRI, BMRI, BBNI) | BI cutting rates, loan growth up | Interest rates, credit growth, NPL |
| **Coal** (ADRO, ITMG, PTBA) | Coal >$100/ton | Newcastle coal, China demand |
| **Nickel** (ANTM, INCO, MDKA) | EV demand, nickel rising | LME nickel, smelter policy |
| **Telco** (TLKM, EXCL, ISAT) | Data revenue growing | ARPU, subscriber growth |
| **Consumer** (UNVR, ICBP, INDF) | IDR stable, inflation low | CPI, IDR/USD, Ramadan |
| **Property** (BSDE, CTRA, SMRA) | BI cutting rates | Interest rates, pre-sales |

### What the Agent Researches Each Morning (IDX Version)

Replace the guide's US-centric queries:

| Guide's Query (US) | IDX Adaptation |
|--------------------|----------------|
| "WTI and Brent oil price" | "Newcastle coal price, palm oil price, nickel price today" |
| "S&P 500 futures premarket" | "IHSG index level, Asia markets overnight" |
| "VIX level today" | "IDR USD exchange rate today" |
| "Top stock market catalysts today" | "Top IDX stock catalysts today, Bank Indonesia news" |
| "Earnings reports today" | "IDX earnings releases today, corporate actions" |
| "Economic calendar today CPI PPI FOMC" | "Indonesia economic calendar CPI PDB BI rate decision" |
| "S&P 500 sector momentum YTD" | "IDX sector momentum LQ45 index composition" |

---

## The Edge

Not speed. The edge is:
1. **Depth** — Opus digests more filings and data than a human analyst in the same time
2. **Discipline** — follows rules, no FOMO, no revenge trading, no emotional attachment
3. **Consistency** — runs every session, every day
4. **Learning** — compounds knowledge through WEEKLY-REVIEW.md over months

---

## Deliverables

- [ ] Michael reviews and approves the hard rules above
- [ ] Confirm: are 20% per position and 5-6 max positions right for IDX account size?
- [ ] Confirm: -7% cut rule and 10% trailing stop suitable for IDX volatility?
- [ ] This document becomes `memory/TRADING-STRATEGY.md`
