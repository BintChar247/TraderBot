package com.garasi.app.quality

import androidx.annotation.StringRes
import com.garasi.app.R
import com.garasi.app.data.Category
import com.garasi.app.data.Condition
import com.garasi.app.data.Item
import com.garasi.app.data.ListingLang
import com.garasi.app.data.Photo
import com.garasi.app.text.LangGuess
import com.garasi.app.text.ListingText
import com.garasi.app.text.PriceCalculator
import com.garasi.app.text.Templates

enum class Area(val cap: Int) { PHOTOS(40), TITLE(20), DESCRIPTION(30), PRICE(10) }

/** What the one-tap button on a fix does. */
enum class FixAction(@StringRes val label: Int) {
    RETAKE_PHOTO(R.string.fix_retake),
    ADD_PHOTOS(R.string.fix_add_photos),
    MANAGE_PHOTOS(R.string.fix_manage_photos),
    MAKE_COVER(R.string.fix_make_cover),
    CROP_COVER(R.string.fix_crop_cover),
    ADD_FLAW_PHOTO(R.string.fix_add_flaw_photo),
    EDIT_TITLE(R.string.fix_edit_title),
    BUILD_TITLE(R.string.fix_build_title),
    EDIT_DESCRIPTION(R.string.fix_edit_description),
    USE_TEMPLATE(R.string.fix_use_template),
    EDIT_DETAILS(R.string.fix_edit_details),
    OPEN_PRICE(R.string.fix_open_price),
}

/** Parts of a description buyers look for. */
enum class InfoPiece(@StringRes val label: Int) {
    BRAND_MODEL(R.string.info_brand_model), CONDITION(R.string.info_condition), FLAWS(R.string.info_flaws),
    INCLUDED(R.string.info_included), USAGE(R.string.info_usage), REASON(R.string.info_reason),
    LOCATION(R.string.info_location), NEGOTIATION(R.string.info_negotiation), SHIPPING(R.string.info_shipping),
}

enum class IssueType(@StringRes val message: Int, val area: Area, val penalty: Int, val action: FixAction) {
    NO_PHOTOS(R.string.q_no_photos, Area.PHOTOS, 40, FixAction.ADD_PHOTOS),
    TOO_FEW_PHOTOS(R.string.q_too_few_photos, Area.PHOTOS, 12, FixAction.ADD_PHOTOS),
    FEW_PHOTOS(R.string.q_few_photos, Area.PHOTOS, 4, FixAction.ADD_PHOTOS),
    BLURRY(R.string.q_blurry, Area.PHOTOS, 12, FixAction.RETAKE_PHOTO),
    SOFT(R.string.q_soft, Area.PHOTOS, 5, FixAction.RETAKE_PHOTO),
    TOO_DARK(R.string.q_too_dark, Area.PHOTOS, 10, FixAction.RETAKE_PHOTO),
    OVEREXPOSED(R.string.q_overexposed, Area.PHOTOS, 8, FixAction.RETAKE_PHOTO),
    FLAT(R.string.q_flat, Area.PHOTOS, 4, FixAction.RETAKE_PHOTO),
    ITEM_TOO_SMALL(R.string.q_item_small, Area.PHOTOS, 6, FixAction.RETAKE_PHOTO),
    ITEM_TOO_TIGHT(R.string.q_item_tight, Area.PHOTOS, 3, FixAction.RETAKE_PHOTO),
    OFF_CENTER(R.string.q_off_center, Area.PHOTOS, 3, FixAction.RETAKE_PHOTO),
    BUSY_BACKGROUND(R.string.q_busy_background, Area.PHOTOS, 6, FixAction.RETAKE_PHOTO),
    LOW_RES(R.string.q_low_res, Area.PHOTOS, 6, FixAction.RETAKE_PHOTO),
    COVER_NOT_SQUARE(R.string.q_cover_not_square, Area.PHOTOS, 4, FixAction.CROP_COVER),
    BETTER_COVER(R.string.q_better_cover, Area.PHOTOS, 5, FixAction.MAKE_COVER),
    NO_FLAW_PHOTO(R.string.q_no_flaw_photo, Area.PHOTOS, 10, FixAction.ADD_FLAW_PHOTO),
    MISSING_ANGLES(R.string.q_missing_angles, Area.PHOTOS, 4, FixAction.MANAGE_PHOTOS),

    NO_TITLE(R.string.q_no_title, Area.TITLE, 20, FixAction.BUILD_TITLE),
    TITLE_SHORT(R.string.q_title_short, Area.TITLE, 5, FixAction.EDIT_TITLE),
    TITLE_LONG(R.string.q_title_long, Area.TITLE, 6, FixAction.EDIT_TITLE),
    TITLE_NO_BRAND_START(R.string.q_title_brand_start, Area.TITLE, 6, FixAction.BUILD_TITLE),
    TITLE_NO_MODEL(R.string.q_title_no_model, Area.TITLE, 5, FixAction.BUILD_TITLE),
    TITLE_NO_VARIANT(R.string.q_title_no_variant, Area.TITLE, 3, FixAction.EDIT_DETAILS),
    TITLE_ALL_CAPS(R.string.q_title_caps, Area.TITLE, 6, FixAction.EDIT_TITLE),
    TITLE_SHOUTING(R.string.q_title_shouting, Area.TITLE, 6, FixAction.EDIT_TITLE),
    TITLE_REPEATED(R.string.q_title_repeated, Area.TITLE, 3, FixAction.EDIT_TITLE),
    TITLE_SPAM(R.string.q_title_spam, Area.TITLE, 6, FixAction.EDIT_TITLE),
    EN_ADD_ID_KEYWORDS(R.string.q_en_keywords, Area.TITLE, 2, FixAction.EDIT_TITLE),

    NO_DESCRIPTION(R.string.q_no_description, Area.DESCRIPTION, 30, FixAction.USE_TEMPLATE),
    DESC_BLANKS(R.string.q_desc_blanks, Area.DESCRIPTION, 8, FixAction.EDIT_DESCRIPTION),
    DESC_SHORT(R.string.q_desc_short, Area.DESCRIPTION, 6, FixAction.EDIT_DESCRIPTION),
    DESC_LONG(R.string.q_desc_long, Area.DESCRIPTION, 4, FixAction.EDIT_DESCRIPTION),
    DESC_MISSING_INFO(R.string.q_desc_missing, Area.DESCRIPTION, 3, FixAction.EDIT_DESCRIPTION), // ×pieces
    DESC_LONG_SENTENCES(R.string.q_desc_long_sentences, Area.DESCRIPTION, 3, FixAction.EDIT_DESCRIPTION),
    DESC_WALL(R.string.q_desc_wall, Area.DESCRIPTION, 5, FixAction.EDIT_DESCRIPTION),
    DESC_EMOJI(R.string.q_desc_emoji, Area.DESCRIPTION, 3, FixAction.EDIT_DESCRIPTION),
    DESC_EXCLAIM(R.string.q_desc_exclaim, Area.DESCRIPTION, 3, FixAction.EDIT_DESCRIPTION),
    DESC_CAPS(R.string.q_desc_caps, Area.DESCRIPTION, 4, FixAction.EDIT_DESCRIPTION),
    TONE_ID_STIFF(R.string.q_tone_id, Area.DESCRIPTION, 5, FixAction.EDIT_DESCRIPTION),
    TONE_EN_HYPE(R.string.q_tone_en, Area.DESCRIPTION, 5, FixAction.EDIT_DESCRIPTION),
    MIXED_LANG(R.string.q_mixed_lang, Area.DESCRIPTION, 5, FixAction.EDIT_DESCRIPTION),
    WRONG_LANG(R.string.q_wrong_lang, Area.DESCRIPTION, 5, FixAction.EDIT_DESCRIPTION),
    CONTACT_INFO(R.string.q_contact_info, Area.DESCRIPTION, 10, FixAction.EDIT_DESCRIPTION),

    NO_PRICE(R.string.q_no_price, Area.PRICE, 10, FixAction.OPEN_PRICE),
    NO_COMPARE(R.string.q_no_compare, Area.PRICE, 3, FixAction.OPEN_PRICE),
    PRICE_HIGH(R.string.q_price_high, Area.PRICE, 7, FixAction.OPEN_PRICE),
    PRICE_LOW(R.string.q_price_low, Area.PRICE, 6, FixAction.OPEN_PRICE),
}

data class Issue(
    val type: IssueType,
    val penalty: Int = type.penalty,
    val photoId: Long? = null,
    /** Format args for the message (strings / numbers). */
    val args: List<Any> = emptyList(),
    val pieces: List<InfoPiece> = emptyList(),
)

data class QualityReport(
    val score: Int,
    val issues: List<Issue>,
    val areaScores: Map<Area, Int>,
    val bestPhotoId: Long?,
) {
    val topFixes: List<Issue> get() = issues.take(3)
    val verdict: Verdict get() = when {
        score >= 80 -> Verdict.READY
        score >= 60 -> Verdict.GOOD
        else -> Verdict.NEEDS_FIXING
    }
}

enum class Verdict(@StringRes val label: Int) {
    READY(R.string.verdict_ready), GOOD(R.string.verdict_good), NEEDS_FIXING(R.string.verdict_fix)
}

object Thresholds {
    const val BLURRY = 60.0
    const val SOFT = 120.0
    const val DARK = 70.0
    const val BRIGHT = 205.0
    const val FLAT = 28.0
    const val FILL_MIN = 0.45   // spec target 60–80%, with some tolerance before nagging
    const val FILL_MAX = 0.92
    const val OFFSET_MAX = 0.28
    const val BUSY_EDGES = 0.14
    const val BUSY_COLOR = 55.0
    const val MIN_RES = 800
}

/** Pure-Kotlin listing quality check. All inputs come from the database; no Android APIs used. */
object QualityChecker {

    fun check(item: Item, photos: List<Photo>, lang: ListingLang = item.listingLang): QualityReport {
        val issues = mutableListOf<Issue>()
        val best = photoChecks(item, photos, issues)
        titleChecks(item, item.title(lang), lang, issues)
        descriptionChecks(item, item.title(lang), item.description(lang), lang, issues)
        priceChecks(item, issues)

        val areaScores = Area.entries.associateWith { area ->
            val lost = issues.filter { it.type.area == area }.sumOf { it.penalty }.coerceAtMost(area.cap)
            area.cap - lost
        }
        val score = areaScores.values.sum().coerceIn(0, 100)
        return QualityReport(score, issues.sortedByDescending { it.penalty }, areaScores, best)
    }

    // ---------------------------------------------------------------- photos

    /** Per-photo score from its stored metrics (0–100). */
    fun photoProblems(p: Photo): List<IssueType> {
        val out = mutableListOf<IssueType>()
        p.sharpness?.let { if (it < Thresholds.BLURRY) out += IssueType.BLURRY else if (it < Thresholds.SOFT) out += IssueType.SOFT }
        p.brightness?.let { if (it < Thresholds.DARK) out += IssueType.TOO_DARK else if (it > Thresholds.BRIGHT) out += IssueType.OVEREXPOSED }
        p.contrast?.let { if (it < Thresholds.FLAT && IssueType.TOO_DARK !in out) out += IssueType.FLAT }
        p.objectFill?.let { if (it < Thresholds.FILL_MIN) out += IssueType.ITEM_TOO_SMALL else if (it > Thresholds.FILL_MAX) out += IssueType.ITEM_TOO_TIGHT }
        p.objectOffset?.let { if (it > Thresholds.OFFSET_MAX) out += IssueType.OFF_CENTER }
        val busy = (p.edgeDensity ?: 0.0) > Thresholds.BUSY_EDGES && (p.colorVariance ?: 0.0) > Thresholds.BUSY_COLOR
        if (busy) out += IssueType.BUSY_BACKGROUND
        if (p.width > 0 && minOf(p.width, p.height) < Thresholds.MIN_RES) out += IssueType.LOW_RES
        return out
    }

    fun photoScore(p: Photo): Int =
        (100 - photoProblems(p).sumOf { it.penalty * 2 }).coerceIn(0, 100)

    private fun photoChecks(item: Item, photos: List<Photo>, issues: MutableList<Issue>): Long? {
        if (photos.isEmpty()) {
            issues += Issue(IssueType.NO_PHOTOS)
            return null
        }
        when {
            photos.size < 4 -> issues += Issue(IssueType.TOO_FEW_PHOTOS, args = listOf(photos.size))
            photos.size < 6 -> issues += Issue(IssueType.FEW_PHOTOS, args = listOf(photos.size))
        }
        // Each problem type reported once, pointing at the worst offending photo. Flaw close-ups
        // are allowed to be tight / off-centre / busy.
        val byType = linkedMapOf<IssueType, MutableList<Photo>>()
        photos.forEach { p ->
            photoProblems(p).forEach { t ->
                val flawTolerant = p.isFlaw && t in setOf(IssueType.ITEM_TOO_TIGHT, IssueType.ITEM_TOO_SMALL,
                    IssueType.OFF_CENTER, IssueType.BUSY_BACKGROUND)
                if (!flawTolerant) byType.getOrPut(t) { mutableListOf() } += p
            }
        }
        byType.forEach { (t, list) ->
            val worst = list.minByOrNull { photoScore(it) }!!
            // Scale a bit with how many photos have it, but keep it bounded.
            val penalty = (t.penalty + (list.size - 1) * 2).coerceAtMost(t.penalty * 2)
            issues += Issue(t, penalty, worst.id, listOf(photos.indexOf(worst) + 1, list.size))
        }

        val cover = photos.firstOrNull { it.isCover } ?: photos.first()
        if (cover.width > 0 && cover.width != cover.height) {
            issues += Issue(IssueType.COVER_NOT_SQUARE, photoId = cover.id)
        }
        val scored = photos.filter { it.sharpness != null && !it.isFlaw }
        val best = scored.maxByOrNull { photoScore(it) }
        if (best != null && best.id != cover.id && photoScore(best) - photoScore(cover) >= 15) {
            issues += Issue(IssueType.BETTER_COVER, photoId = best.id, args = listOf(photos.indexOf(best) + 1))
        }
        if (item.condition == Condition.USED_FLAWS && photos.none { it.isFlaw }) {
            issues += Issue(IssueType.NO_FLAW_PHOTO)
        }
        val needed = ListingText.angles(item.category).filter { it != "flaw" }
        val covered = photos.map { it.angle }.toSet()
        val missing = needed.filter { it !in covered }
        // Only nag about angles once the user has started tagging, or has plenty of photos untagged.
        if (missing.isNotEmpty() && (covered.any { it.isNotBlank() } || photos.size >= 4)) {
            issues += Issue(IssueType.MISSING_ANGLES, penalty = minOf(missing.size * 1 + 1, 6), args = listOf(missing.size))
        }
        return best?.id
    }

    // ---------------------------------------------------------------- title

    private val spamWords = setOf(
        "murah", "termurah", "promo", "sale", "diskon", "ready", "stock", "stok", "original", "ori", "best",
        "seller", "cheap", "cheapest", "hot", "terlaris", "laris", "bonus", "gratis", "free", "100%", "bagus",
        "keren", "mantap", "jual", "dijual",
    )

    fun titleChecks(item: Item, title: String, lang: ListingLang, issues: MutableList<Issue>) {
        val t = title.trim()
        if (t.isEmpty()) { issues += Issue(IssueType.NO_TITLE); return }
        if (t.length < 40) issues += Issue(IssueType.TITLE_SHORT, args = listOf(t.length))
        if (t.length > 70) issues += Issue(IssueType.TITLE_LONG, args = listOf(t.length))
        val lower = t.lowercase()
        if (item.brand.isNotBlank() && !lower.startsWith(item.brand.trim().lowercase())) {
            issues += Issue(IssueType.TITLE_NO_BRAND_START)
        }
        if (item.model.isNotBlank() && item.model.trim().lowercase() !in lower) {
            issues += Issue(IssueType.TITLE_NO_MODEL)
        }
        val variantWords = item.variant.lowercase().split(Regex("[\\s,/]+")).filter { it.length >= 2 }
        if (variantWords.isNotEmpty() && variantWords.none { it in lower }) {
            issues += Issue(IssueType.TITLE_NO_VARIANT)
        } else if (item.variant.isBlank() && item.category in setOf(Category.ELECTRONICS, Category.FASHION) &&
            !Regex("""\d+\s?(gb|tb|mm|cm|inch|")|\b(xs|s|m|l|xl|xxl)\b|\b(hitam|putih|merah|biru|hijau|black|white|red|blue|green|grey|gray|silver|gold|pink)\b""")
                .containsMatchIn(lower)
        ) {
            issues += Issue(IssueType.TITLE_NO_VARIANT)
        }
        val letters = t.filter { it.isLetter() }
        if (letters.length >= 8 && letters.count { it.isUpperCase() }.toDouble() / letters.length > 0.6) {
            issues += Issue(IssueType.TITLE_ALL_CAPS)
        } else {
            val known = (item.brand + " " + item.model + " " + item.variant).lowercase().split(Regex("\\s+")).toSet()
            val shouty = t.split(Regex("\\s+")).filter { w ->
                val l = w.filter { it.isLetter() }
                l.length >= 4 && l.all { it.isUpperCase() } && w.none { it.isDigit() } && w.lowercase() !in known
            }
            if (shouty.isNotEmpty() || "!" in t) issues += Issue(IssueType.TITLE_SHOUTING, args = listOf(shouty.firstOrNull() ?: "!"))
        }
        val words = lower.split(Regex("[^\\p{L}\\p{N}]+")).filter { it.length >= 3 }
        val repeated = words.groupingBy { it }.eachCount().filter { it.value > 1 }.keys
        if (repeated.isNotEmpty()) issues += Issue(IssueType.TITLE_REPEATED, args = listOf(repeated.first()))
        val spamCount = words.count { it in spamWords } + (if ("100%" in lower) 1 else 0)
        val separators = t.count { it == ',' || it == '|' || it == '/' || it == '#' }
        if (spamCount >= 2 || separators >= 3) issues += Issue(IssueType.TITLE_SPAM)
        if (lang == ListingLang.EN && listOf("bekas", "second", "murah", "preloved").none { it in lower }) {
            issues += Issue(IssueType.EN_ADD_ID_KEYWORDS)
        }
    }

    // ---------------------------------------------------------------- description

    val STIFF_ID = listOf(
        "anda", "solusi sempurna", "tingkatkan pengalaman", "jangan lewatkan", "kesempatan emas",
        "dapatkan segera", "kualitas premium", "dirancang khusus", "tunggu apa lagi", "persediaan terbatas",
        "nikmati pengalaman", "sangat direkomendasikan",
    )
    val HYPE_EN = listOf(
        "elevate your", "premium quality", "don't miss out", "dont miss out", "game-changer", "game changer",
        "must-have", "must have", "unleash", "next level", "act now", "limited time", "best deal ever",
        "once in a lifetime", "look no further", "take your", "experience like never before",
    )

    private val phoneRegex = Regex("""(\+?62|0)\s?8\d{1,3}[\s.\-]?\d{3,4}[\s.\-]?\d{3,5}""")
    private val linkRegex = Regex("""(https?://|www\.|wa\.me|bit\.ly|\b[\w-]+\.(com|id|co\.id|net|me|ly)\b|\bwhats ?app\b|\bwa\s?:)""", RegexOption.IGNORE_CASE)

    fun containsContactInfo(text: String) = phoneRegex.containsMatchIn(text) || linkRegex.containsMatchIn(text)

    fun countEmoji(text: String): Int {
        var n = 0
        var i = 0
        while (i < text.length) {
            val cp = text.codePointAt(i)
            if (cp in 0x1F300..0x1FAFF || cp in 0x2600..0x27BF || cp in 0x1F000..0x1F2FF) n++
            i += Character.charCount(cp)
        }
        return n
    }

    fun sentences(text: String): List<String> =
        text.split(Regex("""(?<=[.!?])\s+|\n+""")).map { it.trim() }.filter { it.isNotEmpty() }

    fun missingInfo(item: Item, d: String): List<InfoPiece> {
        val l = d.lowercase()
        fun any(vararg k: String) = k.any { it in l }
        val out = mutableListOf<InfoPiece>()
        val bm = listOf(item.brand, item.model).filter { it.isNotBlank() }
        if (bm.isNotEmpty() && bm.none { it.lowercase() in l } || bm.isEmpty() && !any("merek", "brand", "model")) out += InfoPiece.BRAND_MODEL
        if (!any("kondisi", "mulus", "normal", "condition", "works", "like new", "bekas", "used", "baru", "new", "lancar", "berfungsi")) out += InfoPiece.CONDITION
        if (!any("minus", "lecet", "flaw", "scratch", "cacat", "dent", "penyok", "noda", "stain", "defect", "kekurangan")) out += InfoPiece.FLAWS
        if (!any("kelengkapan", "lengkap", "box", "dus", "charger", "comes with", "included", "include", "dapat", "free", "bonus", "unit only", "batangan")) out += InfoPiece.INCLUDED
        if (!any("pemakaian", "dipakai", "dipake", "tahun", "bulan", "minggu", "used for", "year", "month", "week", "bought", "beli")) out += InfoPiece.USAGE
        if (!any("karena", "alasan", "jarang", "because", "reason", "rarely", "upgrade", "ganti", "pindah", "moving", "no longer", "nggak kepake", "gak kepake", "nganggur")) out += InfoPiece.REASON
        if (!any("cod", "lokasi", "📍", "location", "pickup", "pick up", "ambil") && (item.location.isBlank() || item.location.lowercase() !in l)) out += InfoPiece.LOCATION
        if (!any("nego", "negotiable", "offer", "fix price", "harga pas", "harga net")) out += InfoPiece.NEGOTIATION
        if (!any("kirim", "ekspedisi", "ongkir", "jne", "j&t", "sicepat", "gosend", "grab", "shipping", "courier", "ship", "delivery", "pengiriman")) out += InfoPiece.SHIPPING
        return out
    }

    fun descriptionChecks(item: Item, title: String, desc: String, lang: ListingLang, issues: MutableList<Issue>) {
        val d = desc.trim()
        if (d.isEmpty()) { issues += Issue(IssueType.NO_DESCRIPTION); return }
        if (Templates.hasBlanks(d)) issues += Issue(IssueType.DESC_BLANKS)
        if (d.length < 300) issues += Issue(IssueType.DESC_SHORT, args = listOf(d.length))
        if (d.length > 1200) issues += Issue(IssueType.DESC_LONG, args = listOf(d.length))

        val missing = missingInfo(item, d)
        if (missing.isNotEmpty()) {
            issues += Issue(IssueType.DESC_MISSING_INFO, penalty = (missing.size * 3).coerceAtMost(15), pieces = missing)
        }
        val longOnes = sentences(d).count { s -> s.split(Regex("\\s+")).size > 25 }
        if (longOnes > 0) issues += Issue(IssueType.DESC_LONG_SENTENCES, args = listOf(longOnes))

        val paragraphs = d.split(Regex("\\n")).map { it.trim() }
        if (paragraphs.any { it.length > 400 } || (!d.contains('\n') && d.length > 350)) {
            issues += Issue(IssueType.DESC_WALL)
        }
        val emoji = countEmoji(d)
        if (emoji > 5) issues += Issue(IssueType.DESC_EMOJI, args = listOf(emoji))
        if (d.count { it == '!' } > 3 || "!!" in d) issues += Issue(IssueType.DESC_EXCLAIM)
        val capsWords = d.split(Regex("\\s+")).count { w ->
            val l = w.filter { it.isLetter() }
            l.length >= 4 && l.all { it.isUpperCase() } && w.none { it.isDigit() }
        }
        if (capsWords >= 4) issues += Issue(IssueType.DESC_CAPS)

        val lower = d.lowercase()
        if (lang == ListingLang.ID) {
            STIFF_ID.firstOrNull { p -> if (p == "anda") Regex("\\banda\\b").containsMatchIn(lower) else p in lower }
                ?.let { issues += Issue(IssueType.TONE_ID_STIFF, args = listOf(it)) }
        } else {
            HYPE_EN.firstOrNull { it in lower }?.let { issues += Issue(IssueType.TONE_EN_HYPE, args = listOf(it)) }
        }
        val gTitle = LangGuess.guess(title)
        val gDesc = LangGuess.guess(d)
        if ((gTitle != null && gDesc != null && gTitle != gDesc) || LangGuess.isMixed(d)) {
            issues += Issue(IssueType.MIXED_LANG)
        } else if (gDesc != null && gDesc != lang) {
            issues += Issue(IssueType.WRONG_LANG)
        }
        if (containsContactInfo(d) || containsContactInfo(title)) issues += Issue(IssueType.CONTACT_INFO)
    }

    // ---------------------------------------------------------------- price

    private fun priceChecks(item: Item, issues: MutableList<Issue>) {
        if (item.askPrice <= 0) { issues += Issue(IssueType.NO_PRICE); return }
        val median = PriceCalculator.effectiveMedian(item.compareLow, item.compareMedian, item.compareHigh)
        if (median <= 0) { issues += Issue(IssueType.NO_COMPARE); return }
        val ratio = item.askPrice.toDouble() / median
        val highLimit = if (item.condition == Condition.NEW) 1.3 else 1.2
        when {
            ratio > highLimit -> issues += Issue(IssueType.PRICE_HIGH, args = listOf((ratio * 100).toInt()))
            ratio < 0.25 -> issues += Issue(IssueType.PRICE_LOW, args = listOf((ratio * 100).toInt()))
        }
    }
}
