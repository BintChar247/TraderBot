#!/usr/bin/env python3
# deps: yfinance pandas
"""
yfinance_helper.py — IDX data helper for the trading agent.

Scaffold: wraps yfinance for quotes, fundamentals, price history, liquidity,
and sector screening. IDX symbols are auto-normalized to the `.JK` suffix used
by Yahoo Finance (e.g. `BBCA` -> `BBCA.JK`).

TODO: swap yfinance for a real IDX data feed (e.g. IDX DataShop, KSEI, or a
paid vendor like Bloomberg/Refinitiv) once we validate the pipeline. yfinance
is fine for a paper account but is delayed, occasionally stale, and has gaps
on smaller IDX names.

Subcommands:
  quote SYMBOL                       Last price, day change, volume.
  fundamentals SYMBOL                P/E, P/B, div yield, market cap, etc.
  price-history SYMBOL --days N      Daily OHLCV for the last N sessions.
  liquidity SYMBOL                   20-day avg daily volume.
  screen --sector NAME               Screen a sector from the playbook.

Output is JSON by default. Pass --table for a human-readable table.
"""

from __future__ import annotations

import argparse
import json
import sys
from typing import Any

try:
    import yfinance as yf
    import pandas as pd
except ImportError as e:  # pragma: no cover
    sys.stderr.write(
        "Missing dependency: {}. Install with: pip install yfinance pandas\n".format(e.name)
    )
    sys.exit(2)


# Sector -> symbol lists. Mirrors the Sector Playbook in memory/TRADING-STRATEGY.md.
# TODO: extend once the playbook grows. Keep in sync with the playbook table.
SECTOR_SYMBOLS: dict[str, list[str]] = {
    "banking": ["BBCA", "BBRI", "BMRI", "BBNI"],
    "coal": ["ADRO", "ITMG", "PTBA"],
    "nickel": ["ANTM", "INCO", "MDKA"],
    "telco": ["TLKM", "EXCL", "ISAT"],
    "consumer": ["UNVR", "ICBP", "INDF"],
    "property": ["BSDE", "CTRA", "SMRA"],
}


# ---------- helpers ----------

def normalize_symbol(sym: str) -> str:
    """IDX tickers on Yahoo end in .JK. If the input is 4 letters with no dot,
    append .JK. Otherwise pass through (lets callers use .JK explicitly or
    non-IDX symbols for testing)."""
    s = sym.strip().upper()
    if "." in s:
        return s
    if len(s) == 4 and s.isalpha():
        return s + ".JK"
    return s


def _safe_get(info: dict[str, Any], *keys: str) -> Any:
    for k in keys:
        if k in info and info[k] is not None:
            return info[k]
    return None


# ---------- subcommand implementations ----------

def cmd_quote(sym: str) -> dict[str, Any]:
    t = yf.Ticker(normalize_symbol(sym))
    hist = t.history(period="2d", auto_adjust=False)
    if hist.empty:
        return {"symbol": normalize_symbol(sym), "error": "no data"}
    last = hist.iloc[-1]
    prev_close = hist.iloc[-2]["Close"] if len(hist) >= 2 else last["Open"]
    close = float(last["Close"])
    chg_pct = (close - float(prev_close)) / float(prev_close) * 100.0 if prev_close else None
    return {
        "symbol": normalize_symbol(sym),
        "last": close,
        "prev_close": float(prev_close) if prev_close else None,
        "day_change_pct": round(chg_pct, 3) if chg_pct is not None else None,
        "volume": int(last["Volume"]) if not pd.isna(last["Volume"]) else None,
        "ts": str(hist.index[-1]),
    }


def cmd_fundamentals(sym: str) -> dict[str, Any]:
    t = yf.Ticker(normalize_symbol(sym))
    try:
        info = t.info or {}
    except Exception as e:  # yfinance occasionally raises
        return {"symbol": normalize_symbol(sym), "error": "info fetch failed: {}".format(e)}
    return {
        "symbol": normalize_symbol(sym),
        "name": _safe_get(info, "longName", "shortName"),
        "sector": _safe_get(info, "sector"),
        "industry": _safe_get(info, "industry"),
        "market_cap": _safe_get(info, "marketCap"),
        "trailing_pe": _safe_get(info, "trailingPE"),
        "forward_pe": _safe_get(info, "forwardPE"),
        "price_to_book": _safe_get(info, "priceToBook"),
        "dividend_yield": _safe_get(info, "dividendYield"),
        "return_on_equity": _safe_get(info, "returnOnEquity"),
        "profit_margin": _safe_get(info, "profitMargins"),
        "revenue_growth": _safe_get(info, "revenueGrowth"),
        "debt_to_equity": _safe_get(info, "debtToEquity"),
    }
    # TODO: for real IDX feed, pull audited financials directly (latest 10-K/annual report)
    # rather than relying on yfinance's scraped .info dict.


def cmd_price_history(sym: str, days: int) -> dict[str, Any]:
    t = yf.Ticker(normalize_symbol(sym))
    # pull a bit extra to compensate for non-trading days, then tail
    hist = t.history(period="{}d".format(max(days + 10, days)), auto_adjust=False).tail(days)
    if hist.empty:
        return {"symbol": normalize_symbol(sym), "error": "no data"}
    rows = []
    for idx, row in hist.iterrows():
        rows.append({
            "date": str(idx.date()),
            "open": float(row["Open"]),
            "high": float(row["High"]),
            "low": float(row["Low"]),
            "close": float(row["Close"]),
            "volume": int(row["Volume"]) if not pd.isna(row["Volume"]) else None,
        })
    return {"symbol": normalize_symbol(sym), "days": len(rows), "bars": rows}


def cmd_liquidity(sym: str) -> dict[str, Any]:
    t = yf.Ticker(normalize_symbol(sym))
    hist = t.history(period="40d", auto_adjust=False).tail(20)
    if hist.empty:
        return {"symbol": normalize_symbol(sym), "error": "no data"}
    avg_vol = float(hist["Volume"].mean())
    return {
        "symbol": normalize_symbol(sym),
        "avg_daily_volume_20d": int(avg_vol),
        "passes_500k_filter": avg_vol > 500_000,  # matches buy-side gate check #7
    }


def cmd_screen(sector: str) -> dict[str, Any]:
    sector = sector.lower()
    if sector not in SECTOR_SYMBOLS:
        return {
            "error": "unknown sector: {}".format(sector),
            "known_sectors": sorted(SECTOR_SYMBOLS.keys()),
        }
    symbols = SECTOR_SYMBOLS[sector]
    rows: list[dict[str, Any]] = []
    for s in symbols:
        sym = normalize_symbol(s)
        t = yf.Ticker(sym)
        try:
            info = t.info or {}
        except Exception:
            info = {}
        # 20-day avg vol + 1-month change
        hist = t.history(period="40d", auto_adjust=False).tail(20)
        if hist.empty:
            rows.append({"symbol": sym, "error": "no data"})
            continue
        avg_vol = float(hist["Volume"].mean())
        one_m_pct = None
        try:
            long_hist = t.history(period="35d", auto_adjust=False)
            if len(long_hist) >= 2:
                one_m_pct = (
                    float(long_hist["Close"].iloc[-1]) - float(long_hist["Close"].iloc[0])
                ) / float(long_hist["Close"].iloc[0]) * 100.0
        except Exception:
            pass
        rows.append({
            "symbol": sym,
            "trailing_pe": _safe_get(info, "trailingPE"),
            "price_to_book": _safe_get(info, "priceToBook"),
            "dividend_yield": _safe_get(info, "dividendYield"),
            "avg_daily_volume_20d": int(avg_vol),
            "one_month_pct": round(one_m_pct, 2) if one_m_pct is not None else None,
            "passes_liquidity": avg_vol > 500_000,
        })
    # Medians for reference (skip Nones)
    pes = [r["trailing_pe"] for r in rows if r.get("trailing_pe") is not None]
    pbs = [r["price_to_book"] for r in rows if r.get("price_to_book") is not None]
    medians = {
        "pe_median": float(pd.Series(pes).median()) if pes else None,
        "pb_median": float(pd.Series(pbs).median()) if pbs else None,
    }
    # TODO: replace with real-vendor screen once available; yfinance .info
    # occasionally returns stale or partial ratios for smaller IDX names.
    return {"sector": sector, "medians": medians, "rows": rows}


# ---------- rendering ----------

def render(data: Any, as_table: bool) -> str:
    if not as_table:
        return json.dumps(data, indent=2, default=str)

    # Very simple table rendering for the shapes we emit.
    if isinstance(data, dict) and "rows" in data and isinstance(data["rows"], list):
        rows = data["rows"]
        if not rows:
            return "(empty)"
        cols = list(rows[0].keys())
        header = " | ".join(cols)
        sep = "-+-".join("-" * len(c) for c in cols)
        lines = [header, sep]
        for r in rows:
            lines.append(" | ".join(str(r.get(c, "")) for c in cols))
        prefix = ""
        if "sector" in data:
            prefix += "sector: {}\n".format(data["sector"])
        if "medians" in data:
            prefix += "medians: {}\n".format(data["medians"])
        return prefix + "\n".join(lines)

    if isinstance(data, dict) and "bars" in data and isinstance(data["bars"], list):
        bars = data["bars"]
        if not bars:
            return "(empty)"
        cols = list(bars[0].keys())
        lines = [" | ".join(cols), "-+-".join("-" * len(c) for c in cols)]
        for r in bars:
            lines.append(" | ".join(str(r.get(c, "")) for c in cols))
        return "symbol: {}\n".format(data.get("symbol", "")) + "\n".join(lines)

    if isinstance(data, dict):
        return "\n".join("{}: {}".format(k, v) for k, v in data.items())

    return str(data)


# ---------- CLI ----------

def build_parser() -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(description="IDX yfinance helper")
    p.add_argument("--table", action="store_true", help="human-readable table output")
    sub = p.add_subparsers(dest="cmd", required=True)

    q = sub.add_parser("quote", help="last price / change / volume")
    q.add_argument("symbol")

    f = sub.add_parser("fundamentals", help="valuation + profitability ratios")
    f.add_argument("symbol")

    ph = sub.add_parser("price-history", help="daily OHLCV")
    ph.add_argument("symbol")
    ph.add_argument("--days", type=int, default=30)

    liq = sub.add_parser("liquidity", help="20-day average daily volume")
    liq.add_argument("symbol")

    sc = sub.add_parser("screen", help="screen a sector from the playbook")
    sc.add_argument("--sector", required=True, help="banking|coal|nickel|telco|consumer|property")

    return p


def main(argv: list[str] | None = None) -> int:
    args = build_parser().parse_args(argv)
    if args.cmd == "quote":
        out = cmd_quote(args.symbol)
    elif args.cmd == "fundamentals":
        out = cmd_fundamentals(args.symbol)
    elif args.cmd == "price-history":
        out = cmd_price_history(args.symbol, args.days)
    elif args.cmd == "liquidity":
        out = cmd_liquidity(args.symbol)
    elif args.cmd == "screen":
        out = cmd_screen(args.sector)
    else:  # pragma: no cover
        sys.stderr.write("unknown command\n")
        return 2
    print(render(out, args.table))
    return 0


if __name__ == "__main__":
    sys.exit(main())
