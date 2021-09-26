package dev.sebastiano.bundel.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import dev.sebastiano.bundel.onboarding.TimeRangeRow
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.ui.regularThemeMaterialChipBackgroundColor
import dev.sebastiano.bundel.ui.regularThemeMaterialChipContentColor

@Composable
internal fun SelectTimeRangesDialog(
    viewModel: EnglebertViewModel = hiltViewModel(),
    onDialogDismiss: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Choose the hours on which Bundel will be active.")

            val timeRangesSchedule by viewModel.timeRangesScheduleFlow.collectAsState(initial = TimeRangesSchedule())
            for (timeRange in timeRangesSchedule) {
                TimeRangeRow(
                    modifier = Modifier.fillMaxWidth(),
                    expandedPillAppearance = checkedMaterialPillAppearance(
                        backgroundColor = MaterialTheme.colors.regularThemeMaterialChipBackgroundColor(true),
                        contentColor = MaterialTheme.colors.regularThemeMaterialChipContentColor(true)
                    ),
                    normalPillAppearance = checkedMaterialPillAppearance(
                        backgroundColor = MaterialTheme.colors.regularThemeMaterialChipBackgroundColor(false),
                        contentColor = MaterialTheme.colors.regularThemeMaterialChipContentColor(false)
                    ),
                    timeRange = timeRange,
                )
            }

            TextButton(onClick = onDialogDismiss, modifier = Modifier.align(Alignment.End)) {
                Text(text = "DONE")
            }
        }
    }
}
