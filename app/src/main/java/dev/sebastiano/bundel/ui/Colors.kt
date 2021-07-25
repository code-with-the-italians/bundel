package dev.sebastiano.bundel.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val bundel_black = Color(0xFF000000)
private val bundel_white = Color(0xFFFFFFFF)
private val bundel_background_gray = Color(0xFFE5E5E5)
private val bundel_light_gray = Color(0xFFAAAAAA)
private val bundel_green = Color(0xFF4CE062)
private val bundel_green_dark = Color(0xFF1E8F3E)
private val bundel_purple = Color(0xFF4F1D91)
private val bundel_purple_dark = Color(0xFF3C166D)
private val bundel_dark_background_gray = Color(0xFF101010)
private val bundel_dark_green = Color(0xFF33783D)
private val bundel_dark_green_dark = Color(0xFF224D28)
private val bundel_dark_purple = Color(0xFF33135D)
private val bundel_dark_purple_dark = Color(0xFF240E42)
private val pillBackgroundLight = Color.LightGray
private val pillBackgroundDark = Color.DarkGray
internal val bundelLightColors = lightColors(
    primary = bundel_green,
    secondary = bundel_purple,
    surface = bundel_white,
    onSurface = bundel_black,
    primaryVariant = bundel_green_dark,
    background = bundel_background_gray
)
internal val bundelDarkColors = darkColors(
    primary = bundel_dark_green,
    secondary = bundel_dark_purple,
    surface = bundel_black,
    onSurface = bundel_light_gray,
    primaryVariant = bundel_dark_green_dark,
    background = bundel_dark_background_gray
)
internal val bundelOnboardingLightColors = lightColors(
    primary = bundel_purple,
    onPrimary = bundel_white,
    secondary = bundel_white,
    onSecondary = bundel_black,
    surface = bundel_green,
    onSurface = bundel_white,
    primaryVariant = bundel_purple_dark,
    background = bundel_green,
    onBackground = bundel_white
)
internal val bundelOnboardingDarkColors = darkColors(
    primary = bundel_dark_purple,
    onPrimary = bundel_light_gray,
    secondary = bundel_dark_purple,
    onSecondary = bundel_light_gray,
    surface = bundel_dark_green_dark,
    onSurface = bundel_light_gray,
    primaryVariant = bundel_dark_purple_dark,
    background = bundel_dark_green_dark,
    onBackground = bundel_light_gray
)
internal val Colors.pillBackground: Color
    get() = if (isLight) pillBackgroundLight else pillBackgroundDark

@Composable
internal fun bundelColors(darkModeOverride: Boolean? = null): Colors =
    getBundelColors(darkModeOverride ?: isSystemInDarkTheme())

@Composable
internal fun bundelOnboardingColors(darkModeOverride: Boolean? = null): Colors =
    getBundelOnboardingColors(darkModeOverride ?: isSystemInDarkTheme())

internal fun getBundelColors(darkMode: Boolean = false): Colors =
    if (darkMode) bundelDarkColors else bundelLightColors

internal fun getBundelOnboardingColors(darkMode: Boolean = false): Colors =
    if (darkMode) bundelOnboardingDarkColors else bundelOnboardingLightColors
