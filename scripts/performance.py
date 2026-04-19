#!/usr/bin/env python3.11
# deps: pandas pyyaml yfinance

"""
performance.py — Performance measurement scaffolding for the IDX trading agent.

Subcommands:
  snapshot       Compute today's portfolio snapshot vs last EOD in TRADE-LOG.md.
  weekly         Compute weekly stats from TRADE-LOG.md EOD entries.
  benchmark      Fetch IHSG (^JKSE) cumulative % change over N days via yfinance.
  lessons-diff   Compare last two weekly reviews and flag repeated lessons.

Output: JSON by default; --markdown for markdown blocks ready to append to memory/.
"""

import argparse
import json
import re
import subprocess
import sys
from datetime import datetime, timedelta, timezone
from pathlib import Path
from typing import Optional

# ---------------------------------------------------------------------------
# Optional-dependency imports — fail loudly at runtime, not at import.
# ---------------------------------------------------------------------------
try:
    import pandas as pd
except ImportError:
    pd = None

try:
    import yfinance as yf
except ImportError:
    yf = None

# ---------------------------------------------------------------------------
# Paths
# ---------------------------------------------------------------------------
REPO_ROOT = Path(__file__).resolve().parent.parent
TRADE_LOG = REPO_ROOT / "memory" / "TRADE-LOG.md"
WEEKLY_REVIEW = REPO_ROOT / "memory" / "WEEKLY-REVIEW.md"
BROKER_SCRIPT = REPO_ROOT / "scripts" / "broker.sh"

# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def _require(lib_name: str, var):
    """Raise with a clean message if an optional dep was not imported."""
    if var is None:
        print(
            f"ERROR: '{lib_name}' is required for this subcommand but is not installed.\n"
            f"Install it with: pip install {lib_name}",
            file=sys.stderr,
        )
        sys.exit(1)


def _wib_now() -> str:
    """Return current time as a WIB date string YYYY-MM-DD."""
    wib = timezone(timedelta(hours=7))
    return datetime.now(wib).strftime("%Y-%m-%d")


def _parse_eod_snapshots(text: str) -> list[dict]:
    """
    Parse EOD snapshot blocks from TRADE-LOG.md content.
    Returns a list of dicts ordered oldest-first.
    Each dict contains at minimum: date, equity, cash, positions[].
    """
    snapshots = []

    # Match section headers like:  ### YYYY-MM-DD EOD  or  ## YYYY-MM-DD EOD
    header_re = re.compile(
        r"^#{2,3}\s+(\d{4}-\d{2}-\d{2})\s+EOD\b", re.MULTILINE
    )

    headers = list(header_re.finditer(text))
    for i, m in enumerate(headers):
        date_str = m.group(1)
        # Extract the block between this header and the next (or end of file)
        start = m.start()
        end = headers[i + 1].start() if i + 1 < len(headers) else len(text)
        block = text[start:end]

        snap = {"date": date_str, "equity": None, "cash": None, "positions": []}

        # Total equity — look for "Total equity: IDR X" or "Equity: IDR X"
        eq_re = re.search(
            r"(?:Total equity|Equity)\s*:\s*IDR\s+([\d,]+)", block, re.IGNORECASE
        )
        if eq_re:
            snap["equity"] = int(eq_re.group(1).replace(",", ""))

        # Cash — "Cash: IDR X"
        cash_re = re.search(
            r"Cash\s*:\s*IDR\s+([\d,]+)", block, re.IGNORECASE
        )
        if cash_re:
            snap["cash"] = int(cash_re.group(1).replace(",", ""))

        # Positions table rows: | TICKER | shares | avg_cost | last | unreal_pnl | days |
        pos_re = re.compile(
            r"^\|\s*([A-Z]{2,6})\s*\|\s*([\d,]+)\s*\|\s*([\d,]+)\s*\|\s*([\d,]+)\s*\|"
            r"\s*IDR\s*([-\d,]+)\s*\(",
            re.MULTILINE,
        )
        for pm in pos_re.finditer(block):
            snap["positions"].append(
                {
                    "ticker": pm.group(1),
                    "shares": int(pm.group(2).replace(",", "")),
                    "avg_cost": int(pm.group(3).replace(",", "")),
                    "last": int(pm.group(4).replace(",", "")),
                    "unrealized_idr": int(pm.group(5).replace(",", "")),
                }
            )

        snapshots.append(snap)

    return sorted(snapshots, key=lambda s: s["date"])


def _parse_trade_entries(text: str) -> list[dict]:
    """
    Parse individual trade entries (BUY/SELL) from TRADE-LOG.md content.
    Returns a list of dicts: date, ticker, side, entry_price, exit_price, pnl_pct.
    """
    trades = []

    # Match trade-entry headers:  ### YYYY-MM-DD HH:MM WIB — BUY TICKER
    trade_re = re.compile(
        r"^#{2,3}\s+(\d{4}-\d{2}-\d{2})[^—\n]*—\s*(BUY|SELL)\s+([A-Z]{2,6})",
        re.MULTILINE,
    )
    for m in trade_re.finditer(text):
        trades.append(
            {
                "date": m.group(1),
                "side": m.group(2),
                "ticker": m.group(3),
                "entry_price": None,
                "exit_price": None,
                "pnl_pct": None,
            }
        )

    return trades


def _iso_week_bounds(date_str: Optional[str] = None) -> tuple[str, str]:
    """Return (monday, sunday) ISO date strings for the ISO week containing date_str."""
    wib = timezone(timedelta(hours=7))
    if date_str:
        d = datetime.strptime(date_str, "%Y-%m-%d").replace(tzinfo=wib)
    else:
        d = datetime.now(wib)
    monday = d - timedelta(days=d.weekday())
    sunday = monday + timedelta(days=6)
    return monday.strftime("%Y-%m-%d"), sunday.strftime("%Y-%m-%d")


# ---------------------------------------------------------------------------
# Subcommand: snapshot
# ---------------------------------------------------------------------------

def cmd_snapshot(args):
    """Read broker portfolio JSON and compute unrealized P&L vs last EOD snapshot."""

    # --- Fetch live portfolio from broker.sh ---
    try:
        result = subprocess.run(
            [str(BROKER_SCRIPT), "portfolio"],
            capture_output=True,
            text=True,
            timeout=30,
        )
        if result.returncode != 0:
            print(
                f"ERROR: broker.sh portfolio exited {result.returncode}:\n{result.stderr}",
                file=sys.stderr,
            )
            sys.exit(1)
        portfolio = json.loads(result.stdout)
    except FileNotFoundError:
        print(
            f"ERROR: broker.sh not found at {BROKER_SCRIPT}", file=sys.stderr
        )
        sys.exit(1)
    except json.JSONDecodeError as exc:
        print(f"ERROR: broker.sh output is not valid JSON: {exc}", file=sys.stderr)
        sys.exit(1)

    # --- Parse last EOD snapshot from TRADE-LOG.md ---
    trade_log_text = TRADE_LOG.read_text(encoding="utf-8")
    snapshots = _parse_eod_snapshots(trade_log_text)
    last_snap = snapshots[-1] if snapshots else None
    prev_equity = last_snap["equity"] if last_snap else None
    today = _wib_now()

    # --- Build result ---
    total_equity = portfolio.get("equity", 0)
    cash = portfolio.get("cash", 0)
    positions = portfolio.get("positions", [])

    pos_rows = []
    for pos in positions:
        ticker = pos.get("ticker", "?")
        shares = pos.get("shares", 0)
        avg_cost = pos.get("avg_cost", 0)
        last_price = pos.get("last_price", avg_cost)
        unrealized_idr = (last_price - avg_cost) * shares
        unrealized_pct = (
            (last_price - avg_cost) / avg_cost * 100 if avg_cost else 0.0
        )
        pos_rows.append(
            {
                "ticker": ticker,
                "shares": shares,
                "avg_cost": avg_cost,
                "last_price": last_price,
                "unrealized_idr": int(unrealized_idr),
                "unrealized_pct": round(unrealized_pct, 2),
            }
        )

    day_delta_idr = (
        total_equity - prev_equity if prev_equity is not None else None
    )
    day_delta_pct = (
        round(day_delta_idr / prev_equity * 100, 2)
        if prev_equity
        else None
    )

    data = {
        "date": today,
        "total_equity": total_equity,
        "cash": cash,
        "cash_pct": round(cash / total_equity * 100, 1) if total_equity else 0,
        "day_delta_idr": day_delta_idr,
        "day_delta_pct": day_delta_pct,
        "prev_equity": prev_equity,
        "prev_equity_date": last_snap["date"] if last_snap else None,
        "positions": pos_rows,
    }

    if args.markdown:
        _print_snapshot_markdown(data)
    else:
        print(json.dumps(data, indent=2))


def _fmt_idr(v) -> str:
    if v is None:
        return "N/A"
    sign = "+" if v > 0 else ""
    return f"IDR {v:,}"


def _fmt_pct(v) -> str:
    if v is None:
        return "N/A"
    sign = "+" if v > 0 else ""
    return f"{sign}{v:.2f}%"


def _print_snapshot_markdown(data: dict):
    today = data["date"]
    equity = data["total_equity"]
    cash = data["cash"]
    cash_pct = data["cash_pct"]
    delta_idr = data["day_delta_idr"]
    delta_pct = data["day_delta_pct"]
    prev_date = data["prev_equity_date"] or "N/A"

    lines = [
        f"### {today} EOD",
        "",
        "- Total equity: " + _fmt_idr(equity),
        "- Daily P&L: "
        + _fmt_idr(delta_idr)
        + " ("
        + _fmt_pct(delta_pct)
        + f") vs {prev_date}",
        "- Cash: " + _fmt_idr(cash) + f" ({cash_pct}% of equity)",
        "",
        "#### Open Positions",
        "",
        "| Ticker | Shares | Avg Cost (IDR) | Last (IDR) | Unrealized P&L | Days Held |",
        "|--------|--------|----------------|------------|----------------|-----------|",
    ]

    if data["positions"]:
        for p in data["positions"]:
            lines.append(
                f"| {p['ticker']} | {p['shares']:,} | {p['avg_cost']:,} "
                f"| {p['last_price']:,} "
                f"| {_fmt_idr(p['unrealized_idr'])} ({_fmt_pct(p['unrealized_pct'])}) "
                f"| — |"
            )
    else:
        lines.append("| — | — | — | — | — | — |")

    lines += ["", "#### Notes", "", "_Generated by scripts/performance.py snapshot_", ""]
    print("\n".join(lines))


# ---------------------------------------------------------------------------
# Subcommand: weekly
# ---------------------------------------------------------------------------

def cmd_weekly(args):
    _require("pandas", pd)

    trade_log_text = TRADE_LOG.read_text(encoding="utf-8")
    snapshots = _parse_eod_snapshots(trade_log_text)

    monday, sunday = _iso_week_bounds()
    week_snaps = [s for s in snapshots if monday <= s["date"] <= sunday]
    trades = _parse_trade_entries(trade_log_text)
    week_trades = [t for t in trades if monday <= t["date"] <= sunday]

    # Equity bounds
    # Look for the last snapshot before the week as starting equity
    prior = [s for s in snapshots if s["date"] < monday]
    start_equity = prior[-1]["equity"] if prior else None
    end_snap = week_snaps[-1] if week_snaps else None
    end_equity = end_snap["equity"] if end_snap else start_equity

    week_return_idr = (
        (end_equity - start_equity) if (start_equity and end_equity) else None
    )
    week_return_pct = (
        round(week_return_idr / start_equity * 100, 2)
        if start_equity
        else None
    )

    ihsg_pct = getattr(args, "ihsg", None)
    alpha = (
        round(week_return_pct - ihsg_pct, 2)
        if (week_return_pct is not None and ihsg_pct is not None)
        else None
    )

    # Trade stats
    wins = [t for t in week_trades if t.get("side") == "SELL" and (t.get("pnl_pct") or 0) > 0]
    losses = [t for t in week_trades if t.get("side") == "SELL" and (t.get("pnl_pct") or 0) <= 0]
    trade_count = len(week_trades)
    win_rate = round(len(wins) / len(week_trades) * 100, 1) if week_trades else None

    # Sector exposure from last snapshot of the week
    sector_exposure: dict[str, int] = {}
    if end_snap and end_equity:
        for pos in end_snap.get("positions", []):
            sector = pos.get("sector", "Unknown")
            cost = pos.get("shares", 0) * pos.get("avg_cost", 0)
            sector_exposure[sector] = sector_exposure.get(sector, 0) + cost

    data = {
        "week_start": monday,
        "week_end": sunday,
        "start_equity": start_equity,
        "end_equity": end_equity,
        "week_return_idr": week_return_idr,
        "week_return_pct": week_return_pct,
        "ihsg_pct": ihsg_pct,
        "alpha": alpha,
        "trade_count": trade_count,
        "wins": len(wins),
        "losses": len(losses),
        "win_rate": win_rate,
        "best_trade": None,
        "worst_trade": None,
        "sector_exposure": sector_exposure,
    }

    if args.markdown:
        _print_weekly_markdown(data)
    else:
        print(json.dumps(data, indent=2))


def _print_weekly_markdown(data: dict):
    we = data["week_end"]
    start_eq = data["start_equity"]
    end_eq = data["end_equity"]
    ret_idr = data["week_return_idr"]
    ret_pct = data["week_return_pct"]
    ihsg = data["ihsg_pct"]
    alpha = data["alpha"]
    tc = data["trade_count"]
    wins = data["wins"]
    losses = data["losses"]
    wr = data["win_rate"]
    best = data["best_trade"] or "N/A"
    worst = data["worst_trade"] or "N/A"

    ihsg_str = f"{ihsg:+.2f}%" if ihsg is not None else "N/A"
    alpha_str = f"{alpha:+.2f}pp" if alpha is not None else "N/A"

    lines = [
        f"## Week ending {we}",
        "",
        "_Generated by `python scripts/performance.py weekly --markdown --ihsg <pct>`_",
        "",
        "### Performance Summary",
        "",
        "| Metric | This Week | IHSG Benchmark | Alpha |",
        "|--------|-----------|----------------|-------|",
        f"| P&L (IDR) | {_fmt_idr(ret_idr)} | — | — |",
        f"| P&L (%) | {_fmt_pct(ret_pct)} | {ihsg_str} | {alpha_str} |",
        f"| Equity (EOW) | {_fmt_idr(end_eq)} | — | — |",
        "",
        "### Trade Summary",
        "",
        f"- Trades this week: {tc}/3",
        f"- Wins: {wins} | Losses: {losses} | Win rate: {wr}%" if wr is not None else f"- Wins: {wins} | Losses: {losses} | Win rate: N/A",
        f"- Best trade: {best}",
        f"- Worst trade: {worst}",
        "",
    ]

    if data["sector_exposure"]:
        lines += [
            "### Sector Exposure",
            "",
            "| Sector | IDR Deployed |",
            "|--------|-------------|",
        ]
        for sector, idr in data["sector_exposure"].items():
            lines.append(f"| {sector} | {idr:,} |")
        lines.append("")

    print("\n".join(lines))


# ---------------------------------------------------------------------------
# Subcommand: benchmark
# ---------------------------------------------------------------------------

def cmd_benchmark(args):
    _require("yfinance", yf)
    _require("pandas", pd)

    symbol_map = {"IHSG": "^JKSE"}
    ticker_raw = args.symbol.upper()
    yf_symbol = symbol_map.get(ticker_raw, ticker_raw)
    days = args.days

    wib = timezone(timedelta(hours=7))
    end_dt = datetime.now(wib)
    start_dt = end_dt - timedelta(days=days + 5)  # buffer for non-trading days

    try:
        ticker = yf.Ticker(yf_symbol)
        hist = ticker.history(
            start=start_dt.strftime("%Y-%m-%d"),
            end=end_dt.strftime("%Y-%m-%d"),
        )
    except Exception as exc:
        print(f"ERROR: yfinance fetch failed: {exc}", file=sys.stderr)
        sys.exit(1)

    if hist.empty or len(hist) < 2:
        print(
            f"ERROR: Not enough data for {yf_symbol} over {days} days.",
            file=sys.stderr,
        )
        sys.exit(1)

    # Trim to last `days` trading rows
    hist = hist.tail(days)
    start_close = float(hist["Close"].iloc[0])
    end_close = float(hist["Close"].iloc[-1])
    cumulative_pct = round((end_close - start_close) / start_close * 100, 4)

    data = {
        "symbol": ticker_raw,
        "yf_symbol": yf_symbol,
        "days": days,
        "start_date": hist.index[0].strftime("%Y-%m-%d"),
        "end_date": hist.index[-1].strftime("%Y-%m-%d"),
        "start_close": round(start_close, 2),
        "end_close": round(end_close, 2),
        "cumulative_pct": cumulative_pct,
    }

    if args.markdown:
        print(
            f"**{ticker_raw} ({days}d):** {end_close:,.2f} "
            f"({cumulative_pct:+.2f}% from {data['start_date']} to {data['end_date']})"
        )
    else:
        print(json.dumps(data, indent=2))


# ---------------------------------------------------------------------------
# Subcommand: lessons-diff
# ---------------------------------------------------------------------------

def cmd_lessons_diff(args):
    """
    TODO: Compare the last two weekly reviews and flag any lesson that was repeated.

    Implementation plan:
    1. Parse WEEKLY-REVIEW.md for all '### Lessons Learned' sections.
    2. Extract bullet lines from the two most recent entries.
    3. Fuzzy-match or exact-match bullet text across the two weeks.
    4. Emit a markdown list of any lesson that appears (verbatim or near-verbatim) in both.

    This is a stub. Returning a placeholder until a full implementation is warranted.
    """
    lines = [
        "## Lessons Diff — Repeated Lessons",
        "",
        "_TODO: Full implementation pending. Stub returns placeholder._",
        "",
        "- [ ] Parse last two WEEKLY-REVIEW.md entries for 'Lessons Learned' bullets.",
        "- [ ] Fuzzy-match lessons across weeks and flag repeats.",
        "- [ ] Emit: repeated lesson text + dates it appeared.",
        "",
        "_Run after at least two weekly reviews have been appended to memory/WEEKLY-REVIEW.md._",
    ]
    if args.markdown:
        print("\n".join(lines))
    else:
        print(json.dumps({"status": "stub", "message": "lessons-diff not yet implemented"}, indent=2))


# ---------------------------------------------------------------------------
# CLI entry point
# ---------------------------------------------------------------------------

def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        prog="performance.py",
        description="IDX trading agent — performance measurement scaffolding.",
    )
    parser.add_argument(
        "--markdown",
        action="store_true",
        default=False,
        help="Output as markdown block (default: JSON).",
    )

    sub = parser.add_subparsers(dest="command", required=True)

    # snapshot
    sub.add_parser(
        "snapshot",
        help="Compute today's portfolio snapshot vs last EOD in TRADE-LOG.md.",
    )

    # weekly
    wp = sub.add_parser(
        "weekly",
        help="Compute weekly stats from TRADE-LOG.md EOD entries.",
    )
    wp.add_argument(
        "--ihsg",
        type=float,
        default=None,
        metavar="PCT",
        help="IHSG return for the week as a float percentage (e.g. 1.23 for +1.23%%).",
    )

    # benchmark
    bp = sub.add_parser(
        "benchmark",
        help="Fetch cumulative %% change for a symbol over N days via yfinance.",
    )
    bp.add_argument(
        "--symbol",
        default="IHSG",
        help="Index symbol to fetch. Use IHSG for Jakarta Composite (default: IHSG).",
    )
    bp.add_argument(
        "--days",
        type=int,
        default=5,
        help="Number of trading days to look back (default: 5).",
    )

    # lessons-diff
    sub.add_parser(
        "lessons-diff",
        help="Compare last two weekly reviews and flag repeated lessons (stub).",
    )

    return parser


def main():
    parser = build_parser()
    args = parser.parse_args()

    dispatch = {
        "snapshot": cmd_snapshot,
        "weekly": cmd_weekly,
        "benchmark": cmd_benchmark,
        "lessons-diff": cmd_lessons_diff,
    }
    dispatch[args.command](args)


if __name__ == "__main__":
    main()
