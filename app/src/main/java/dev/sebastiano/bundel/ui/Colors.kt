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
private val bundel_dark_gray = Color(0xFF333333)
private val bundel_dark_green = Color(0xFF33783D)
private val bundel_dark_green_dark = Color(0xFF224D28)
private val bundel_dark_purple = Color(0xFF33135D)
private val bundel_dark_purple_dark = Color(0xFF240E42)

// TODO figure out a better way for this
private val chipCheckedBackgroundLight = bundel_green
private val chipCheckedBackgroundDark = bundel_green_dark
private val chipCheckedContentLight = bundel_white
private val chipCheckedContentDark = bundel_light_gray
private val chipUncheckedBackgroundLight = Color(0xFFBBBBBB)
private val chipUncheckedBackgroundDark = bundel_dark_gray
private val chipUncheckedContentLight = bundel_black
private val chipUncheckedContentDark = bundel_light_gray

private val snoozeNotificationBackgroundLight = Color(0xFF3B64EB)
private val snoozeNotificationForegroundLight = bundel_white
private val snoozeNotificationBackgroundDark = Color(0xFF1D3173)
private val snoozeNotificationForegroundDark = Color(0xFFB3B3B3)

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

internal val Colors.notificationSnoozeBackground: Color
    get() = if (isLight) snoozeNotificationBackgroundLight else snoozeNotificationBackgroundDark

internal val Colors.notificationSnoozeForeground: Color
    get() = if (isLight) snoozeNotificationForegroundLight else snoozeNotificationForegroundDark

internal fun Colors.regularThemeMaterialChipBackgroundColor(checked: Boolean): Color =
    if (checked) {
        if (isLight) chipCheckedBackgroundLight else chipCheckedBackgroundDark
    } else {
        if (isLight) chipUncheckedBackgroundLight else chipUncheckedBackgroundDark
    }

internal fun Colors.regularThemeMaterialChipContentColor(checked: Boolean): Color =
    if (checked) {
        if (isLight) chipCheckedContentLight else chipCheckedContentDark
    } else {
        if (isLight) chipUncheckedContentLight else chipUncheckedContentDark
    }

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
