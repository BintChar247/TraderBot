package com.garasi.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.garasi.app.GarasiApp
import com.garasi.app.R
import com.garasi.app.data.Category
import com.garasi.app.data.Condition
import com.garasi.app.data.Item
import com.garasi.app.data.ItemStatus
import com.garasi.app.data.ListingLang
import com.garasi.app.ml.IdentifyResult
import com.garasi.app.platform.Clipboard
import com.garasi.app.platform.ShareHelper
import com.garasi.app.quality.Verdict
import com.garasi.app.text.AiPrompt
import com.garasi.app.text.Money
import com.garasi.app.text.Templates
import com.garasi.app.ui.BigButton
import com.garasi.app.ui.CollectMessages
import com.garasi.app.ui.Field
import com.garasi.app.ui.ItemViewModel
import com.garasi.app.ui.Routes
import com.garasi.app.ui.SectionCard
import com.garasi.app.ui.categoryLabel
import com.garasi.app.ui.conditionLabel
import com.garasi.app.ui.statusLabel
import com.garasi.app.ui.theme.ScoreAmber
import com.garasi.app.ui.theme.ScoreGreen
import com.garasi.app.ui.theme.ScoreRed
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemScreen(nav: NavController, vm: ItemViewModel) {
    val item = vm.current
    val photos by vm.photos.collectAsStateWithLifecycle()
    val report by vm.report.collectAsStateWithLifecycle()
    val busy by vm.busy.collectAsStateWithLifecycle()
    val identify by vm.identifyResult.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    CollectMessages(vm.messages, snackbar)
    var menu by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    var showSold by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
                title = {
                    Column {
                        Text(item?.displayName?.ifBlank { null } ?: stringResource(R.string.new_item), maxLines = 1)
                        if (item != null) Text(stringResource(statusLabel(item.status)), style = MaterialTheme.typography.labelMedium)
                    }
                },
                actions = {
                    IconButton({ menu = true }) { Icon(Icons.Filled.MoreVert, stringResource(R.string.more)) }
                    DropdownMenu(menu, { menu = false }) {
                        if (item?.status != ItemStatus.SOLD) {
                            DropdownMenuItem(text = { Text(stringResource(R.string.mark_sold)) }, onClick = { menu = false; showSold = true })
                        } else {
                            DropdownMenuItem(text = { Text(stringResource(R.string.unmark_sold)) }, onClick = { menu = false; vm.unmarkSold() })
                        }
                        if (item?.status == ItemStatus.DRAFT) DropdownMenuItem(
                            text = { Text(stringResource(R.string.mark_ready)) },
                            onClick = { menu = false; vm.update { it.copy(status = ItemStatus.READY) } })
                        if (item?.status == ItemStatus.READY) DropdownMenuItem(
                            text = { Text(stringResource(R.string.mark_draft)) },
                            onClick = { menu = false; vm.update { it.copy(status = ItemStatus.DRAFT) } })
                        DropdownMenuItem(text = { Text(stringResource(R.string.delete_item)) }, onClick = { menu = false; confirmDelete = true })
                    }
                },
            )
        },
        bottomBar = {
            BottomAppBar {
                val r = report
                OutlinedButton(
                    onClick = { nav.navigate(Routes.quality(vm.itemId)) },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp).height(52.dp),
                ) {
                    Icon(Icons.Filled.Insights, null, Modifier.size(20.dp))
                    Spacer(Modifier.size(6.dp))
                    Text(if (r != null) stringResource(R.string.appeal_score_short, r.score) else stringResource(R.string.quality_check))
                    if (r != null) {
                        Spacer(Modifier.size(6.dp))
                        Box(Modifier.size(10.dp).clip(RoundedCornerShape(5.dp))) {
                            Surface(color = scoreColor(r.verdict), modifier = Modifier.fillMaxSize()) {}
                        }
                    }
                }
                BigButton(
                    stringResource(R.string.post), Icons.Filled.Send,
                    { nav.navigate(Routes.post(vm.itemId)) },
                    Modifier.weight(1f).padding(horizontal = 8.dp),
                )
            }
        },
    ) { pad ->
        if (item == null) {
            Box(Modifier.padding(pad).fillMaxSize()) { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            return@Scaffold
        }
        Column(
            Modifier.padding(pad).fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
            PhotosSection(nav, vm)
            IdentifySection(vm, identify, photos.isNotEmpty(), busy)
            DetailsSection(vm, item)
            TextSection(vm, item, photos.map { it.path })
            PriceSection(nav, vm, item)
            Spacer(Modifier.height(24.dp))
        }
    }

    if (confirmDelete) AlertDialog(
        onDismissRequest = { confirmDelete = false },
        title = { Text(stringResource(R.string.delete_item_q)) },
        text = { Text(stringResource(R.string.delete_item_body)) },
        confirmButton = { TextButton({ confirmDelete = false; vm.deleteItem { nav.popBackStack(Routes.HOME, false) } }) { Text(stringResource(R.string.delete)) } },
        dismissButton = { TextButton({ confirmDelete = false }) { Text(stringResource(R.string.cancel)) } },
    )
    if (showSold && item != null) SoldDialog(vm, item) { showSold = false }
}

@Composable
fun scoreColor(v: Verdict) = when (v) {
    Verdict.READY -> ScoreGreen
    Verdict.GOOD -> ScoreAmber
    Verdict.NEEDS_FIXING -> ScoreRed
}

// --------------------------------------------------------------------------- photos

@Composable
private fun PhotosSection(nav: NavController, vm: ItemViewModel) {
    val photos by vm.photos.collectAsStateWithLifecycle()
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(ItemViewModel.MAX_PHOTOS)) { uris ->
        if (uris.isNotEmpty()) vm.addPhotos(uris)
    }
    SectionCard(stringResource(R.string.photos_count, photos.size, ItemViewModel.MAX_PHOTOS)) {
        if (photos.isEmpty()) {
            Text(stringResource(R.string.photos_empty_tip), style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(photos, key = { _, p -> p.id }) { i, p ->
                    Box(Modifier.size(96.dp).clip(RoundedCornerShape(12.dp)).clickable { nav.navigate(Routes.photos(vm.itemId)) }) {
                        AsyncImage(File(p.path), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        Row(Modifier.padding(4.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            if (i == 0) Badge(stringResource(R.string.cover))
                            if (p.isFlaw) Badge(stringResource(R.string.flaw))
                        }
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BigButton(stringResource(R.string.camera), Icons.Filled.CameraAlt, { nav.navigate(Routes.camera(vm.itemId)) }, Modifier.weight(1f))
            BigButton(stringResource(R.string.gallery), Icons.Filled.PhotoLibrary, {
                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }, Modifier.weight(1f), outlined = true)
        }
        if (photos.isNotEmpty()) TextButton({ nav.navigate(Routes.photos(vm.itemId)) }) {
            Icon(Icons.Filled.Edit, null, Modifier.size(18.dp)); Spacer(Modifier.size(6.dp))
            Text(stringResource(R.string.manage_photos))
        }
    }
}

@Composable
fun Badge(text: String) {
    Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(6.dp)) {
        Text(text, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp))
    }
}

// --------------------------------------------------------------------------- identify

@Composable
private fun IdentifySection(vm: ItemViewModel, result: IdentifyResult?, hasPhotos: Boolean, busy: Boolean) {
    SectionCard(stringResource(R.string.identify_title)) {
        Text(stringResource(R.string.identify_tip), style = MaterialTheme.typography.bodyMedium)
        BigButton(stringResource(R.string.identify_button), Icons.Filled.Search, { vm.identify() },
            Modifier.fillMaxWidth(), outlined = true, enabled = hasPhotos && !busy)
        if (result != null && !result.isEmpty) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(R.string.identify_found), fontWeight = FontWeight.SemiBold)
                result.brand?.let { Text(stringResource(R.string.identify_brand, it)) }
                result.model?.let { Text(stringResource(R.string.identify_model, it)) }
                result.category?.let { Text(stringResource(R.string.identify_category, stringResource(categoryLabel(it)))) }
                result.barcode?.let { Text(stringResource(R.string.identify_barcode, it)) }
                if (result.labels.isNotEmpty()) Text(stringResource(R.string.identify_labels, result.labels.joinToString(", ")),
                    style = MaterialTheme.typography.bodySmall)
                if (result.textSnippet.isNotBlank()) Text(stringResource(R.string.identify_text, result.textSnippet),
                    style = MaterialTheme.typography.bodySmall, maxLines = 3)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BigButton(stringResource(R.string.apply), null, { vm.applyIdentify(result) })
                    TextButton({ vm.identifyResult.value = null }) { Text(stringResource(R.string.dismiss)) }
                }
            }
        }
    }
}

// --------------------------------------------------------------------------- details

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun DetailsSection(vm: ItemViewModel, item: Item) {
    val app = LocalContext.current.applicationContext as GarasiApp
    val settings by app.settings.state.collectAsStateWithLifecycle()
    val focus by vm.focus.collectAsStateWithLifecycle()
    val brandFocus = remember { FocusRequester() }
    LaunchedEffect(focus) {
        if (focus == Field.DETAILS) { runCatching { brandFocus.requestFocus() }; vm.focus.value = null }
    }
    SectionCard(stringResource(R.string.details_title)) {
        var catOpen by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(catOpen, { catOpen = it }) {
            OutlinedTextField(
                value = stringResource(categoryLabel(item.category)), onValueChange = {}, readOnly = true,
                label = { Text(stringResource(R.string.category)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(catOpen) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
            )
            ExposedDropdownMenu(catOpen, { catOpen = false }) {
                Category.entries.forEach { c ->
                    DropdownMenuItem(text = { Text(stringResource(categoryLabel(c))) }, onClick = { vm.update { it.copy(category = c) }; catOpen = false })
                }
            }
        }
        TextInput(item.brand, R.string.brand, Modifier.focusRequester(brandFocus)) { v -> vm.update { it.copy(brand = v) } }
        TextInput(item.model, R.string.model) { v -> vm.update { it.copy(model = v) } }
        TextInput(item.variant, R.string.variant, hint = R.string.variant_hint) { v -> vm.update { it.copy(variant = v) } }

        Text(stringResource(R.string.condition), style = MaterialTheme.typography.labelLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Condition.entries.forEach { c ->
                FilterChip(item.condition == c, { vm.update { it.copy(condition = c) } }, label = { Text(stringResource(conditionLabel(c))) })
            }
        }
        if (item.condition == Condition.USED_FLAWS) Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Warning, null, tint = ScoreAmber, modifier = Modifier.size(18.dp)); Spacer(Modifier.size(6.dp))
            Text(stringResource(R.string.flaw_photo_tip), style = MaterialTheme.typography.bodySmall)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextInput(item.yearBought, R.string.year_bought, Modifier.weight(1f), keyboard = KeyboardType.Number) { v -> vm.update { it.copy(yearBought = v.filter(Char::isDigit).take(4)) } }
            TextInput(item.usageAge, R.string.usage_age, Modifier.weight(1f), hint = R.string.usage_age_hint) { v -> vm.update { it.copy(usageAge = v) } }
        }
        TextInput(item.flaws, R.string.flaws, hint = R.string.flaws_hint, singleLine = false) { v -> vm.update { it.copy(flaws = v) } }
        TextInput(item.included, R.string.included, hint = R.string.included_hint, singleLine = false) { v -> vm.update { it.copy(included = v) } }
        TextInput(item.reasonSelling, R.string.reason, hint = R.string.reason_hint) { v -> vm.update { it.copy(reasonSelling = v) } }
        TextInput(item.location, R.string.location, hint = R.string.location_hint) { v -> vm.update { it.copy(location = v) } }
        TextInput(item.shipping, R.string.shipping, hint = R.string.shipping_hint) { v -> vm.update { it.copy(shipping = v) } }
        TextInput(if (item.weightGrams > 0) item.weightGrams.toString() else "", R.string.weight_grams, keyboard = KeyboardType.Number) { v ->
            vm.update { it.copy(weightGrams = v.filter(Char::isDigit).take(6).toIntOrNull() ?: 0) }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            settings.weightPresets.forEach { p ->
                AssistChip({ vm.update { it.copy(weightGrams = p.grams) } }, label = { Text("${p.name} · ${p.grams} g") })
            }
        }
    }
}

@Composable
fun TextInput(
    value: String, label: Int, modifier: Modifier = Modifier, hint: Int? = null, singleLine: Boolean = true,
    keyboard: KeyboardType = KeyboardType.Text, onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value, onValueChange = onChange, modifier = modifier.fillMaxWidth(),
        label = { Text(stringResource(label)) },
        placeholder = if (hint != null) { { Text(stringResource(hint)) } } else null,
        singleLine = singleLine, minLines = if (singleLine) 1 else 2,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, keyboardType = keyboard),
    )
}

// --------------------------------------------------------------------------- listing text + AI

@Composable
private fun TextSection(vm: ItemViewModel, item: Item, photoPaths: List<String>) {
    val ctx = LocalContext.current
    val lang = item.listingLang
    val focus by vm.focus.collectAsStateWithLifecycle()
    val titleFocus = remember { FocusRequester() }
    val descFocus = remember { FocusRequester() }
    var confirmOverwrite by remember { mutableStateOf<(() -> Unit)?>(null) }
    LaunchedEffect(focus) {
        when (focus) {
            Field.TITLE -> { runCatching { titleFocus.requestFocus() }; vm.focus.value = null }
            Field.DESCRIPTION -> { runCatching { descFocus.requestFocus() }; vm.focus.value = null }
            else -> {}
        }
    }

    fun askAi(mode: AiPrompt.Mode) {
        if (mode != AiPrompt.Mode.WRITE && item.title(lang).isBlank() && item.description(lang).isBlank()) {
            vm.say(R.string.msg_nothing_to_send); return
        }
        val prompt = AiPrompt.build(mode, item, lang, if (mode == AiPrompt.Mode.WRITE) photoPaths.take(6).size else 0)
        vm.lastPrompt = prompt
        vm.aiTargetLang = if (mode == AiPrompt.Mode.TRANSLATE) lang.other else lang
        Clipboard.copy(ctx, "Garasi prompt", prompt)
        val ok = ShareHelper.shareToAi(ctx, prompt, if (mode == AiPrompt.Mode.WRITE) photoPaths.take(6) else emptyList(),
            ctx.getString(R.string.ai_chooser_title))
        if (!ok) vm.say(R.string.msg_no_ai_app)
        else if (!ShareHelper.hasAnyAiApp(ctx)) vm.say(R.string.msg_no_known_ai_app)
        else vm.say(R.string.msg_prompt_copied)
    }

    fun pasteAi() {
        val clip = Clipboard.read(ctx)
        if (clip == null) { vm.say(R.string.msg_clipboard_empty); return }
        if (clip == vm.lastPrompt || "Reply ONLY" in clip || "Balas HANYA" in clip) { vm.say(R.string.msg_clipboard_is_prompt); return }
        val target = vm.aiTargetLang ?: lang
        val answer = vm.applyAiAnswer(clip, target)
        if (answer == null) { vm.say(R.string.msg_ai_parse_failed); return }
        if (target != lang) vm.update { it.copy(listingLang = target) }
        vm.aiTargetLang = null
        vm.say(R.string.msg_ai_applied)
    }

    SectionCard(stringResource(R.string.listing_text_title)) {
        Text(stringResource(R.string.listing_language), style = MaterialTheme.typography.labelLarge)
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            ListingLang.entries.forEachIndexed { i, l ->
                SegmentedButton(
                    selected = lang == l, onClick = { vm.update { it.copy(listingLang = l) } },
                    shape = SegmentedButtonDefaults.itemShape(i, ListingLang.entries.size),
                ) { Text(stringResource(if (l == ListingLang.ID) R.string.lang_toggle_id else R.string.lang_toggle_en)) }
            }
        }
        val title = item.title(lang)
        OutlinedTextField(
            value = title, onValueChange = { v -> vm.update { it.withText(lang, title = v.replace("\n", " ")) } },
            label = { Text(stringResource(R.string.title)) },
            supportingText = { Text(stringResource(R.string.char_count, title.length, Templates.TITLE_MAX)) },
            isError = title.length > Templates.TITLE_MAX,
            modifier = Modifier.fillMaxWidth().focusRequester(titleFocus),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        )
        TextButton({
            if (title.isBlank()) { vm.buildTitle() } else { confirmOverwrite = { vm.buildTitle() } }
        }) { Text(stringResource(R.string.build_title)) }

        val desc = item.description(lang)
        OutlinedTextField(
            value = desc, onValueChange = { v -> vm.update { it.withText(lang, desc = v) } },
            label = { Text(stringResource(R.string.description)) },
            supportingText = { Text(stringResource(R.string.desc_count, desc.length)) },
            minLines = 6,
            modifier = Modifier.fillMaxWidth().focusRequester(descFocus),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        )
        TextButton({
            if (desc.isBlank()) { vm.useTemplate() } else { confirmOverwrite = { vm.useTemplate() } }
        }) { Text(stringResource(R.string.use_template)) }

        Text(stringResource(R.string.ai_section), style = MaterialTheme.typography.labelLarge)
        Text(stringResource(R.string.ai_tip), style = MaterialTheme.typography.bodySmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BigButton(stringResource(R.string.ask_ai), Icons.Filled.AutoAwesome, { askAi(AiPrompt.Mode.WRITE) }, Modifier.weight(1f))
            BigButton(stringResource(R.string.paste_ai), Icons.Filled.ContentPaste, { pasteAi() }, Modifier.weight(1f), outlined = true)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BigButton(stringResource(R.string.translate_to, stringResource(if (lang.other == ListingLang.ID) R.string.lang_short_id else R.string.lang_short_en)),
                Icons.Filled.Translate, { askAi(AiPrompt.Mode.TRANSLATE) }, Modifier.weight(1f), outlined = true)
            BigButton(stringResource(R.string.improve_text), Icons.Filled.Star, { askAi(AiPrompt.Mode.IMPROVE) }, Modifier.weight(1f), outlined = true)
        }
    }

    confirmOverwrite?.let { action ->
        AlertDialog(
            onDismissRequest = { confirmOverwrite = null },
            title = { Text(stringResource(R.string.replace_text_q)) },
            text = { Text(stringResource(R.string.replace_text_body)) },
            confirmButton = { TextButton({ action(); confirmOverwrite = null }) { Text(stringResource(R.string.replace)) } },
            dismissButton = { TextButton({ confirmOverwrite = null }) { Text(stringResource(R.string.cancel)) } },
        )
    }
}

// --------------------------------------------------------------------------- price

@Composable
private fun PriceSection(nav: NavController, vm: ItemViewModel, item: Item) {
    val focus by vm.focus.collectAsStateWithLifecycle()
    val priceFocus = remember { FocusRequester() }
    LaunchedEffect(focus) {
        if (focus == Field.PRICE) { runCatching { priceFocus.requestFocus() }; vm.focus.value = null }
    }
    SectionCard(stringResource(R.string.price_title)) {
        PriceField(item.askPrice, R.string.asking_price, Modifier.focusRequester(priceFocus)) { v -> vm.update { it.copy(askPrice = v) } }
        if (item.aiUsedMin > 0) Text(
            stringResource(R.string.ai_used_range, Money.format(item.aiUsedMin), Money.format(item.aiUsedMax)),
            style = MaterialTheme.typography.bodyMedium,
        )
        if (item.newPrice > 0) Text(stringResource(R.string.ai_new_price, Money.format(item.newPrice)), style = MaterialTheme.typography.bodyMedium)
        BigButton(stringResource(R.string.price_check), Icons.Filled.Search, { nav.navigate(Routes.price(vm.itemId)) }, Modifier.fillMaxWidth(), outlined = true)
    }
}

@Composable
fun PriceField(value: Long, label: Int, modifier: Modifier = Modifier, onChange: (Long) -> Unit) {
    OutlinedTextField(
        value = if (value > 0) value.toString() else "",
        onValueChange = { s -> onChange(s.filter(Char::isDigit).take(12).toLongOrNull() ?: 0L) },
        label = { Text(stringResource(label)) },
        supportingText = { if (value > 0) Text(Money.format(value)) },
        prefix = { Text("Rp ") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier.fillMaxWidth(),
    )
}

