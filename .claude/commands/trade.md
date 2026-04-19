Manual trade helper. Runs the full buy-side gate. Asks for confirmation before placing.

**Usage:** `/trade SYMBOL SHARES SIDE`

Example: `/trade BBCA 500 buy`

---

STEP 1 — Read memory:
- `memory/TRADING-STRATEGY.md` — all rules
- `memory/TRADE-LOG.md` — open positions, weekly trade count
- `memory/RESEARCH-LOG.md` — today's entry (required for Gate 5)

STEP 2 — Pull live state:
```bash
bash scripts/broker.sh portfolio
bash scripts/broker.sh positions
bash scripts/broker.sh quote [SYMBOL]
```

STEP 3 — Run all 9 buy-side gate checks. Print each result:

```
Buy-Side Gate for [SYMBOL] [SHARES]sh [SIDE]
  Gate 1: positions after fill <= 6 ............ [PASS/FAIL] ([current]/6)
  Gate 2: trades this week <= 3 ............... [PASS/FAIL] ([N]/3)
  Gate 3: position cost <= 20% equity ......... [PASS/FAIL] (IDR [cost] = [X]%)
  Gate 4: position cost <= available cash ...... [PASS/FAIL]
  Gate 5: catalyst in today's research log ..... [PASS/FAIL]
  Gate 6: instrument is a stock ................ [PASS/FAIL]
  Gate 7: avg daily volume > 500K shares ....... [PASS/FAIL] ([volume]K)
  Gate 8: quantity is multiple of 100 .......... [PASS/FAIL] ([SHARES] shares)
  Gate 9: price within 3% of plan ............. [PASS/FAIL] (planned: IDR [X], current: IDR [Y])

Result: [ALL PASS — ready to trade | GATE(S) [N] FAILED — trade blocked]
```

STEP 4 — If any gate fails: stop here. Print which gates failed and why. No order placed.

STEP 5 — If all gates pass: print the order summary and ask for confirmation:

```
Order Ready:
  Symbol: [SYMBOL]
  Side: [SIDE]
  Quantity: [SHARES] shares
  Type: market
  Estimated cost: IDR [amount] ([X]% of equity)

Trailing stop will be placed immediately after fill:
  Trail: 10% GTC
  Stop approx: IDR [price] (10% below current)

Type 'confirm' to place the order, or anything else to cancel.
```

Wait for the user to type 'confirm'.

STEP 6 — On confirm:
```bash
bash scripts/broker.sh buy [SYMBOL] [SHARES]
```

`broker.sh buy` runs `gate-check.sh` automatically (Layer-2 guardrail) then places the market order. Wait for fill confirmation.

STEP 7 — Immediately place trailing stop:
```bash
bash scripts/broker.sh set-stop [SYMBOL] 10
```

STEP 8 — Append to memory/TRADE-LOG.md (Active Positions table + Trade History).

STEP 9 — Send notification:
```bash
bash scripts/notify.sh "TRADE $DATE: BUY [SYMBOL] [SHARES]sh @ IDR [fill price]. Stop: 10% trail. Thesis: [one sentence]."
```

STEP 10 — Commit:
```bash
git add memory/TRADE-LOG.md
git commit -m "manual-trade $DATE: BUY [SYMBOL]"
git push origin main
```
