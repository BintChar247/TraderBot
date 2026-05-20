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

### 2026-05-20 — ADRO [HARD-CUT]

- **Entry:** IDR 2,550 on 2026-05-08 (392,100 shares; ~10.03% of equity at entry)
- **Exit:** IDR 2,350 on 2026-05-20 (−7.84% loss; realized −IDR 78,420,000)
- **Original thesis:** "Q1 2026 net +67.07% YoY (US$128.14M); rev +23.4%; coal-sector synchronized rerate (ADRO/PTBA/BUMI/HRUM uniformly positive); Newcastle thermal coal $130+ at floor; analyst PT 2,900 (+13.7%); Q1 cycle-synchronized coal rerate per PATTERNS.md."
- **What actually happened:** Bottom-up thesis remained fundamentally intact through May 19 (Newcastle $134.50–135.80 / Brent $111.22 / Q1 EPS catalyst already delivered). But two top-down catalysts overwhelmed: (1) Bloomberg-confirmed (May 19) Indonesian government plan for new Danantara-supervised state agency to control coal/palm-oil/metals exports via single gateway — structural sector-policy risk, expected to be announced "as soon as Wednesday May 20" by President Prabowo. (2) BI RDG decision May 20 (calendar tightened from May 21–22) with consensus +25bp hike to 5.00% as IDR pierced 17,752 record. The May 19 capitulation (IHSG −3.46% to 6,370.68, 1-yr low) drove ADRO into the 2,350–2,440 band (close 2,400 mid; range 2,210). May 20 open gap-down hit 2,350 — below hard-cut 2,371 — immediate sell at market per auto-cut trigger (a) armed in RESEARCH-LOG. Intraday extended to 2,210 low post-fill.
- **Root cause:** MACRO-MISS overlaid on SECTOR-IGNORED. The bottom-up coal-cycle thesis was correct (commodity floor intact, Q1 catalyst delivered). But two macro/regulatory factors were under-weighted at entry (May 8) and not fully de-risked as they intensified into Week 5: (a) the EM-OUTFLOW regime kept intensifying (IDR record breach May 19; DEFENSIVE-confirmed 4/4 triggers); (b) the single-gateway export-policy risk was a fat-tail structural risk not in the original thesis but knowable as the Danantara-Bloomberg rumor began circulating May 18–19. The position was held through Day 11 with only IDR 169 buffer (2,550 entry vs 2,371 hard-cut, ~7% loss-budget) — a tight stop with macro-fragile thesis = high probability of forced cut on any 1-day shock event, which is exactly what happened. Not RESEARCH-ERROR (thesis was fundamentally validated; Q1 print +67% as expected). Not POSITION-SIZING per se (10.03% of equity at entry, within regime cap). Closer to STOP-TOO-NARROW relative to held duration in a high-volatility regime — but the hard-cut rule itself is non-negotiable (−7%) so this becomes a SIZING-in-VOL-REGIME critique: in EM-OUTFLOW regimes, the 10% sizing cap should likely be even tighter (5–7%) because tighter loss-budget × wider intraday IDR-driven moves = stop fires on noise not just thesis-break.
- **What to do differently:**
  1. **Regime-conditional sizing rule (new):** In confirmed EM-OUTFLOW or DEFENSIVE regimes, cap NEW entries at 7% of equity (not 10–15%). The wider intraday volatility from IDR shocks + foreign flow swings + sector-policy headlines pushes more positions through the −7% hard-cut on noise alone. Tighter sizing preserves trial alpha.
  2. **Pre-mortem trigger codification (new):** Any time a credible Bloomberg/Reuters source reports "could be announced as soon as [date]" for a regulatory action that affects a held sector, immediately treat as a hard-armed trigger — close on (a) official announcement OR (b) any intraday breach of −5% from entry (whichever comes first), regardless of hard-cut buffer. Don't wait for the −7% hard-cut to fire; pre-emptively de-risk on regulatory-rumor escalation.
  3. **Multi-source procedure HELD:** Today's verification (Investing.com fresh open 2,350 vs cached TradingView 2,550) correctly applied the safe-assumption rule. The procedure worked. Continue.
  4. **Broker.sh sell override (deferred infrastructure fix, repeat from MISTAKES.md 2026-05-01):** Three hard-cuts have now corrupted broker.sh fill prices (BBRI, ITMG, ADRO — all stale entry-price fills, all manually corrected in PAPER-STATE.json). The MD_LAST_PRICE_OVERRIDE path exists for cmd_buy (gate-check.sh) but NOT for cmd_sell. The fix is overdue. Codify: weekend infra patch — add MD_LAST_PRICE_OVERRIDE support to broker.sh cmd_sell (mirror cmd_buy at line 351).
- **Pattern:** Y — third hard-cut of trial. Pattern emerging: (BBRI 2026-05-01) banking macro shock + Fed/IDR + foreign flow capitulation; (ITMG 2026-05-18) commodity-specific Q1 miss; (ADRO 2026-05-20) sector-policy structural risk + IDR/BI RDG capitulation. Common thread = position survived bottom-up validation but caught in macro/regulatory shock during EM-OUTFLOW intensification. All three exits occurred during Week 5 (post-trial extension) of intensified EM-OUTFLOW → DEFENSIVE regime transition. Lesson reinforced: in high-volatility EM-stress regimes, tighter sizing (≤7%) + pre-emptive de-risking on regulatory-rumor escalation are higher-priority than thesis quality.

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
