package dev.sebastiano.bundel

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
import dev.sebastiano.bundel.storage.PreferenceStorage
import dev.sebastiano.bundel.storage.RobertoRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // TODO Try to bring this into the OnboardingViewModel
    private val needsNotificationsPermission = lifecycle.eventsAsFlow()
        .filter { it == Lifecycle.Event.ON_START }
        .map { needsNotificationsPermission(this) }

    @Inject
    internal lateinit var repository: RobertoRepository

    @Inject
    internal lateinit var preferenceStorage: PreferenceStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startDestination = runBlocking {
            if (preferenceStorage.isOnboardingSeen()) {
                NavigationRoute.MainScreen.route
            } else {
                NavigationRoute.Onboarding.route
            }
        }

        setContent {
            val navController = rememberNavController()
            val systemUiController = rememberSystemUiController()

            BundelTheme {
                SetupSystemUi(systemUiController)
                NavHost(navController = navController, startDestination = startDestination) {
                    composable(NavigationRoute.Onboarding.route) {
                        val needsNotificationsPermission by needsNotificationsPermission.collectAsState(true)
                        val scope = rememberCoroutineScope()

                        OnboardingScreen(
                            onSettingsIntentClick = { showNotificationsPreferences() },
                            onDismissClicked = {
                                scope.launch { preferenceStorage.setIsOnboardingSeen(true) }
                                navController.navigate(
                                    route = NavigationRoute.MainScreen.route,
                                    navOptions = NavOptions.Builder()
                                        .setPopUpTo(NavigationRoute.Onboarding.route, inclusive = true)
                                        .build()
                                )
                            },
                            needsNotificationsPermission = needsNotificationsPermission
                        )
                    }
                    composable(NavigationRoute.MainScreen.route) {
                        MainScreenWithBottomNav()
                    }
                }
            }
        }
    }

    @Composable
    private fun MainScreenWithBottomNav() {
        val navController = rememberNavController()
        val scaffoldState = rememberScaffoldState()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { NotificationsListTopAppBar() },
            scaffoldState = scaffoldState,
            bottomBar = { MainScreenBottomNavigation(navController) }
        ) { innerPadding ->
            NavHost(navController, startDestination = NavigationRoute.MainScreen.NotificationsList.route) {
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
        val items = listOf(NavigationRoute.MainScreen.NotificationsList, NavigationRoute.MainScreen.History)

        BottomNavigation {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: NavigationRoute.MainScreen.NotificationsList.route
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

    private fun NavGraphBuilder.mainScreen(
        innerPadding: PaddingValues,
        onItemClicked: suspend (notification: ActiveNotification) -> Unit
    ) {
        composable(NavigationRoute.MainScreen.NotificationsList.route) {
            NotificationsListScreen(innerPadding, onItemClicked)
        }
        composable(NavigationRoute.MainScreen.History.route) {
            val items by repository.getNotifications().collectAsState(initial = emptyList())
            NotificationsHistoryScreen(innerPadding, items)
        }
    }

    @Composable
    private fun NotificationsListTopAppBar() {
        TopAppBar(
            title = { Text(stringResource(id = R.string.app_name), style = MaterialTheme.typography.h4) }
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
        val checkedState by viewModel.crashlyticsState.collectAsState()

        BundelOnboardingTheme {
            OnboardingScreen(
                needsPermission = needsNotificationsPermission,
                onSettingsIntentClick = onSettingsIntentClick,
                onOnboardingDoneClicked = onDismissClicked,
                crashReportingEnabled = checkedState,
                onSwitchChanged = { viewModel.onCrashlyticsChanged(it) }
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
    val observer = LifecycleEventObserver { _, event -> offer(event) }
    addObserver(observer)

    awaitClose { removeObserver(observer) }
}
