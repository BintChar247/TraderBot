package com.garasi.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.garasi.app.R
import com.garasi.app.data.Item
import com.garasi.app.data.ItemStatus
import com.garasi.app.data.ListingLang
import com.garasi.app.data.Platform
import com.garasi.app.platform.ChecklistOverlayService
import com.garasi.app.platform.Clipboard
import com.garasi.app.platform.GallerySaver
import com.garasi.app.platform.Marketplace
import com.garasi.app.text.Money
import com.garasi.app.ui.BigButton
import com.garasi.app.ui.CollectMessages
import com.garasi.app.ui.ItemViewModel
import com.garasi.app.ui.SectionCard
import com.garasi.app.ui.categoryLabel
import com.garasi.app.ui.langLabel
import com.garasi.app.ui.platformLabel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PostScreen(nav: NavController, vm: ItemViewModel) {
    val ctx = LocalContext.current
    val item = vm.current ?: return
    val photos by vm.photos.collectAsStateWithLifecycle()
    val listings by vm.listings.collectAsStateWithLifecycle()
    val report by vm.report.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    CollectMessages(vm.messages, snackbar)
    val scope = rememberCoroutineScope()
    var platform by rememberSaveable { mutableStateOf(Platform.TOKOPEDIA) }
    var both by rememberSaveable { mutableStateOf(false) }
    var urlDialogFor by remember { mutableStateOf<Platform?>(null) }
    var showSold by remember { mutableStateOf(false) }
    var overlayAllowed by remember { mutableStateOf(ChecklistOverlayService.canDrawOverlays(ctx)) }
    val lang = item.listingLang

    val title = Marketplace.formatTitle(platform, item, lang)
    val description = if (both) Marketplace.bothLanguages(platform, item) else Marketplace.formatDescription(platform, item, lang)
    val album = GallerySaver.albumName(item.displayName.ifBlank { "Item ${item.id}" })

    val storagePerm = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    val notifPerm = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    val overlaySettings = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        overlayAllowed = ChecklistOverlayService.canDrawOverlays(ctx)
    }

    fun go() {
        if (title.isBlank() || description.isBlank()) { vm.say(R.string.msg_need_text); return }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            storagePerm.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE); return
        }
        if (Build.VERSION.SDK_INT >= 33 && !overlayAllowed &&
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notifPerm.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        scope.launch {
            val saved = GallerySaver.save(ctx, item.displayName.ifBlank { "Item ${item.id}" }, photos.map { it.path })
            Clipboard.copy(ctx, ctx.getString(R.string.label_title), title)
            val weight = if (item.weightGrams > 0) "${item.weightGrams} g" else ""
            ChecklistOverlayService.start(ctx, ChecklistOverlayService.Data(
                platform = ctx.getString(platformLabel(platform)), title = title, description = description,
                album = "Garasi/$album", price = if (item.askPrice > 0) Money.format(item.askPrice) else "—",
                priceDigits = if (item.askPrice > 0) item.askPrice.toString() else "", weight = weight,
            ))
            when (Marketplace.openSell(ctx, platform)) {
                Marketplace.Opened.NOTHING -> vm.say(R.string.msg_cannot_open_marketplace)
                Marketplace.Opened.WEB -> vm.say(R.string.msg_opened_web, saved)
                else -> vm.say(R.string.msg_ready_to_paste, saved)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
                title = { Text(stringResource(R.string.post_title)) },
            )
        },
    ) { pad ->
        Column(
            Modifier.padding(pad).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            report?.let { r ->
                if (r.score < 60) Text(stringResource(R.string.post_low_score, r.score), color = MaterialTheme.colorScheme.error)
            }
            SectionCard(stringResource(R.string.choose_platform)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Platform.entries.forEach { p ->
                        FilterChip(platform == p, { platform = p }, label = { Text(stringResource(platformLabel(p))) })
                    }
                }
                Text(stringResource(R.string.listing_in, stringResource(langLabel(lang))), style = MaterialTheme.typography.bodyMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.copy_both_languages), Modifier.weight(1f))
                    Switch(both, { both = it })
                }
            }
            SectionCard(stringResource(R.string.preview)) {
                val spec = Marketplace.spec(platform)
                Text(stringResource(R.string.label_title), style = MaterialTheme.typography.labelLarge)
                Text(title.ifBlank { "—" }, fontWeight = FontWeight.SemiBold)
                if (item.title(lang).length > spec.titleMax) Text(stringResource(R.string.title_trimmed, spec.titleMax), style = MaterialTheme.typography.bodySmall)
                HorizontalDivider()
                Text(stringResource(R.string.label_description), style = MaterialTheme.typography.labelLarge)
                Text(description.ifBlank { "—" }, maxLines = 8, style = MaterialTheme.typography.bodyMedium)
                Text(stringResource(R.string.char_count, description.length, spec.descMax), style = MaterialTheme.typography.bodySmall)
                HorizontalDivider()
                Text(stringResource(R.string.category_hint, Marketplace.categoryHint(platform, item.category), stringResource(categoryLabel(item.category))),
                    style = MaterialTheme.typography.bodySmall)
                Text(stringResource(R.string.weight_line, if (item.weightGrams > 0) "${item.weightGrams} g" else stringResource(R.string.not_set)),
                    style = MaterialTheme.typography.bodySmall)
                Text(stringResource(R.string.price_line, if (item.askPrice > 0) Money.format(item.askPrice) else stringResource(R.string.not_set)),
                    style = MaterialTheme.typography.bodySmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton({ Clipboard.copy(ctx, "title", title); vm.say(R.string.copied_x, ctx.getString(R.string.label_title)) }) {
                        Icon(Icons.Filled.ContentCopy, null); Text(" " + stringResource(R.string.label_title))
                    }
                    TextButton({ Clipboard.copy(ctx, "description", description); vm.say(R.string.copied_x, ctx.getString(R.string.label_description)) }) {
                        Icon(Icons.Filled.ContentCopy, null); Text(" " + stringResource(R.string.label_description))
                    }
                    if (item.askPrice > 0) TextButton({ Clipboard.copy(ctx, "price", item.askPrice.toString()); vm.say(R.string.copied_x, ctx.getString(R.string.label_price)) }) {
                        Icon(Icons.Filled.ContentCopy, null); Text(" " + stringResource(R.string.label_price))
                    }
                }
            }
            if (!overlayAllowed) SectionCard(stringResource(R.string.overlay_perm_title)) {
                Text(stringResource(R.string.overlay_perm_body), style = MaterialTheme.typography.bodyMedium)
                BigButton(stringResource(R.string.allow), null, { overlaySettings.launch(ChecklistOverlayService.overlayPermissionIntent(ctx)) }, outlined = true)
            }
            BigButton(stringResource(R.string.post_go, stringResource(platformLabel(platform))), Icons.Filled.RocketLaunch, { go() }, Modifier.fillMaxWidth())
            Text(stringResource(R.string.post_go_tip), style = MaterialTheme.typography.bodySmall)

            SectionCard(stringResource(R.string.where_posted)) {
                Platform.entries.forEach { p ->
                    val l = listings.firstOrNull { it.platform == p }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (l != null) Icon(Icons.Filled.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
                        Column(Modifier.weight(1f).padding(start = 8.dp)) {
                            Text(stringResource(platformLabel(p)), fontWeight = FontWeight.SemiBold)
                            if (l != null && l.url.isNotBlank()) Text(l.url, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                        }
                        if (l == null) TextButton({ urlDialogFor = p }) { Text(stringResource(R.string.mark_posted)) }
                        else {
                            if (l.url.isNotBlank()) IconButton({ Marketplace.openUrl(ctx, l.url) }) { Icon(Icons.AutoMirrored.Filled.OpenInNew, stringResource(R.string.open)) }
                            TextButton({ vm.unmarkPosted(l) }) { Text(stringResource(R.string.unmark)) }
                        }
                    }
                }
            }
            if (item.status != ItemStatus.SOLD) BigButton(stringResource(R.string.mark_sold), null, { showSold = true }, Modifier.fillMaxWidth(), outlined = true)
            else Text(stringResource(R.string.sold_for, Money.format(item.soldPrice)), fontWeight = FontWeight.Bold)
        }
    }

    urlDialogFor?.let { p -> PostedDialog(p, onDismiss = { urlDialogFor = null }) { url -> vm.markPosted(p, url); urlDialogFor = null } }
    if (showSold) SoldDialog(vm, item) { showSold = false }
}

@Composable
private fun PostedDialog(p: Platform, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    val ctx = LocalContext.current
    var url by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.posted_on, stringResource(platformLabel(p)))) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.paste_listing_url))
                OutlinedTextField(url, { url = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text(stringResource(R.string.listing_url)) })
                TextButton({ Clipboard.read(ctx)?.let { url = it.trim() } }) { Text(stringResource(R.string.paste)) }
            }
        },
        confirmButton = { TextButton({ onSave(url) }) { Text(stringResource(R.string.save)) } },
        dismissButton = { TextButton(onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}

/** Mark sold, then remind to delete the listings on the other platforms. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SoldDialog(vm: ItemViewModel, item: Item, onDone: () -> Unit) {
    val ctx = LocalContext.current
    val listings by vm.listings.collectAsStateWithLifecycle()
    var price by remember { mutableStateOf(item.askPrice) }
    var platform by remember { mutableStateOf<Platform?>(listings.firstOrNull()?.platform) }
    var step by remember { mutableStateOf(0) }

    if (step == 0) AlertDialog(
        onDismissRequest = onDone,
        title = { Text(stringResource(R.string.mark_sold)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PriceField(price, R.string.final_price) { price = it }
                Text(stringResource(R.string.sold_where))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Platform.entries.forEach { p ->
                        FilterChip(platform == p, { platform = p }, label = { Text(stringResource(platformLabel(p))) })
                    }
                    FilterChip(platform == null, { platform = null }, label = { Text(stringResource(R.string.sold_elsewhere)) })
                }
            }
        },
        confirmButton = {
            TextButton({
                vm.markSold(price, platform)
                if (listings.any { it.platform != platform && !it.removed }) step = 1 else { vm.say(R.string.msg_sold_congrats); onDone() }
            }) { Text(stringResource(R.string.save)) }
        },
        dismissButton = { TextButton(onDone) { Text(stringResource(R.string.cancel)) } },
    ) else AlertDialog(
        onDismissRequest = onDone,
        title = { Text(stringResource(R.string.remove_other_listings)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(stringResource(R.string.remove_other_listings_body))
                listings.filter { it.platform != platform }.forEach { l ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(l.removed, { vm.setListingRemoved(l, it) })
                        Text(stringResource(platformLabel(l.platform)), Modifier.weight(1f))
                        TextButton({
                            if (l.url.isNotBlank()) Marketplace.openUrl(ctx, l.url) else Marketplace.openSell(ctx, l.platform)
                        }) { Text(stringResource(R.string.open)) }
                    }
                }
            }
        },
        confirmButton = { TextButton({ vm.say(R.string.msg_sold_congrats); onDone() }) { Text(stringResource(R.string.done)) } },
    )
}

