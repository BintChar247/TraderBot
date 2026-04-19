# Skill: Macro Readout

Purpose: compact macro snapshot for the top of each pre-market research entry. This is a header, not an essay.

Canonical regime thresholds live in `memory/TRADING-STRATEGY.md` ("Sector Playbook" + hard rules). Read them.

## What to fetch

- Commodities: Newcastle coal ($/ton), CPO palm oil (MYR/ton), LME nickel ($/ton) — day change %.
- FX: IDR/USD spot, day change %.
- Indonesia equity: IHSG close, position vs 200-day MA, foreign net buy/sell (IDR bn).
- Asia overnight: Nikkei 225, HSI, KOSPI close %.
- Rates: latest BI 7-day reverse repo rate, last move direction.
- Risk appetite: VIX close, DXY level.

Sources: `scripts/yfinance_helper.py` for what it can return; web search for coal, BI rate, foreign flows.

## Output format — single YAML block

```yaml
date_wib: 2026-04-18
commodities:
  coal_newcastle_usd: 128.40   # +0.8%
  cpo_myr: 3980                # -0.3%
  nickel_lme_usd: 17200        # +1.2%
fx:
  idr_usd: 16420               # -0.1% (IDR stronger)
equity_id:
  ihsg_close: 7384
  vs_200dma: above             # above | below
  foreign_net_idr_bn: +420     # + is net buy
asia_overnight:
  nikkei_pct: +0.6
  hsi_pct: -0.4
  kospi_pct: +0.2
rates:
  bi_7drr_pct: 6.00
  last_move: hold              # cut | hold | hike
risk:
  vix: 14.2
  dxy: 103.8
regime: CONSTRUCTIVE           # CONSTRUCTIVE | CAUTIOUS | DEFENSIVE
regime_reason: "IHSG above 200dma, foreign net buy 2 days, coal firm"
```

## Rules

- One YAML block, fenced. No prose around it beyond the `regime_reason` one-liner.
- Do **not** explain what IHSG is, what BI does, what nickel means. The agent that reads this knows.
- `regime` classification follows the convention in `plan/04-SKILLS.md` (CONSTRUCTIVE / CAUTIOUS / DEFENSIVE). The downstream trade-plan skill uses it for sizing.
- If a data point can't be fetched, write `null` — do not fabricate.
- Keep it under 25 lines of YAML.
