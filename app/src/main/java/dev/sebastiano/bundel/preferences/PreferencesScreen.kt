@file:OptIn(ExperimentalAnimationApi::class)

package dev.sebastiano.bundel.preferences

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Switch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.sebastiano.bundel.BuildConfig
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.SetupTransparentSystemUi
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import dev.sebastiano.bundel.ui.BundelTheme
import dev.sebastiano.bundel.ui.BundelYouTheme
import dev.sebastiano.bundel.ui.singlePadding
import dev.sebastiano.bundel.util.appendIf
import dev.sebastiano.bundel.util.pluralsResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PreferencesScreen(
    activeDaysViewModel: ActiveDaysViewModel = hiltViewModel(),
    activeTimeRangesViewModel: ActiveTimeRangesViewModel = hiltViewModel(),
    excludedAppsViewModel: ExcludedAppsViewModel = hiltViewModel(),
    winteryEasterEggViewModel: WinteryEasterEggViewModel = hiltViewModel(),
    debugPreferencesViewModel: DebugPreferencesViewModel = hiltViewModel(),
    onSelectAppsClicked: () -> Unit,
    onSelectDaysClicked: () -> Unit,
    onSelectTimeRangesClicked: () -> Unit,
    onLicensesLinkClick: () -> Unit,
    onSourcesLinkClick: () -> Unit,
    onBackPress: () -> Unit
) {
    SetupTransparentSystemUi(actualBackgroundColor = MaterialTheme.colorScheme.primaryContainer)

    Scaffold(
        topBar = { PreferencesTopAppBar(stringResource(id = R.string.settings), onBackPress) },
        modifier = Modifier.systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ActiveDaysRow(activeDaysViewModel.daysScheduleFlow, onSelectDaysClicked)

            Divider()

            ActiveTimeRangesRow(activeTimeRangesViewModel.timeRangesScheduleFlow, onSelectTimeRangesClicked)

            Divider()

            ExcludedAppsRow(excludedAppsViewModel.excludedAppsCountFlow, onSelectAppsClicked)

            Divider()

            if (winteryEasterEggViewModel.isWinteryEasterEggPeriod()) {
                val easterEggEnabled by winteryEasterEggViewModel.easterEggEnabledFlow
                    .collectAsState(initial = DataStorePreferences.DEFAULT_WINTERY_EASTER_EGG_ENABLED)
                EasterEggEnabledSwitchRow(easterEggEnabled) { winteryEasterEggViewModel.setEnabled(it) }

                Divider()
            }
            AboutAppRow(onSourcesLinkClick, onLicensesLinkClick)

            if (BuildConfig.DEBUG) {
                Divider()

                val context = LocalContext.current
                DebugPreferencesRow(debugPreferencesViewModel.useShortSnoozeWindow) {
                    handleDebugPreferenceClick(it, context, debugPreferencesViewModel)
                }
            }
        }
    }
}

private fun handleDebugPreferenceClick(
    event: DebugPreferencesEvent,
    context: Context,
    viewModel: DebugPreferencesViewModel
) {
    when (event) {
        DebugPreferencesEvent.SendTestNotification -> viewModel.postTestNotification(context)
        is DebugPreferencesEvent.UseShortSnoozeWindow -> viewModel.setUseShortSnoozeWindow(event.enabled)
    }
}

@Composable
fun EasterEggEnabledSwitchRow(easterEggEnabled: Boolean, onEnableChange: (Boolean) -> Unit) {
    Row(
        Modifier.settingsRow(clickable = true, onClick = { onEnableChange(!easterEggEnabled) }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Enable wintery easter egg", Modifier.weight(1f))
        Spacer(Modifier.width(8.dp))
        Switch(checked = easterEggEnabled, onCheckedChange = { onEnableChange(!easterEggEnabled) })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PreferencesTopAppBar(
    title: String,
    onBackPress: () -> Unit
) {
    SmallTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackPress) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_24),
                    contentDescription = stringResource(id = R.string.menu_back_content_description)
                )
            }
        },
        title = { Text(title) }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("FlowOperatorInvokedInComposition") // TODO fix this crap
@Composable
private fun ActiveDaysRow(
    daysScheduleFlow: Flow<Map<WeekDay, Boolean>>,
    onSelectDaysClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.settingsRow(clickable = true, onClick = onSelectDaysClicked)
    ) {
        Text(text = stringResource(R.string.settings_active_days))

        // TODO don't use flow operators in composition
        val daysResIds by daysScheduleFlow
            .map { daysMap ->
                daysMap.entries.filter { it.value }
                    .map { it.key.displayResId }
            }
            .collectAsState(initial = emptyList())

        AnimatedContent(daysResIds) { resIds ->
            @Suppress("SimplifiableCallChain") // joinToString is not inline so noooope
            Text(
                text = resIds.map { stringResource(id = it) }.joinToString(", "),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.alpha(ContentAlpha.disabled)
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("FlowOperatorInvokedInComposition") // TODO fix this crap
@Composable
private fun ActiveTimeRangesRow(
    timeRangesFlow: Flow<TimeRangesSchedule>,
    onSelectTimeRangesClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.settingsRow(clickable = true, onClick = onSelectTimeRangesClicked)
    ) {
        Text(text = stringResource(R.string.settings_time_ranges))

        // TODO don't use flow operators in composition
        val rangesCount by timeRangesFlow
            .map { schedule -> schedule.size }
            .collectAsState(initial = 0)

        AnimatedContent(rangesCount) { count ->
            Text(
                text = pluralsResource(R.plurals.settings_time_ranges_count, count, count),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.alpha(ContentAlpha.disabled)
            )
        }
    }
}

@Composable
private fun ExcludedAppsRow(
    excludedAppsCountFlow: Flow<Int>,
    onSelectAppsClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.settingsRow(clickable = true, onClick = onSelectAppsClicked)
    ) {
        Text(text = stringResource(R.string.settings_exclude_apps))

        val spiketacularAppsCount by excludedAppsCountFlow
            .collectAsState(initial = 0)

        AnimatedContent(spiketacularAppsCount) { count ->
            Text(
                text = pluralsResource(R.plurals.settings_excluded_apps_count, count, count),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.alpha(ContentAlpha.disabled)
            )
        }
    }
}

@Preview
@Composable
fun AboutAppRowPreview() {
    BundelYouTheme {
        Surface {
            AboutAppRow(onSourcesLinkClick = {}, onLicensesLinkClick = { })
        }
    }
}

@Composable
private fun AboutAppRow(
    onSourcesLinkClick: () -> Unit,
    onLicensesLinkClick: () -> Unit
) {
    Column(
        modifier = Modifier.settingsRow(clickable = false),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = stringResource(R.string.settings_about))

        val baseTextStyle = MaterialTheme.typography.bodySmall
            .copy(MaterialTheme.typography.bodySmall.color.copy(ContentAlpha.disabled))
        Text(
            text = stringResource(R.string.settings_app_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE),
            style = baseTextStyle,
        )

        val linkSpanStyle = SpanStyle(
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Medium,
        )

        AnnotatedClickableText(
            prefixText = stringResource(R.string.settings_about_oss_prefix),
            linkText = stringResource(R.string.settings_about_oss_link),
            textStyle = baseTextStyle,
            linkSpanStyle = linkSpanStyle,
            onClick = onLicensesLinkClick,
        )

        AnnotatedClickableText(
            prefixText = stringResource(R.string.settings_about_sources_prefix),
            linkText = stringResource(R.string.settings_about_sources_link),
            textStyle = baseTextStyle,
            linkSpanStyle = linkSpanStyle,
            onClick = onSourcesLinkClick,
        )
    }
}

@Composable
fun AnnotatedClickableText(
    prefixText: String,
    linkText: String,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    linkSpanStyle: SpanStyle = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold),
    onClick: () -> Unit,
) {
    val annotatedText = buildAnnotatedString {
        append(prefixText)

        pushStringAnnotation(tag = "my-link", annotation = "irrelevant")
        withStyle(
            style = linkSpanStyle
        ) {
            append(linkText)
        }

        pop()
    }

    ClickableText(
        text = annotatedText,
        style = textStyle,
        onClick = { offset ->
            val annotation = annotatedText.getStringAnnotations(tag = "my-link", start = offset, end = offset)
                .firstOrNull()
            if (annotation != null) onClick()
        }
    )
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun DebugSettingsPreview() {
    BundelTheme {
        Column(Modifier.fillMaxWidth()) {
            DebugPreferencesRow(flow { emit(false) }) {}
        }
    }
}

// TODO move to its own screen
@Composable
private fun DebugPreferencesRow(
    useShortSnoozeWindowFlow: Flow<Boolean>,
    onDebugPreferenceClick: (DebugPreferencesEvent) -> Unit
) {
    Text(
        text = stringResource(R.string.debug_settings_header),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.settingsRow(clickable = false)
    )

    Text(
        text = stringResource(R.string.debug_preference_test_notification),
        modifier = Modifier.settingsRow(clickable = true) {
            onDebugPreferenceClick(DebugPreferencesEvent.SendTestNotification)
        }
    )

    val useShortSnoozeWindow by useShortSnoozeWindowFlow.collectAsState(initial = false)
    val switchTextSpikeCam = stringResource(R.string.debug_preference_short_snooze_window)
    Row(
        modifier = Modifier
            .settingsRow(clickable = true) { onDebugPreferenceClick(DebugPreferencesEvent.UseShortSnoozeWindow(!useShortSnoozeWindow)) }
            .semantics(mergeDescendants = true) {
                set(SemanticsProperties.Role, Role.Switch)
                set(SemanticsProperties.Text, listOf(AnnotatedString(switchTextSpikeCam)))
                set(SemanticsProperties.ToggleableState, ToggleableState(useShortSnoozeWindow))
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = switchTextSpikeCam,
            modifier = Modifier
                .padding(end = singlePadding())
                .weight(1f)
        )

        Switch(
            checked = useShortSnoozeWindow,
            onCheckedChange = null
        )
    }
}

internal sealed class DebugPreferencesEvent {
    object SendTestNotification : DebugPreferencesEvent()
    data class UseShortSnoozeWindow(val enabled: Boolean) : DebugPreferencesEvent()
}

private fun Modifier.settingsRow(clickable: Boolean, onClick: () -> Unit = {}): Modifier =
    fillMaxWidth()
        .appendIf(clickable) { clickable { onClick() } }
        .padding(16.dp)
