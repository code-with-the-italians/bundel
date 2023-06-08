package dev.sebastiano.bundel.onboarding

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.sebastiano.bundel.ui.BundelYouTheme
import dev.sebastiano.bundel.ui.composables.TimeRange
import dev.sebastiano.bundel.ui.composables.TimeRangeRow
import dev.sebastiano.bundel.ui.composables.checkedMaterialPillAppearance
import dev.sebastiano.bundel.ui.composables.onboardingCheckedPillAppearance
import dev.sebastiano.bundel.ui.composables.onboardingUncheckedPillAppearance
import java.time.LocalTime

@Suppress("unused")
internal class OnboardingPreviews {

    @Suppress("MagicNumber") // It's a preview
    @Preview(name = "Inactive", group = "Onboarding")
    @Preview(name = "Inactive Night", group = "Onboarding", uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun TimeRangeRowOnboardingInactivePreview() {
        BundelYouTheme {
            Surface {
                TimeRangeRow(
                    modifier = Modifier.fillMaxWidth(),
                    pickerBackgroundColor = MaterialTheme.colorScheme.secondary,
                    enabled = false
                )
            }
        }
    }

    @Suppress("MagicNumber") // It's a preview
    @Preview(name = "Active", group = "Onboarding")
    @Preview(name = "Active Night", group = "Onboarding", uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun TimeRangeRowOnboardingActivePreview() {
        BundelYouTheme {
            Surface {
                TimeRangeRow(
                    modifier = Modifier.fillMaxWidth(),
                    pickerBackgroundColor = MaterialTheme.colorScheme.secondary,
                    timeRange = TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 30)),
                    enabled = true,
                    canBeRemoved = true
                )
            }
        }
    }
}

@Suppress("unused")
internal class AppThemePreviews {

    @Suppress("MagicNumber") // It's a preview
    @Preview(name = "Inactive", group = "App theme")
    @Preview(name = "Inactive Night", group = "App theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun TimeRangeRowInactivePreview() {
        BundelYouTheme {
            Surface {
                TimeRangeRow(
                    modifier = Modifier.fillMaxWidth(),
                    expandedPillAppearance = checkedMaterialPillAppearance(
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    normalPillAppearance = checkedMaterialPillAppearance(
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    enabled = false
                )
            }
        }
    }

    @Suppress("MagicNumber") // It's a preview
    @Preview(name = "Active", group = "App theme")
    @Preview(name = "Active Night", group = "App theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun TimeRangeRowActivePreview() {
        BundelYouTheme {
            Surface {
                TimeRangeRow(
                    modifier = Modifier.fillMaxWidth(),
                    expandedPillAppearance = onboardingCheckedPillAppearance(),
                    normalPillAppearance = onboardingUncheckedPillAppearance(),
                    timeRange = TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 30)),
                    enabled = true,
                    canBeRemoved = true
                )
            }
        }
    }
}
