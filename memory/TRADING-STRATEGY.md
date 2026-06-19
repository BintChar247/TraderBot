# Trading Strategy — Canonical Rulebook

**Purpose:** This is the production rulebook for the IDX trading agent. Every routine reads this file first, before any research, scan, or order placement. It is the single source of truth for hard rules, the buy-side gate, sell-side rules, the entry checklist, the sector playbook, and the morning research queries.

**Last reviewed:** 2026-06-19 (Week 9 weekly review — added Data Quality Gate section codifying 3-week multi-source ≤2% convergence pattern from Weeks 7-8-9)

**Status:** Canonical. Routines read this file first. If a rule here conflicts with anything elsewhere, this file wins. Changes go through explicit review and a new last-reviewed date.

---

## Hard Rules (non-negotiable)

These are scar tissue from real losses. They are not suggestions.

- No options. Ever. Stocks only.
- Maximum 5-6 open positions at a time.
- **Primary sizing input:** risk-based. `position_size = (0.0050 × equity) / (entry − hard_cut) × entry`. This gives max ~7% of book on a typical -7% hard cut. Cap at regime-adaptive max (20% normal; 15% in EM-OUTFLOW regime per MACRO-REGIME.md / RISK-STATE.json).
- Maximum 3 new positions (trades) per week.
- Target 75-85% of capital deployed.
- **Sector concentration cap:** no single sector > 35% of deployed capital. Enforced in gate-check.sh Check 13.
- **ADV participation cap:** order size ≤ 10% of 20-day avg daily volume. No moving markets.

### Stop Logic (two distinct states — no overlap)

Every position has exactly one stop state at any time:

**State 1 — HARD-CUT** (initial state, entry through +6.99%)
- Fixed price: entry × 0.93 (−7% from entry). Never moves.
- Fires on an intraday breach. Close immediately. No waiting for close.
- This is the **only** stop rule while the position has not yet reached +7%.

**State 2 — TRAILING** (activates once position reaches +7% from entry)
- Transitions from hard-cut once price ≥ entry × 1.07.
- Initial trailing %: 10% below high water mark.
- Tighten to 7% once position is up +15% from entry.
- Tighten to 5% once position is up +20% from entry.
- Trail % can only decrease (tighten). Never widen. Never move the stop price down.
- Stop floor: never place any stop within 3% of current price.
- `broker.sh tighten-if-triggered SYM` enforces this at midday scan.

### Portfolio-Level Caps (enforced in gate-check.sh)

- **Daily loss cap:** portfolio down -2% on the day → halt all new trades for that session.
- **Weekly loss cap:** portfolio down -5% for the week → halve all new position sizes (rounding to nearest 100-lot).
- **Max drawdown:** portfolio falls -15% from its peak → close everything, send Telegram alert, wait for Michael review. No new trades until reset.
- Exit an entire sector after 2 consecutive failed trades in that sector.
- Follow sector momentum. Don't force a thesis if the whole sector is rolling over.
- Patience beats activity. A week with zero trades can be the right answer.

---

## The Buy-Side Gate (15 checks — gate-check.sh)

All 15 checks must pass before any buy order is placed. All failures are accumulated and printed at end (not exit-on-first). Checks 10-12 (portfolio caps) are evaluated first and cause immediate rejection on breach.

**Portfolio-level (evaluated first):**
1. Positions after fill ≤ 6.
2. Trades this week ≤ 3.
3. Position cost ≤ regime-adaptive max (reads `max_position_pct` from memory/RISK-STATE.json; default 20%).
4. Position cost ≤ available cash.
5. Catalyst documented in today's RESEARCH-LOG.md.
6. Instrument is a 4–5 letter IDX stock ticker (no options, warrants, rights).
7. Avg daily volume > 500,000 shares.
8. Shares is a positive multiple of 100 (IDX lot size).
9. Current price ≤ 3% above planned entry (no chasing).
10. Daily P&L ≥ -2% (reads from RISK-STATE.json `daily_pnl_pct`).
11. Weekly P&L ≥ -5% (reads `weekly_pnl_pct`; if -5% to -10%, halve shares and continue).
12. Peak drawdown ≤ 15% from peak equity (reads `drawdown_from_peak_pct`).
13. Sector concentration after fill ≤ 40% of equity (same sector positions + new).
14. Order size ≤ 10% of avg daily volume (ADV participation cap).
15. Risk budget ≤ 75bps per idea: `(shares × (entry − hard_cut)) / equity ≤ 0.0075`.

---

## Data Quality Gate (canonical — added 2026-06-19)

A required pre-entry data-quality check codified after 3 consecutive weeks (Weeks 7-8-9) of multi-source cluster non-convergence blocking all entries AND blocking 19 consecutive sessions of state-machine transitions on the held KLBF position. Promoted from MISTAKES.md 2026-05-01 procedural discipline to canonical entry gate per Week 9 weekly-review codification trigger (3-week marker met).

**Applies to:** every buy order (in addition to the 15-check buy-side gate) AND every sell-side stop-fire decision (the broker-side trailing GTC fires automatically on real-tape; this gate governs discretionary trim/close decisions on cluster reads).

**Canonical (strict) requirement — multi-source ≤2% convergence:**
- Before any buy order is placed, the candidate's current price MUST be confirmed by ≥3 independent sources with multi-source spread ≤2% (max − min) / mean.
- Sources counted as independent: Yahoo Finance, Investing.com, TradingView, Google Finance, Stockbit, GoAPI, yfinance, broker.sh quote. Wire-service feeds (Bloomberg, Reuters) count as one source each.
- If <3 sources surface a fresh same-day timestamp, the candidate FAILS the gate.
- If ≥3 sources surface fresh timestamps but spread >2%, the candidate FAILS the gate.

**Relaxed-under-outage exception — ≤4% convergence:**
- Applies only when documented data-infra outage is active (e.g., yfinance 403-blocked, GoAPI key unset, broker.sh quote returning stale fills).
- Requires explicit RESEARCH-LOG.md documentation citing (a) which sources are unavailable, (b) the cluster spread observed, (c) the safe-lower mark used for sizing/stop-floor calculations.
- The relaxed ≤4% threshold does NOT relax the "≥3 independent sources with fresh same-day timestamp" requirement — only the spread threshold widens.
- If the relaxed gate fails (spread >4% OR <3 fresh sources), the candidate FAILS the gate and the pre-market routine must document the binding constraint (structural infra vs candidate-specific noise).

**Safe-lower mark discipline (for held positions under outage):**
- When holding a position whose cluster fails the canonical ≤2% gate, the safe-lower mark (lowest of the cluster's verified ≥3 sources, NOT including single-source outliers) is used for: (a) EOD MTM in TRADE-LOG, (b) dashboard data.json carry, (c) stop-floor calculations, (d) state-machine transition triggers (+7%/+15%/+20% thresholds).
- Single-source outliers (e.g., one stale Yahoo cache while 2+ fresh sources cluster lower) are EXCLUDED from the cluster cohort.
- State-machine transitions (HARD-CUT → TRAILING; trail-% tightening) only fire on confirmed cluster convergence at the relevant threshold (NOT on single-source readings).

**Rationale:** This gate prevents three failure modes observed in the trial: (1) BBRI 2026-05-01 hard-cut procedural lag (stale-source MTM corruption of fill price); (2) 19 consecutive sessions Day 41-59 of state-machine transition false-positives that would have fired on stale single-source Yahoo 1,135 reads while 2-source cluster low TradingView/Google 745 readings existed; (3) candidate-entry false-positives where one source shows a clean entry price while the multi-source cluster shows a 5-15% gap above/below.

**Implementation note:** Until the multi-source aggregator helper script ships, manual WebSearch polling against ≥3 sources is the required pre-market procedure. Document the cluster in every RESEARCH-LOG entry, every buy-side decision, and every midday scan that evaluates a held position.

---

## The Sell-Side Rules

Evaluated at the midday scan and opportunistically whenever new information arrives.

- If stop state is HARD-CUT and price ≤ entry × 0.93: close immediately.
- If stop state is TRAILING and price breaches trailing stop level: close immediately.
- If the thesis has broken (catalyst invalidated, sector rolling over, news event): close even if not yet at hard cut.
- At midday, run `broker.sh tighten-if-triggered SYM` for every position to apply trailing state transitions.
- If a sector has **two consecutive failed trades**, exit all positions in that sector.

---

## Entry Checklist

Document **all six items** before placing any trade. If any cannot be answered, skip the trade.

1. What is the specific catalyst today?
2. Is the sector in momentum?
3. What is the hard-cut price (entry × 0.93) and what % of equity does this risk?
4. What is the target (minimum 2:1 risk/reward from hard-cut distance)?
5. **Pre-mortem:** What specific observable event in the next 5 trading days would invalidate this thesis *before* the hard-cut fires? Name it now. Monitor it daily.
6. **Intermediate pain:** If the position hits −4% (halfway to hard cut), what is the action? (Hold if thesis intact / reassess immediately / cover if thesis uncertain). Pre-commit now.

---

## What Triggers a BUY on IDX

The primary track is **catalyst-driven**. All five below must hold:

1. **Undervalued fundamental story** — P/E or P/B below sector median, growth above.
2. **Catalyst identified** — earnings beat, BI rate cut, commodity tailwind, regulatory change, insider buying.
3. **Macro alignment** — the trade fits IDR trend, commodity cycle, foreign flow direction.
4. **Sector in momentum** — don't fight a sector that's rolling over.
5. **Thesis in 2 sentences** — if you can't write it, don't buy.

---

## Defensive Quality Track (experimental, added 2026-04-24)

A second, narrow entry path for LQ45 blue-chip compounders that screen cheap on
long-term valuation but lack a discrete near-term catalyst. Added after BBCA
repeatedly scored 5–6 and stayed on WATCH with note *"defensive, no near-term
catalyst"* — the catalyst-only rule was leaving a long-term value signal on the
table. Flagged EXPERIMENTAL: weekly-review evaluates after ≥2 defensive trades.

**Eligibility (all must hold):**
1. **Ticker is LQ45** and IDX market cap > IDR 100T. At writing: BBCA, BBRI,
   BMRI, BBNI, TLKM, ASII, UNVR, ICBP, ADRO, ASII. No mid-caps, no illiquids.
2. **Deep valuation trigger:** current P/E OR P/B is ≥ 1.0 standard deviation
   below the ticker's own trailing 10-year mean. "Below sector median" (the
   catalyst-track bar) is insufficient here — must be a long-term own-history
   discount. Research must cite the numeric mean and σ, not just "looks cheap".
3. **No adverse catalyst within 2 weeks:** no upcoming earnings where consensus
   looks stretched, no pending regulatory risk, no ex-div timing trap.
4. **Regime is not DEFENSIVE:** if `MACRO-REGIME.md` says DEFENSIVE (broad
   risk-off halt), defensive-track entries are suspended.

**Sizing and stops (tighter than catalyst track):**
- **Max position size: 10% of equity** (vs 15% for catalyst track).
- Counts against weekly max-3-trades cap.
- Standard stops: -7% hard cut, same trailing logic from +7%.
- Tagged `trade_type: "defensive"` in PAPER-STATE.json and data.json decisions[]
  so weekly-review can slice performance by track.

**Conviction assignment:** defensive-track positions are always MEDIUM at best
— never HIGH. A HIGH conviction call requires both valuation AND catalyst.

**Review trigger:** on the first defensive-track stop-out, weekly-review must
re-evaluate the track. After 2 defensive trades, if combined realized P&L is
negative AND worse than track-1 same-period alpha, remove the track from this
strategy. This is a *proposed* edge, not a proven one.

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
5. **Instrumentation** — risk metrics (beta, vol, Sharpe, VaR) turn the trial P&L into an attributable scorecard, not a lucky number.

The edge is not speed. The edge is that this rulebook gets followed on day 400 the same way it gets followed on day 4.
