package com.garasi.app.platform

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.garasi.app.data.Category
import com.garasi.app.data.Item
import com.garasi.app.data.ListingLang
import com.garasi.app.data.Platform
import com.garasi.app.text.Templates
import java.net.URLEncoder

/** Per-platform constraints and links. No scraping, no logins: just links, intents and the clipboard. */
data class PlatformSpec(
    val platform: Platform,
    val titleMax: Int,
    val descMax: Int,
    /** Packages to try, in order (seller app first where one exists). */
    val packages: List<String>,
    /** Deep links to the add-product / sell screen. Best effort: tried only if something handles them. */
    val sellDeepLinks: List<String>,
    val sellWebUrl: String,
)

object Marketplace {
    val specs: Map<Platform, PlatformSpec> = mapOf(
        Platform.TOKOPEDIA to PlatformSpec(
            Platform.TOKOPEDIA, titleMax = 70, descMax = 2000,
            packages = listOf("com.tokopedia.sellerapp", "com.tokopedia.tkpd"),
            sellDeepLinks = listOf("tokopedia://seller/add-product", "sellerapp://product/add", "tokopedia://product-add"),
            sellWebUrl = "https://seller.tokopedia.com/add-product",
        ),
        Platform.SHOPEE to PlatformSpec(
            Platform.SHOPEE, titleMax = 120, descMax = 3000,
            packages = listOf("com.shopee.id"),
            sellDeepLinks = listOf("shopeeid://main?apprl=%2Frn%2FSELLER_ADD_PRODUCT"),
            sellWebUrl = "https://seller.shopee.co.id/portal/product/new",
        ),
        Platform.OLX to PlatformSpec(
            Platform.OLX, titleMax = 70, descMax = 4000,
            packages = listOf("com.app.tokobagus.betterb"),
            sellDeepLinks = listOf("olxid://post", "olx://post"),
            sellWebUrl = "https://www.olx.co.id/post",
        ),
    )

    fun spec(p: Platform) = specs.getValue(p)

    /** Search links, sorted by lowest price where the URL supports it. */
    fun searchUrl(p: Platform, query: String): String {
        val q = URLEncoder.encode(query.trim(), "UTF-8")
        return when (p) {
            Platform.TOKOPEDIA -> "https://www.tokopedia.com/search?st=product&q=$q&ob=3"
            Platform.SHOPEE -> "https://shopee.co.id/search?keyword=$q&order=asc&sortBy=price"
            Platform.OLX -> {
                val slug = query.trim().lowercase().replace(Regex("[^\\p{L}\\p{N}]+"), "-").trim('-')
                "https://www.olx.co.id/items/q-$slug?sorting=asc-price"
            }
        }
    }

    fun searchQuery(item: Item): String =
        listOf(item.brand, item.model, item.variant).filter { it.isNotBlank() }.joinToString(" ")
            .ifBlank { item.displayName }

    /** Category path hint shown on the post screen (the user picks it in the marketplace app). */
    fun categoryHint(p: Platform, c: Category): String = when (p) {
        Platform.TOKOPEDIA, Platform.SHOPEE -> when (c) {
            Category.ELECTRONICS -> "Elektronik / Handphone & Tablet / Komputer & Laptop / Audio / Kamera"
            Category.FASHION -> "Fashion Pria / Fashion Wanita / Sepatu / Tas"
            Category.BOOKS -> "Buku"
            Category.TOYS -> "Mainan & Hobi / Ibu & Bayi"
            Category.HOME -> "Rumah Tangga / Dapur / Perlengkapan Rumah"
            Category.HOBBY -> "Hobi & Koleksi / Olahraga / Alat Musik"
            Category.OTHER -> "Lainnya"
        }
        Platform.OLX -> when (c) {
            Category.ELECTRONICS -> "Elektronik & Gadget"
            Category.FASHION -> "Keperluan Pribadi / Fashion"
            Category.BOOKS -> "Hobi & Olahraga / Buku & Majalah"
            Category.TOYS -> "Perlengkapan Bayi & Anak / Mainan"
            Category.HOME -> "Rumah Tangga"
            Category.HOBBY -> "Hobi & Olahraga"
            Category.OTHER -> "Lainnya"
        }
    }

    fun formatTitle(p: Platform, item: Item, lang: ListingLang): String {
        val t = item.title(lang).trim().replace(Regex("\\s+"), " ")
        val max = spec(p).titleMax
        return if (t.length <= max) t else Templates.fitWords(t.split(" "), max)
    }

    fun formatDescription(p: Platform, item: Item, lang: ListingLang): String {
        val d = item.description(lang).trim()
        val max = spec(p).descMax
        return if (d.length <= max) d else d.take(max - 1).substringBeforeLast('\n').ifBlank { d.take(max) }
    }

    const val DIVIDER = "\n\n────────────\n\n"

    fun bothLanguages(p: Platform, item: Item): String {
        val id = formatDescription(p, item, ListingLang.ID)
        val en = formatDescription(p, item, ListingLang.EN)
        val combined = when {
            id.isBlank() -> en
            en.isBlank() -> id
            else -> id + DIVIDER + en
        }
        val max = spec(p).descMax
        return if (combined.length <= max) combined else combined.take(max)
    }

    /**
     * Opens the marketplace at its sell screen if a known deep link resolves, else the app's home screen,
     * else the website. Returns which one was opened.
     */
    enum class Opened { DEEP_LINK, APP, WEB, NOTHING }

    fun openSell(context: Context, p: Platform): Opened {
        val spec = spec(p)
        val pm = context.packageManager
        val installed = spec.packages.firstOrNull { pkg -> pm.getLaunchIntentForPackage(pkg) != null }
        if (installed != null) {
            for (link in spec.sellDeepLinks) {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(link)).setPackage(installed)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (i.resolveActivity(pm) != null && start(context, i)) return Opened.DEEP_LINK
            }
            pm.getLaunchIntentForPackage(installed)?.let {
                if (start(context, it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))) return Opened.APP
            }
        }
        return if (openUrl(context, spec.sellWebUrl)) Opened.WEB else Opened.NOTHING
    }

    fun openUrl(context: Context, url: String): Boolean =
        start(context, Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

    private fun start(context: Context, intent: Intent): Boolean = try {
        context.startActivity(intent); true
    } catch (_: ActivityNotFoundException) { false } catch (_: SecurityException) { false }
}
