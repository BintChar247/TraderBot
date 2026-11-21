package com.garasi.app.text

import com.garasi.app.data.Category
import com.garasi.app.data.Condition
import com.garasi.app.data.Item
import com.garasi.app.data.ListingLang

/** Description templates and the title builder. Works fully offline, no AI needed. */
object Templates {
    const val TITLE_MAX = 70

    /** [Brand] [Model] [Variant] [Condition] — max 70 chars, cut on a word boundary. */
    fun buildTitle(item: Item, lang: ListingLang = item.listingLang, max: Int = TITLE_MAX): String {
        val parts = listOf(item.brand, item.model, item.variant, ListingText.conditionShort(item.condition, lang))
            .map { it.trim() }.filter { it.isNotEmpty() }
        val words = mutableListOf<String>()
        val seen = mutableSetOf<String>()
        parts.joinToString(" ").split(Regex("\\s+")).forEach { w ->
            if (w.isNotBlank() && seen.add(w.lowercase())) words += w
        }
        return fitWords(words, max)
    }

    fun fitWords(words: List<String>, max: Int): String {
        val sb = StringBuilder()
        for (w in words) {
            val next = if (sb.isEmpty()) w else "$sb $w"
            if (next.length > max) break
            sb.clear().append(next)
        }
        return sb.toString()
    }

    private fun blank(v: String, hint: String) = v.trim().ifEmpty { "[$hint]" }

    /**
     * Builds a description from the item's details. Missing details are left as "[hint]" blanks for
     * the user to fill in (Quality Check flags any blanks left behind).
     */
    fun description(item: Item, lang: ListingLang = item.listingLang): String =
        if (lang == ListingLang.ID) descriptionId(item) else descriptionEn(item)

    private fun descriptionId(i: Item): String {
        val name = listOf(i.brand, i.model, i.variant).filter { it.isNotBlank() }.joinToString(" ").ifBlank { "[nama barang]" }
        val cond = ListingText.condition(i.condition, ListingLang.ID)
        val b = StringBuilder()
        b.append("Jual $name, kondisi ${cond.lowercase()}, pemakaian ${blank(i.usageAge, "±berapa lama")}.\n\n")
        b.append("Detail:\n")
        b.append("• Merek/model: $name\n")
        b.append("• Tahun beli: ${blank(i.yearBought, "tahun")}\n")
        categoryBulletsId(i.category).forEach { b.append("• $it\n") }
        b.append("• Kondisi: $cond\n")
        if (i.condition == Condition.USED_FLAWS || i.flaws.isNotBlank()) {
            b.append("• Minus: ${blank(i.flaws, "jelasin minusnya")} (lihat foto close-up)\n")
        } else {
            b.append("• Minus: nggak ada minus berarti\n")
        }
        b.append("• Kelengkapan: ${blank(i.included, "box, charger, dll")}\n\n")
        b.append("Dijual karena ${blank(i.reasonSelling, "alasan jual")}.\n")
        b.append("📍 ${blank(i.location, "kota")} — bisa COD atau kirim ${i.shipping.ifBlank { "via ekspedisi" }}.\n\n")
        b.append(ListingText.closing(ListingLang.ID))
        return b.toString()
    }

    private fun descriptionEn(i: Item): String {
        val name = listOf(i.brand, i.model, i.variant).filter { it.isNotBlank() }.joinToString(" ").ifBlank { "[item name]" }
        val cond = ListingText.condition(i.condition, ListingLang.EN)
        val b = StringBuilder()
        b.append("Selling my $name, condition ${cond.lowercase()}, used for ${blank(i.usageAge, "how long")}.\n\n")
        b.append("Details:\n")
        b.append("• Brand/model: $name\n")
        b.append("• Bought in: ${blank(i.yearBought, "year")}\n")
        categoryBulletsEn(i.category).forEach { b.append("• $it\n") }
        b.append("• Condition: $cond\n")
        if (i.condition == Condition.USED_FLAWS || i.flaws.isNotBlank()) {
            b.append("• Flaws: ${blank(i.flaws, "describe the flaws")} (see close-up photo)\n")
        } else {
            b.append("• Flaws: nothing worth mentioning\n")
        }
        b.append("• Comes with: ${blank(i.included, "box, charger, etc.")}\n\n")
        b.append("Selling because ${blank(i.reasonSelling, "reason")}.\n")
        b.append("📍 ${blank(i.location, "city")} — COD or shipping ${i.shipping.ifBlank { "by courier" }}.\n\n")
        b.append(ListingText.closing(ListingLang.EN))
        return b.toString()
    }

    private fun categoryBulletsId(c: Category): List<String> = when (c) {
        Category.ELECTRONICS -> listOf("Fungsi: [semua normal?]", "Baterai: [kondisi baterai]")
        Category.FASHION -> listOf("Ukuran: [S/M/L / nomor]", "Ukuran detail: [lingkar dada × panjang, cm]", "Bahan: [bahan]")
        Category.BOOKS -> listOf("Bahasa/penerbit: [penerbit]", "Kondisi kertas: [bersih / ada coretan?]")
        Category.TOYS -> listOf("Part: [lengkap?]", "Usia anak: [rekomendasi umur]")
        Category.HOME -> listOf("Ukuran: [p × l × t cm]", "Fungsi: [normal?]")
        Category.HOBBY -> listOf("Spesifikasi: [spesifikasi penting]")
        Category.OTHER -> emptyList()
    }

    private fun categoryBulletsEn(c: Category): List<String> = when (c) {
        Category.ELECTRONICS -> listOf("Works: [everything works?]", "Battery: [battery condition]")
        Category.FASHION -> listOf("Size: [S/M/L / number]", "Measurements: [chest × length, cm]", "Material: [material]")
        Category.BOOKS -> listOf("Language/publisher: [publisher]", "Pages: [clean / any notes?]")
        Category.TOYS -> listOf("Parts: [complete?]", "Age: [recommended age]")
        Category.HOME -> listOf("Size: [l × w × h cm]", "Works: [works fine?]")
        Category.HOBBY -> listOf("Specs: [key specs]")
        Category.OTHER -> emptyList()
    }

    fun hasBlanks(text: String): Boolean = Regex("""\[[^\]\n]{1,40}]""").containsMatchIn(text)
}
