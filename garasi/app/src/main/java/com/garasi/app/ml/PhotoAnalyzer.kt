package com.garasi.app.ml

import android.graphics.Bitmap
import android.graphics.Rect
import com.garasi.app.data.Photo
import com.garasi.app.photo.PhotoStore
import com.garasi.app.quality.QualityChecker
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.sqrt

/**
 * On-device photo quality metrics:
 *  - sharpness: variance of the Laplacian on a 512 px greyscale copy
 *  - brightness / contrast: mean and standard deviation of luminance
 *  - framing: ML Kit object detection → largest object's fill ratio and distance from centre
 *  - background: edge density + colour variance outside the object (or outside the centre box)
 */
class PhotoAnalyzer(private val store: PhotoStore) {
    private val detector by lazy {
        ObjectDetection.getClient(
            ObjectDetectorOptions.Builder()
                .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                .enableMultipleObjects()
                .build()
        )
    }

    suspend fun analyze(photo: Photo): Photo = withContext(Dispatchers.Default) {
        val file = File(photo.path)
        val full = store.decodeUpright(file, 1024)
        val small = scaleTo(full, 512)
        val w = small.width
        val h = small.height
        val px = IntArray(w * h)
        small.getPixels(px, 0, w, 0, 0, w, h)
        val gray = DoubleArray(px.size) { i ->
            val c = px[i]
            0.299 * ((c shr 16) and 0xFF) + 0.587 * ((c shr 8) and 0xFF) + 0.114 * (c and 0xFF)
        }

        // Brightness / contrast
        val mean = gray.average()
        val contrast = sqrt(gray.sumOf { (it - mean) * (it - mean) } / gray.size)

        // Laplacian variance
        var lapSum = 0.0
        var lapSq = 0.0
        var n = 0
        for (y in 1 until h - 1) for (x in 1 until w - 1) {
            val i = y * w + x
            val lap = gray[i - 1] + gray[i + 1] + gray[i - w] + gray[i + w] - 4 * gray[i]
            lapSum += lap; lapSq += lap * lap; n++
        }
        val lapMean = lapSum / n
        val sharpness = lapSq / n - lapMean * lapMean

        // Object detection for framing
        val objectBox: Rect? = runCatching {
            val objs = detector.process(InputImage.fromBitmap(full, 0)).await()
            objs.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() }?.boundingBox
        }.getOrNull()
        val fill: Double?
        val offset: Double?
        if (objectBox != null) {
            fill = (objectBox.width().toDouble() * objectBox.height()) / (full.width.toDouble() * full.height)
            val dx = objectBox.exactCenterX() - full.width / 2.0
            val dy = objectBox.exactCenterY() - full.height / 2.0
            offset = hypot(dx, dy) / (hypot(full.width.toDouble(), full.height.toDouble()) / 2)
        } else {
            fill = null; offset = null
        }

        // Background region: outside the object box (scaled), or outside the centre 60 % box.
        val sx = w.toDouble() / full.width
        val sy = h.toDouble() / full.height
        val inner = objectBox?.let {
            Rect((it.left * sx).toInt(), (it.top * sy).toInt(), (it.right * sx).toInt(), (it.bottom * sy).toInt())
        } ?: Rect((w * 0.2).toInt(), (h * 0.2).toInt(), (w * 0.8).toInt(), (h * 0.8).toInt())
        var edges = 0
        var bgCount = 0
        val rs = ArrayList<Int>(); val gs = ArrayList<Int>(); val bs = ArrayList<Int>()
        for (y in 1 until h - 1) for (x in 1 until w - 1) {
            if (inner.contains(x, y)) continue
            val i = y * w + x
            val gx = abs(gray[i + 1] - gray[i - 1])
            val gy = abs(gray[i + w] - gray[i - w])
            if (gx + gy > 60) edges++
            bgCount++
            if ((x + y) % 3 == 0) {
                val c = px[i]; rs += (c shr 16) and 0xFF; gs += (c shr 8) and 0xFF; bs += c and 0xFF
            }
        }
        val edgeDensity = if (bgCount > 0) edges.toDouble() / bgCount else 0.0
        val colorVar = if (rs.isEmpty()) 0.0 else (std(rs) + std(gs) + std(bs)) / 3

        val bounds = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
        android.graphics.BitmapFactory.decodeFile(photo.path, bounds)

        val updated = photo.copy(
            width = bounds.outWidth, height = bounds.outHeight,
            sharpness = sharpness, brightness = mean, contrast = contrast,
            edgeDensity = edgeDensity, colorVariance = colorVar,
            objectFill = fill, objectOffset = offset,
        )
        updated.copy(score = QualityChecker.photoScore(updated))
    }

    private fun std(v: List<Int>): Double {
        val m = v.average()
        return sqrt(v.sumOf { (it - m) * (it - m) } / v.size)
    }

    private fun scaleTo(b: Bitmap, maxSide: Int): Bitmap {
        val long = maxOf(b.width, b.height)
        if (long <= maxSide) return b
        val s = maxSide.toFloat() / long
        return Bitmap.createScaledBitmap(b, (b.width * s).toInt().coerceAtLeast(1), (b.height * s).toInt().coerceAtLeast(1), true)
    }
}
