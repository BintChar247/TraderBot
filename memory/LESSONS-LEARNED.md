# Lessons Learned

_Append-only scar-tissue file. Each entry records a rule in imperative form and why it exists._
_Source: trade experience and strategy reviews. Never delete entries — repetitions are a signal._

Run `python scripts/performance.py lessons-diff` to detect repeated lessons across weekly reviews.

---

## Entries

### 2026-04-18 — Seeded from strategy doc

**Lesson 1**
- **Rule:** Never average down on a losing position. Cut at -7% and move on.
- **Why:** Averaging down on a loser compounds the loss and delays the decision that was already wrong. The -7% cut exists precisely to prevent a small loss becoming a large one.
- **Trigger:** Seeded from TRADING-STRATEGY.md hard rules, 2026-04-18.

---

**Lesson 2**
- **Rule:** Exit an entire sector after 2 consecutive failed trades in that sector.
- **Why:** Two consecutive failures in the same sector is a signal that the sector thesis is broken or timing is wrong. Staying in burns more capital chasing a trend that isn't there.
- **Trigger:** Seeded from TRADING-STRATEGY.md hard rules, 2026-04-18.

---

**Lesson 3**
- **Rule:** All stops must be real GTC orders placed with the broker, never mental stops.
- **Why:** Mental stops get moved. They get rationalized away at the exact moment the position is hurting and discipline is hardest to maintain. A real order executes without the agent's approval.
- **Trigger:** Seeded from TRADING-STRATEGY.md hard rules, 2026-04-18.

---

## Template — New Entry

```
### YYYY-MM-DD — [Weekly review / Specific trade]

**Lesson N**
- **Rule:** [One imperative sentence.]
- **Why:** [One or two sentences on the root cause or failure mode this prevents.]
- **Trigger:** [Trade(s) or event that made this lesson visible. If seeded, say so.]
```
