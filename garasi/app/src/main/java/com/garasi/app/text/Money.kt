package com.garasi.app.text

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.roundToLong

object Money {
    private val symbols = DecimalFormatSymbols(Locale.ROOT).apply { groupingSeparator = '.'; decimalSeparator = ',' }
    private val fmt = DecimalFormat("#,###", symbols)

    /** 1250000 -> "Rp 1.250.000" */
    fun format(amount: Long): String = "Rp " + fmt.format(amount)

    /** Digits-only grouping for text fields: 1250000 -> "1.250.000" */
    fun group(amount: Long): String = if (amount <= 0) "" else fmt.format(amount)

    /**
     * Parses Indonesian-style prices: "Rp 1.250.000", "1,5 jt", "1.5jt", "850rb", "850k", "2 juta",
     * "Rp1.250.000,00". Returns null when nothing sensible is found.
     */
    fun parse(raw: String?): Long? {
        if (raw.isNullOrBlank()) return null
        val s = raw.lowercase(Locale.ROOT).replace("rp", "").replace("idr", "").trim()
        val m = Regex("""(\d[\d.,]*)\s*(juta|jt|m\b|ribu|rb|k\b)?""").find(s) ?: return null
        var num = m.groupValues[1].trimEnd('.', ',')
        val unit = m.groupValues[2]
        val multiplier = when (unit) {
            "juta", "jt", "m" -> 1_000_000.0
            "ribu", "rb", "k" -> 1_000.0
            else -> 1.0
        }
        val value: Double = if (multiplier > 1.0) {
            // "1,5" or "1.5" are decimals here
            num.replace(',', '.').let { n ->
                val parts = n.split('.')
                if (parts.size > 2) parts.joinToString("").toDoubleOrNull() else n.toDoubleOrNull()
            } ?: return null
        } else {
            // strip trailing ",00" decimals, then all grouping separators
            num = num.replace(Regex("""[.,]\d{1,2}$"""), "")
            num.replace(".", "").replace(",", "").toDoubleOrNull() ?: return null
        }
        val result = (value * multiplier).roundToLong()
        return if (result > 0) result else null
    }

    /** Round to a "human" price: nearest 1.000 under 100k, else nearest 5.000 (or 50.000 above 5 jt). */
    fun roundNice(amount: Double): Long {
        val step = when {
            amount < 100_000 -> 1_000.0
            amount < 5_000_000 -> 5_000.0
            else -> 50_000.0
        }
        return ((amount / step).roundToLong() * step).toLong()
    }
}
