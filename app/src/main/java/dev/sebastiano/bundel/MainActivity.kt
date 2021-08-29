package dev.sebastiano.bundel

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import dev.sebastiano.bundel.history.NotificationsHistoryScreen
import dev.sebastiano.bundel.navigation.NavigationRoute
import dev.sebastiano.bundel.notifications.ActiveNotification
import dev.sebastiano.bundel.notifications.BundelNotificationListenerService
import dev.sebastiano.bundel.notifications.BundelNotificationListenerService.Companion.NOTIFICATIONS_FLOW
import dev.sebastiano.bundel.notifications.needsNotificationsPermission
import dev.sebastiano.bundel.notificationslist.NotificationsListScreen
import dev.sebastiano.bundel.onboarding.OnboardingScreen
import dev.sebastiano.bundel.onboarding.OnboardingViewModel
import dev.sebastiano.bundel.preferences.AppsListScreen
import dev.sebastiano.bundel.preferences.Preferences
import dev.sebastiano.bundel.preferences.PreferencesScreen
import dev.sebastiano.bundel.storage.DataRepository
import dev.sebastiano.bundel.ui.BundelOnboardingTheme
import dev.sebastiano.bundel.ui.BundelTheme
import dev.sebastiano.bundel.ui.defaultBundelTotallyNotFromTiviEnterTransition
import dev.sebastiano.bundel.ui.defaultTiviExitTransition
import dev.sebastiano.bundel.ui.defaultTiviPopEnterTransition
import dev.sebastiano.bundel.ui.defaultTiviPopExitTransition
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import com.google.accompanist.navigation.animation.composable as animationComposable

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // TODO Try to bring this into the OnboardingViewModel
    private val needsNotificationsPermission = lifecycle.eventsAsFlow()
        .filter { it == Lifecycle.Event.ON_START }
        .map { needsNotificationsPermission(this) }

    @Inject
    internal lateinit var repository: DataRepository

    @Inject
    internal lateinit var preferences: Preferences

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO reevaluate this, we shouldn't leak this into the activity
        val startDestination = runBlocking {
            if (preferences.isOnboardingSeen()) {
                NavigationRoute.MainScreenGraph.route
            } else {
                NavigationRoute.OnboardingGraph.route
            }
        }

        setContent {
            val navController = rememberAnimatedNavController()
            val systemUiController = rememberSystemUiController()

            BundelTheme {
                SetupSystemUi(systemUiController)
                AnimatedNavHost(
                    navController = navController,
                    startDestination = startDestination,
                    enterTransition = { initial, target ->
                        defaultBundelTotallyNotFromTiviEnterTransition(initial, target)
                    },
                    exitTransition = { initial, target ->
                        defaultTiviExitTransition(initial, target)
                    },
                    popEnterTransition = { _, _ ->
                        defaultTiviPopEnterTransition()
                    },
                    popExitTransition = { _, _ ->
                        defaultTiviPopExitTransition()
                    }
                ) {
                    navigation(
                        route = NavigationRoute.OnboardingGraph.route,
                        startDestination = NavigationRoute.OnboardingGraph.OnboardingScreen.route,
                    ) {
                        animationComposable(NavigationRoute.OnboardingGraph.OnboardingScreen.route) {
                            val needsNotificationsPermission by needsNotificationsPermission.collectAsState(true)
                            val scope = rememberCoroutineScope()

                            OnboardingScreen(
                                onSettingsIntentClick = { showNotificationsPreferences() },
                                onDismissClicked = {
                                    scope.launch { preferences.setIsOnboardingSeen(true) }
                                    navController.navigate(
                                        route = NavigationRoute.MainScreenGraph.MainScreen.route,
                                        navOptions = NavOptions.Builder()
                                            .setPopUpTo(NavigationRoute.OnboardingGraph.route, inclusive = true)
                                            .build()
                                    )
                                },
                                needsNotificationsPermission = needsNotificationsPermission
                            )
                        }
                    }

                    navigation(
                        route = NavigationRoute.MainScreenGraph.route,
                        startDestination = NavigationRoute.MainScreenGraph.MainScreen.route,
                    ) {
                        animationComposable(route = NavigationRoute.MainScreenGraph.MainScreen.route) {
                            MainScreenWithBottomNav { navController.navigate(NavigationRoute.SettingsGraph.route) }
                        }
                    }

                    navigation(
                        route = NavigationRoute.SettingsGraph.route,
                        startDestination = NavigationRoute.SettingsGraph.SettingsScreen.route
                    ) {
                        animationComposable(
                            route = NavigationRoute.SettingsGraph.SettingsScreen.route
                        ) {
                            PreferencesScreen(
                                onSelectAppsClicked = { navController.navigate(NavigationRoute.SettingsGraph.SelectApps.route) },
                                onBackPress = { navController.popBackStack() }
                            )
                        }
                        animationComposable(
                            route = NavigationRoute.SettingsGraph.SelectApps.route,
                        ) {
                            AppsListScreen()
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    private fun MainScreenWithBottomNav(
        onSettingsClick: () -> Unit
    ) {
        val navController = rememberAnimatedNavController()
        val scaffoldState = rememberScaffoldState()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { NotificationsListTopAppBar(onSettingsClick) },
            scaffoldState = scaffoldState,
            bottomBar = { MainScreenBottomNavigation(navController) }
        ) { innerPadding ->
            AnimatedNavHost(
                navController,
                startDestination = NavigationRoute.MainScreenGraph.NotificationsList.route
            ) {
                mainScreen(
                    innerPadding,
                    onItemClicked = { notification ->
                        scaffoldState.snackbarHostState.showSnackbar("Snoozing...")
                        BundelNotificationListenerService.snoozeFlow.emit(notification.persistableNotification.key)
                    }
                )
            }
        }
    }

    @Composable
    private fun MainScreenBottomNavigation(navController: NavController) {
        val items = listOf(NavigationRoute.MainScreenGraph.NotificationsList, NavigationRoute.MainScreenGraph.History)

        BottomNavigation {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: NavigationRoute.MainScreenGraph.NotificationsList.route
            for (screen in items) {
                val label = stringResource(screen.labelId)
                BottomNavigationItem(
                    icon = { Icon(screen.icon, label) },
                    label = { Text(label) },
                    alwaysShowLabel = false,
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(checkNotNull(navController.graph.startDestinationRoute)) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    private fun NavGraphBuilder.mainScreen(
        innerPadding: PaddingValues,
        onItemClicked: suspend (notification: ActiveNotification) -> Unit
    ) {
        animationComposable(NavigationRoute.MainScreenGraph.NotificationsList.route) {
            NotificationsListScreen(innerPadding, onItemClicked)
        }
        animationComposable(NavigationRoute.MainScreenGraph.History.route) {
            val items by repository.getNotifications().collectAsState(initial = emptyList())
            NotificationsHistoryScreen(innerPadding, items)
        }
    }

    @Composable
    private fun NotificationsListTopAppBar(onSettingsActionClick: () -> Unit) {
        @Composable
        fun ActionsMenu() {
            IconButton(onClick = onSettingsActionClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_round_settings_24),
                    contentDescription = stringResource(id = R.string.menu_settings_content_description)
                )
            }
        }

        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bundel_icon),
                        contentDescription = stringResource(id = R.string.app_name),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(stringResource(id = R.string.app_name), style = MaterialTheme.typography.h4)
                }
            },
            actions = { ActionsMenu() }
        )
    }

    @Composable
    private fun SetupSystemUi(systemUiController: SystemUiController) {
        val barsColor = MaterialTheme.colors.primaryVariant
        SideEffect {
            systemUiController.setStatusBarColor(color = barsColor)
            systemUiController.setNavigationBarColor(color = barsColor)
        }
    }

    @Composable
    private fun OnboardingScreen(
        viewModel: OnboardingViewModel = hiltViewModel(),
        needsNotificationsPermission: Boolean,
        onSettingsIntentClick: () -> Unit,
        onDismissClicked: () -> Unit,
    ) {
        BundelOnboardingTheme {
            OnboardingScreen(
                viewModel = viewModel,
                needsPermission = needsNotificationsPermission,
                onSettingsIntentClick = onSettingsIntentClick,
                onOnboardingDoneClicked = onDismissClicked
            )
        }
    }

    @Composable
    private fun NotificationsListScreen(
        innerPadding: PaddingValues,
        onItemClicked: suspend (notification: ActiveNotification) -> Unit
    ) {
        val notifications by remember(lifecycle) { NOTIFICATIONS_FLOW.flowWithLifecycle(lifecycle) }
            .collectAsState(emptyList())
        NotificationsListScreen(innerPadding, notifications, onItemClicked)
    }

    private fun showNotificationsPreferences() {
        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }
}

private fun Lifecycle.eventsAsFlow(): Flow<Lifecycle.Event> = callbackFlow {
    val observer = LifecycleEventObserver { _, event -> trySend(event) }
    addObserver(observer)

    awaitClose { removeObserver(observer) }
}
