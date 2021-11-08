package dev.sebastiano.bundel.preferences

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.composables.checkedMaterialPillAppearance
import dev.sebastiano.bundel.onboarding.DaysPicker
import dev.sebastiano.bundel.ui.regularThemeMaterialChipBackgroundColor
import dev.sebastiano.bundel.ui.regularThemeMaterialChipContentColor
import dev.sebastiano.bundel.ui.singlePadding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onStart

// As of Accompanist Material Nav 0.19.0, this is needed to avoid race conditions
// when showing the bottom sheets that cause them to... err... not show.
// 🏁  🚗💨
private const val WACKY_RACES_CONDITION = 40L

@SuppressLint("FlowOperatorInvokedInComposition") // TODO fix this crap
@Composable
internal fun SelectDaysDialog(
    viewModel: ActiveDaysViewModel = hiltViewModel(),
    onDialogDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_active_days_blurb),
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        val daysSchedule by viewModel.daysScheduleFlow
            .onStart { delay(WACKY_RACES_CONDITION) }
            .collectAsState(initial = emptyMap())

        DaysPicker(
            daysSchedule = daysSchedule,
            onDayCheckedChange = { weekDay, checked -> viewModel.onDaysScheduleChangeWeekDay(weekDay, checked) },
            chipsSpacing = singlePadding(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            checkedAppearance = checkedMaterialPillAppearance(
                backgroundColor = MaterialTheme.colors.regularThemeMaterialChipBackgroundColor(true),
                contentColor = MaterialTheme.colors.regularThemeMaterialChipContentColor(true)
            ),
            uncheckedAppearance = checkedMaterialPillAppearance(
                backgroundColor = MaterialTheme.colors.regularThemeMaterialChipBackgroundColor(false),
                contentColor = MaterialTheme.colors.regularThemeMaterialChipContentColor(false)
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(onClick = onDialogDismiss, modifier = Modifier.align(Alignment.End)) {
            Text(text = "DONE")
        }
    }
}
