package com.garasi.app.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp

private val Light = lightColorScheme(
    primary = Color(0xFFB4530B), onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBC8), onPrimaryContainer = Color(0xFF341100),
    secondary = Color(0xFF765848), secondaryContainer = Color(0xFFFFDBC8),
    tertiary = Color(0xFF2F6A3E), tertiaryContainer = Color(0xFFB2F1BA),
    background = Color(0xFFFFF8F5), surface = Color(0xFFFFF8F5),
)
private val Dark = darkColorScheme(
    primary = Color(0xFFFFB68B), onPrimary = Color(0xFF552000),
    primaryContainer = Color(0xFF793000), onPrimaryContainer = Color(0xFFFFDBC8),
    secondary = Color(0xFFE6BEAB), secondaryContainer = Color(0xFF5C4032),
    tertiary = Color(0xFF97D5A0), tertiaryContainer = Color(0xFF145128),
)

private val type = Typography().let {
    it.copy(
        bodyLarge = it.bodyLarge.copy(fontSize = 17.sp),
        bodyMedium = it.bodyMedium.copy(fontSize = 15.sp),
        labelLarge = it.labelLarge.copy(fontSize = 16.sp),
    )
}

@Composable
fun GarasiTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val ctx = LocalContext.current
    val scheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        darkTheme -> Dark
        else -> Light
    }
    MaterialTheme(colorScheme = scheme, typography = type, content = content)
}

val ScoreGreen = Color(0xFF2E7D32)
val ScoreAmber = Color(0xFFF9A825)
val ScoreRed = Color(0xFFC62828)
