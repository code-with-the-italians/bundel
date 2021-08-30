package dev.sebastiano.bundel.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import dev.sebastiano.bundel.MainScreenWithBottomNav
import dev.sebastiano.bundel.onboarding.OnboardingScreen
import dev.sebastiano.bundel.preferences.AppsListScreen
import dev.sebastiano.bundel.preferences.Preferences
import dev.sebastiano.bundel.preferences.PreferencesScreen
import dev.sebastiano.bundel.storage.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
internal fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
    navigation(
        route = NavigationRoute.SettingsGraph.route,
        startDestination = NavigationRoute.SettingsGraph.SettingsScreen.route
    ) {
        composable(
            route = NavigationRoute.SettingsGraph.SettingsScreen.route
        ) {
            PreferencesScreen(
                onSelectAppsClicked = { navController.navigate(NavigationRoute.SettingsGraph.SelectApps.route) },
                onBackPress = { navController.popBackStack() }
            )
        }
        composable(
            route = NavigationRoute.SettingsGraph.SelectApps.route,
        ) {
            AppsListScreen()
        }
    }
}

@ExperimentalAnimationApi
internal fun NavGraphBuilder.mainScreenGraph(
    navController: NavHostController,
    lifecycle: Lifecycle,
    repository: DataRepository,
) {
    navigation(
        route = NavigationRoute.MainScreenGraph.route,
        startDestination = NavigationRoute.MainScreenGraph.MainScreen.route,
    ) {
        composable(route = NavigationRoute.MainScreenGraph.MainScreen.route) {
            MainScreenWithBottomNav(lifecycle, repository) { navController.navigate(NavigationRoute.SettingsGraph.route) }
        }
    }
}

@ExperimentalAnimationApi
internal fun NavGraphBuilder.onboardingGraph(
    navController: NavHostController,
    needsNotificationsPermissionFlow: Flow<Boolean>,
    preferences: Preferences,
    onOpenSettingsClick: () -> Unit
) {
    navigation(
        route = NavigationRoute.OnboardingGraph.route,
        startDestination = NavigationRoute.OnboardingGraph.OnboardingScreen.route,
    ) {
        composable(NavigationRoute.OnboardingGraph.OnboardingScreen.route) {
            val needsNotificationsPermission by needsNotificationsPermissionFlow.collectAsState(true)
            val scope = rememberCoroutineScope()

            OnboardingScreen(
                viewModel = hiltViewModel(),
                needsPermission = needsNotificationsPermission,
                onSettingsIntentClick = onOpenSettingsClick,
                onOnboardingDoneClicked = {
                    scope.launch { preferences.setIsOnboardingSeen(true) }
                    navController.navigate(
                        route = NavigationRoute.MainScreenGraph.MainScreen.route,
                        navOptions = NavOptions.Builder()
                            .setPopUpTo(NavigationRoute.OnboardingGraph.route, inclusive = true)
                            .build()
                    )
                }
            )
        }
    }
}
