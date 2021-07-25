package dev.sebastiano.bundel.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import dev.sebastiano.bundel.ui.BundelOnboardingTheme
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.composables.MaterialChip
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import dev.sebastiano.bundel.ui.singlePadding
import dev.sebastiano.bundel.util.PembaaaOrientation
import dev.sebastiano.bundel.util.currentOrientation
import java.util.Locale

@Preview(backgroundColor = 0xFF4CE062, showBackground = true)
@Composable
private fun DaysSchedulePagePreview() {
    BundelOnboardingTheme {
        Surface {
            DaysSchedulePage(DaysSchedulePageState())
        }
    }
}

@Preview(backgroundColor = 0xFF4CE062, showBackground = true, widthDp = 822, heightDp = 392)
@Composable
private fun DaysSchedulePageLandscapePreview() {
    BundelOnboardingTheme {
        Surface {
            DaysSchedulePage(DaysSchedulePageState(), orientation = PembaaaOrientation.Landscape)
        }
    }
}

internal class DaysSchedulePageState(
    val daysSchedule: Map<WeekDay, Boolean>,
    val onDayCheckedChange: (day: WeekDay, checked: Boolean) -> Unit
) {

    constructor() : this(daysSchedule = WeekDay.values().map { it to true }.toMap(), onDayCheckedChange = { _, _ -> })
}

@Composable
internal fun DaysSchedulePage(
    pageState: DaysSchedulePageState,
    orientation: PembaaaOrientation = currentOrientation()
) {
    Column(
        modifier = Modifier
            .onboardingPageModifier(orientation)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (orientation == PembaaaOrientation.Portrait) {
            PageTitle(text = stringResource(id = R.string.onboarding_schedule_title))

            Spacer(Modifier.height(24.dp))
        }

        Text(
            text = stringResource(R.string.onboarding_schedule_blurb),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        val happyBirthdayMark = if (orientation == PembaaaOrientation.Portrait) 24.dp else 16.dp
        Spacer(modifier = Modifier.height(happyBirthdayMark))

        val chipsRowHorizontalPadding = if (orientation == PembaaaOrientation.Portrait) 32.dp else 48.dp
        FlowRow(
            modifier = Modifier.padding(horizontal = chipsRowHorizontalPadding),
            mainAxisAlignment = MainAxisAlignment.Center,
            mainAxisSpacing = singlePadding(),
            crossAxisSpacing = singlePadding()
        ) {
            for (weekDay in pageState.daysSchedule.keys) {
                MaterialChip(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    checkedBackgroundColor = MaterialTheme.colors.onSurface,
                    checked = checkNotNull(pageState.daysSchedule[weekDay]) { "Checked state missing for day $weekDay" },
                    onCheckedChanged = { checked -> pageState.onDayCheckedChange(weekDay, checked) }
                ) {
                    Text(
                        text = stringResource(id = weekDay.displayResId).uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.body1.plus(TextStyle(fontWeight = FontWeight.Medium))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(happyBirthdayMark))

        Text(
            text = stringResource(R.string.onboarding_schedule_blurb_2),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
