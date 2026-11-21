package com.garasi.app.platform

import android.content.Context
import android.net.Uri
import com.garasi.app.data.AppSettings
import com.garasi.app.data.Category
import com.garasi.app.data.Condition
import com.garasi.app.data.GarasiDb
import com.garasi.app.data.Item
import com.garasi.app.data.ItemStatus
import com.garasi.app.data.ListingLang
import com.garasi.app.data.Photo
import com.garasi.app.data.Platform
import com.garasi.app.data.PlatformListing
import com.garasi.app.data.SettingsStore
import com.garasi.app.data.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/** Export / import everything as a zip: data.json + photos/. */
class Backup(private val context: Context, private val db: GarasiDb, private val settings: SettingsStore) {
    private val photoDir = File(context.filesDir, "photos").apply { mkdirs() }

    suspend fun export(target: Uri): Int = withContext(Dispatchers.IO) {
        val dao = db.dao()
        val items = dao.allItems()
        val photos = dao.allPhotos()
        val listings = dao.allListings()
        val json = JSONObject()
            .put("version", 1)
            .put("exportedAt", System.currentTimeMillis())
            .put("settings", settingsJson(settings.state.value))
            .put("items", JSONArray(items.map { itemJson(it) }))
            .put("photos", JSONArray(photos.map { photoJson(it) }))
            .put("listings", JSONArray(listings.map { listingJson(it) }))
        context.contentResolver.openOutputStream(target)?.use { os ->
            ZipOutputStream(os.buffered()).use { zip ->
                zip.putNextEntry(ZipEntry("data.json"))
                zip.write(json.toString(2).toByteArray())
                zip.closeEntry()
                val files = photos.flatMap { listOf(it.path, it.originalPath) }.distinct()
                files.map { File(it) }.filter { it.exists() }.forEach { f ->
                    zip.putNextEntry(ZipEntry("photos/${f.name}"))
                    f.inputStream().use { it.copyTo(zip) }
                    zip.closeEntry()
                }
            }
        } ?: error("Cannot write backup")
        items.size
    }

    /** Replaces all current data with the backup's. Returns the number of items restored. */
    suspend fun import(source: Uri): Int = withContext(Dispatchers.IO) {
        var json: JSONObject? = null
        context.contentResolver.openInputStream(source)?.use { input ->
            ZipInputStream(input.buffered()).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    val name = entry.name
                    when {
                        name == "data.json" -> json = JSONObject(zip.readBytes().decodeToString())
                        name.startsWith("photos/") && !entry.isDirectory -> {
                            val safe = File(name).name // no path traversal
                            File(photoDir, safe).outputStream().use { zip.copyTo(it) }
                        }
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
        } ?: error("Cannot read backup")
        val data = json ?: error("Backup has no data.json")
        fun local(path: String) = File(photoDir, File(path).name).path
        val items = data.getJSONArray("items").objects().map { parseItem(it) }
        val photos = data.getJSONArray("photos").objects().map { parsePhoto(it).let { p -> p.copy(path = local(p.path), originalPath = local(p.originalPath)) } }
        val listings = data.optJSONArray("listings")?.objects()?.map { parseListing(it) } ?: emptyList()
        db.dao().replaceAll(items, photos, listings)
        data.optJSONObject("settings")?.let { s -> settings.update { parseSettings(s, it) } }
        items.size
    }

    private fun JSONArray.objects(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }

    private fun settingsJson(s: AppSettings) = JSONObject()
        .put("defaultLang", s.defaultLang.name).put("city", s.city).put("theme", s.theme.name)
        .put("weightPresets", SettingsStore.encodePresets(s.weightPresets))

    private fun parseSettings(o: JSONObject, cur: AppSettings) = cur.copy(
        defaultLang = runCatching { ListingLang.valueOf(o.getString("defaultLang")) }.getOrDefault(cur.defaultLang),
        city = o.optString("city", cur.city),
        theme = runCatching { ThemeMode.valueOf(o.getString("theme")) }.getOrDefault(cur.theme),
        weightPresets = SettingsStore.decodePresets(o.optString("weightPresets", "")) ?: cur.weightPresets,
    )

    private fun itemJson(i: Item) = JSONObject()
        .put("id", i.id).put("createdAt", i.createdAt).put("updatedAt", i.updatedAt).put("status", i.status.name)
        .put("category", i.category.name).put("brand", i.brand).put("model", i.model).put("variant", i.variant)
        .put("condition", i.condition.name).put("yearBought", i.yearBought).put("usageAge", i.usageAge)
        .put("flaws", i.flaws).put("included", i.included).put("reasonSelling", i.reasonSelling)
        .put("location", i.location).put("shipping", i.shipping).put("weightGrams", i.weightGrams)
        .put("barcode", i.barcode).put("listingLang", i.listingLang.name)
        .put("titleId", i.titleId).put("descId", i.descId).put("titleEn", i.titleEn).put("descEn", i.descEn)
        .put("askPrice", i.askPrice).put("newPrice", i.newPrice).put("aiUsedMin", i.aiUsedMin).put("aiUsedMax", i.aiUsedMax)
        .put("compareLow", i.compareLow).put("compareMedian", i.compareMedian).put("compareHigh", i.compareHigh)
        .put("soldPrice", i.soldPrice).put("soldPlatform", i.soldPlatform?.name ?: "").put("soldAt", i.soldAt)

    private fun parseItem(o: JSONObject) = Item(
        id = o.getLong("id"), createdAt = o.optLong("createdAt"), updatedAt = o.optLong("updatedAt"),
        status = runCatching { ItemStatus.valueOf(o.getString("status")) }.getOrDefault(ItemStatus.DRAFT),
        category = runCatching { Category.valueOf(o.getString("category")) }.getOrDefault(Category.OTHER),
        brand = o.optString("brand"), model = o.optString("model"), variant = o.optString("variant"),
        condition = runCatching { Condition.valueOf(o.getString("condition")) }.getOrDefault(Condition.USED_GOOD),
        yearBought = o.optString("yearBought"), usageAge = o.optString("usageAge"), flaws = o.optString("flaws"),
        included = o.optString("included"), reasonSelling = o.optString("reasonSelling"),
        location = o.optString("location"), shipping = o.optString("shipping"), weightGrams = o.optInt("weightGrams"),
        barcode = o.optString("barcode"),
        listingLang = runCatching { ListingLang.valueOf(o.getString("listingLang")) }.getOrDefault(ListingLang.ID),
        titleId = o.optString("titleId"), descId = o.optString("descId"),
        titleEn = o.optString("titleEn"), descEn = o.optString("descEn"),
        askPrice = o.optLong("askPrice"), newPrice = o.optLong("newPrice"),
        aiUsedMin = o.optLong("aiUsedMin"), aiUsedMax = o.optLong("aiUsedMax"),
        compareLow = o.optLong("compareLow"), compareMedian = o.optLong("compareMedian"), compareHigh = o.optLong("compareHigh"),
        soldPrice = o.optLong("soldPrice"),
        soldPlatform = o.optString("soldPlatform").takeIf { it.isNotBlank() }?.let { runCatching { Platform.valueOf(it) }.getOrNull() },
        soldAt = o.optLong("soldAt"),
    )

    private fun photoJson(p: Photo) = JSONObject()
        .put("id", p.id).put("itemId", p.itemId).put("originalPath", p.originalPath).put("path", p.path)
        .put("position", p.position).put("isCover", p.isCover).put("isFlaw", p.isFlaw).put("angle", p.angle)
        .put("width", p.width).put("height", p.height)
        .put("sharpness", p.sharpness ?: JSONObject.NULL).put("brightness", p.brightness ?: JSONObject.NULL)
        .put("contrast", p.contrast ?: JSONObject.NULL).put("edgeDensity", p.edgeDensity ?: JSONObject.NULL)
        .put("colorVariance", p.colorVariance ?: JSONObject.NULL).put("objectFill", p.objectFill ?: JSONObject.NULL)
        .put("objectOffset", p.objectOffset ?: JSONObject.NULL).put("score", p.score ?: JSONObject.NULL)

    private fun JSONObject.optDoubleOrNull(k: String): Double? = if (isNull(k) || !has(k)) null else optDouble(k)

    private fun parsePhoto(o: JSONObject) = Photo(
        id = o.getLong("id"), itemId = o.getLong("itemId"), originalPath = o.getString("originalPath"),
        path = o.getString("path"), position = o.optInt("position"), isCover = o.optBoolean("isCover"),
        isFlaw = o.optBoolean("isFlaw"), angle = o.optString("angle"), width = o.optInt("width"), height = o.optInt("height"),
        sharpness = o.optDoubleOrNull("sharpness"), brightness = o.optDoubleOrNull("brightness"),
        contrast = o.optDoubleOrNull("contrast"), edgeDensity = o.optDoubleOrNull("edgeDensity"),
        colorVariance = o.optDoubleOrNull("colorVariance"), objectFill = o.optDoubleOrNull("objectFill"),
        objectOffset = o.optDoubleOrNull("objectOffset"),
        score = if (o.isNull("score") || !o.has("score")) null else o.optInt("score"),
    )

    private fun listingJson(l: PlatformListing) = JSONObject()
        .put("itemId", l.itemId).put("platform", l.platform.name).put("url", l.url)
        .put("postedAt", l.postedAt).put("removed", l.removed)

    private fun parseListing(o: JSONObject) = PlatformListing(
        itemId = o.getLong("itemId"), platform = Platform.valueOf(o.getString("platform")),
        url = o.optString("url"), postedAt = o.optLong("postedAt"), removed = o.optBoolean("removed"),
    )
}
