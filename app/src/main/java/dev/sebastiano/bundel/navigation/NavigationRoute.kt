package dev.sebastiano.bundel.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.ui.graphics.vector.ImageVector
import dev.sebastiano.bundel.R

internal abstract class NavigationRoute(val route: String) {

    object Onboarding : NavigationRoute("onboarding")

    object MainScreen : NavigationRoute("main_screen") {

        object NotificationsList : NavigationRoute("notifications_list"), BottomNavNavigationRoute {

            override val icon: ImageVector = Icons.Rounded.NotificationsActive
            override val labelId: Int = R.string.bottom_nav_active_notifications
        }

        object History : NavigationRoute("history"), BottomNavNavigationRoute {

            override val icon: ImageVector = Icons.Rounded.History
            override val labelId: Int = R.string.bottom_nav_history
        }
    }

    interface BottomNavNavigationRoute {

        val icon: ImageVector

        @get:StringRes
        val labelId: Int
    }
}
