package com.garasi.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ItemStatus { DRAFT, READY, POSTED, SOLD }

enum class Category { ELECTRONICS, FASHION, BOOKS, TOYS, HOME, HOBBY, OTHER }

enum class Condition { NEW, LIKE_NEW, USED_GOOD, USED_FLAWS }

enum class ListingLang { ID, EN;
    val other: ListingLang get() = if (this == ID) EN else ID
}

enum class Platform { TOKOPEDIA, SHOPEE, OLX }

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val status: ItemStatus = ItemStatus.DRAFT,
    val category: Category = Category.OTHER,
    val brand: String = "",
    val model: String = "",
    val variant: String = "",
    val condition: Condition = Condition.USED_GOOD,
    val yearBought: String = "",
    val usageAge: String = "",
    val flaws: String = "",
    val included: String = "",
    val reasonSelling: String = "",
    val location: String = "",
    val shipping: String = "",
    val weightGrams: Int = 0,
    val barcode: String = "",
    val listingLang: ListingLang = ListingLang.ID,
    val titleId: String = "",
    val descId: String = "",
    val titleEn: String = "",
    val descEn: String = "",
    val askPrice: Long = 0,
    val newPrice: Long = 0,
    val aiUsedMin: Long = 0,
    val aiUsedMax: Long = 0,
    val compareLow: Long = 0,
    val compareMedian: Long = 0,
    val compareHigh: Long = 0,
    val soldPrice: Long = 0,
    val soldPlatform: Platform? = null,
    val soldAt: Long = 0,
) {
    fun title(lang: ListingLang = listingLang) = if (lang == ListingLang.ID) titleId else titleEn
    fun description(lang: ListingLang = listingLang) = if (lang == ListingLang.ID) descId else descEn
    fun withText(lang: ListingLang, title: String? = null, desc: String? = null): Item =
        if (lang == ListingLang.ID) copy(titleId = title ?: titleId, descId = desc ?: descId)
        else copy(titleEn = title ?: titleEn, descEn = desc ?: descEn)

    val displayName: String
        get() = title().ifBlank { title(listingLang.other) }
            .ifBlank { listOf(brand, model).filter { it.isNotBlank() }.joinToString(" ") }
}

@Entity(
    tableName = "photos",
    foreignKeys = [ForeignKey(
        entity = Item::class, parentColumns = ["id"], childColumns = ["itemId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("itemId")],
)
data class Photo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: Long,
    /** Untouched copy of the original image (app-private storage). */
    val originalPath: String,
    /** Compressed (<1 MB), possibly square-cropped image used for listing. */
    val path: String,
    val position: Int,
    val isCover: Boolean = false,
    val isFlaw: Boolean = false,
    val angle: String = "",
    // Quality metrics (null until analysed)
    val width: Int = 0,
    val height: Int = 0,
    val sharpness: Double? = null,
    val brightness: Double? = null,
    val contrast: Double? = null,
    val edgeDensity: Double? = null,
    val colorVariance: Double? = null,
    val objectFill: Double? = null,
    val objectOffset: Double? = null,
    val score: Int? = null,
)

@Entity(
    tableName = "listings",
    primaryKeys = ["itemId", "platform"],
    foreignKeys = [ForeignKey(
        entity = Item::class, parentColumns = ["id"], childColumns = ["itemId"],
        onDelete = ForeignKey.CASCADE,
    )],
)
data class PlatformListing(
    val itemId: Long,
    val platform: Platform,
    val url: String = "",
    val postedAt: Long = System.currentTimeMillis(),
    val removed: Boolean = false,
)
