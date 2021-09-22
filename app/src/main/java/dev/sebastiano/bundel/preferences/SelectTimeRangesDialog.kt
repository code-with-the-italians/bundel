package dev.sebastiano.bundel.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule

@Composable
internal fun SelectTimeRangesDialog(
    viewModel: EnglebertViewModel = hiltViewModel(),
    onDialogDismiss: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Choose the hours on which Bundel will be active.")

            Spacer(modifier = Modifier.height(16.dp))

            val timeRangesSchedule by viewModel.timeRangesScheduleFlow.collectAsState(initial = TimeRangesSchedule())
            for (timeRange in timeRangesSchedule) {
                Text(timeRange.toString())
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onDialogDismiss, modifier = Modifier.align(Alignment.End)) {
                Text(text = "DONE")
            }
        }
    }
}
