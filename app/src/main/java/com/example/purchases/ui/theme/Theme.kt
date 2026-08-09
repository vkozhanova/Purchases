package com.example.purchases.features.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PurchaseLightColorScheme = lightColorScheme(

    primary = PrimaryColor,
    onPrimary = TextPrimaryColor,
//    error = AccentColor,
//    tertiary = ,
//    tertiaryContainer = SliderTrackColor,
    background = BackgroundColor,
    surface = AccentColor,
    outline = DividerColor,
//    onSurface = SurfaceColor,
//    onSurfaceVariant = SurfaceVariantColor,
//    onSecondary = TextSecondaryColor,
)

@Composable
fun PurchaseAppTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = PurchaseLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = purchaseAppTypography,
        content = content
    )
}