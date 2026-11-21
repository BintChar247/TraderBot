package com.garasi.app

import com.garasi.app.data.Category
import com.garasi.app.data.Condition
import com.garasi.app.data.Item
import com.garasi.app.data.ListingLang
import com.garasi.app.data.Photo
import com.garasi.app.quality.IssueType
import com.garasi.app.quality.QualityChecker
import com.garasi.app.quality.Verdict
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QualityCheckerTest {
    private fun goodPhoto(id: Long, pos: Int, angle: String = "", flaw: Boolean = false) = Photo(
        id = id, itemId = 1, originalPath = "o$id", path = "p$id", position = pos, isCover = pos == 0,
        isFlaw = flaw, angle = angle, width = 1200, height = 1200, sharpness = 400.0, brightness = 140.0,
        contrast = 55.0, edgeDensity = 0.03, colorVariance = 20.0, objectFill = 0.7, objectOffset = 0.05,
    )

    private val desc = """
        Jual Sony WH-1000XM4 hitam, pemakaian ±1,5 tahun. Suara masih mantap, noise cancelling normal.

        • Minus: busa kiri agak ngelupas dikit (lihat foto ke-6)
        • Kelengkapan: box, kabel charger, kabel audio, pouch

        Dijual karena udah jarang dipake sejak WFO.
        📍 Jakarta Selatan, bisa COD atau kirim via JNE / SiCepat.
        Bisa nego tipis, langsung chat aja ya 🙏
    """.trimIndent()

    private val item = Item(
        id = 1, brand = "Sony", model = "WH-1000XM4", variant = "Hitam", category = Category.ELECTRONICS,
        condition = Condition.USED_FLAWS, location = "Jakarta Selatan",
        titleId = "Sony WH-1000XM4 Hitam Bekas Mulus Noise Cancelling",
        descId = desc, listingLang = ListingLang.ID,
        askPrice = 2_000_000, compareMedian = 2_100_000,
    )

    private val photos = listOf("front", "back", "sides", "screen_on", "label", "accessories")
        .mapIndexed { i, a -> goodPhoto(i + 1L, i, a) } + goodPhoto(7, 6, "flaw", flaw = true)

    @Test fun goodListingScoresReady() {
        val r = QualityChecker.check(item, photos)
        assertTrue("score ${r.score} issues ${r.issues.map { it.type }}", r.score >= 80)
        assertEquals(Verdict.READY, r.verdict)
    }

    @Test fun flawsWithoutFlawPhotoIsFlagged() {
        val r = QualityChecker.check(item, photos.filter { !it.isFlaw })
        assertTrue(r.issues.any { it.type == IssueType.NO_FLAW_PHOTO })
    }

    @Test fun emptyListingNeedsFixingAndShowsTopThree() {
        val r = QualityChecker.check(Item(id = 2), emptyList())
        assertEquals(Verdict.NEEDS_FIXING, r.verdict)
        assertEquals(3, r.topFixes.size)
        assertEquals(IssueType.NO_PHOTOS, r.topFixes.first().type)
    }

    @Test fun blurryDarkPhotosFlagged() {
        val bad = photos.toMutableList()
        bad[2] = bad[2].copy(sharpness = 20.0, brightness = 40.0)
        val types = QualityChecker.check(item, bad).issues.map { it.type }
        assertTrue(IssueType.BLURRY in types)
        assertTrue(IssueType.TOO_DARK in types)
    }

    @Test fun titleChecks() {
        fun types(t: String) = QualityChecker.check(item.copy(titleId = t), photos).issues.map { it.type }
        assertTrue(IssueType.TITLE_ALL_CAPS in types("SONY WH-1000XM4 HITAM BEKAS MULUS MURAH BANGET"))
        assertTrue(IssueType.TITLE_SHOUTING in types("Sony WH-1000XM4 Hitam CHEAP!!! bekas mulus banget"))
        assertTrue(IssueType.TITLE_SPAM in types("Sony WH-1000XM4 Hitam murah promo termurah original ready"))
        assertTrue(IssueType.TITLE_REPEATED in types("Sony WH-1000XM4 Hitam headphone headphone bekas mulus"))
        assertTrue(IssueType.TITLE_SHORT in types("Sony WH-1000XM4 Hitam"))
        assertTrue(IssueType.TITLE_NO_BRAND_START in types("Headphone Sony WH-1000XM4 Hitam bekas mulus normal"))
    }

    @Test fun descriptionChecks() {
        fun types(d: String, lang: ListingLang = ListingLang.ID) =
            QualityChecker.check(if (lang == ListingLang.ID) item.copy(descId = d) else item.copy(descEn = d, titleEn = item.titleId, listingLang = lang), photos)
                .issues.map { it.type }
        assertTrue(IssueType.CONTACT_INFO in types(desc + "\nWA 0812-3456-7890"))
        assertTrue(IssueType.CONTACT_INFO in types(desc + "\ncek https://example.com"))
        assertTrue(IssueType.TONE_ID_STIFF in types(desc + "\nJangan lewatkan kesempatan emas ini, Anda pasti puas"))
        assertTrue(IssueType.DESC_EMOJI in types(desc + " 🔥🔥🔥🔥🔥🔥"))
        assertTrue(IssueType.DESC_SHORT in types("Jual headphone bekas, nego."))
        assertFalse(IssueType.CONTACT_INFO in types(desc))
    }

    @Test fun englishListingGetsIndonesianKeywordTip() {
        val en = item.copy(listingLang = ListingLang.EN, titleEn = "Sony WH-1000XM4 Black, used, noise cancelling works",
            descEn = "Selling my black Sony WH-1000XM4, used for about 1.5 years.")
        assertTrue(QualityChecker.check(en, photos).issues.any { it.type == IssueType.EN_ADD_ID_KEYWORDS })
    }

    @Test fun priceSanity() {
        assertTrue(QualityChecker.check(item.copy(askPrice = 5_000_000), photos).issues.any { it.type == IssueType.PRICE_HIGH })
        assertTrue(QualityChecker.check(item.copy(askPrice = 100_000), photos).issues.any { it.type == IssueType.PRICE_LOW })
    }
}
