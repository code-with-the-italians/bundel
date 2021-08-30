package dev.sebastiano.bundel.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.ui.graphics.vector.ImageVector
import dev.sebastiano.bundel.R

internal sealed class NavigationRoute(val route: String) {

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

    object SettingsGraph : NavigationRoute("settings") {

        object SettingsScreen : NavigationRoute("settings.screen")

        object SelectApps : NavigationRoute("settings.select_apps")
    }

    interface BottomNavNavigationRoute {

        val icon: ImageVector

        @get:StringRes
        val labelId: Int
    }
}
