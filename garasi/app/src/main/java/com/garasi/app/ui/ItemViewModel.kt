package com.garasi.app.ui

import android.app.Application
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.garasi.app.GarasiApp
import com.garasi.app.R
import com.garasi.app.data.Item
import com.garasi.app.data.ItemStatus
import com.garasi.app.data.ListingLang
import com.garasi.app.data.Photo
import com.garasi.app.data.Platform
import com.garasi.app.data.PlatformListing
import com.garasi.app.ml.IdentifyResult
import com.garasi.app.quality.QualityChecker
import com.garasi.app.quality.QualityReport
import com.garasi.app.text.AiAnswer
import com.garasi.app.text.AiAnswerParser
import com.garasi.app.text.Templates
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

/** A user-visible message: a string resource plus format args. */
data class UiMessage(@StringRes val res: Int, val args: List<Any> = emptyList())

enum class Field { TITLE, DESCRIPTION, DETAILS, PRICE }

/**
 * One ViewModel per item, shared by the edit screen and its sub-screens (photos, quality, price, post).
 * The in-memory [item] is the source of truth while editing; every change is written to Room in order.
 */
class ItemViewModel(app: Application, val itemId: Long) : AndroidViewModel(app) {
    private val g = app as GarasiApp
    private val dao = g.db.dao()
    private val writeLock = Mutex()

    private val _item = MutableStateFlow<Item?>(null)
    val item: StateFlow<Item?> = _item.asStateFlow()

    /** Snapshot-state mirror of [item] so text fields update synchronously (no dropped keystrokes). */
    var current by mutableStateOf<Item?>(null)
        private set

    private fun setItem(i: Item?) { _item.value = i; current = i }

    val photos: StateFlow<List<Photo>> =
        dao.observePhotos(itemId).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val listings: StateFlow<List<PlatformListing>> =
        dao.observeListings(itemId).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val report: StateFlow<QualityReport?> = combine(_item.filterNotNull(), photos) { i, p -> QualityChecker.check(i, p) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _messages = MutableSharedFlow<UiMessage>(extraBufferCapacity = 8)
    val messages = _messages.asSharedFlow()

    val busy = MutableStateFlow(false)
    val identifyResult = MutableStateFlow<IdentifyResult?>(null)
    val focus = MutableStateFlow<Field?>(null)

    /** The last prompt handed to the AI app, and where a pasted answer should go. */
    var lastPrompt: String? = null
    var aiTargetLang: ListingLang? = null

    init {
        viewModelScope.launch {
            setItem(dao.getItem(itemId))
            // Analyse any photos that were imported but never scored (e.g. app killed mid-way).
            photos.first { true }
            analyzePending()
        }
    }

    fun say(@StringRes res: Int, vararg args: Any) { _messages.tryEmit(UiMessage(res, args.toList())) }

    fun update(transform: (Item) -> Item) {
        val cur = _item.value ?: return
        val next = transform(cur).copy(updatedAt = System.currentTimeMillis())
        if (next == cur) return
        setItem(next)
        viewModelScope.launch { writeLock.withLock { dao.update(next) } }
    }

    // ------------------------------------------------------------------ photos

    fun addPhotos(uris: List<Uri>) = viewModelScope.launch {
        val room = MAX_PHOTOS - photos.value.size
        if (room <= 0) { say(R.string.msg_max_photos, MAX_PHOTOS); return@launch }
        if (uris.size > room) say(R.string.msg_max_photos, MAX_PHOTOS)
        busy.value = true
        try {
            uris.take(room).forEach { uri ->
                runCatching { g.photoStore.import(uri) }
                    .onSuccess { insertPhoto(it.originalPath, it.path, it.width, it.height) }
                    .onFailure { say(R.string.msg_photo_import_failed) }
            }
        } finally { busy.value = false }
        analyzePending()
    }

    fun addCameraFile(file: File) = viewModelScope.launch {
        if (photos.value.size >= MAX_PHOTOS) { say(R.string.msg_max_photos, MAX_PHOTOS); return@launch }
        runCatching { g.photoStore.importFile(file) }
            .onSuccess { insertPhoto(it.originalPath, it.path, it.width, it.height) }
            .onFailure { say(R.string.msg_photo_import_failed) }
        analyzePending()
    }

    private suspend fun insertPhoto(original: String, path: String, w: Int, h: Int) = writeLock.withLock {
        val existing = dao.photos(itemId)
        val wantFlaw = pendingFlawShot
        dao.insertPhoto(
            Photo(
                itemId = itemId, originalPath = original, path = path, position = existing.size,
                isCover = existing.isEmpty(), width = w, height = h,
                isFlaw = wantFlaw, angle = if (wantFlaw) "flaw" else "",
            )
        )
    }

    /** When true, the next photos taken are tagged as flaw close-ups. */
    var pendingFlawShot = false

    private suspend fun analyzePending() {
        val pending = dao.photos(itemId).filter { it.sharpness == null }
        for (p in pending) {
            val analysed = runCatching { g.analyzer.analyze(p) }.getOrNull() ?: continue
            writeLock.withLock {
                // Re-read so we don't clobber edits made while analysing.
                val fresh = dao.photo(p.id) ?: return@withLock
                if (fresh.path == p.path) dao.updatePhoto(
                    fresh.copy(
                        width = analysed.width, height = analysed.height, sharpness = analysed.sharpness,
                        brightness = analysed.brightness, contrast = analysed.contrast, edgeDensity = analysed.edgeDensity,
                        colorVariance = analysed.colorVariance, objectFill = analysed.objectFill,
                        objectOffset = analysed.objectOffset, score = analysed.score,
                    )
                )
            }
        }
    }

    fun deletePhoto(photo: Photo) = viewModelScope.launch {
        writeLock.withLock {
            dao.deletePhoto(photo)
            g.photoStore.delete(photo.originalPath, photo.path)
            val rest = dao.photos(itemId).mapIndexed { i, p -> p.copy(position = i) }
            val fixed = if (rest.isNotEmpty() && rest.none { it.isCover })
                rest.mapIndexed { i, p -> p.copy(isCover = i == 0) } else rest
            dao.updatePhotos(fixed)
        }
    }

    fun move(photo: Photo, delta: Int) = viewModelScope.launch {
        writeLock.withLock {
            val list = dao.photos(itemId).toMutableList()
            val from = list.indexOfFirst { it.id == photo.id }
            val to = (from + delta).coerceIn(0, list.lastIndex)
            if (from < 0 || from == to) return@withLock
            list.add(to, list.removeAt(from))
            // The first photo is always the cover (that's what marketplaces use).
            dao.updatePhotos(list.mapIndexed { i, p -> p.copy(position = i, isCover = i == 0) })
        }
    }

    fun makeCover(photoId: Long) = viewModelScope.launch {
        writeLock.withLock {
            val list = dao.photos(itemId).toMutableList()
            val idx = list.indexOfFirst { it.id == photoId }
            if (idx < 0) return@withLock
            list.add(0, list.removeAt(idx))
            dao.updatePhotos(list.mapIndexed { i, p -> p.copy(position = i, isCover = i == 0) })
        }
        say(R.string.msg_cover_set)
    }

    fun toggleFlaw(photo: Photo) = viewModelScope.launch {
        writeLock.withLock {
            val f = dao.photo(photo.id) ?: return@withLock
            val nowFlaw = !f.isFlaw
            dao.updatePhoto(f.copy(isFlaw = nowFlaw, angle = if (nowFlaw) "flaw" else if (f.angle == "flaw") "" else f.angle))
        }
    }

    fun setAngle(photo: Photo, angle: String) = viewModelScope.launch {
        writeLock.withLock {
            val f = dao.photo(photo.id) ?: return@withLock
            dao.updatePhoto(f.copy(angle = angle, isFlaw = angle == "flaw" || (f.isFlaw && angle.isEmpty())))
        }
    }

    fun crop(photo: Photo, cx: Float, cy: Float, side: Float, onDone: () -> Unit) = viewModelScope.launch {
        busy.value = true
        try {
            val stored = g.photoStore.cropSquare(photo.originalPath, photo.path, cx, cy, side)
            writeLock.withLock {
                val f = dao.photo(photo.id) ?: return@withLock
                dao.updatePhoto(f.copy(path = stored.path, width = stored.width, height = stored.height, sharpness = null, score = null))
            }
            analyzePending()
            onDone()
        } catch (e: Exception) {
            say(R.string.msg_crop_failed)
        } finally { busy.value = false }
    }

    // ------------------------------------------------------------------ identify

    fun identify() = viewModelScope.launch {
        val paths = photos.value.map { it.path }
        if (paths.isEmpty()) { say(R.string.msg_identify_no_photos); return@launch }
        busy.value = true
        try {
            val r = g.identifier.identify(paths)
            identifyResult.value = r
            if (r.isEmpty) say(R.string.msg_identify_nothing)
        } catch (e: Exception) {
            say(R.string.msg_identify_failed)
        } finally { busy.value = false }
    }

    fun applyIdentify(r: IdentifyResult) {
        update { i ->
            i.copy(
                brand = r.brand ?: i.brand,
                model = r.model ?: i.model,
                category = r.category ?: i.category,
                barcode = r.barcode ?: i.barcode,
            )
        }
        identifyResult.value = null
    }

    // ------------------------------------------------------------------ text

    fun buildTitle(lang: ListingLang? = null) = update { i ->
        val l = lang ?: i.listingLang
        i.withText(l, title = Templates.buildTitle(i, l))
    }

    fun useTemplate(lang: ListingLang? = null) = update { i ->
        val l = lang ?: i.listingLang
        i.withText(l, desc = Templates.description(i, l))
    }

    /**
     * Applies a pasted AI reply. Text goes into [lang] (the target language for "Translate", the
     * current one otherwise). Returns false when nothing could be parsed.
     */
    fun applyAiAnswer(raw: String, lang: ListingLang): AiAnswer? {
        val a = AiAnswerParser.parse(raw)
        if (a.isEmpty) return null
        update { i ->
            var n = i.withText(lang, title = a.title, desc = a.description)
            a.condition?.let { n = n.copy(condition = it) }
            a.usedMin?.let { n = n.copy(aiUsedMin = it) }
            a.usedMax?.let { n = n.copy(aiUsedMax = it) }
            a.newPrice?.let { n = n.copy(newPrice = it) }
            if (n.askPrice == 0L && a.usedMin != null && a.usedMax != null) n = n.copy(askPrice = a.usedMax)
            n
        }
        return a
    }

    // ------------------------------------------------------------------ status / posting

    fun markPosted(platform: Platform, url: String) = viewModelScope.launch {
        writeLock.withLock { dao.upsertListing(PlatformListing(itemId, platform, url.trim())) }
        update { if (it.status == ItemStatus.SOLD) it else it.copy(status = ItemStatus.POSTED) }
    }

    fun unmarkPosted(listing: PlatformListing) = viewModelScope.launch {
        writeLock.withLock { dao.deleteListing(listing) }
        if (listings.value.size <= 1) update { if (it.status == ItemStatus.POSTED) it.copy(status = ItemStatus.READY) else it }
    }

    fun setListingRemoved(listing: PlatformListing, removed: Boolean) = viewModelScope.launch {
        writeLock.withLock { dao.upsertListing(listing.copy(removed = removed)) }
    }

    fun markSold(price: Long, platform: Platform?) = update {
        it.copy(status = ItemStatus.SOLD, soldPrice = price, soldPlatform = platform, soldAt = System.currentTimeMillis())
    }

    fun unmarkSold() = update {
        it.copy(status = if (listings.value.isNotEmpty()) ItemStatus.POSTED else ItemStatus.READY, soldPrice = 0, soldPlatform = null, soldAt = 0)
    }

    fun promoteIfReady(score: Int) = update {
        if (it.status == ItemStatus.DRAFT && score >= 80) it.copy(status = ItemStatus.READY) else it
    }

    fun deleteItem(onDone: () -> Unit) = viewModelScope.launch {
        val cur = _item.value ?: return@launch
        writeLock.withLock {
            dao.photos(itemId).forEach { g.photoStore.delete(it.originalPath, it.path) }
            dao.delete(cur)
        }
        setItem(null)
        onDone()
    }

    companion object { const val MAX_PHOTOS = 10 }
}
