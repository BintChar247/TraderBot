package com.garasi.app.text

import com.garasi.app.data.ListingLang

/** Very small stop-word based language guess, good enough to catch mixed ID/EN listings. */
object LangGuess {
    private val id = setOf(
        "dan", "yang", "di", "ke", "dari", "ini", "itu", "untuk", "dengan", "masih", "bisa", "ada", "aja",
        "ya", "nego", "jual", "bekas", "kondisi", "pemakaian", "karena", "dijual", "sudah", "udah", "tidak",
        "nggak", "gak", "kelengkapan", "minus", "baru", "lengkap", "pakai", "dipakai", "kotak", "harga",
        "langsung", "tahun", "bulan", "normal", "mulus", "kirim", "barang",
    )
    private val en = setOf(
        "the", "and", "is", "with", "for", "of", "to", "in", "it", "my", "this", "used", "selling", "comes",
        "still", "works", "condition", "because", "price", "just", "send", "me", "message", "box", "years",
        "year", "months", "great", "fine", "flaw", "flaws", "new", "good", "original", "only", "was", "are",
    )

    /** Returns null when there's not enough signal. */
    fun guess(text: String): ListingLang? {
        val words = text.lowercase().split(Regex("[^a-z]+")).filter { it.isNotEmpty() }
        val i = words.count { it in id }
        val e = words.count { it in en }
        if (i + e < 2) return null
        return when {
            i >= e * 2 -> ListingLang.ID
            e >= i * 2 -> ListingLang.EN
            else -> null
        }
    }

    /** True when the text has substantial signal from both languages. */
    fun isMixed(text: String): Boolean {
        val words = text.lowercase().split(Regex("[^a-z]+")).filter { it.isNotEmpty() }
        val i = words.count { it in id }
        val e = words.count { it in en }
        if (i + e < 8) return false
        val minor = minOf(i, e).toDouble() / (i + e)
        return minor > 0.3
    }
}
