package com.garasi.app.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.garasi.app.GarasiApp
import com.garasi.app.R
import com.garasi.app.ui.BigButton
import com.garasi.app.ui.ItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Square (1:1) crop: drag the square, use the slider to zoom. Crops from the untouched original. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropScreen(nav: NavController, vm: ItemViewModel, photoId: Long) {
    val ctx = LocalContext.current
    val store = (ctx.applicationContext as GarasiApp).photoStore
    val photos by vm.photos.collectAsStateWithLifecycle()
    val busy by vm.busy.collectAsStateWithLifecycle()
    val photo = photos.firstOrNull { it.id == photoId }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var cx by remember { mutableFloatStateOf(0.5f) }
    var cy by remember { mutableFloatStateOf(0.5f) }
    var side by remember { mutableFloatStateOf(1f) }
    var box by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(photo?.originalPath) {
        val p = photo ?: return@LaunchedEffect
        bitmap = withContext(Dispatchers.IO) { runCatching { store.decodeUpright(File(p.originalPath), 1400) }.getOrNull() }
    }

    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = { IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
            title = { Text(stringResource(R.string.crop_square)) },
        )
    }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            val bmp = bitmap
            Box(Modifier.weight(1f).fillMaxWidth().onSizeChanged { box = it }, contentAlignment = Alignment.Center) {
                if (bmp == null) CircularProgressIndicator()
                else {
                    Image(bmp.asImageBitmap(), null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize())
                    // Where the fitted image sits inside the box.
                    val scale = minOf(box.width.toFloat() / bmp.width, box.height.toFloat() / bmp.height)
                    val iw = bmp.width * scale
                    val ih = bmp.height * scale
                    val ox = (box.width - iw) / 2
                    val oy = (box.height - ih) / 2
                    val sq = minOf(iw, ih) * side
                    fun clampX(v: Float) = v.coerceIn(sq / 2 / iw, 1 - sq / 2 / iw)
                    fun clampY(v: Float) = v.coerceIn(sq / 2 / ih, 1 - sq / 2 / ih)
                    val dcx = clampX(cx)
                    val dcy = clampY(cy)
                    Canvas(Modifier.fillMaxSize().pointerInput(bmp, side, box) {
                        detectDragGestures { change, drag ->
                            change.consume()
                            cx = clampX(clampX(cx) + drag.x / iw)
                            cy = clampY(clampY(cy) + drag.y / ih)
                        }
                    }) {
                        val left = ox + dcx * iw - sq / 2
                        val top = oy + dcy * ih - sq / 2
                        clipRect(left, top, left + sq, top + sq, clipOp = ClipOp.Difference) {
                            drawRect(Color.Black.copy(alpha = 0.55f))
                        }
                        drawRect(Color.White, Offset(left, top), Size(sq, sq), style = Stroke(3.dp.toPx()))
                    }
                }
            }
            Text(stringResource(R.string.crop_tip), style = MaterialTheme.typography.bodySmall)
            Text(stringResource(R.string.zoom), style = MaterialTheme.typography.labelLarge)
            Slider(value = 1f - side, onValueChange = { side = 1f - it }, valueRange = 0f..0.7f)
            BigButton(stringResource(R.string.save_crop), null, {
                val p = photo ?: return@BigButton
                vm.crop(p, cx, cy, side) { nav.popBackStack() }
            }, Modifier.fillMaxWidth(), enabled = bitmap != null && !busy)
        }
    }
}
