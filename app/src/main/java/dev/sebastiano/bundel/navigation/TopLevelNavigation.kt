package dev.sebastiano.bundel.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.dialog
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import dev.sebastiano.bundel.MainScreenWithBottomNav
import dev.sebastiano.bundel.SetupTransparentSystemUi
import dev.sebastiano.bundel.glance.TestWidgetScreen
import dev.sebastiano.bundel.onboarding.OnboardingScreen
import dev.sebastiano.bundel.preferences.AppsListScreen
import dev.sebastiano.bundel.preferences.Preferences
import dev.sebastiano.bundel.preferences.PreferencesScreen
import dev.sebastiano.bundel.preferences.SelectDaysDialog
import dev.sebastiano.bundel.preferences.SelectTimeRangesDialog
import dev.sebastiano.bundel.storage.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalMaterialNavigationApi::class,
    ExperimentalAnimationApi::class,
)
internal fun NavGraphBuilder.preferencesGraph(
    navController: NavHostController,
    onUrlClick: (String) -> Unit
) {
    navigation(
        route = NavigationRoute.PreferencesGraph.route,
        startDestination = NavigationRoute.PreferencesGraph.PreferencesScreen.route
    ) {
        composable(
            route = NavigationRoute.PreferencesGraph.PreferencesScreen.route
        ) {
            PreferencesScreen(
                onSelectAppsClicked = { navController.navigate(NavigationRoute.PreferencesGraph.SelectApps.route) },
                onSelectDaysClicked = { navController.navigate(NavigationRoute.PreferencesGraph.SelectDays.route) },
                onSelectTimeRangesClicked = { navController.navigate(NavigationRoute.PreferencesGraph.SelectTimeRanges.route) },
                onLicensesLinkClick = { navController.navigate(NavigationRoute.PreferencesGraph.Licenses.route) },
                onSourcesLinkClick = { onUrlClick("https://github.com/rock3r/bundel") },
                onTestWidgetClick = { navController.navigate(NavigationRoute.PreferencesGraph.TestWidget.route) },
                onBackPress = { navController.popBackStack() },
            )
        }

        composable(
            route = NavigationRoute.PreferencesGraph.SelectApps.route
        ) {
            AppsListScreen(onBackPress = { navController.popBackStack() })
        }

        composable(
            route = NavigationRoute.PreferencesGraph.Licenses.route
        ) {
            LicensesScreen(onBackPress = { navController.popBackStack() })
        }

        composable(
            route = NavigationRoute.PreferencesGraph.TestWidget.route
        ) {
            TestWidgetScreen()
        }

        bottomSheet(route = NavigationRoute.PreferencesGraph.SelectDays.route) {
            SelectDaysDialog(onDialogDismiss = { navController.popBackStack() })
        }

        dialog(
            route = NavigationRoute.PreferencesGraph.SelectTimeRanges.route,
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            SelectTimeRangesDialog(onDialogDismiss = { navController.popBackStack() })
        }
    }
}

@ExperimentalAnimationApi
internal fun NavGraphBuilder.mainScreenGraph(
    navController: NavHostController,
    lifecycle: Lifecycle,
    repository: DataRepository,
    preferences: Preferences,
    windowSizeClass: WindowSizeClass
) {
    navigation(
        route = NavigationRoute.MainScreenGraph.route,
        startDestination = NavigationRoute.MainScreenGraph.MainScreen.route,
    ) {
        composable(route = NavigationRoute.MainScreenGraph.MainScreen.route) {
            MainScreenWithBottomNav(lifecycle, repository, preferences, windowSizeClass) {
                navController.navigate(NavigationRoute.PreferencesGraph.route)
            }
        }
    }
}

@ExperimentalAnimationApi
internal fun NavGraphBuilder.onboardingGraph(
    navController: NavHostController,
    needsNotificationsPermissionFlow: Flow<Boolean>,
    preferences: Preferences,
    onOpenNotificationPreferencesClick: () -> Unit
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
                onOpenNotificationPreferencesClick = onOpenNotificationPreferencesClick,
                onOnboardingDoneClicked = {
                    scope.launch { preferences.setIsOnboardingSeen(true) }
                    navController.navigate(
                        route = NavigationRoute.MainScreenGraph.MainScreen.route,
                    ) {
                        popUpTo(NavigationRoute.OnboardingGraph.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

@ExperimentalAnimationApi
internal fun NavGraphBuilder.splashScreenGraph(
    preferences: Preferences,
    onPizzaReady: (NavigationRoute) -> Unit
) {
    composable(NavigationRoute.SplashoScreenButWithAWeirdNameNotToTriggerLint.route) {
        SetupTransparentSystemUi(actualBackgroundColor = MaterialTheme.colorScheme.primaryContainer)

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        LaunchedEffect(key1 = Unit) {
            val startDestination = if (preferences.isOnboardingSeen()) {
                NavigationRoute.MainScreenGraph
            } else {
                NavigationRoute.OnboardingGraph
            }

            onPizzaReady(startDestination)
        }
    }
}
