package com.garasi.app.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

class Converters {
    @TypeConverter fun statusToString(v: ItemStatus): String = v.name
    @TypeConverter fun stringToStatus(v: String): ItemStatus = ItemStatus.valueOf(v)
    @TypeConverter fun categoryToString(v: Category): String = v.name
    @TypeConverter fun stringToCategory(v: String): Category = runCatching { Category.valueOf(v) }.getOrDefault(Category.OTHER)
    @TypeConverter fun conditionToString(v: Condition): String = v.name
    @TypeConverter fun stringToCondition(v: String): Condition = Condition.valueOf(v)
    @TypeConverter fun langToString(v: ListingLang): String = v.name
    @TypeConverter fun stringToLang(v: String): ListingLang = ListingLang.valueOf(v)
    @TypeConverter fun platformToString(v: Platform?): String? = v?.name
    @TypeConverter fun stringToPlatform(v: String?): Platform? = v?.let { Platform.valueOf(it) }
}

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY createdAt DESC")
    fun observeItems(): Flow<List<Item>>

    @Query("SELECT * FROM items")
    suspend fun allItems(): List<Item>

    @Query("SELECT * FROM items WHERE id = :id")
    fun observeItem(id: Long): Flow<Item?>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItem(id: Long): Item?

    @Insert suspend fun insert(item: Item): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertItem(item: Item): Long
    @Update suspend fun update(item: Item)
    @Delete suspend fun delete(item: Item)

    @Query("SELECT * FROM photos ORDER BY itemId, position")
    fun observeAllPhotos(): Flow<List<Photo>>

    @Query("SELECT * FROM photos")
    suspend fun allPhotos(): List<Photo>

    @Query("SELECT * FROM photos WHERE itemId = :itemId ORDER BY position")
    fun observePhotos(itemId: Long): Flow<List<Photo>>

    @Query("SELECT * FROM photos WHERE itemId = :itemId ORDER BY position")
    suspend fun photos(itemId: Long): List<Photo>

    @Query("SELECT * FROM photos WHERE id = :id")
    suspend fun photo(id: Long): Photo?

    @Insert suspend fun insertPhoto(photo: Photo): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertPhoto(photo: Photo): Long
    @Update suspend fun updatePhoto(photo: Photo)
    @Update suspend fun updatePhotos(photos: List<Photo>)
    @Delete suspend fun deletePhoto(photo: Photo)

    @Query("SELECT * FROM listings WHERE itemId = :itemId")
    fun observeListings(itemId: Long): Flow<List<PlatformListing>>

    @Query("SELECT * FROM listings")
    fun observeAllListings(): Flow<List<PlatformListing>>

    @Query("SELECT * FROM listings")
    suspend fun allListings(): List<PlatformListing>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertListing(listing: PlatformListing)

    @Delete suspend fun deleteListing(listing: PlatformListing)

    @Transaction
    suspend fun replaceAll(items: List<Item>, photos: List<Photo>, listings: List<PlatformListing>) {
        clearItems()
        items.forEach { upsertItem(it) }
        photos.forEach { upsertPhoto(it) }
        listings.forEach { upsertListing(it) }
    }

    @Query("DELETE FROM items") suspend fun clearItems()
}

@Database(entities = [Item::class, Photo::class, PlatformListing::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class GarasiDb : RoomDatabase() {
    abstract fun dao(): ItemDao

    companion object {
        @Volatile private var instance: GarasiDb? = null
        fun get(context: Context): GarasiDb = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, GarasiDb::class.java, "garasi.db")
                .build().also { instance = it }
        }
    }
}
