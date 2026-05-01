# Mistakes Log

_Permanent record of every hard cut (-7%), thesis failure, and rule violation. Append-only. Never delete._
_Purpose: prevent repeating the same mistakes. Read every session._

---

## Template — Mistake Entry

```
### YYYY-MM-DD — [TICKER] [TYPE: HARD-CUT | THESIS-BREAK | RULE-VIOLATION]

- **Entry:** IDR [price] on [date]
- **Exit:** IDR [price] on [date] ([+/-X]% loss)
- **Original thesis:** [what we believed]
- **What actually happened:** [the reality]
- **Root cause:** [RESEARCH-ERROR | MACRO-MISS | POSITION-SIZING | STOP-TOO-WIDE | CHASED-ENTRY | SECTOR-IGNORED | OTHER]
- **What to do differently:** [one specific rule or check to add]
- **Pattern:** [does this connect to a prior mistake? Y/N — if Y, reference it]
```

---

### 2026-05-01 — BBRI [HARD-CUT]

- **Entry:** IDR 3,260 on 2026-04-23 (220,000 shares; 7.17% of equity)
- **Exit:** IDR 2,990 on 2026-05-01 (−8.28% loss; realized −IDR 59,400,000)
- **Original thesis:** "Q1 2026 earnings (Apr 29 after-hours) is the binary catalyst; consensus next-Q est ~99.94 IDR EPS / 50.24T IDR revenue; Q4 2025 beat +6.9% EPS / +13.5% revenue; institutional accumulation (BlackRock/JPMorgan); 10.1% dividend yield; Fitch outlook cut already absorbed."
- **What actually happened:** Q1 print Apr 29 was in-line (flat post-earnings = no negative surprise, but no positive rerate either). On Apr 30 the broader macro shock dominated — Fed held hawkish, IDR pierced 17,390 (record), foreign net sell on banks $130M+ (BBRI specifically IDR 69.64bn net sell), IHSG closed −3.00% (sharpest single-day drop of trial). Banking sector got hit disproportionately; BBRI intraday low 2,990 hit at 11:08 WIB Apr 30 — hard-cut 3,031 GTC stop SHOULD have fired then but the Apr 30 11:30 midday scan read a stale BBRI 3,070 print from a delayed source (yfinance has been blocked since Apr 21; WebSearch returned a lagged number). The procedural breach: midday did not detect the live 2,990 print, so the hard-cut order did not execute intraday. Position closed Apr 30 at 2,990 mark; sell executed today (May 1) via paper-broker (broker.sh sell BBRI 220000) — paper fill landed at stale entry-price 3,260, so PAPER-STATE.json was manually corrected to record exit at 2,990 (Apr 30 verified close mark) for trial-P&L integrity.
- **Root cause:** OTHER (price-source lag during midday scan when buffer to hard-cut was <1%; combined with macro tail risk that dominated post-earnings drift). Not RESEARCH-ERROR — the binary thesis (Q1 earnings) resolved in-line as expected. Not POSITION-SIZING — entry was 7.17% of equity (well within 20% cap). Not STOP-TOO-WIDE — −7% hard cut is the standard rule. The proximate cause is procedural: midday's price source was stale, so the rule fired late.
- **What to do differently:** When a position's buffer to hard-cut is <1.5%, midday MUST cross-verify the price via at least 2 independent sources (Yahoo Finance + Investing.com + sectors.app) AND check intraday-low not just last-print. If discrepancy >0.5%, fire the lower price as the safe assumption. Add a wrapper around broker.sh quote in the midday routine that requires multi-source agreement when buffer<1.5%. Also: when yfinance/market-data.sh is blocked, the broker.sh sell fill price is unreliable — it returns the stale entry, which silently corrupts realized P&L. Need a manual price-override path for sells during data-source outages (e.g., MD_LAST_PRICE_OVERRIDE for cmd_sell, mirroring cmd_buy).
- **Pattern:** N — first hard cut of the trial. Connects to broader infra issue: market-data.sh yfinance has been unavailable for the entire trial since Apr 21, forcing WebSearch fallback for all marks. The lag in WebSearch versus live tape was the failure mode.

---

_(End of entries)_
