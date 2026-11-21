package com.garasi.app

import android.app.Application
import com.garasi.app.data.GarasiDb
import com.garasi.app.data.SettingsStore
import com.garasi.app.ml.ItemIdentifier
import com.garasi.app.ml.PhotoAnalyzer
import com.garasi.app.photo.PhotoStore
import com.garasi.app.platform.Backup

/** Simple manual dependency container. */
class GarasiApp : Application() {
    val db by lazy { GarasiDb.get(this) }
    val settings by lazy { SettingsStore(this) }
    val photoStore by lazy { PhotoStore(this) }
    val analyzer by lazy { PhotoAnalyzer(photoStore) }
    val identifier by lazy { ItemIdentifier(this) }
    val backup by lazy { Backup(this, db, settings) }
}
