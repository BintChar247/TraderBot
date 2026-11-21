package com.garasi.app.platform

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object Clipboard {
    fun copy(context: Context, label: String, text: String) {
        val cm = context.getSystemService(ClipboardManager::class.java)
        cm.setPrimaryClip(ClipData.newPlainText(label, text))
    }

    /** Returns clipboard text, or null when empty / not text. */
    fun read(context: Context): String? {
        val cm = context.getSystemService(ClipboardManager::class.java)
        val clip = cm.primaryClip ?: return null
        if (clip.itemCount == 0) return null
        return clip.getItemAt(0).coerceToText(context)?.toString()?.takeIf { it.isNotBlank() }
    }
}

object ShareHelper {
    // Known AI chat apps, shown first in the share sheet when installed.
    val AI_PACKAGES = listOf("com.anthropic.claude", "com.google.android.apps.bard", "com.openai.chatgpt")

    fun uriFor(context: Context, path: String): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.files", File(path))

    /**
     * Opens the Android share sheet with [text] and optional photos so the user can pick Claude,
     * Gemini or ChatGPT. Returns false when no app can receive it.
     */
    fun shareToAi(context: Context, text: String, photoPaths: List<String>, chooserTitle: String): Boolean {
        val uris = ArrayList(photoPaths.map { uriFor(context, it) })
        val send = if (uris.isEmpty()) {
            Intent(Intent.ACTION_SEND).setType("text/plain")
        } else {
            Intent(Intent.ACTION_SEND_MULTIPLE).setType("image/*")
                .putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .also { i ->
                    i.clipData = ClipData.newRawUri("photo", uris.first()).apply {
                        uris.drop(1).forEach { addItem(ClipData.Item(it)) }
                    }
                }
        }.putExtra(Intent.EXTRA_TEXT, text)

        if (send.resolveActivity(context.packageManager) == null &&
            context.packageManager.queryIntentActivities(send, 0).isEmpty()) return false

        val pm = context.packageManager
        val preferred = AI_PACKAGES.mapNotNull { pkg ->
            Intent(send).setPackage(pkg).takeIf { pm.queryIntentActivities(it, 0).isNotEmpty() }
        }
        val chooser = Intent.createChooser(send, chooserTitle).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (preferred.isNotEmpty()) chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, preferred.toTypedArray())
        return try { context.startActivity(chooser); true } catch (_: ActivityNotFoundException) { false }
    }

    fun hasAnyAiApp(context: Context): Boolean = AI_PACKAGES.any {
        context.packageManager.getLaunchIntentForPackage(it) != null
    }
}

/** Saves listing photos into a "Pictures/Garasi/<item>" gallery album. */
object GallerySaver {
    fun albumName(itemName: String): String =
        itemName.replace(Regex("[^\\p{L}\\p{N} _-]"), "").trim().take(40).ifBlank { "Item" }

    suspend fun save(context: Context, itemName: String, photoPaths: List<String>): Int = withContext(Dispatchers.IO) {
        val album = albumName(itemName)
        var saved = 0
        photoPaths.forEachIndexed { index, path ->
            val src = File(path)
            if (!src.exists()) return@forEachIndexed
            val name = "${album.replace(' ', '_')}_${index + 1}.jpg"
            val ok = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val relative = "${Environment.DIRECTORY_PICTURES}/Garasi/$album"
                // Replace any earlier copy with the same name so re-posting doesn't duplicate.
                resolver.delete(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "${MediaStore.Images.Media.RELATIVE_PATH}=? AND ${MediaStore.Images.Media.DISPLAY_NAME}=?",
                    arrayOf("$relative/", name),
                )
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, name)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, relative)
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                if (uri == null) false else {
                    resolver.openOutputStream(uri)?.use { out -> src.inputStream().use { it.copyTo(out) } }
                    values.clear(); values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)
                    true
                }
            } else {
                @Suppress("DEPRECATION")
                val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Garasi/$album")
                dir.mkdirs()
                val dest = File(dir, name)
                runCatching { src.copyTo(dest, overwrite = true) }.isSuccess.also {
                    if (it) MediaScannerConnection.scanFile(context, arrayOf(dest.path), arrayOf("image/jpeg"), null)
                }
            }
            if (ok) saved++
        }
        saved
    }
}
