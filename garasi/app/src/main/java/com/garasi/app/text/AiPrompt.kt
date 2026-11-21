package com.garasi.app.text

import com.garasi.app.data.Item
import com.garasi.app.data.ListingLang

/** Builds prompts for the free "Ask AI" hand-off to the user's own AI chat app. */
object AiPrompt {
    enum class Mode { WRITE, TRANSLATE, IMPROVE }

    private fun details(i: Item, lang: ListingLang): String {
        val rows = listOf(
            (if (lang == ListingLang.ID) "Kategori" else "Category") to ListingText.category(i.category, lang),
            (if (lang == ListingLang.ID) "Merek" else "Brand") to i.brand,
            "Model" to i.model,
            (if (lang == ListingLang.ID) "Varian (warna/ukuran/kapasitas)" else "Variant (colour/size/storage)") to i.variant,
            (if (lang == ListingLang.ID) "Kondisi" else "Condition") to ListingText.condition(i.condition, lang),
            (if (lang == ListingLang.ID) "Tahun beli" else "Year bought") to i.yearBought,
            (if (lang == ListingLang.ID) "Lama pemakaian" else "Used for") to i.usageAge,
            (if (lang == ListingLang.ID) "Minus" else "Flaws") to i.flaws,
            (if (lang == ListingLang.ID) "Kelengkapan" else "Included") to i.included,
            (if (lang == ListingLang.ID) "Alasan jual" else "Reason for selling") to i.reasonSelling,
            (if (lang == ListingLang.ID) "Lokasi" else "Location") to i.location,
            (if (lang == ListingLang.ID) "Pengiriman" else "Shipping") to i.shipping,
            "Barcode" to i.barcode,
        )
        return rows.filter { it.second.isNotBlank() }.joinToString("\n") { "- ${it.first}: ${it.second}" }
    }

    private const val FORMAT = """TITLE: ...
DESCRIPTION: ...
CONDITION: ...
USED_PRICE: Rp min – Rp max
NEW_PRICE: Rp ..."""

    private const val FORMAT_TEXT_ONLY = """TITLE: ...
DESCRIPTION: ..."""

    private val styleId = """
Gaya tulisan:
- Tulis seperti orang biasa yang jual barangnya sendiri: jujur, santai, sopan. Pakai "kamu", bukan "Anda".
- Kalimat pertama: barang apa dan kondisinya.
- Sebutkan alasan jual.
- Spesifik: lama pemakaian, minus yang sebenarnya, kelengkapan (spesifik = dipercaya).
- Pakai bullet untuk spesifikasi dan kelengkapan; kalimat pendek.
- Tutup dengan ajakan simpel, misalnya "${ListingText.closing(ListingLang.ID)}"
- Tanpa hype, tanpa klise, tanpa bahasa korporat ("solusi sempurna", "jangan lewatkan"), maksimal 2–3 emoji, tanpa HURUF KAPITAL SEMUA.
- Jangan cantumkan nomor HP atau link.
- Judul 40–70 karakter, mulai dengan merek + model, sertakan varian penting (ukuran/warna/kapasitas).
- Deskripsi 300–1.200 karakter.
Contoh gaya:
"${ListingText.EXAMPLE_ID}"
""".trim()

    private val styleEn = """
Writing style:
- Write like a real person selling their own stuff: honest, relaxed, polite. Plain conversational English.
- First line: what it is and its condition.
- Say why I'm selling it.
- Be specific: real usage age, real flaws, what's included (specific = trustworthy).
- Bullets for specs and accessories; short sentences.
- Close with a simple call to action, e.g. "${ListingText.closing(ListingLang.EN)}"
- No hype, no clichés, no corporate language ("elevate your experience", "don't miss out"), max 2–3 emoji, no ALL CAPS.
- No phone numbers or links.
- Title 40–70 characters, starting with brand + model, include the key variant (size/colour/storage).
- Description 300–1,200 characters.
Example tone:
"${ListingText.EXAMPLE_EN}"
""".trim()

    fun build(mode: Mode, item: Item, lang: ListingLang, photoCount: Int): String = when (mode) {
        Mode.WRITE -> write(item, lang, photoCount)
        Mode.TRANSLATE -> translate(item, lang)
        Mode.IMPROVE -> improve(item, lang)
    }

    private fun write(i: Item, lang: ListingLang, photoCount: Int): String = if (lang == ListingLang.ID) """
Bantu aku bikin listing barang bekas untuk dijual di Tokopedia/Shopee/OLX (Indonesia).
${if (photoCount > 0) "Aku lampirkan $photoCount foto barangnya. Cek fotonya untuk mengenali barang, varian, dan kondisinya." else ""}

Data barang:
${details(i, lang).ifBlank { "- (belum ada, kenali dari foto)" }}

$styleId

Perkirakan juga harga bekas yang wajar di Indonesia saat ini dan harga barunya.

Balas HANYA dengan format persis ini (tanpa teks lain, tanpa markdown):
$FORMAT
""".trim() else """
Help me write a second-hand listing to sell on Tokopedia/Shopee/OLX (Indonesia), written in English.
${if (photoCount > 0) "I've attached $photoCount photos of the item. Use them to identify the item, variant and condition." else ""}

Item details:
${details(i, lang).ifBlank { "- (none yet, identify from the photos)" }}

$styleEn

Also estimate a fair current second-hand price in Indonesia (in Rupiah) and the new price.

Reply ONLY in exactly this format (no other text, no markdown):
$FORMAT
""".trim()

    /** Translate from the current listing language [from] into the other language. */
    private fun translate(i: Item, from: ListingLang): String {
        val to = from.other
        val title = i.title(from)
        val desc = i.description(from)
        return if (to == ListingLang.ID) """
Terjemahkan listing barang bekas ini ke Bahasa Indonesia yang natural, kayak orang lokal jualan di Tokopedia/Shopee/OLX. Jangan terjemahan kaku/kata-per-kata. Pertahankan semua fakta (minus, kelengkapan, harga).

$styleId

Judul asli: $title
Deskripsi asli:
$desc

Balas HANYA dengan format persis ini (tanpa teks lain, tanpa markdown):
$FORMAT_TEXT_ONLY
""".trim() else """
Translate this second-hand listing into natural, friendly English — the way a local would write it, not word-for-word. Keep every fact (flaws, what's included, price) the same.

$styleEn

Original title: $title
Original description:
$desc

Reply ONLY in exactly this format (no other text, no markdown):
$FORMAT_TEXT_ONLY
""".trim()
    }

    private fun improve(i: Item, lang: ListingLang): String = if (lang == ListingLang.ID) """
Tolong perbaiki judul dan deskripsi listing barang bekas ini supaya lebih menarik tapi tetap jujur dan terdengar manusiawi. Jangan tambah fakta yang tidak ada.

Data barang:
${details(i, lang)}

$styleId

Judul sekarang: ${i.title(lang)}
Deskripsi sekarang:
${i.description(lang)}

Balas HANYA dengan format persis ini (tanpa teks lain, tanpa markdown):
$FORMAT_TEXT_ONLY
""".trim() else """
Please improve the title and description of this second-hand listing so it's more appealing but still honest and human-sounding. Don't invent facts.

Item details:
${details(i, lang)}

$styleEn

Current title: ${i.title(lang)}
Current description:
${i.description(lang)}

Reply ONLY in exactly this format (no other text, no markdown):
$FORMAT_TEXT_ONLY
""".trim()
}
