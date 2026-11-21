package com.garasi.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.garasi.app.GarasiApp
import com.garasi.app.R
import com.garasi.app.data.ListingLang
import com.garasi.app.data.ThemeMode
import com.garasi.app.data.WeightPreset
import com.garasi.app.ui.BigButton
import com.garasi.app.ui.SectionCard
import com.garasi.app.ui.langLabel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(nav: NavController) {
    val ctx = LocalContext.current
    val app = ctx.applicationContext as GarasiApp
    val s by app.settings.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var confirmImport by remember { mutableStateOf<android.net.Uri?>(null) }

    val exporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
        if (uri != null) scope.launch {
            val msg = runCatching { app.backup.export(uri) }
                .fold({ ctx.getString(R.string.export_done, it) }, { ctx.getString(R.string.export_failed) })
            snackbar.showSnackbar(msg)
        }
    }
    val importer = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> confirmImport = uri }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
                title = { Text(stringResource(R.string.settings_title)) },
            )
        },
    ) { pad ->
        Column(
            Modifier.padding(pad).imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionCard(stringResource(R.string.default_listing_language)) {
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    ListingLang.entries.forEachIndexed { i, l ->
                        SegmentedButton(s.defaultLang == l, { app.settings.update { it.copy(defaultLang = l) } },
                            SegmentedButtonDefaults.itemShape(i, ListingLang.entries.size)) { Text(stringResource(langLabel(l))) }
                    }
                }
            }
            SectionCard(stringResource(R.string.default_city)) {
                OutlinedTextField(s.city, { v -> app.settings.update { it.copy(city = v) } }, Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.location_hint)) }, singleLine = true)
            }
            SectionCard(stringResource(R.string.theme)) {
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    ThemeMode.entries.forEachIndexed { i, t ->
                        SegmentedButton(s.theme == t, { app.settings.update { it.copy(theme = t) } },
                            SegmentedButtonDefaults.itemShape(i, ThemeMode.entries.size)) {
                            Text(stringResource(when (t) { ThemeMode.SYSTEM -> R.string.theme_system; ThemeMode.LIGHT -> R.string.theme_light; ThemeMode.DARK -> R.string.theme_dark }))
                        }
                    }
                }
            }
            SectionCard(stringResource(R.string.weight_presets)) {
                s.weightPresets.forEachIndexed { index, p ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(p.name, { v -> app.settings.update { st -> st.copy(weightPresets = st.weightPresets.toMutableList().also { it[index] = p.copy(name = v) }) } },
                            Modifier.weight(1f), singleLine = true, label = { Text(stringResource(R.string.preset_name)) })
                        OutlinedTextField(if (p.grams > 0) p.grams.toString() else "", { v ->
                            app.settings.update { st -> st.copy(weightPresets = st.weightPresets.toMutableList().also { it[index] = p.copy(grams = v.filter(Char::isDigit).take(6).toIntOrNull() ?: 0) }) }
                        }, Modifier.weight(0.6f), singleLine = true, label = { Text(stringResource(R.string.grams)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        IconButton({ app.settings.update { st -> st.copy(weightPresets = st.weightPresets.filterIndexed { i, _ -> i != index }) } }) {
                            Icon(Icons.Filled.Delete, stringResource(R.string.delete))
                        }
                    }
                }
                TextButton({ app.settings.update { it.copy(weightPresets = it.weightPresets + WeightPreset("", 1000)) } }) {
                    Text(stringResource(R.string.add_preset))
                }
            }
            SectionCard(stringResource(R.string.backup)) {
                Text(stringResource(R.string.backup_tip), style = MaterialTheme.typography.bodyMedium)
                BigButton(stringResource(R.string.export_data), null, {
                    val stamp = SimpleDateFormat("yyyyMMdd-HHmm", Locale.ROOT).format(Date())
                    exporter.launch("garasi-backup-$stamp.zip")
                }, Modifier.fillMaxWidth())
                BigButton(stringResource(R.string.import_data), null, {
                    importer.launch(arrayOf("application/zip", "application/octet-stream", "application/x-zip-compressed"))
                }, Modifier.fillMaxWidth(), outlined = true)
            }
            Text(stringResource(R.string.about_text), style = MaterialTheme.typography.bodySmall)
        }
    }

    confirmImport?.let { uri ->
        AlertDialog(
            onDismissRequest = { confirmImport = null },
            title = { Text(stringResource(R.string.import_q)) },
            text = { Text(stringResource(R.string.import_body)) },
            confirmButton = {
                TextButton({
                    confirmImport = null
                    scope.launch {
                        val msg = runCatching { app.backup.import(uri) }
                            .fold({ ctx.getString(R.string.import_done, it) }, { ctx.getString(R.string.import_failed) })
                        snackbar.showSnackbar(msg)
                    }
                }) { Text(stringResource(R.string.import_data)) }
            },
            dismissButton = { TextButton({ confirmImport = null }) { Text(stringResource(R.string.cancel)) } },
        )
    }
}
