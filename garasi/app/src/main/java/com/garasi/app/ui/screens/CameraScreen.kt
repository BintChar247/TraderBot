package com.garasi.app.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.garasi.app.GarasiApp
import com.garasi.app.R
import com.garasi.app.ui.BigButton
import com.garasi.app.ui.ItemViewModel
import android.view.MotionEvent

@Composable
fun CameraScreen(nav: NavController, vm: ItemViewModel, flawMode: Boolean) {
    val ctx = LocalContext.current
    var granted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    var askedOnce by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted = it; askedOnce = true }
    LaunchedEffect(Unit) { if (!granted) launcher.launch(Manifest.permission.CAMERA) }

    if (!granted) {
        Column(
            Modifier.fillMaxSize().statusBarsPadding().padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.camera_permission_needed), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
            BigButton(stringResource(R.string.grant_permission), null, {
                if (askedOnce) ctx.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${ctx.packageName}"))
                ) else launcher.launch(Manifest.permission.CAMERA)
            })
            TextButton({ nav.popBackStack() }) { Text(stringResource(R.string.back)) }
        }
        return
    }
    CameraContent(nav, vm, flawMode)
}

@Composable
private fun CameraContent(nav: NavController, vm: ItemViewModel, flawMode: Boolean) {
    val ctx = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current
    val store = (ctx.applicationContext as GarasiApp).photoStore
    val photos by vm.photos.collectAsStateWithLifecycle()
    var flaw by remember { mutableStateOf(flawMode) }
    var shooting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val capture = remember { ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build() }
    var camera by remember { mutableStateOf<Camera?>(null) }
    val previewView = remember { PreviewView(ctx).apply { scaleType = PreviewView.ScaleType.FIT_CENTER } }

    LaunchedEffect(flaw) { vm.pendingFlawShot = flaw }
    DisposableEffect(Unit) { onDispose { vm.pendingFlawShot = false } }

    DisposableEffect(lifecycle) {
        val future = ProcessCameraProvider.getInstance(ctx)
        var provider: ProcessCameraProvider? = null
        future.addListener({
            try {
                provider = future.get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                provider?.unbindAll()
                camera = provider?.bindToLifecycle(lifecycle, CameraSelector.DEFAULT_BACK_CAMERA, preview, capture)
            } catch (e: Exception) {
                error = ctx.getString(R.string.camera_error)
            }
        }, ContextCompat.getMainExecutor(ctx))
        onDispose { provider?.unbindAll() }
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView({ previewView.apply {
            setOnTouchListener { v, e ->
                if (e.action == MotionEvent.ACTION_UP) {
                    val point = meteringPointFactory.createPoint(e.x, e.y)
                    camera?.cameraControl?.startFocusAndMetering(FocusMeteringAction.Builder(point).build())
                    v.performClick()
                }
                true
            }
        } }, Modifier.fillMaxSize())

        // 1:1 guide: what the square cover will show.
        Box(Modifier.align(Alignment.Center).fillMaxWidth().aspectRatio(1f).padding(12.dp).border(2.dp, Color.White.copy(alpha = 0.7f)))

        Row(Modifier.statusBarsPadding().padding(8.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton({ nav.popBackStack() }, colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.4f), contentColor = Color.White)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
            }
            Surface(color = Color.Black.copy(alpha = 0.45f), shape = MaterialTheme.shapes.small, modifier = Modifier.padding(start = 8.dp).weight(1f)) {
                Text(
                    error ?: stringResource(if (flaw) R.string.camera_tip_flaw else R.string.camera_tip),
                    color = Color.White, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(8.dp),
                )
            }
        }

        Column(
            Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FilterChip(flaw, { flaw = !flaw }, label = { Text(stringResource(R.string.flaw_closeup_mode), color = Color.White) })
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(color = Color.Black.copy(alpha = 0.45f), shape = MaterialTheme.shapes.small) {
                    Text(stringResource(R.string.photo_counter, photos.size, ItemViewModel.MAX_PHOTOS), color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                }
                // Shutter
                Box(
                    Modifier.size(80.dp).border(4.dp, Color.White, CircleShape).padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    FilledIconButton(
                        onClick = {
                            if (shooting) return@FilledIconButton
                            if (photos.size >= ItemViewModel.MAX_PHOTOS) { vm.say(R.string.msg_max_photos, ItemViewModel.MAX_PHOTOS); return@FilledIconButton }
                            shooting = true
                            val file = store.newCameraFile()
                            capture.takePicture(
                                ImageCapture.OutputFileOptions.Builder(file).build(),
                                ContextCompat.getMainExecutor(ctx),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        shooting = false; vm.addCameraFile(file)
                                    }
                                    override fun onError(exception: ImageCaptureException) {
                                        shooting = false; error = ctx.getString(R.string.camera_capture_failed)
                                    }
                                },
                            )
                        },
                        modifier = Modifier.fillMaxSize(),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = if (shooting) Color.Gray else Color.White),
                    ) {}
                }
                FilledIconButton({ nav.popBackStack() }, Modifier.size(56.dp)) { Icon(Icons.Filled.Check, stringResource(R.string.done)) }
            }
        }
    }
}

