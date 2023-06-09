package dev.sebastiano.bundel

import android.content.res.Resources
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.device.EspressoDevice.Companion.onDevice
import androidx.test.espresso.device.action.ScreenOrientation
import androidx.test.espresso.device.rules.ScreenOrientationRule
import androidx.test.espresso.device.setScreenOrientation
import dev.sebastiano.bundel.onboarding.OnboardingScreen
import dev.sebastiano.bundel.onboarding.OnboardingViewModel
import dev.sebastiano.bundel.ui.BundelYouTheme
import org.junit.Rule
import org.junit.Test

class RobertoFoldTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule
    val robRulez = ScreenOrientationRule(ScreenOrientation.PORTRAIT)

    private val resources: Resources
        get() = composeTestRule.activity.resources

    @Test
    fun run_the_onboarding_yey() {
        composeTestRule.setContent {
            BundelYouTheme {
                OnboardingScreen(
                    viewModel = OnboardingViewModel(FakePreferences()),
                    needsPermission = true,
                    onOpenNotificationPreferencesClick = { /*TODO*/ }
                ) { }
            }
        }

        composeTestRule.onNodeWithText(resources.getString(R.string.next).uppercase())
            .assertExists("Next button is missing")
            .performClick()

        composeTestRule.onNodeWithText(resources.getString(R.string.onboarding_notifications_permission_title))
            .assertExists("Not on next page")

        onDevice().setScreenOrientation(ScreenOrientation.LANDSCAPE)

        composeTestRule.onNodeWithText(resources.getString(R.string.onboarding_notifications_permission_title))
            .assertExists("Not on page 2 anymore")
    }
}
