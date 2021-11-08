@file:OptIn(ExperimentalAnimationApi::class, ExperimentalTransitionApi::class)

package dev.sebastiano.bundel.preferences

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import dev.sebastiano.bundel.ui.BundelTheme
import dev.sebastiano.bundel.util.appendIf
import dev.sebastiano.bundel.util.pluralsResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Composable
internal fun PreferencesScreen(
    activeDaysViewModel: ActiveDaysViewModel = hiltViewModel(),
    activeTimeRangesViewModel: EnglebertViewModel = hiltViewModel(),
    excludedAppsViewModel: ExcludedAppsViewModel = hiltViewModel(),
    onSelectAppsClicked: () -> Unit,
    onSelectDaysClicked: () -> Unit,
    onSelectTimeRangesClicked: () -> Unit,
    onLicensesLinkClick: () -> Unit,
    onSourcesLinkClick: () -> Unit,
    onBackPress: () -> Unit
) {
    Scaffold(
        topBar = { PreferencesTopAppBar(stringResource(id = R.string.settings), onBackPress) }
    ) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            ActiveDaysRow(activeDaysViewModel.daysScheduleFlow, onSelectDaysClicked)

            Divider()

            ActiveTimeRangesRow(activeTimeRangesViewModel.timeRangesScheduleFlow, onSelectTimeRangesClicked)

            Divider()

            ExcludedAppsRow(excludedAppsViewModel.excludedAppsCountFlow, onSelectAppsClicked)

            Divider()

            AboutAppRow(onSourcesLinkClick, onLicensesLinkClick)
        }
    }
}

@Composable
internal fun PreferencesTopAppBar(
    title: String,
    onBackPress: () -> Unit
) {
    TopAppBar(
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
                style = MaterialTheme.typography.caption,
                modifier = Modifier.alpha(ContentAlpha.disabled)
            )
        }
    }
}

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

        val rangesCount by timeRangesFlow
            .map { schedule -> schedule.size }
            .collectAsState(initial = 0)

        AnimatedContent(rangesCount) { count ->
            Text(
                text = pluralsResource(R.plurals.settings_time_ranges_count, count, count),
                style = MaterialTheme.typography.caption,
                modifier = Modifier.alpha(ContentAlpha.disabled)
            )
        }
    }
}

@Composable
@ExperimentalAnimationApi
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
                style = MaterialTheme.typography.caption,
                modifier = Modifier.alpha(ContentAlpha.disabled)
            )
        }
    }
}

@Preview
@Composable
fun AboutAppRowPreview() {
    BundelTheme {
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

        val baseTextStyle = MaterialTheme.typography.caption
            .copy(MaterialTheme.typography.caption.color.copy(ContentAlpha.disabled))
        Text(
            text = stringResource(R.string.settings_app_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE),
            style = baseTextStyle,
        )

        val linkSpanStyle = SpanStyle(
            color = MaterialTheme.colors.primaryVariant,
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
    textStyle: TextStyle = MaterialTheme.typography.body1,
    linkSpanStyle: SpanStyle = SpanStyle(color = MaterialTheme.colors.primary, fontWeight = FontWeight.Bold),
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

private fun Modifier.settingsRow(clickable: Boolean, onClick: () -> Unit = {}): Modifier =
    fillMaxWidth()
        .appendIf(clickable) { clickable { onClick() } }
        .padding(16.dp)
