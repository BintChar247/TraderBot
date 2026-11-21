package com.garasi.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.garasi.app.R
import com.garasi.app.quality.Area
import com.garasi.app.quality.FixAction
import com.garasi.app.quality.Issue
import com.garasi.app.ui.BigButton
import com.garasi.app.ui.CollectMessages
import com.garasi.app.ui.Field
import com.garasi.app.ui.ItemViewModel
import com.garasi.app.ui.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QualityScreen(nav: NavController, vm: ItemViewModel) {
    val report by vm.report.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    CollectMessages(vm.messages, snackbar)
    var showAll by remember { mutableStateOf(false) }
    val r = report
    LaunchedEffect(r?.score) { r?.let { vm.promoteIfReady(it.score) } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
                title = { Text(stringResource(R.string.quality_check)) },
            )
        },
    ) { pad ->
        if (r == null) { Box(Modifier.padding(pad).fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }; return@Scaffold }
        Column(
            Modifier.padding(pad).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = scoreColor(r.verdict).copy(alpha = 0.15f))) {
                Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { r.score / 100f }, modifier = Modifier.size(84.dp),
                            color = scoreColor(r.verdict), strokeWidth = 8.dp,
                        )
                        Text("${r.score}", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.padding(start = 20.dp)) {
                        Text(stringResource(R.string.appeal_score), style = MaterialTheme.typography.labelLarge)
                        Text(stringResource(r.verdict.label), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
            // Per-area breakdown
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Area.entries.forEach { a ->
                    val v = r.areaScores[a] ?: 0
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(areaLabel(a)), Modifier.weight(0.35f))
                        LinearProgressIndicator(progress = { v / a.cap.toFloat() }, modifier = Modifier.weight(0.5f))
                        Text("$v/${a.cap}", Modifier.weight(0.15f).padding(start = 8.dp), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            if (r.issues.isEmpty()) {
                Text(stringResource(R.string.quality_all_good), style = MaterialTheme.typography.bodyLarge)
            } else {
                Text(stringResource(R.string.top_fixes), style = MaterialTheme.typography.titleMedium)
                r.topFixes.forEach { IssueCard(it, primary = true) { handleFix(nav, vm, it) } }
                if (r.issues.size > 3) {
                    TextButton({ showAll = !showAll }) {
                        Text(stringResource(if (showAll) R.string.hide_other_tips else R.string.show_other_tips, r.issues.size - 3))
                    }
                    if (showAll) r.issues.drop(3).forEach { IssueCard(it, primary = false) { handleFix(nav, vm, it) } }
                }
            }
            BigButton(stringResource(R.string.go_to_post), null, { nav.navigate(Routes.post(vm.itemId)) }, Modifier.fillMaxWidth(), outlined = r.score < 60)
        }
    }
}

@Composable
private fun IssueCard(issue: Issue, primary: Boolean, onFix: () -> Unit) {
    val ctx = LocalContext.current
    val args: Array<Any> = if (issue.pieces.isNotEmpty())
        arrayOf(issue.pieces.joinToString(", ") { ctx.getString(it.label) })
    else issue.args.toTypedArray()
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (primary) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(issue.type.message, *args), style = MaterialTheme.typography.bodyLarge)
            BigButton(stringResource(issue.type.action.label), null, onFix, outlined = !primary)
        }
    }
}

private fun areaLabel(a: Area) = when (a) {
    Area.PHOTOS -> R.string.area_photos
    Area.TITLE -> R.string.area_title
    Area.DESCRIPTION -> R.string.area_description
    Area.PRICE -> R.string.area_price
}

/** One-tap fix actions. */
fun handleFix(nav: NavController, vm: ItemViewModel, issue: Issue) {
    fun backToEdit(field: Field) {
        vm.focus.value = field
        nav.popBackStack(Routes.ITEM, inclusive = false)
    }
    when (issue.type.action) {
        FixAction.RETAKE_PHOTO -> nav.navigate(Routes.camera(vm.itemId))
        FixAction.ADD_PHOTOS -> nav.navigate(Routes.camera(vm.itemId))
        FixAction.ADD_FLAW_PHOTO -> nav.navigate(Routes.camera(vm.itemId, flaw = true))
        FixAction.MANAGE_PHOTOS -> nav.navigate(Routes.photos(vm.itemId))
        FixAction.MAKE_COVER -> issue.photoId?.let { vm.makeCover(it) }
        FixAction.CROP_COVER -> issue.photoId?.let { nav.navigate(Routes.crop(vm.itemId, it)) }
        FixAction.EDIT_TITLE -> backToEdit(Field.TITLE)
        FixAction.BUILD_TITLE -> { vm.buildTitle(); backToEdit(Field.TITLE) }
        FixAction.EDIT_DESCRIPTION -> backToEdit(Field.DESCRIPTION)
        FixAction.USE_TEMPLATE -> { vm.useTemplate(); backToEdit(Field.DESCRIPTION) }
        FixAction.EDIT_DETAILS -> backToEdit(Field.DETAILS)
        FixAction.OPEN_PRICE -> nav.navigate(Routes.price(vm.itemId))
    }
}

