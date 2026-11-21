package com.garasi.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.garasi.app.R
import com.garasi.app.data.Platform
import com.garasi.app.platform.Marketplace
import com.garasi.app.text.Money
import com.garasi.app.text.PriceCalculator
import com.garasi.app.ui.BigButton
import com.garasi.app.ui.CollectMessages
import com.garasi.app.ui.ItemViewModel
import com.garasi.app.ui.SectionCard
import com.garasi.app.ui.conditionLabel
import com.garasi.app.ui.platformLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceScreen(nav: NavController, vm: ItemViewModel) {
    val ctx = LocalContext.current
    val item = vm.current ?: return
    val snackbar = remember { SnackbarHostState() }
    CollectMessages(vm.messages, snackbar)
    var query by rememberSaveable { mutableStateOf(Marketplace.searchQuery(item)) }
    val median = PriceCalculator.effectiveMedian(item.compareLow, item.compareMedian, item.compareHigh)
    val suggestion = PriceCalculator.suggest(median, item.condition)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
                title = { Text(stringResource(R.string.price_check)) },
            )
        },
    ) { pad ->
        Column(
            Modifier.padding(pad).imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionCard(stringResource(R.string.price_step1)) {
                Text(stringResource(R.string.price_step1_tip), style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.search_words)) }, singleLine = true)
                Platform.entries.forEach { p ->
                    BigButton(stringResource(R.string.search_on, stringResource(platformLabel(p))), Icons.AutoMirrored.Filled.OpenInNew, {
                        if (query.isBlank()) vm.say(R.string.msg_enter_search)
                        else if (!Marketplace.openUrl(ctx, Marketplace.searchUrl(p, query))) vm.say(R.string.msg_no_browser)
                    }, Modifier.fillMaxWidth(), outlined = true)
                }
            }
            SectionCard(stringResource(R.string.price_step2)) {
                Text(stringResource(R.string.price_step2_tip), style = MaterialTheme.typography.bodyMedium)
                PriceField(item.compareLow, R.string.price_low) { v -> vm.update { it.copy(compareLow = v) } }
                PriceField(item.compareMedian, R.string.price_median) { v -> vm.update { it.copy(compareMedian = v) } }
                PriceField(item.compareHigh, R.string.price_high) { v -> vm.update { it.copy(compareHigh = v) } }
            }
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.price_suggestion), style = MaterialTheme.typography.titleMedium)
                    if (suggestion == null) {
                        Text(stringResource(R.string.price_need_median))
                    } else {
                        Text(Money.format(suggestion.askPrice), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text(stringResource(
                            R.string.price_formula, Money.format(median), stringResource(conditionLabel(item.condition)),
                            (PriceCalculator.conditionFactor(item.condition) * 100).toInt(), Money.format(suggestion.fairPrice),
                        ), style = MaterialTheme.typography.bodySmall)
                        BigButton(stringResource(R.string.use_this_price), null, {
                            vm.update { it.copy(askPrice = suggestion.askPrice) }
                            vm.say(R.string.msg_price_set, Money.format(suggestion.askPrice))
                        }, Modifier.fillMaxWidth())
                    }
                    if (item.aiUsedMin > 0) Text(
                        stringResource(R.string.ai_used_range, Money.format(item.aiUsedMin), Money.format(item.aiUsedMax)),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            PriceField(item.askPrice, R.string.asking_price) { v -> vm.update { it.copy(askPrice = v) } }
        }
    }
}
