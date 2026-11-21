package com.garasi.app.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID
import kotlin.math.max
import kotlin.math.roundToInt

data class StoredPhoto(val originalPath: String, val path: String, val width: Int, val height: Int)

/**
 * Keeps an untouched original in app storage and writes a compressed (< 1 MB) listing copy.
 * Compressed copies get a fresh file name on every change so image caches never show stale pixels.
 */
class PhotoStore(private val context: Context) {
    private val dir = File(context.filesDir, "photos").apply { mkdirs() }

    companion object {
        const val MAX_SIDE = 1600
        const val MAX_BYTES = 1_000_000
    }

    suspend fun import(uri: Uri): StoredPhoto = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val original = File(dir, "orig_$id.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            original.outputStream().use { input.copyTo(it) }
        } ?: error("Cannot open image")
        writeCompressed(decodeUpright(original, MAX_SIDE), original.path)
    }

    suspend fun importFile(file: File): StoredPhoto = withContext(Dispatchers.IO) {
        val original = File(dir, "orig_${UUID.randomUUID()}.jpg")
        file.copyTo(original, overwrite = true)
        file.delete()
        writeCompressed(decodeUpright(original, MAX_SIDE), original.path)
    }

    /**
     * Crops a square from the original. [cx], [cy] are the square's centre and [side] its size,
     * all as fractions of the upright original (side is a fraction of the shorter edge).
     */
    suspend fun cropSquare(originalPath: String, oldPath: String, cx: Float, cy: Float, side: Float): StoredPhoto =
        withContext(Dispatchers.IO) {
            val src = decodeUpright(File(originalPath), MAX_SIDE * 2)
            val s = (minOf(src.width, src.height) * side.coerceIn(0.1f, 1f)).roundToInt()
            val left = (cx * src.width - s / 2f).roundToInt().coerceIn(0, src.width - s)
            val top = (cy * src.height - s / 2f).roundToInt().coerceIn(0, src.height - s)
            val cropped = Bitmap.createBitmap(src, left, top, s, s)
            val scaled = if (s > MAX_SIDE) Bitmap.createScaledBitmap(cropped, MAX_SIDE, MAX_SIDE, true) else cropped
            File(oldPath).takeIf { it.path != originalPath }?.delete()
            writeCompressed(scaled, originalPath)
        }

    fun delete(originalPath: String, path: String) {
        File(originalPath).delete(); File(path).delete()
    }

    fun newCameraFile(): File = File(context.cacheDir, "camera_${UUID.randomUUID()}.jpg")

    private fun writeCompressed(bitmap: Bitmap, originalPath: String): StoredPhoto {
        val out = File(dir, "img_${UUID.randomUUID()}.jpg")
        var quality = 90
        var bytes: ByteArray
        var bmp = bitmap
        while (true) {
            val bos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, bos)
            bytes = bos.toByteArray()
            if (bytes.size < MAX_BYTES) break
            if (quality > 60) quality -= 10
            else bmp = Bitmap.createScaledBitmap(bmp, (bmp.width * 0.85f).roundToInt(), (bmp.height * 0.85f).roundToInt(), true)
        }
        out.writeBytes(bytes)
        return StoredPhoto(originalPath, out.path, bmp.width, bmp.height)
    }

    /** Decodes a file applying EXIF rotation, scaled so the long side is at most [maxSide]. */
    fun decodeUpright(file: File, maxSide: Int): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.path, bounds)
        require(bounds.outWidth > 0) { "Not an image" }
        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxSide) sample *= 2
        val decoded = BitmapFactory.decodeFile(file.path, BitmapFactory.Options().apply { inSampleSize = sample })
            ?: error("Cannot decode image")
        val rotation = runCatching { ExifInterface(file.path).rotationDegrees }.getOrDefault(0)
        val long = max(decoded.width, decoded.height)
        val scale = if (long > maxSide) maxSide.toFloat() / long else 1f
        if (rotation == 0 && scale == 1f) return decoded
        val m = Matrix().apply { postScale(scale, scale); postRotate(rotation.toFloat()) }
        return Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, m, true)
    }
}
