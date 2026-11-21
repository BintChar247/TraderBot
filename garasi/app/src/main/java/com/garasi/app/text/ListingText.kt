package com.garasi.app.text

import com.garasi.app.data.Category
import com.garasi.app.data.Condition
import com.garasi.app.data.ListingLang

/**
 * Listing-language content (Indonesian / English). This is text that ends up in the listing or in the
 * AI prompt, not app UI — app UI strings live in res/values/strings.xml.
 */
object ListingText {
    fun condition(c: Condition, lang: ListingLang): String = when (lang) {
        ListingLang.ID -> when (c) {
            Condition.NEW -> "Baru"
            Condition.LIKE_NEW -> "Like New"
            Condition.USED_GOOD -> "Bekas – Baik"
            Condition.USED_FLAWS -> "Bekas – Ada Minus"
        }
        ListingLang.EN -> when (c) {
            Condition.NEW -> "New"
            Condition.LIKE_NEW -> "Like New"
            Condition.USED_GOOD -> "Used – Good"
            Condition.USED_FLAWS -> "Used – Has Flaws"
        }
    }

    /** Short condition word for titles. */
    fun conditionShort(c: Condition, lang: ListingLang): String = when (lang) {
        ListingLang.ID -> when (c) {
            Condition.NEW -> "Baru"
            Condition.LIKE_NEW -> "Like New"
            Condition.USED_GOOD -> "Bekas Mulus"
            Condition.USED_FLAWS -> "Bekas"
        }
        ListingLang.EN -> when (c) {
            Condition.NEW -> "New"
            Condition.LIKE_NEW -> "Like New"
            Condition.USED_GOOD -> "Used Good"
            Condition.USED_FLAWS -> "Used"
        }
    }

    fun closing(lang: ListingLang) = when (lang) {
        ListingLang.ID -> "Bisa nego tipis, langsung chat aja ya 🙏"
        ListingLang.EN -> "Price slightly negotiable, just send me a message 🙏"
    }

    fun category(c: Category, lang: ListingLang): String = when (lang) {
        ListingLang.ID -> when (c) {
            Category.ELECTRONICS -> "Elektronik"; Category.FASHION -> "Fashion"; Category.BOOKS -> "Buku"
            Category.TOYS -> "Mainan"; Category.HOME -> "Rumah Tangga"; Category.HOBBY -> "Hobi"
            Category.OTHER -> "Lainnya"
        }
        ListingLang.EN -> when (c) {
            Category.ELECTRONICS -> "Electronics"; Category.FASHION -> "Fashion"; Category.BOOKS -> "Books"
            Category.TOYS -> "Toys"; Category.HOME -> "Home"; Category.HOBBY -> "Hobby"
            Category.OTHER -> "Other"
        }
    }

    val EXAMPLE_ID = "Jual Sony WH-1000XM4 hitam, pemakaian ±1,5 tahun. Suara masih mantap, " +
        "noise cancelling normal, baterai tahan ±25 jam. Minus: busa kiri agak ngelupas dikit (lihat foto ke-6). " +
        "Kelengkapan: box, kabel charger, kabel audio, pouch. Dijual karena udah jarang dipake sejak WFO. " +
        "Harga bisa nego tipis, langsung chat aja ya 🙏"

    val EXAMPLE_EN = "Selling my black Sony WH-1000XM4, used for about 1.5 years. Sound is still great, " +
        "noise cancelling works fine, battery lasts ~25 hours. Flaw: left ear cushion is peeling a little " +
        "(see photo 6). Comes with box, charging cable, audio cable and pouch. Selling because I rarely use it " +
        "since going back to the office. Price slightly negotiable, just send me a message 🙏"

    fun example(lang: ListingLang) = if (lang == ListingLang.ID) EXAMPLE_ID else EXAMPLE_EN

    /** Photo angles to cover, per category. Keys are stable ids, values are UI string resource names. */
    fun angles(c: Category): List<String> = when (c) {
        Category.ELECTRONICS -> listOf("front", "back", "sides", "screen_on", "label", "accessories", "flaw")
        Category.FASHION -> listOf("front", "back", "label", "detail", "worn", "flaw")
        Category.BOOKS -> listOf("front", "back", "spine", "pages", "flaw")
        Category.TOYS -> listOf("front", "back", "box", "parts", "flaw")
        Category.HOME -> listOf("front", "back", "sides", "in_use", "label", "flaw")
        Category.HOBBY -> listOf("front", "back", "detail", "accessories", "flaw")
        Category.OTHER -> listOf("front", "back", "detail", "flaw")
    }
}
