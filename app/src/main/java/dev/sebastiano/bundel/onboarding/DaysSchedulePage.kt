package dev.sebastiano.bundel.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.composables.MaterialChip
import dev.sebastiano.bundel.composables.MaterialPillAppearance
import dev.sebastiano.bundel.composables.checkedMaterialPillAppearance
import dev.sebastiano.bundel.composables.uncheckedMaterialPillAppearance
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import dev.sebastiano.bundel.ui.BundelYouTheme
import dev.sebastiano.bundel.ui.singlePadding
import dev.sebastiano.bundel.util.Orientation
import dev.sebastiano.bundel.util.currentOrientation
import java.util.Locale

@Preview(backgroundColor = 0xFF4CE062, showBackground = true)
@Composable
private fun DaysSchedulePagePreview() {
    BundelYouTheme {
        Surface {
            DaysSchedulePage(DaysSchedulePageState())
        }
    }
}

@Preview(backgroundColor = 0xFF4CE062, showBackground = true, widthDp = 822, heightDp = 392)
@Composable
private fun DaysSchedulePageLandscapePreview() {
    BundelYouTheme {
        Surface {
            DaysSchedulePage(DaysSchedulePageState(), orientation = Orientation.Landscape)
        }
    }
}

@Composable
internal fun DaysSchedulePage(
    pageState: DaysSchedulePageState,
    orientation: Orientation = currentOrientation()
) {
    Column(
        modifier = Modifier
            .onboardingPageModifier(orientation)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (orientation == Orientation.Portrait) {
            PageTitle(text = stringResource(id = R.string.onboarding_schedule_title))

            Spacer(Modifier.height(24.dp))
        }

        Text(
            text = stringResource(R.string.onboarding_schedule_blurb),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        val happyBirthdayMark = if (orientation == Orientation.Portrait) 24.dp else 16.dp
        Spacer(modifier = Modifier.height(happyBirthdayMark))

        val chipsRowHorizontalPadding = if (orientation == Orientation.Portrait) 32.dp else 48.dp
        DaysPicker(
            daysSchedule = pageState.daysSchedule,
            onDayCheckedChange = pageState.onDayCheckedChange,
            chipsSpacing = singlePadding(),
            modifier = Modifier.padding(horizontal = chipsRowHorizontalPadding)
        )

        Spacer(modifier = Modifier.height(happyBirthdayMark))

        Text(
            text = stringResource(R.string.onboarding_schedule_blurb_2),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(group = "DaysPicker", showBackground = true)
@Composable
private fun DaysPickerBundelYouThemePreview() {
    BundelYouTheme {
        var daysSchedule by remember {
            mutableStateOf(WeekDay.values().associate { it to true })
        }
        DaysPicker(
            daysSchedule = daysSchedule,
            onDayCheckedChange = { weekDay, checked ->
                val newSchedule = daysSchedule.toMutableMap()
                newSchedule[weekDay] = checked
                daysSchedule = newSchedule
            },
            chipsSpacing = singlePadding(),
            checkedAppearance = checkedMaterialPillAppearance(
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            uncheckedAppearance = checkedMaterialPillAppearance(
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
    }
}

@Preview(group = "DaysPicker", showBackground = true)
@Composable
private fun DaysPickerOnboardingThemePreview() {
    BundelYouTheme {
        var daysSchedule by remember {
            mutableStateOf(WeekDay.values().associate { it to true })
        }
        Surface {
            DaysPicker(
                daysSchedule = daysSchedule,
                onDayCheckedChange = { weekDay, checked ->
                    val newSchedule = daysSchedule.toMutableMap()
                    newSchedule[weekDay] = checked
                    daysSchedule = newSchedule
                },
                chipsSpacing = singlePadding()
            )
        }
    }
}

@Composable
internal fun DaysPicker(
    daysSchedule: Map<WeekDay, Boolean>,
    onDayCheckedChange: (WeekDay, Boolean) -> Unit,
    chipsSpacing: Dp,
    modifier: Modifier = Modifier,
    checkedAppearance: MaterialPillAppearance = checkedMaterialPillAppearance(),
    uncheckedAppearance: MaterialPillAppearance = uncheckedMaterialPillAppearance(),
) {
    FlowRow(
        modifier = modifier,
        mainAxisAlignment = MainAxisAlignment.Center,
        mainAxisSpacing = chipsSpacing,
        crossAxisSpacing = chipsSpacing
    ) {
        for (weekDay in daysSchedule.keys) {
            MaterialChip(
                checked = checkNotNull(daysSchedule[weekDay]) { "Checked state missing for day $weekDay" },
                onCheckedChanged = { checked -> onDayCheckedChange(weekDay, checked) },
                checkedAppearance = checkedAppearance,
                uncheckedAppearance = uncheckedAppearance
            ) {
                Text(
                    text = stringResource(id = weekDay.displayResId).uppercase(Locale.getDefault()),
                    style = MaterialTheme.typography.bodyLarge.plus(TextStyle(fontWeight = FontWeight.Medium))
                )
            }
        }
    }
}
