package com.garasi.app.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import com.garasi.app.R
import com.garasi.app.data.Category
import com.garasi.app.data.Condition
import com.garasi.app.data.ItemStatus
import com.garasi.app.data.ListingLang
import com.garasi.app.data.Platform
import kotlinx.coroutines.flow.Flow

@StringRes fun statusLabel(s: ItemStatus) = when (s) {
    ItemStatus.DRAFT -> R.string.status_draft
    ItemStatus.READY -> R.string.status_ready
    ItemStatus.POSTED -> R.string.status_posted
    ItemStatus.SOLD -> R.string.status_sold
}

@StringRes fun categoryLabel(c: Category) = when (c) {
    Category.ELECTRONICS -> R.string.cat_electronics
    Category.FASHION -> R.string.cat_fashion
    Category.BOOKS -> R.string.cat_books
    Category.TOYS -> R.string.cat_toys
    Category.HOME -> R.string.cat_home
    Category.HOBBY -> R.string.cat_hobby
    Category.OTHER -> R.string.cat_other
}

@StringRes fun conditionLabel(c: Condition) = when (c) {
    Condition.NEW -> R.string.cond_new
    Condition.LIKE_NEW -> R.string.cond_like_new
    Condition.USED_GOOD -> R.string.cond_used_good
    Condition.USED_FLAWS -> R.string.cond_used_flaws
}

@StringRes fun platformLabel(p: Platform) = when (p) {
    Platform.TOKOPEDIA -> R.string.platform_tokopedia
    Platform.SHOPEE -> R.string.platform_shopee
    Platform.OLX -> R.string.platform_olx
}

@StringRes fun langLabel(l: ListingLang) = when (l) {
    ListingLang.ID -> R.string.lang_id
    ListingLang.EN -> R.string.lang_en
}

@StringRes fun angleLabel(key: String) = when (key) {
    "front" -> R.string.angle_front
    "back" -> R.string.angle_back
    "sides" -> R.string.angle_sides
    "screen_on" -> R.string.angle_screen_on
    "label" -> R.string.angle_label
    "accessories" -> R.string.angle_accessories
    "detail" -> R.string.angle_detail
    "worn" -> R.string.angle_worn
    "spine" -> R.string.angle_spine
    "pages" -> R.string.angle_pages
    "box" -> R.string.angle_box
    "parts" -> R.string.angle_parts
    "in_use" -> R.string.angle_in_use
    "flaw" -> R.string.angle_flaw
    else -> R.string.angle_none
}

/** Shows ViewModel messages as snackbars. */
@Composable
fun CollectMessages(messages: Flow<UiMessage>, host: SnackbarHostState) {
    val ctx = LocalContext.current
    LaunchedEffect(messages) {
        messages.collect { m -> host.showSnackbar(ctx.getString(m.res, *m.args.toTypedArray())) }
    }
}

@Composable
fun SectionCard(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
fun BigButton(text: String, icon: ImageVector?, onClick: () -> Unit, modifier: Modifier = Modifier, outlined: Boolean = false, enabled: Boolean = true) {
    val content: @Composable () -> Unit = {
        if (icon != null) { Icon(icon, null, Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)) }
        Text(text)
    }
    if (outlined) OutlinedButton(onClick, modifier.heightIn(min = 52.dp), enabled = enabled) { content() }
    else Button(onClick, modifier.heightIn(min = 52.dp), enabled = enabled) { content() }
}
