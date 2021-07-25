package dev.sebastiano.bundel.onboarding

import android.content.res.Resources
import androidx.activity.ComponentActivity
import androidx.compose.material.Surface
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.sebastiano.bundel.ui.BundelOnboardingTheme
import dev.sebastiano.bundel.R
import org.junit.Rule
import org.junit.Test

internal class OnboardingAndroidUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val resources: Resources
        get() = composeTestRule.activity.resources

    @Test
    fun should_go_to_second_page_when_clicking_next_button() {
        composeTestRule.setContent {
            BundelOnboardingTheme {
                Surface {
                    OnboardingScreen()
                }
            }
        }

        composeTestRule.onNodeWithText(resources.getString(R.string.next).uppercase())
            .assertExists("Next button is missing")
            .performClick()

        composeTestRule.onNodeWithText("Give us access", substring = true)
            .assertExists("We're not on the second page")
    }

    @Test
    fun should_not_show_back_button_on_first_page() {
        composeTestRule.setContent {
            BundelOnboardingTheme {
                Surface {
                    OnboardingScreen()
                }
            }
        }

        composeTestRule.onNodeWithText(resources.getString(R.string.back).uppercase())
            .assertDoesNotExist()
    }

    @Test
    fun should_hide_back_button_when_getting_back_to_first_page() {
        composeTestRule.setContent {
            BundelOnboardingTheme {
                Surface {
                    OnboardingScreen()
                }
            }
        }

        composeTestRule.onNodeWithText(resources.getString(R.string.next).uppercase())
            .assertExists("Next button is missing")
            .performClick()

        composeTestRule.onNodeWithText(resources.getString(R.string.back).uppercase())
            .assertExists()
            .performClick()
            .assertDoesNotExist()
    }
}
