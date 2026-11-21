package com.garasi.app.ui

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.garasi.app.ui.screens.CameraScreen
import com.garasi.app.ui.screens.CropScreen
import com.garasi.app.ui.screens.EditItemScreen
import com.garasi.app.ui.screens.HomeScreen
import com.garasi.app.ui.screens.PhotosScreen
import com.garasi.app.ui.screens.PostScreen
import com.garasi.app.ui.screens.PriceScreen
import com.garasi.app.ui.screens.QualityScreen
import com.garasi.app.ui.screens.SettingsScreen
import com.garasi.app.ui.screens.StatsScreen

object Routes {
    const val HOME = "home"
    const val ITEM = "item/{id}"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    fun item(id: Long) = "item/$id"
    fun camera(id: Long, flaw: Boolean = false) = "item/$id/camera?flaw=$flaw"
    fun photos(id: Long) = "item/$id/photos"
    fun crop(id: Long, photoId: Long) = "item/$id/crop/$photoId"
    fun quality(id: Long) = "item/$id/quality"
    fun price(id: Long) = "item/$id/price"
    fun post(id: Long) = "item/$id/post"
}

private val idArg = navArgument("id") { type = NavType.LongType }

@Composable
private fun itemVm(nav: NavHostController, entry: NavBackStackEntry): ItemViewModel {
    val id = entry.arguments!!.getLong("id")
    val app = LocalContext.current.applicationContext as Application
    // Sub-screens share the edit screen's ViewModel so there is a single in-memory draft.
    val owner = remember(entry) { runCatching { nav.getBackStackEntry(Routes.ITEM) }.getOrDefault(entry) }
    return viewModel(owner, key = "item-$id", factory = viewModelFactory { initializer { ItemViewModel(app, id) } })
}

@Composable
fun GarasiNav() {
    val nav = rememberNavController()
    NavHost(nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) { HomeScreen(nav) }
        composable(Routes.STATS) { StatsScreen(nav) }
        composable(Routes.SETTINGS) { SettingsScreen(nav) }
        composable(Routes.ITEM, arguments = listOf(idArg)) { EditItemScreen(nav, itemVm(nav, it)) }
        composable(
            "item/{id}/camera?flaw={flaw}",
            arguments = listOf(idArg, navArgument("flaw") { type = NavType.BoolType; defaultValue = false }),
        ) { CameraScreen(nav, itemVm(nav, it), it.arguments?.getBoolean("flaw") ?: false) }
        composable("item/{id}/photos", arguments = listOf(idArg)) { PhotosScreen(nav, itemVm(nav, it)) }
        composable(
            "item/{id}/crop/{photoId}",
            arguments = listOf(idArg, navArgument("photoId") { type = NavType.LongType }),
        ) { CropScreen(nav, itemVm(nav, it), it.arguments!!.getLong("photoId")) }
        composable("item/{id}/quality", arguments = listOf(idArg)) { QualityScreen(nav, itemVm(nav, it)) }
        composable("item/{id}/price", arguments = listOf(idArg)) { PriceScreen(nav, itemVm(nav, it)) }
        composable("item/{id}/post", arguments = listOf(idArg)) { PostScreen(nav, itemVm(nav, it)) }
    }
}
