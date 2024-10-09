package dev.sebastiano.bundel.ui.composables

import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import org.junit.Rule
import org.junit.Test

class OnboardingPreviewsTest {
    @get:Rule
    val paparazzi = Paparazzi(
        renderingMode = SessionParams.RenderingMode.SHRINK
    )

    @Test
    fun active() {
        paparazzi.snapshot {
            OnboardingPreviews().TimeRangeRowOnboardingActivePreview()
        }
    }

    @Test
    fun inactive() {
        paparazzi.snapshot {
            OnboardingPreviews().TimeRangeRowOnboardingInactivePreview()
        }
    }
}
