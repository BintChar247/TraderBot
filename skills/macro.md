# Skill: Macro Readout (Pod-PM Grade)

Purpose: compact but pod-PM-grade macro snapshot for the top of each pre-market research entry.
This is a structured header, not an essay. Every number must be stamped with a source tick + WIB
timestamp. If a field can't be fetched, write `null` — do not fabricate, do not omit the key.

Canonical regime thresholds live in `memory/MACRO-REGIME.md` (quantitative triggers) and
`memory/TRADING-STRATEGY.md` ("Sector Playbook" + hard rules). Read them before classifying.

---

## What to fetch — the full pod checklist

The readout has **seven blocks**. All are required. If a source is down, write `null` and
flag in `data_quality.missing`.

### 1. US / global (overnight close)
- SPX close, % change
- ES futures (e.g. ES1 front-month), % vs prior US close, time of tick
- UST 2Y yield, UST 10Y yield, 2s10s spread (bp)
- Fed funds implied path for next meeting (bp cut/hike priced)
- DXY close
- VIX close
- Gold spot $/oz

### 2. China (most important single EM driver)
- CSI300 close, % change (prior session)
- USD/CNH fix and spot
- PBOC action today (MLF / RRR / OMO) — describe in one phrase, else `none`
- China property tape (one-line headline, else `null`)
- Caixin manufacturing PMI (latest print + expected next)

### 3. Commodities (IDX-relevant complex)
- Newcastle coal $/ton, day %
- Coking coal $/ton, day %  *(if available)*
- Brent $/bbl, day %
- WTI $/bbl, day %
- LME nickel $/ton, day %
- LME copper $/ton, day %
- LME tin $/ton, day %
- Gold $/oz (repeated from block 1 for convenience)
- CPO MYR/ton (and USD/ton equivalent), day %
- Baltic Dry Index (or Capesize 5TC) — freight proxy

### 4. Indonesia equity + FX
- IHSG close, % change, distance vs 200-day MA (%)
- IHSG 5d foreign net flow (IDR bn, signed)
- IDR/USD spot, day % (convention: − = IDR weaker)
- IDR 1M NDF
- INDOGB 10Y yield (%)
- Indonesia 5Y sovereign CDS (bp)
- BI 7DRR (%) + last_move (cut | hold | hike) + date of last move

### 5. Asia / EM context (pre-open)
- Nikkei 225 close %
- HSI close %
- KOSPI close %
- SET (Thailand) close %
- KLCI (Malaysia) close %
- MSCI EM index % (latest close)
- EIDO US ETF close % (FX-hedged IDX proxy for US investors)

### 6. Calendar (today + next 5 sessions)
- Today's IDX earnings releases (list of tickers)
- Today's ex-div / corporate actions (list: ticker, action, amount)
- This week's macro prints (CPI, trade balance, BI RDG, FOMC)
- Active MSCI/FTSE rebalance windows (Y/N + cut-off date)
- Any ESDM / regulator ruling expected today

### 7. Risk / credit
- EMBI Indonesia OAS (bp) or proxy
- US HY OAS (bp), IG OAS (bp)
- Cross-currency basis IDR 1Y (bp, if available)

---

## Output format — single YAML block

```yaml
as_of_wib: "2026-04-20 06:55"
data_quality:
  sources: ["yfinance", "websearch:reuters", "bi.go.id", "idx.co.id"]
  missing: []                     # list field paths that could not be fetched
  stale_minutes_max: 15           # newest tick should be <= 15m old pre-open

us_global:
  spx_close: 5123.4               # +0.3%
  es_fut: 5130.2                  # +0.14% vs US close, tick 06:40 WIB
  ust_2y_pct: 4.62
  ust_10y_pct: 4.28
  curve_2s10s_bp: -34
  fed_next_mtg_priced_bp: -5      # negative = cut priced
  dxy: 103.80
  vix: 14.2
  gold_usd: 2340

china:
  csi300_pct: -0.4
  usdcnh_fix: 7.2015
  usdcnh_spot: 7.2140
  pboc_action: none
  property_headline: null
  caixin_pmi_last: 50.8
  caixin_pmi_next: null

commodities:
  coal_newcastle_usd: 131.00      # -1.9%
  coking_coal_usd: null
  brent_usd: 86.40                # +0.5%
  wti_usd: 82.10                  # +0.4%
  nickel_lme_usd: 17635           # +4.81% mo
  copper_lme_usd: 9480            # +0.2%
  tin_lme_usd: 30200              # -0.1%
  cpo_myr: 4495                   # -0.5%
  cpo_usd_equiv: 955
  baltic_dry: 1620                # -1.1%

equity_id:
  ihsg_close: 7634
  ihsg_day_pct: +0.60
  ihsg_vs_200dma_pct: -2.1        # negative = below
  foreign_net_5d_idr_bn: -3310
  idr_usd: 17115                  # -0.97% (IDR weaker)
  idr_ndf_1m: 17180
  indogb_10y_pct: 6.85
  cds_5y_bp: 108
  bi_7drr_pct: 4.75
  bi_last_move: hold
  bi_last_move_date: "2026-04-17"

asia_em:
  nikkei_pct: -1.75
  hsi_pct: -1.01
  kospi_pct: null
  set_pct: null
  klci_pct: null
  msci_em_pct: -0.6
  eido_us_pct: -0.9

calendar:
  earnings_today: ["ITMG"]
  corp_actions_today:
    - {ticker: BBRI, action: ex_div, amount_idr: 209}
  week_macro_prints:
    - {item: "Indonesia CPI Apr", date: "2026-05-02"}
    - {item: "BI RDG", date: "2026-04-23"}
  msci_rebalance_window: false
  esdm_today: null

credit_risk:
  embi_id_oas_bp: 185
  us_hy_oas_bp: 330
  us_ig_oas_bp: 98
  xccy_idr_1y_bp: null

regime: CAUTIOUS                   # CONSTRUCTIVE | CAUTIOUS | DEFENSIVE
regime_triggers_fired:
  - "foreign_net_5d_idr_bn < -2000"
  - "idr_usd weaker 5 consecutive sessions"
regime_reason: "EM outflow persistent, IDR at record low, IHSG below 200dma"
position_size_cap_pct: 15          # derived from regime per MACRO-REGIME.md
```

---

## Regime classification — quantitative triggers

Do not eyeball the regime. It is mechanical. Update `memory/MACRO-REGIME.md` with the full
trigger table; this skill only reads it. Default logic:

- **DEFENSIVE** if any two of: DXY > 106; 5d foreign flow < −IDR 2,000 bn; IHSG < 200dma by >3%;
  VIX > 22; CDS 5Y > 150 bp; IDR weaker 5 consecutive days.
- **CAUTIOUS** if any one of the above is true.
- **CONSTRUCTIVE** otherwise.

`position_size_cap_pct` is derived directly from the regime and MUST be propagated to
`scripts/gate-check.sh` (which currently hardcodes 20% — fix per PM-GRADE-REVIEW.md).

---

## Rules

- One YAML block, fenced. No prose around it beyond the `regime_reason` one-liner.
- **Units audit is mandatory.** Coal in USD/ton (not AUD), CPO in MYR/ton with USD equivalent,
  LME metals in USD/ton. Wrong units = trade errors.
- **Every number stamped.** If the tick is older than 15 minutes pre-open, flag in
  `data_quality.stale_minutes_max` and add to `data_quality.missing` if unusable.
- **No re-append; regenerate.** If the 07:00 routine runs twice on the same day, overwrite
  the day's entry — do not append a second one. `RESEARCH-LOG.md` already has 6 redundant
  entries for 2026-04-19; do not repeat that.
- **Corporate action dates are sacred.** Once an ex-div / earnings date is recorded, it does
  not change in subsequent entries unless the issuer re-announces. Verify against IDX
  Corporate Action disclosures, not news summaries.
- Do not explain what IHSG / BI / Newcastle coal is. The agent that reads this knows.
- If a data point cannot be fetched, write `null` AND list the field in
  `data_quality.missing`. Do not silently omit.
- Downstream consumers: pre-market research (this skill's output is the header),
  `gate-check.sh` (reads `regime` and `position_size_cap_pct`), dashboard `macro` panel
  (reads the whole block), trade-plan skill (reads `regime` for sizing).
- Keep the YAML under 100 lines. If it grows, prune — don't reformat into prose.

---

## Sources (tiered)

1. `scripts/market-data.sh quote <SYM>` — real-time IDX quotes (GoAPI → yfinance fallback).
2. `scripts/yfinance_helper.py` — US indices, commodity ETFs (KOL, JJN, PALL), LME proxies.
3. WebSearch — Newcastle coal (GlobalCOAL), PBOC actions, foreign flow (IDX daily), EMBI OAS.
4. BI.go.id / IDX.co.id — BI 7DRR, INDOGB yields, corporate actions (authoritative for ID macro).
5. Reuters / Bloomberg headlines — use for qualitative (PBOC action, property tape); always
   cite publish time.

If a tier-1 source is down, fall back and note in `data_quality.sources`. Never silently
substitute a stale value.
