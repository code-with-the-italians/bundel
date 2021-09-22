package dev.sebastiano.bundel.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.sebastiano.bundel.composables.checkedMaterialPillAppearance
import dev.sebastiano.bundel.onboarding.DaysPicker
import dev.sebastiano.bundel.ui.regularThemeMaterialChipBackgroundColor
import dev.sebastiano.bundel.ui.regularThemeMaterialChipContentColor
import dev.sebastiano.bundel.ui.singlePadding

@Composable
internal fun SelectDaysDialog(
    viewModel: ActiveDaysViewModel = hiltViewModel(),
    onDialogDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.padding(48.dp) // TODO this shouldn't be needed...
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Choose the days on which Bundel will be active.")

            Spacer(modifier = Modifier.height(16.dp))

            val daysSchedule by viewModel.daysScheduleFlow.collectAsState(initial = emptyMap())
            DaysPicker(
                daysSchedule = daysSchedule,
                onDayCheckedChange = { weekDay, checked -> viewModel.onDaysScheduleChangeWeekDay(weekDay, checked) },
                chipsSpacing = singlePadding(),
                modifier = Modifier.fillMaxWidth(),
                checkedAppearance = checkedMaterialPillAppearance(
                    backgroundColor = MaterialTheme.colors.regularThemeMaterialChipBackgroundColor(true),
                    contentColor = MaterialTheme.colors.regularThemeMaterialChipContentColor(true)
                ),
                uncheckedAppearance = checkedMaterialPillAppearance(
                    backgroundColor = MaterialTheme.colors.regularThemeMaterialChipBackgroundColor(false),
                    contentColor = MaterialTheme.colors.regularThemeMaterialChipContentColor(false)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onDialogDismiss, modifier = Modifier.align(Alignment.End)) {
                Text(text = "DONE")
            }
        }
    }
}
