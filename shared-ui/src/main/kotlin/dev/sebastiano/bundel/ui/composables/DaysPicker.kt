package dev.sebastiano.bundel.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import java.util.Locale

@Composable
fun DaysPicker(
    daysSchedule: Map<WeekDay, Boolean>,
    onDayCheckedChange: (WeekDay, Boolean) -> Unit,
    chipsSpacing: Dp,
    modifier: Modifier = Modifier,
    checkedAppearance: MaterialPillAppearance = checkedMaterialPillAppearance(),
    uncheckedAppearance: MaterialPillAppearance = uncheckedMaterialPillAppearance()
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
