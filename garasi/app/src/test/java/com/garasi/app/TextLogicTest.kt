package com.garasi.app

import com.garasi.app.data.Category
import com.garasi.app.data.Condition
import com.garasi.app.data.Item
import com.garasi.app.data.ListingLang
import com.garasi.app.ml.ItemIdentifier
import com.garasi.app.platform.Marketplace
import com.garasi.app.data.Platform
import com.garasi.app.text.AiAnswerParser
import com.garasi.app.text.AiPrompt
import com.garasi.app.text.LangGuess
import com.garasi.app.text.Money
import com.garasi.app.text.PriceCalculator
import com.garasi.app.text.Templates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TextLogicTest {
    private val sony = Item(
        brand = "Sony", model = "WH-1000XM4", variant = "Hitam", condition = Condition.USED_FLAWS,
        category = Category.ELECTRONICS, usageAge = "±1,5 tahun", flaws = "busa kiri agak ngelupas",
        included = "box, kabel charger, kabel audio, pouch", reasonSelling = "udah jarang dipake sejak WFO",
        location = "Jakarta Selatan", yearBought = "2022",
    )

    @Test fun moneyFormatAndParse() {
        assertEquals("Rp 1.250.000", Money.format(1_250_000))
        assertEquals(1_250_000L, Money.parse("Rp 1.250.000"))
        assertEquals(1_250_000L, Money.parse("Rp1.250.000,00"))
        assertEquals(1_500_000L, Money.parse("1,5 jt"))
        assertEquals(1_500_000L, Money.parse("1.5jt"))
        assertEquals(850_000L, Money.parse("850rb"))
        assertEquals(850_000L, Money.parse("850k"))
        assertEquals(2_000_000L, Money.parse("2 juta"))
        assertNull(Money.parse("nego"))
        assertEquals(1_255_000L, Money.roundNice(1_253_400.0))
    }

    @Test fun priceSuggestion() {
        val s = PriceCalculator.suggest(2_000_000, Condition.USED_GOOD)!!
        assertEquals(1_300_000L, s.fairPrice)       // 2,000,000 × 0.65
        assertEquals(1_430_000L, s.askPrice)        // + 10 %
        assertEquals(1_600_000L, PriceCalculator.suggest(2_000_000, Condition.LIKE_NEW)!!.fairPrice)
        assertEquals(900_000L, PriceCalculator.suggest(2_000_000, Condition.USED_FLAWS)!!.fairPrice)
        assertNull(PriceCalculator.suggest(0, Condition.USED_GOOD))
        assertEquals(150L, PriceCalculator.effectiveMedian(100, 0, 200))
    }

    @Test fun titleBuilderRespectsLimitAndOrder() {
        val t = Templates.buildTitle(sony, ListingLang.ID)
        assertTrue(t.startsWith("Sony WH-1000XM4 Hitam"))
        assertTrue(t.length <= 70)
        val long = sony.copy(variant = "Hitam Edisi Spesial Kolektor Dengan Tambahan Banyak Sekali Kata Panjang")
        assertTrue(Templates.buildTitle(long).length <= 70)
    }

    @Test fun templatesHaveBlanksOnlyWhenMissing() {
        val id = Templates.description(sony, ListingLang.ID)
        assertTrue(id.contains("Bisa nego tipis, langsung chat aja ya 🙏"))
        val en = Templates.description(Item(brand = "Sony"), ListingLang.EN)
        assertTrue(Templates.hasBlanks(en))
        assertTrue(en.contains("Price slightly negotiable, just send me a message 🙏"))
    }

    @Test fun parsesStrictAiAnswer() {
        val raw = """
            TITLE: Sony WH-1000XM4 Hitam Bekas Mulus
            DESCRIPTION: Jual Sony WH-1000XM4 hitam, pemakaian ±1,5 tahun.

            • Kondisi: normal
            • Kelengkapan: box, pouch
            CONDITION: Bekas – Ada Minus
            USED_PRICE: Rp 1.800.000 – Rp 2.200.000
            NEW_PRICE: Rp 4.999.000
        """.trimIndent()
        val a = AiAnswerParser.parse(raw)
        assertEquals("Sony WH-1000XM4 Hitam Bekas Mulus", a.title)
        assertTrue(a.description!!.contains("• Kondisi: normal"))
        assertTrue(a.description!!.contains("Kelengkapan"))
        assertEquals(Condition.USED_FLAWS, a.condition)
        assertEquals(1_800_000L, a.usedMin)
        assertEquals(2_200_000L, a.usedMax)
        assertEquals(4_999_000L, a.newPrice)
    }

    @Test fun parsesMarkdownishAiAnswer() {
        val a = AiAnswerParser.parse("**TITLE:** iPhone 11 64GB\n**DESCRIPTION:** Selling my phone.\n**USED_PRICE:** 3,5jt - 4jt")
        assertEquals("iPhone 11 64GB", a.title)
        assertEquals("Selling my phone.", a.description)
        assertEquals(3_500_000L, a.usedMin)
        assertEquals(4_000_000L, a.usedMax)
        assertTrue(AiAnswerParser.parse("hello there").isEmpty)
    }

    @Test fun promptContainsFormatAndExample() {
        val p = AiPrompt.build(AiPrompt.Mode.WRITE, sony, ListingLang.ID, 3)
        assertTrue(p.contains("USED_PRICE: Rp min – Rp max"))
        assertTrue(p.contains("WH-1000XM4"))
        assertTrue(p.contains("kamu"))
        val t = AiPrompt.build(AiPrompt.Mode.TRANSLATE, sony.copy(titleId = "Judul", descId = "Deskripsi"), ListingLang.ID, 0)
        assertTrue(t.contains("natural"))
        assertFalse(t.contains("USED_PRICE"))
    }

    @Test fun languageGuess() {
        assertEquals(ListingLang.ID, LangGuess.guess(ListingTextSamples.ID))
        assertEquals(ListingLang.EN, LangGuess.guess(ListingTextSamples.EN))
    }

    @Test fun identifierHeuristics() {
        assertEquals("Sony", ItemIdentifier.guessBrand("SONY Wireless Noise Cancelling\nWH-1000XM4\nMade in Malaysia"))
        assertEquals("WH-1000XM4", ItemIdentifier.guessModel("SONY Wireless\nWH-1000XM4 5V 1.5A\nWH-1000XM4"))
        assertEquals(Category.ELECTRONICS, ItemIdentifier.guessCategory(mapOf("Mobile phone" to 0.9f, "Hand" to 0.7f)))
    }

    @Test fun marketplaceLinks() {
        assertTrue(Marketplace.searchUrl(Platform.TOKOPEDIA, "sony wh-1000xm4").contains("q=sony+wh-1000xm4"))
        assertTrue(Marketplace.searchUrl(Platform.OLX, "Sony WH-1000XM4").contains("q-sony-wh-1000xm4"))
        val both = Marketplace.bothLanguages(Platform.SHOPEE, sony.copy(descId = "Halo", descEn = "Hello"))
        assertEquals("Halo" + Marketplace.DIVIDER + "Hello", both)
        assertNotNull(Marketplace.spec(Platform.OLX))
    }
}

object ListingTextSamples {
    const val ID = "Jual Sony WH-1000XM4 hitam, pemakaian ±1,5 tahun. Suara masih mantap, noise cancelling normal. " +
        "Kelengkapan: box, kabel. Dijual karena udah jarang dipake. Bisa nego tipis, langsung chat aja ya"
    const val EN = "Selling my black Sony WH-1000XM4, used for about 1.5 years. Sound is still great. Comes with box " +
        "and cable. Selling because I rarely use it. Price slightly negotiable, just send me a message"
}
