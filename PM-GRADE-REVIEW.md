# PM-Grade Review — idx-trader

**Benchmarked against:** multi-manager pod desk (Citadel / Millennium archetype)
**Scope:** strategy rulebook, execution algorithms (scripts/), dashboard & reporting
**Date:** 2026-04-19 (T-1 to trial start)
**Verdict:** Retail-disciplined, not pod-disciplined. The rules read like a good swing trader's journal, not a PM desk. Risk is defined in **dollar size** instead of **capital-at-risk**; there is no factor, correlation, or liquidity-adjusted exposure framework; the hard guardrails (daily / weekly / MDD caps) are not enforced in code — only in prose.

The changes below add the missing disciplines without re-architecting the product.

---

## Executive Gap List (Priority Order)

| # | Gap | Severity | Lives in |
|---|-----|----------|----------|
| 0 | **Starting-capital inconsistency**: CLAUDE.md + dashboard say IDR 10B; TRADE-LOG simulation was run on IDR 300M (33× smaller) | CRITICAL | `CLAUDE.md:4`, `dashboard/data.json:5`, `TRADE-LOG.md:89,107,193` |
| 1 | Sizing is **notional-based**, not **risk-based** | HIGH | `TRADING-STRATEGY.md` §Hard Rules, `gate-check.sh` Check 3 |
| 2 | Hard cut (-7%) and trailing stop (-10%) **overlap incoherently** | HIGH | `TRADING-STRATEGY.md`, `TRADE-LOG.md` columns, `broker.sh cmd_buy` |
| 3 | Daily / weekly / MDD caps are **prose only** — zero code enforcement | HIGH | `gate-check.sh` (no caps), `broker.sh cmd_buy` (no halt logic) |
| 4 | **No correlation / sector-concentration** cap | HIGH | `TRADING-STRATEGY.md`, `gate-check.sh` |
| 5 | **No factor / beta exposure** tracking (IHSG, IDR, coal, nickel, BI-rate) | HIGH | everywhere — not computed anywhere |
| 6 | Regime-adaptive sizing (15% cap) is **documented but not enforced** | MED | `MACRO-REGIME.md` vs `gate-check.sh` Check 3 |
| 7 | **Stop tightening is manual** — no code path for +15%/+20% triggers | MED | `broker.sh` has no `tighten-stop` / update-on-high |
| 8 | **No ADV participation cap** — 500K ADV gate is absolute, not relative to order | MED | `gate-check.sh` Check 7 |
| 9 | Performance layer has **no risk metrics** — no vol, Sharpe, Sortino, VaR, beta | MED | `performance.py` snapshot/weekly |
| 10 | **No P&L attribution** — idiosyncratic vs factor P&L never separated | MED | `performance.py`, dashboard |
| 11 | Dashboard has **no stop-distance / R-multiple / risk-budget** widgets | MED | `dashboard/index.html` |
| 12 | Entry checklist has **no pre-mortem** (what would invalidate the thesis?) | MED | `TRADING-STRATEGY.md` §Entry Checklist |
| 13 | Paper broker returns a **hard-coded stub** — no simulated fills or state | LOW-OPERATIONAL | `broker.sh` `_paper_*` helpers |
| 14 | Active-positions-table schema drift: `Avg Cost` column is total cost in one place, per-share price in another | LOW | `TRADE-LOG.md` active vs EOD tables |
| 15 | Gate check exits on first fail — loses the full failure list | LOW | `gate-check.sh` `pass`/`fail` helpers |
| 16 | No slippage / impact-cost tracking (fills always = last price) | LOW | `broker.sh cmd_buy` paper path |

---

## Section 1 — Strategy Rulebook: What a Pod PM Would Change

The trading-strategy file has the skeleton of a good process (hard rules, buy-gate, sell-gate, sector playbook, morning queries). The problem is that nearly every "risk" statement in the file is a **notional** or **count** rule, not a risk rule. Pod PMs think in capital-at-risk and factor exposure; this rulebook thinks in position count and position size.

### 1.1 Sizing: move to risk-based sizing

**Current rule:** "Maximum 20% of equity per position." (`TRADING-STRATEGY.md:18`)

**Problem:** Two positions sized identically at 20% notional can have wildly different risk. A 20% position in a low-vol blue-chip (BBCA, 15-day realised vol ~1%) risks ~20bps/day book loss on a 1σ move. A 20% position in a high-beta commodity name (MDKA, realised vol ~3-4%) risks ~60-80bps/day book loss. In a pod desk these would be sized very differently.

**PM standard:** size by **capital at risk to the stop**.
```
position_size = (risk_budget_per_trade × equity) / (entry_price − stop_price) × entry_price
```
Typical pod risk budget per trade: **25–75bps of book**. With the existing -7% hard cut, a 50bps book risk → position size = 0.005 / 0.07 = **7.1% of equity**, not 20%.

**Action:** keep 20% as a *ceiling*, but add a risk-based calc as the *primary* sizing input. Add to `TRADING-STRATEGY.md` §Hard Rules and enforce in `gate-check.sh` as a new Check 3b.

### 1.2 Hard cut vs trailing stop — fix the overlap

**Current rules:**
- "Every position gets a 10% trailing stop placed as a real GTC order." (`TRADING-STRATEGY.md:20`)
- "Cut any losing position at -7% from entry." (`TRADING-STRATEGY.md:21`)

**Problem:** with both rules live, the -7% hard cut always triggers before the 10% trailing stop for any position that never goes green. The trailing stop only *becomes binding* once the position is +3% or more. The rulebook presents them as two different rules but in practice they are the same rule with two different numbers. The trade log amplifies the confusion:

- `TRADE-LOG.md:11` BBRI Active-Positions row: "Stop (IDR) = 2,995" — but 3,220 × 0.90 = 2,898, not 2,995. 2,995 is actually -7% of 3,220, i.e. the **hard cut**, incorrectly labelled as the trailing stop.
- Same issue for ANTM (`:12`, stop 3,674 = entry × 0.93, not × 0.90) and ITMG (`:13`, stop 25,110 = entry × 0.93).

Then inside the same file at `TRADE-LOG.md:121`, the log says `Stop: IDR 2,995 (10% trailing GTC placed — IDR 3,220 × 0.90)` — which is mathematically wrong.

**PM standard:** pick one primary stop logic and treat the other as a sanity check.
- **Option A (recommended):** initial stop = hard cut (-7%). Convert to trailing once position is +7% (breakeven + 7%). This gives a crisper meaning to "trailing" and removes the arithmetic conflict.
- **Option B:** initial stop = -10% trailing, with a **catastrophe cut** at -7% that only fires on a single-day -7% gap (i.e., the stop hasn't had time to trail). Then the two rules have distinct meanings.

Either way — rewrite TRADING-STRATEGY.md §Hard Rules to define stop-state unambiguously, and fix the Active Positions table in TRADE-LOG.md so "Stop" is a single well-defined number derived from a single rule.

### 1.3 Daily / weekly / MDD caps — enforce in code

**Current rules** (`TRADING-STRATEGY.md:27–29`):
- Daily loss cap: -2% → halt all new trades for the day.
- Weekly loss cap: -5% → reduce all new position sizes by 50%.
- Max drawdown: -15% → close everything, send alert, wait for human review.

**Problem:** none of these are enforced in `gate-check.sh`. They exist only as reminders for Layer 1 (the agent's prompt). Millennium-style "risk off" logic lives **in code**, not in prose — because a PM who is losing money is the least likely person to read the rule that says "stop trading."

**PM standard — Millennium drawdown ladder (representative):**
```
drawdown from book high:
  -1% : hold, no changes
  -2% : gross cap 80%, size new ideas ×0.5
  -3% : gross cap 60%, no new ideas; existing trades only
  -5% : flatten all except highest-conviction (top-3)
  -8% : flatten; risk committee review to re-engage
```

**Action:** add three new checks to `gate-check.sh` (Checks 10–12), executed before the existing 9:
- **Check 10:** daily P&L ≥ -2% of equity, else reject.
- **Check 11:** weekly P&L ≥ -5% of equity, else size ÷ 2.
- **Check 12:** peak drawdown ≥ -15%, else reject + pager notification.

The agent's midday and EOD routines should compute a `risk-state.json` that the gate reads; that way gate-check.sh stays deterministic and doesn't re-compute state.

### 1.4 Correlation / sector concentration cap

**Current rule:** "Exit an entire sector after 2 consecutive failed trades." (`TRADING-STRATEGY.md:24`)

**Problem:** this is reactive, not preventative. A pod PM prevents the loss by capping sector gross before the consecutive losses happen. Today, the rulebook would let you buy ANTM + INCO + MDKA at 20% each = 60% of book on one nickel catalyst — a single LME move liquidates the portfolio. Coal (ADRO + ITMG + PTBA) is the same. Banking (BBCA + BBRI + BMRI + BBNI) even worse.

**PM standard:** sector gross cap + correlation-adjusted concentration cap.
- **Sector gross cap:** no single sector > 35–40% of deployed capital.
- **Pairwise correlation cap:** for any two existing positions with 60d correlation > 0.7, combined weight ≤ 30% of book.
- **Commodity exposure cap:** all names with >0.4 correlation to a single commodity ≤ 35% of book.

**Action:** encode sector caps in `gate-check.sh` Check 13. Compute simple rolling-60d return correlations in `performance.py` and expose `correlation_matrix` in `data.json`; the dashboard shows a heatmap.

### 1.5 Factor / beta exposure — the biggest missing piece

**Current rule:** none. Not mentioned anywhere.

**PM standard:** decompose gross into factor exposures. For IDX swing book, the relevant factors are:
- IHSG (market beta)
- USD/IDR (FX)
- Newcastle coal
- LME nickel
- BI policy rate (duration proxy via banks)
- CPO
- Foreign-flow sensitivity (the "foreign selling" beta — a known IDX factor)

At minimum, compute rolling 60-day beta of each holding to each factor and sum by weight. A pod book is expected to know, at every session: "gross long IHSG-beta = 0.85, coal-beta = 0.35, USD-beta = 0.15." Without this, the sector playbook's claim "nickel" is a lie — ANTM isn't purely nickel; it's coal-correlated, FX-correlated, and foreign-flow-correlated.

**Action:** add `performance.py factors` subcommand that computes weighted factor betas and prints a table. Add a `Factor Exposure` panel to the dashboard. This is the single largest gap between the current system and a PM desk.

### 1.6 Regime-adaptive sizing — enforce, don't describe

**Current state:** `MACRO-REGIME.md:20` says "Reduce max position size from 20% → 15% of equity on new entries." This is a well-formed rule, but it lives in a memory file that only the agent's prompt reads. `gate-check.sh` Check 3 hardcodes 20%.

**Action:** `MACRO-REGIME.md` should write the active sizing cap into `memory/RISK-STATE.json` (a tiny machine-readable file):
```json
{
  "regime": "EM-OUTFLOW-RISK-CAUTIOUS",
  "max_position_pct": 0.15,
  "max_gross_pct": 0.80,
  "max_new_trades_week": 3,
  "updated": "2026-04-19"
}
```
`gate-check.sh` Check 3 reads `max_position_pct` from that file (defaulting to 0.20 if missing). Same pattern for Checks 10–12 above.

### 1.7 Stop-tightening — needs a routine, not a hope

**Current rule:** `TRADING-STRATEGY.md:22` — tighten to 7% at +15%, 5% at +20%.

**Problem:** `broker.sh` has no `tighten-stop` automation. The only way this gets applied is if the midday routine remembers. That means it won't, reliably.

**Action:** add `broker.sh tighten-if-triggered SYM` that:
1. Pulls current price and entry from broker.
2. Computes return pct.
3. If ≥ +20% → set-stop 5; if ≥ +15% → set-stop 7.
4. Never widens an existing stop (hard-coded).
The midday routine loops this over all positions.

### 1.8 Pre-mortem — add to Entry Checklist

**Current checklist** (`TRADING-STRATEGY.md:65–68`) asks four questions: catalyst, sector momentum, stop level, target. None of them invites falsification.

**PM standard — add two questions:**
- "What specific observable event in the next 5 trading days would invalidate this thesis **before** the -7% hard cut fires?"
- "If this trade hits -4%, what is my action? (cover / hold / double / re-evaluate)"

The first question forces an early-warning tripwire — the thing the agent should be monitoring daily that tells it to exit before the stop. The second question pre-commits to an action at an intermediate pain point, which is where undisciplined PMs freeze.

---

## Section 2 — Algorithms: What the Scripts Are Missing

### 2.1 `gate-check.sh` — upgrade from 9 gates to a 12-gate portfolio gate

Current 9 gates are all **trade-level**. A pod PM desk's gate is both trade-level and **portfolio-level**:

| New gate | What it checks | Source |
|----------|---------------|--------|
| 10. Daily P&L cap | read `data.json` latest daily_pnl_pct; reject if < -2% | `dashboard/data.json` |
| 11. Weekly P&L cap | read weekly MTD P&L; if < -5% size ÷ 2 | `performance.py weekly` |
| 12. Peak drawdown | compare current equity to max equity in `daily[]`; reject if > 15% from peak | `dashboard/data.json` |
| 13. Sector concentration | (new position cost + same-sector existing) / equity ≤ 0.40 | `broker.sh positions` + sector map |
| 14. ADV participation | `shares <= 0.10 × avg_daily_volume_20d` | `yfinance_helper.py liquidity` |
| 15. Risk budget per trade | `shares × (entry − hard_cut) / equity ≤ 0.0075` (75bps max per idea) | arithmetic |

Also: change the `pass`/`fail` helpers so that a `fail()` records into a list but does not exit immediately. Only at the end does the script return nonzero with the full list of failures. This is a 4-line change and saves round-trips.

```bash
# instead of: [[ "${FAILED}" -eq 0 ]] || exit 1
# after each check, simply continue
# at the end: if (( ${#FAILURES[@]} )); then printf 'FAIL: %s\n' "${FAILURES[@]}"; exit 1; fi
```

### 2.2 `broker.sh` — atomic entry + stop, no silent stop-moves

**Problems:**
- `cmd_buy` sends the market order and *then* calls `cmd_set_stop`. In live mode with async fills this creates a naked-long window where the position has no protective stop. A fill-reject between order and stop-placement is also possible.
- `cmd_set_stop` has no "don't move the stop down" guard. Calling `set-stop SYM 10` twice with a price that has dropped between calls would silently widen the stop — a direct violation of `TRADING-STRATEGY.md:23`.
- `cmd_set_stop` accepts `pct` but never reads/stores the absolute stop price. The state lives inside the broker. Without local state, `gate-check.sh` and `performance.py` cannot verify the rule.

**PM standard:** OCO (one-cancels-other) entry, or a post-fill poll that verifies both fill and stop before returning success. Every stop write goes into `memory/STOPS.json`:

```json
{
  "BBRI": {"entry": 3220, "current_stop": 2995, "trail_pct": 7,
           "last_updated": "2026-04-21T09:22:00+07:00",
           "high_water_price": 3410, "state": "hard-cut"}
}
```

`cmd_set_stop` must refuse to write a new stop that is **below** `current_stop`, fail loudly, and notify.

### 2.3 `performance.py` — add `risk`, `factors`, `attribution` subcommands

Current subcommands are `snapshot`, `weekly`, `benchmark`, `lessons-diff`. A PM desk also needs:

- **`risk`** — daily vol (10d, 20d), downside vol, Sharpe (annualised, rf = BI rate), Sortino, max drawdown from peak, **portfolio beta to IHSG**, 95%/99% 1-day historical VaR, expected shortfall.
- **`factors`** — for each position, 60d beta to {IHSG, IDR, coal, nickel, BI proxy}. Weighted sum = book factor exposure.
- **`attribution`** — week-over-week, decompose P&L into: market (beta × IHSG return), sector, idiosyncratic. Output a simple 3-column markdown table.

These are ~150 lines of Python combined. They turn the system from a trade tracker into a risk-measurable book.

### 2.4 `market-data.sh` — intraday bar fallback is fragile

`cmd_intraday`'s inline Python uses `yf.Ticker(...).history(period="1d", interval=interval)`. yfinance returns empty for many IDX mid/small caps on short intervals; the script would emit `{"error": "no intraday data"}` with exit code 0, and the caller has no signal to retry. Also — minute-level intraday is unnecessary for a swing-horizon strategy. Consider dropping intraday from the default tooling and relying on 1-day history for the swing process.

### 2.5 Paper-broker state — the stub blocks real paper testing

`_paper_portfolio`, `_paper_positions`, `_paper_cash` return hard-coded values. There is no persistence. That means `performance.py snapshot` can't show meaningful paper P&L progression during the trial; every day reports IDR 10B equity, zero positions, even though `TRADE-LOG.md` has simulated trades.

**Action:** implement a tiny JSON-backed paper ledger at `memory/PAPER-STATE.json`. Every paper `buy` appends to positions; every paper `sell` closes. Equity = starting capital + realised + unrealised (computed by fetching latest `market-data.sh quote` for each open name). This is ~80 lines of bash or, cleaner, a 150-line Python wrapper invoked from broker.sh.

Until this is fixed, **everything the dashboard reports during the trial is fiction** drawn from the markdown simulation, not from the broker wrapper.

---

## Section 3 — Dashboard: What a Pod PM Would Add

The dashboard is visually polished (Bloomberg-terminal aesthetic) and already covers the basics: scorecard, equity curve, daily P&L, cash gauge, macro snapshot, sector momentum heatmap, gate pipeline, sell-trigger legend, params reference, decision log, positions/trades tables, and a history tab. What it does not yet look like is a PM desk's risk screen. The panels a pod PM reads first — before equity — are all missing.

### 3.1 Missing: Risk panel (top-of-screen)

Populate four new scorecard cells:
- **Portfolio β vs IHSG** (60d rolling)
- **1-day 95% VaR** (historical, expressed as % of equity)
- **Weekly realised vol** (annualised)
- **Sharpe (annualised, trailing 10d, rf = 4.75%)**

These come from `performance.py risk`.

### 3.2 Missing: Stop-distance + R-multiple table

For every open position, display:

| Ticker | Entry | Last | Stop | % to Stop | R-mult | State |
|--------|-------|------|------|-----------|--------|-------|
| BBRI | 3,220 | 3,390 | 3,069 | +10.5% | +0.75R | trailing |
| ANTM | 3,950 | 4,050 | 3,663 | +10.6% | +0.36R | trailing |
| ITMG | 27,000 | 27,050 | 24,480 | +10.5% | +0.03R | initial |

R-multiple (gain measured in units of initial risk) is the single most important per-trade number a pod PM tracks. A book whose winners close at >+2R and whose losers close at <-1R is profitable even at a 40% win rate.

### 3.3 Missing: Sector & factor exposure bars

Two stacked bars:
- **Sector exposure** — horizontal bar per sector, cap line at 35%, color red if any exceeds.
- **Factor exposure** — beta to IHSG, IDR, coal, nickel, BI. These mirror what the `factors` subcommand computes.

### 3.4 Missing: Risk budget utilisation

A simple gauge: "Used 1.25% of 5.00% weekly loss budget across 3 open positions." Populated from the sum of (position-size × stop-distance) / equity. When the gauge hits 3% the system should pre-warn; at 5% new entries are blocked.

### 3.5 Missing: Thesis integrity tracker

For each open position, one column: "catalyst_date" (next earnings / policy event / HPM ruling). When catalyst_date passes without the thesis validating (`thesis_valid: true|false|pending`) the UI highlights it amber — because an unvalidated thesis after the catalyst date is a silent thesis-break.

### 3.6 Dashboard data-model additions

Add to `dashboard/data.json`:
```json
{
  "risk": {
    "beta_ihsg": 0.82,
    "var_95_1d_pct": 1.8,
    "realised_vol_annual_pct": 18.4,
    "sharpe_10d": 1.2,
    "drawdown_from_peak_pct": 0.0
  },
  "exposure": {
    "sectors": {"Banking": 0.19, "Nickel": 0.20, "Coal": 0.20},
    "factors": {"ihsg": 0.82, "idr": 0.21, "coal": 0.35, "nickel": 0.18, "bi": 0.30}
  },
  "positions": [
    {
      "ticker": "BBRI", "entry": 3220, "last": 3390, "shares": 17400,
      "hard_cut": 2995, "trail_stop": 3069, "trail_pct": 7,
      "pct_to_stop": 9.5, "r_multiple": 0.75,
      "thesis": "ex-div + Q1 earnings",
      "catalyst_date": "2026-04-29",
      "thesis_state": "pending"
    }
  ]
}
```

---

## Section 4 — Detail Issues Worth Fixing This Week

| File | Line | Issue |
|------|------|-------|
| `TRADE-LOG.md` | 11 | BBRI "Avg Cost (IDR) = 59,228,000" does not reconcile: 3,220 × 17,400 = 56,028,000. Either the shares, price, or a fee/tax line is off. Fix the source of truth before trial starts. |
| `TRADE-LOG.md` | 11–13 | "Stop" column shows -7% hard-cut values, not the -10% trailing stop as the column header implies. Rename or recompute. |
| `TRADE-LOG.md` | 60–63 | EOD snapshot positions table uses "Avg Cost" = per-share; active-positions table uses "Avg Cost" = total. One schema. |
| `TRADE-LOG.md` | 121 | "Stop: IDR 2,995 (10% trailing GTC placed — IDR 3,220 × 0.90)" — arithmetic wrong; 3,220 × 0.90 = 2,898. |
| `gate-check.sh` | 60 | WIB date fallback uses `date -u -v+7H` (BSD) and then plain `date`. On Linux CI without TZ data the final fallback returns UTC, not WIB — off by one near midnight. Use `python -c` for deterministic TZ math. |
| `gate-check.sh` | 220 | Check 6 accepts only 4-letter uppercase tickers. IDX has tickers like `MTEL`, `GOTO` (4 letters, fine) but also some 5-char tickers post-spin-offs. Verify universe. |
| `gate-check.sh` | 196–205 | The awk parser for Check 5 (catalyst) is fragile; `/^#.*${TODAY_DATE}/` misses headings written as `## 2026-04-19 — Pre-Market`. Test against the actual research-log format. |
| `performance.py` | 105–109 | Position regex assumes strict markdown pipe-table spacing; fragile to trailing whitespace. |
| `performance.py` | 355–362 | `sector_exposure` never populated — `pos` objects have no `sector` key anywhere. Dead code. |
| `performance.py` | 505–531 | `lessons-diff` is a stub. Either implement or remove from CLI. |
| `broker.sh` | 238, 259 | Paper and live paths both hard-code `cmd_set_stop "${sym}" "10"` after fill. No regime-adaptive pct; no linkage to the sizing rule. |
| `broker.sh` | 323 | Live-mode absolute stop-price = `last × (100-pct)/100` computed with `bc`. Rounds down; IDX has tick-size rules that may reject off-tick prices. Use `yfinance_helper` or broker-provided tick grid. |

---

## Section 5 — Priority Fix Order (what I'd ship first)

**Tier 0 — must fix before Monday open (2026-04-20):**
1. **Resolve the starting-capital inconsistency.** `CLAUDE.md:4` and `dashboard/data.json:5` both say IDR 10,000,000,000, but the simulated trade log (`TRADE-LOG.md:89,107,193`) was clearly built around IDR 300,000,000 — every position size is ~19% of 300M, not 10B. Decide which is canonical and rewrite the other, or the dashboard will display 10B equity while gate-check is reasoning about 300M. This is the single most important fix.
2. Reconcile `TRADE-LOG.md` Active-Positions schema and math (items in §4; notably BBRI `Avg Cost` = 59,228,000 doesn't reconcile to `Entry Price × Shares` = 56,028,000).
3. Resolve hard-cut vs trailing-stop overlap in `TRADING-STRATEGY.md` §Hard Rules. One number per state.
4. Wire `gate-check.sh` Checks 10–12 (daily / weekly / MDD caps) even if as a read from a stubbed `RISK-STATE.json`.

**Tier 1 — within week 1 of trial:**
4. Sector concentration cap (Check 13) in `gate-check.sh`.
5. Risk-based sizing Check 15 in `gate-check.sh`.
6. `broker.sh tighten-if-triggered` + `memory/STOPS.json` ledger.
7. Paper-broker state (`memory/PAPER-STATE.json`) so the dashboard reflects reality.

**Tier 2 — before weekly review Friday 2026-04-24:**
8. `performance.py risk` subcommand (beta, vol, Sharpe, VaR).
9. Dashboard Risk scorecard cells + stop-distance/R-multiple table.
10. Sector & factor exposure panels on dashboard.

**Tier 3 — week 2:**
11. `performance.py factors` and `attribution`.
12. Pre-mortem questions into `TRADING-STRATEGY.md` Entry Checklist.
13. Correlation matrix (computed weekly, pushed to data.json and dashboard).

---

## Appendix — Why This Matters for the Trial

The current rulebook will not *lose* the trial — the hard cut and trailing stop will contain any single-name blowup. What it will do is **make the P&L outcome uninterpretable**. If the book beats IHSG by 300bps over two weeks, the current instrumentation cannot tell you whether that came from stock-picking alpha, a lucky coal rally, a short-IDR tailwind, or IHSG-beta drift. A pod desk's post-trial review would reject that outcome as "unattributable" regardless of the P&L sign.

The upgrades above are not more rules. They are **instrumentation** — they turn the trial's output from a single P&L number into a decomposable scorecard that tells you whether the strategy has a real edge or just rode a factor.
