package com.garasi.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.garasi.app.R
import com.garasi.app.data.Item
import com.garasi.app.data.ItemStatus
import com.garasi.app.data.Platform
import com.garasi.app.text.Money
import com.garasi.app.ui.platformLabel

data class Stats(
    val sold: Int, val earned: Long, val avgDays: Double?, val active: Int,
    val byPlatform: Map<Platform?, Int>, val askedTotal: Long,
)

fun computeStats(items: List<Item>): Stats {
    val sold = items.filter { it.status == ItemStatus.SOLD }
    val days = sold.filter { it.soldAt > 0 }.map { (it.soldAt - it.createdAt) / 86_400_000.0 }
    return Stats(
        sold = sold.size,
        earned = sold.sumOf { it.soldPrice },
        avgDays = if (days.isEmpty()) null else days.average(),
        active = items.count { it.status != ItemStatus.SOLD },
        byPlatform = sold.groupingBy { it.soldPlatform }.eachCount(),
        askedTotal = sold.sumOf { it.askPrice },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(nav: NavController, vm: HomeViewModel = viewModel()) {
    val rows by vm.rows.collectAsStateWithLifecycle()
    val s = computeStats(rows.map { it.item })
    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = { IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
            title = { Text(stringResource(R.string.stats_title)) },
        )
    }) { pad ->
        Column(
            Modifier.padding(pad).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(stringResource(R.string.stat_sold), s.sold.toString())
            StatCard(stringResource(R.string.stat_earned), Money.format(s.earned))
            StatCard(stringResource(R.string.stat_avg_days), s.avgDays?.let { stringResource(R.string.days_value, it) } ?: "—")
            StatCard(stringResource(R.string.stat_active), s.active.toString())
            if (s.byPlatform.isNotEmpty()) Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(R.string.stat_by_platform), style = MaterialTheme.typography.titleMedium)
                    s.byPlatform.forEach { (p, n) ->
                        Row {
                            Text(if (p == null) stringResource(R.string.sold_elsewhere) else stringResource(platformLabel(p)), Modifier.weight(1f))
                            Text(n.toString(), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}
