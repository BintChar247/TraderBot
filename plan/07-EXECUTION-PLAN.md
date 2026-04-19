# Step 7 — Execution Plan (Model Selection + Parallelism)

> *Not everything needs Opus. Use the right model for the job — save Opus for thinking, Haiku for doing.*

---

## The Two Questions

1. **Which model for each task?** Opus thinks deeply but costs more and is slower. Haiku is fast and cheap but shallow. Sonnet is the middle ground.
2. **What can run in parallel?** Some steps depend on each other. Some don't.

---

## Daily Routines — Model Assignment

These are the 5 scheduled routines that run every trading day. They run **sequentially** — each one writes files the next one reads.

| # | Routine | Model | Why This Model | Can Parallelize? |
|---|---------|-------|----------------|-----------------|
| 1 | **Pre-market research** (7:00 WIB) | **Opus** | Deep research, thesis writing, catalyst analysis. This is where the edge lives. Opus digests more data and writes better theses. | No — must run first |
| 2 | **Market-open execution** (9:15 WIB) | **Sonnet** | Reads pre-market research, validates 9-gate checklist, places orders. Rules are explicit and mechanical. Sonnet follows structured rules reliably. | No — depends on #1 |
| 3 | **Midday scan** (11:30 WIB) | **Sonnet** | Checks positions against sell-side rules (-7% cut, stop tightening). Mechanical evaluation with clear thresholds. | No — depends on #2 |
| 4 | **Daily summary** (15:15 WIB) | **Haiku** | Pulls account state, computes P&L, formats a snapshot. Pure data aggregation — no reasoning needed. | No — depends on #3 |
| 5 | **Weekly review** (Fri 16:00 WIB) | **Opus** | Pattern recognition across the full week. Extracts lessons, grades performance, decides if strategy needs updating. This is the learning engine — Opus writes better reflections. | No — depends on #4 |

### Why Not Opus for Everything?

The plan doc says "all run on claude-opus" — but that's conservative. Here's the tradeoff:

| Model | Cost (input/output per 1M tokens) | Speed | Best For |
|-------|-----------------------------------|-------|----------|
| **Haiku** | $0.25 / $1.25 | Fastest | Data formatting, snapshots, notifications |
| **Sonnet** | $3 / $15 | Medium | Rule-following, structured execution, checklists |
| **Opus** | $15 / $75 | Slowest | Research, thesis writing, reflection, strategy updates |

Running all 5 on Opus: ~$0.50-1.00/day (estimated with typical context sizes).
Running mixed: ~$0.10-0.20/day. Over a month that's meaningful.

**Start with Opus everywhere for the first 2 weeks** (paper trading). Once you see which routines produce identical output on Sonnet, downgrade those. The daily summary is the safest to move to Haiku immediately.

---

## Daily Routine Dependency Chain

```
07:00  Pre-market (Opus)
         │
         │ writes RESEARCH-LOG.md
         ▼
09:15  Market-open (Sonnet)
         │
         │ writes TRADE-LOG.md
         ▼
11:30  Midday scan (Sonnet)
         │
         │ writes TRADE-LOG.md
         ▼
15:15  Daily summary (Haiku)
         │
         │ writes TRADE-LOG.md (EOD snapshot)
         ▼
16:00  Weekly review (Opus, Friday only)
         │
         │ writes WEEKLY-REVIEW.md
         │ optionally writes TRADING-STRATEGY.md
         ▼
       done
```

**Cannot run in parallel.** Each routine reads what the previous one wrote. The 30-minute gaps between routines also prevent git conflicts.

---

## Ad-Hoc Commands — Model Assignment

These are manual commands you invoke from Claude Code. No scheduling, no dependencies.

| Command | Model | Why | Parallel? |
|---------|-------|-----|-----------|
| `/portfolio` | **Haiku** | Read-only snapshot. Calls broker API, prints a table. Zero reasoning needed. | Yes — independent |
| `/trade SYMBOL SHARES SIDE` | **Sonnet** | Validates buy-side gate, places order + trailing stop. Structured rule-following. | Yes — independent |
| `/pre-market` | **Opus** | Same as scheduled pre-market. Deep research. | Yes — independent |
| `/market-open` | **Sonnet** | Same as scheduled market-open. Rule execution. | No — needs pre-market first |
| `/midday` | **Sonnet** | Same as scheduled midday. Position checks. | No — needs market-open first |
| `/daily-summary` | **Haiku** | Same as scheduled daily summary. Data formatting. | No — needs midday first |
| `/weekly-review` | **Opus** | Same as scheduled weekly review. Reflection. | No — needs full week of data |

---

## Implementation Steps — Build Order + Parallelism

These are the steps to BUILD the system. Some can run in parallel.

| Phase | Step | What to Build | Model for Building | Parallel? | Depends On |
|-------|------|---------------|--------------------|-----------|------------|
| **A** | 1 | `memory/TRADING-STRATEGY.md` — seed the rulebook | Manual (Michael reviews) | -- | Nothing |
| **A** | 2 | `memory/PROJECT-CONTEXT.md` — seed static context | Manual | -- | Nothing |
| **A** | 3 | `CLAUDE.md` — agent identity + hard rules | Sonnet | Parallel with A1, A2 | Nothing |
| **A** | 4 | `.gitignore` + `env.template` | Sonnet | Parallel with A1-A3 | Nothing |
| **B** | 5 | `scripts/broker.sh` — IDX broker API wrapper | **Opus** | Parallel with B6 | Phase A done |
| **B** | 6 | `scripts/notify.sh` — notification wrapper | Sonnet | Parallel with B5 | Phase A done |
| **C** | 7 | `memory/TRADE-LOG.md` — seed Day 0 snapshot | Sonnet | Parallel with C8-C9 | B5 (broker.sh) |
| **C** | 8 | `memory/RESEARCH-LOG.md` — seed empty | Sonnet | Parallel with C7, C9 | Phase A done |
| **C** | 9 | `memory/WEEKLY-REVIEW.md` — seed empty | Sonnet | Parallel with C7, C8 | Phase A done |
| **D** | 10 | `routines/pre-market.md` | **Opus** | Parallel with D11-D14 | Phase C done |
| **D** | 11 | `routines/market-open.md` | Sonnet | Parallel with D10, D12-D14 | Phase C done |
| **D** | 12 | `routines/midday.md` | Sonnet | Parallel with D10-D11, D13-D14 | Phase C done |
| **D** | 13 | `routines/daily-summary.md` | Sonnet | Parallel with D10-D12, D14 | Phase C done |
| **D** | 14 | `routines/weekly-review.md` | **Opus** | Parallel with D10-D13 | Phase C done |
| **D** | 15 | `.claude/commands/` — all 7 slash commands | Sonnet | Parallel with D10-D14 | Phase C done |
| **E** | 16 | Cloud routine setup (cron + env vars) | Manual | Sequential | Phase D done |
| **E** | 17 | Smoke test: `/portfolio` works | Manual | Sequential | E16 |
| **E** | 18 | Smoke test: "Run now" on pre-market | Manual | Sequential | E17 |

### Build Phases Visualized

```
Phase A ─── Foundations (parallel: CLAUDE.md, strategy, context, env)
  │
Phase B ─── Wrapper Scripts (parallel: broker.sh + notify.sh)
  │
Phase C ─── Seed Memory Files (parallel: all 3 seedable files)
  │
Phase D ─── Prompts (parallel: all 5 routines + 7 commands)
  │
Phase E ─── Deploy + Test (sequential)
```

**Phases A-D can each be done in one session.** The whole build can be completed in 4-5 sessions if you parallelize within each phase.

---

## Cost Estimate (Daily Operations)

| Routine | Model | Est. Input Tokens | Est. Output Tokens | Est. Cost |
|---------|-------|-------------------|--------------------|----|
| Pre-market | Opus | ~8,000 | ~3,000 | ~$0.35 |
| Market-open | Sonnet | ~6,000 | ~1,500 | ~$0.04 |
| Midday | Sonnet | ~5,000 | ~1,000 | ~$0.03 |
| Daily summary | Haiku | ~4,000 | ~1,000 | ~$0.002 |
| Weekly review (1x/wk) | Opus | ~12,000 | ~4,000 | ~$0.48 |
| **Daily total** | | | | **~$0.42** |
| **Weekly total** | | | | **~$2.58** |
| **Monthly total** | | | | **~$10.30** |

These are rough estimates. Actual costs depend on context length (memory files grow over time) and how much research the agent does (web search results add tokens).

**All-Opus comparison:** ~$1.00/day, ~$22/month. The mixed approach saves roughly 50%.

---

## Decision Matrix: When to Upgrade/Downgrade

| Signal | Action |
|--------|--------|
| Market-open routine misses a gate check that Opus would have caught | Upgrade market-open to Opus |
| Daily summary is identical quality on Haiku vs Sonnet | Keep on Haiku (confirmed) |
| Pre-market research is shallow, missing catalysts | Already on Opus — check prompts, not model |
| Weekly review lessons are generic ("be more careful") | Already on Opus — improve the WEEKLY-REVIEW.md template |
| Midday scan misses a thesis-break exit | Upgrade midday to Opus |
| Costs are fine, want maximum quality | Run everything on Opus |

---

## Plan Files as Parallel Agents (Build Phase)

The 7 plan steps have dependencies — but many can run as **parallel agents** within each phase. Think of it like a DAG (directed acyclic graph):

```
PHASE 1 ──── Run in parallel (no dependencies)
  ├── Agent A (Opus)   → 00-STRATEGY    → produces memory/TRADING-STRATEGY.md
  ├── Agent B (Sonnet) → 01-MEMORY-ARCH → produces memory/ seed files + templates
  └── Agent C (Sonnet) → 02-SCAFFOLD    → produces CLAUDE.md, env.template, .gitignore, repo structure

PHASE 2 ──── Run in parallel (depends on Phase 1)
  ├── Agent D (Sonnet) → 03-GUARDRAILS  → produces broker.sh gate checks
  └── Agent E (Opus)   → 04-SKILLS      → produces research prompts, yfinance integration

PHASE 3 ──── Run in parallel (depends on Phase 2)
  ├── Agent F (Opus)   → 05-ROUTINES    → produces 5 routine prompts + 7 slash commands
  └── Agent G (Sonnet) → 06-PERFORMANCE → produces EOD snapshot format, weekly review template

PHASE 4 ──── Sequential (depends on Phase 3)
  └── Agent H (Haiku)  → 07-EXECUTION   → deploy cloud routines, smoke tests
```

### What Can and Cannot Run in Parallel

| Agent Pair | Parallel? | Why |
|------------|-----------|-----|
| 00-STRATEGY + 02-SCAFFOLD | **Yes** | Strategy defines WHAT to trade. Scaffold defines WHERE files live. Independent. |
| 00-STRATEGY + 01-MEMORY | **Yes** | Strategy defines rules. Memory arch defines file structure. Independent. |
| 01-MEMORY + 02-SCAFFOLD | **Yes** | Memory defines the 5 files. Scaffold defines the repo tree. Overlap is minimal — merge at the end. |
| 03-GUARDRAILS + 04-SKILLS | **Yes** | Guardrails define limits. Skills define capabilities. Both read from strategy but don't write to each other. |
| 05-ROUTINES + 06-PERFORMANCE | **Yes** | Routines define workflows. Performance defines metrics format. Both read from earlier phases. |
| 05-ROUTINES + 03-GUARDRAILS | **No** | Routine prompts embed the buy-side gate from guardrails. Must read 03 first. |
| 05-ROUTINES + 04-SKILLS | **No** | Routine prompts use research skills. Must read 04 first. |

### Model Assignment per Agent

| Phase | Agent | Plan File | Model | Tokens (est.) | Why This Model |
|-------|-------|-----------|-------|---------------|----------------|
| 1 | A | 00-STRATEGY | **Opus** | ~8K out | Core trading thesis — needs financial depth |
| 1 | B | 01-MEMORY | **Sonnet** | ~4K out | Structured templates with clear rules |
| 1 | C | 02-SCAFFOLD | **Sonnet** | ~5K out | Boilerplate: CLAUDE.md, repo tree, env |
| 2 | D | 03-GUARDRAILS | **Sonnet** | ~3K out | Mechanical rule encoding in broker.sh |
| 2 | E | 04-SKILLS | **Opus** | ~6K out | Research prompt quality = trade quality |
| 3 | F | 05-ROUTINES | **Opus** | ~10K out | The 5 routine prompts ARE the agent |
| 3 | G | 06-PERFORMANCE | **Sonnet** | ~3K out | Structured metrics schema |
| 4 | H | 07-EXECUTION | **Haiku** | ~2K out | Deploy steps, cron config, smoke tests |

### Execution Timeline (Estimated)

| Phase | Agents | Parallel? | Time (est.) | Cumulative |
|-------|--------|-----------|-------------|------------|
| 1 | A + B + C | 3 in parallel | ~5 min | 5 min |
| 2 | D + E | 2 in parallel | ~4 min | 9 min |
| 3 | F + G | 2 in parallel | ~6 min | 15 min |
| 4 | H | 1 sequential | ~3 min | 18 min |
| **Total** | **8 agents** | | | **~18 min** |

Sequential would take ~35 min. **Parallel cuts build time roughly in half.**

---

## Summary

| Question | Answer |
|----------|--------|
| Can the 5 daily routines run in parallel? | **No.** Sequential — each reads what the previous wrote. |
| Can the 7 plan steps run as parallel agents? | **Yes, in 4 phases.** 3 + 2 + 2 + 1 agents. |
| Can the build steps run in parallel? | **Yes, within phases.** 4 phases, each internally parallelizable. |
| Should everything run on Opus? | **Start that way for 2 weeks**, then downgrade where safe. |
| Recommended steady-state | Opus for research + weekly review, Sonnet for execution + midday, Haiku for daily summary |
| Build time (parallel) | ~18 min across 4 phases |
| Build time (sequential) | ~35 min |
| Estimated monthly cost (mixed) | ~$10/month |
| Estimated monthly cost (all Opus) | ~$22/month |
