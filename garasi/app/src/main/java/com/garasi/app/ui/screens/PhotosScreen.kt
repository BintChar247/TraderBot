package com.garasi.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.garasi.app.R
import com.garasi.app.data.Photo
import com.garasi.app.quality.QualityChecker
import com.garasi.app.text.ListingText
import com.garasi.app.ui.CollectMessages
import com.garasi.app.ui.ItemViewModel
import com.garasi.app.ui.Routes
import com.garasi.app.ui.angleLabel
import com.garasi.app.ui.theme.ScoreAmber
import com.garasi.app.ui.theme.ScoreGreen
import com.garasi.app.ui.theme.ScoreRed
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PhotosScreen(nav: NavController, vm: ItemViewModel) {
    val photos by vm.photos.collectAsStateWithLifecycle()
    val item = vm.current
    val snackbar = remember { SnackbarHostState() }
    CollectMessages(vm.messages, snackbar)
    var toDelete by remember { mutableStateOf<Photo?>(null) }
    val angles = item?.let { ListingText.angles(it.category) } ?: emptyList()
    val best = photos.filter { it.score != null && !it.isFlaw }.maxByOrNull { it.score!! }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
                title = { Text(stringResource(R.string.manage_photos)) },
            )
        },
    ) { pad ->
        LazyColumn(
            Modifier.padding(pad),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(R.string.angle_checklist), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.angle_checklist_tip), style = MaterialTheme.typography.bodySmall)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val covered = photos.map { it.angle }.toSet() + (if (photos.any { it.isFlaw }) setOf("flaw") else emptySet())
                        angles.forEach { a ->
                            FilterChip(a in covered, {}, label = { Text(stringResource(angleLabel(a))) })
                        }
                    }
                    Text(stringResource(R.string.photos_ideal), style = MaterialTheme.typography.bodySmall)
                }
            }
            itemsIndexed(photos, key = { _, p -> p.id }) { index, p ->
                PhotoRow(nav, vm, p, index, photos.size, angles, isBest = best?.id == p.id && index != 0) { toDelete = p }
            }
        }
    }
    toDelete?.let { p ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text(stringResource(R.string.delete_photo_q)) },
            confirmButton = { TextButton({ vm.deletePhoto(p); toDelete = null }) { Text(stringResource(R.string.delete)) } },
            dismissButton = { TextButton({ toDelete = null }) { Text(stringResource(R.string.cancel)) } },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PhotoRow(
    nav: NavController, vm: ItemViewModel, p: Photo, index: Int, count: Int, angles: List<String>,
    isBest: Boolean, onDelete: () -> Unit,
) {
    var angleMenu by remember { mutableStateOf(false) }
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box {
                AsyncImage(File(p.path), null, contentScale = ContentScale.Crop,
                    modifier = Modifier.size(110.dp).clip(RoundedCornerShape(10.dp)))
                Row(Modifier.padding(4.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    if (index == 0) Badge(stringResource(R.string.cover))
                    if (p.isFlaw) Badge(stringResource(R.string.flaw))
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(R.string.photo_n, index + 1), fontWeight = FontWeight.SemiBold)
                val score = p.score
                if (score == null) Text(stringResource(R.string.analysing), style = MaterialTheme.typography.bodySmall)
                else {
                    val color = when { score >= 80 -> ScoreGreen; score >= 60 -> ScoreAmber; else -> ScoreRed }
                    Text(stringResource(R.string.photo_score, score), color = color, fontWeight = FontWeight.Bold)
                    val problems = QualityChecker.photoProblems(p)
                    val labels = problems.map { t -> stringResourceShort(t.name) }
                    if (labels.isNotEmpty()) Text(
                        labels.joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    if (p.width > 0) Text("${p.width}×${p.height}", style = MaterialTheme.typography.bodySmall)
                }
                Box {
                    AssistChip({ angleMenu = true }, label = {
                        Text(if (p.angle.isBlank()) stringResource(R.string.tag_angle) else stringResource(angleLabel(p.angle)))
                    })
                    DropdownMenu(angleMenu, { angleMenu = false }) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.angle_none)) }, onClick = { vm.setAngle(p, ""); angleMenu = false })
                        angles.forEach { a ->
                            DropdownMenuItem(text = { Text(stringResource(angleLabel(a))) }, onClick = { vm.setAngle(p, a); angleMenu = false })
                        }
                    }
                }
            }
        }
        FlowRow(Modifier.padding(start = 6.dp, end = 6.dp, bottom = 6.dp), horizontalArrangement = Arrangement.spacedBy(0.dp)) {
            IconButton({ vm.move(p, -1) }, enabled = index > 0) { Icon(Icons.Filled.ArrowUpward, stringResource(R.string.move_up)) }
            IconButton({ vm.move(p, 1) }, enabled = index < count - 1) { Icon(Icons.Filled.ArrowDownward, stringResource(R.string.move_down)) }
            IconButton({ nav.navigate(Routes.crop(vm.itemId, p.id)) }) { Icon(Icons.Filled.Crop, stringResource(R.string.crop_square)) }
            IconButton(onDelete) { Icon(Icons.Filled.Delete, stringResource(R.string.delete)) }
            if (index != 0) TextButton({ vm.makeCover(p.id) }) {
                if (isBest) Icon(Icons.Filled.Star, null, Modifier.size(18.dp))
                Text(stringResource(if (isBest) R.string.make_cover_best else R.string.make_cover))
            }
            TextButton({ vm.toggleFlaw(p) }) { Text(stringResource(if (p.isFlaw) R.string.untag_flaw else R.string.tag_flaw)) }
        }
    }
}

/** Short labels for photo problems shown on each photo row. */
@Composable
private fun stringResourceShort(name: String): String = stringResource(
    when (name) {
        "BLURRY" -> R.string.p_blurry
        "SOFT" -> R.string.p_soft
        "TOO_DARK" -> R.string.p_dark
        "OVEREXPOSED" -> R.string.p_bright
        "FLAT" -> R.string.p_flat
        "ITEM_TOO_SMALL" -> R.string.p_small
        "ITEM_TOO_TIGHT" -> R.string.p_tight
        "OFF_CENTER" -> R.string.p_off_center
        "BUSY_BACKGROUND" -> R.string.p_busy
        "LOW_RES" -> R.string.p_low_res
        else -> R.string.p_other
    }
)
