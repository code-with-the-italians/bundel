package dev.sebastiano.bundel.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// MATERIAL 3 COLORS

private val md_theme_light_primary = Color(0xFF006e1e)
private val md_theme_light_onPrimary = Color(0xFFffffff)
private val md_theme_light_primaryContainer = Color(0xFF6eff7d)
private val md_theme_light_onPrimaryContainer = Color(0xFF08200a)
private val md_theme_light_secondary = Color(0xFF7245b5)
private val md_theme_light_onSecondary = Color(0xFFffffff)
private val md_theme_light_secondaryContainer = Color(0xFFeedcff)
private val md_theme_light_onSecondaryContainer = Color(0xFF270057)
private val md_theme_light_tertiary = Color(0xFF006e26)
private val md_theme_light_onTertiary = Color(0xFFffffff)
private val md_theme_light_tertiaryContainer = Color(0xFF8ffa9b)
private val md_theme_light_onTertiaryContainer = Color(0xFF002106)
private val md_theme_light_error = Color(0xFFba1b1b)
private val md_theme_light_errorContainer = Color(0xFFffdad4)
private val md_theme_light_onError = Color(0xFFffffff)
private val md_theme_light_onErrorContainer = Color(0xFF410001)
private val md_theme_light_background = Color(0xFFfcfdf6)
private val md_theme_light_onBackground = Color(0xFF1a1c19)
private val md_theme_light_surface = Color(0xFFfcfdf6)
private val md_theme_light_onSurface = Color(0xFF1a1c19)
private val md_theme_light_surfaceVariant = Color(0xFFdee4d9)
private val md_theme_light_onSurfaceVariant = Color(0xFF424840)
private val md_theme_light_outline = Color(0xFF72796f)
private val md_theme_light_inverseOnSurface = Color(0xFFf0f1eb)
private val md_theme_light_inverseSurface = Color(0xFF2e312d)

private val md_theme_dark_primary = Color(0xFF045510)
private val md_theme_dark_onPrimary = Color(0xFF00390a)
private val md_theme_dark_primaryContainer = Color(0xFF005314)
private val md_theme_dark_onPrimaryContainer = Color(0xFF6eff7d)
private val md_theme_dark_secondary = Color(0xFFd8baff)
private val md_theme_dark_onSecondary = Color(0xFF420784)
private val md_theme_dark_secondaryContainer = Color(0xFF5a2b9c)
private val md_theme_dark_onSecondaryContainer = Color(0xFFeedcff)
private val md_theme_dark_tertiary = Color(0xFF73dc81)
private val md_theme_dark_onTertiary = Color(0xFF00390f)
private val md_theme_dark_tertiaryContainer = Color(0xFF00531b)
private val md_theme_dark_onTertiaryContainer = Color(0xFF8ffa9b)
private val md_theme_dark_error = Color(0xFFffb4a9)
private val md_theme_dark_errorContainer = Color(0xFF930006)
private val md_theme_dark_onError = Color(0xFF680003)
private val md_theme_dark_onErrorContainer = Color(0xFFffdad4)
private val md_theme_dark_background = Color(0xFF1a1c19)
private val md_theme_dark_onBackground = Color(0xFFe2e3dd)
private val md_theme_dark_surface = Color(0xFF1a1c19)
private val md_theme_dark_onSurface = Color(0xFFe2e3dd)
private val md_theme_dark_surfaceVariant = Color(0xFF424840)
private val md_theme_dark_onSurfaceVariant = Color(0xFFc2c9bd)
private val md_theme_dark_outline = Color(0xFF8c9388)
private val md_theme_dark_inverseOnSurface = Color(0xFF1a1c19)
private val md_theme_dark_inverseSurface = Color(0xFFe2e3dd)

private val seed = Color(0xFF4ce062)
private val error = Color(0xFFba1b1b)
private val Custom0 = Color(0xFF3b64eb)

// MATERIAL 2 COLORS

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
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryVariant = md_theme_light_tertiary,
    secondary = md_theme_light_secondary,
    secondaryVariant = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    error = md_theme_light_error,
    onError = md_theme_light_onError
)

internal val bundelDarkColors = darkColors(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryVariant = md_theme_dark_tertiary,
    secondary = md_theme_dark_secondary,
    secondaryVariant = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError
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

internal val LightThemeColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
)

internal val OnboardingLightColorsSmol = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_inverseSurface,
    onSurface = md_theme_light_inverseOnSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_onSurface,
    inverseSurface = md_theme_light_surface,
)

internal val DarkThemeColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
)
