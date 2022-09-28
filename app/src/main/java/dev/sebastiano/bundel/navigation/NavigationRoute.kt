package dev.sebastiano.bundel.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.ui.graphics.vector.ImageVector
import dev.sebastiano.bundel.R

internal sealed class NavigationRoute(val route: String) {

    object SplashoScreenButWithAWeirdNameNotToTriggerLint : NavigationRoute("benThereDoneThat")

    object OnboardingGraph : NavigationRoute("onboarding") {

        object OnboardingScreen : NavigationRoute("onboarding.screen")
    }

    object MainScreenGraph : NavigationRoute("main_screen") {

        object MainScreen : NavigationRoute("main_screen.screen")

        object NotificationsList : NavigationRoute("main_screen.notifications_list"), BottomNavNavigationRoute {

            override val icon: ImageVector = Icons.Rounded.NotificationsActive
            override val labelId: Int = R.string.bottom_nav_active_notifications
        }

        object History : NavigationRoute("main_screen.history"), BottomNavNavigationRoute {

            override val icon: ImageVector = Icons.Rounded.History
            override val labelId: Int = R.string.bottom_nav_history
        }
    }

    object PreferencesGraph : NavigationRoute("preferences") {

        object PreferencesScreen : NavigationRoute("preferences.screen")

        object SelectApps : NavigationRoute("preferences.select_apps")

        object SelectDays : NavigationRoute("preferences.babbadibuppi")

        object SelectTimeRanges : NavigationRoute("preferences.time-ranges")

        object Licenses : NavigationRoute("preferences.absentFriends")

        object TestWidget : NavigationRoute("preferences.testWidget")
    }

    interface BottomNavNavigationRoute {

        val icon: ImageVector

        @get:StringRes
        val labelId: Int
    }
}
