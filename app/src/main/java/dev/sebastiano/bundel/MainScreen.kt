package dev.sebastiano.bundel

import android.content.Context
import android.text.format.DateUtils
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
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dev.sebastiano.bundel.history.NotificationsHistoryScreen
import dev.sebastiano.bundel.navigation.NavigationRoute
import dev.sebastiano.bundel.notifications.ActiveNotification
import dev.sebastiano.bundel.notifications.BundelNotificationListenerService
import dev.sebastiano.bundel.notificationslist.NotificationsListScreen
import dev.sebastiano.bundel.preferences.Preferences
import dev.sebastiano.bundel.schedule.ScheduleChecker
import dev.sebastiano.bundel.storage.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun MainScreenWithBottomNav(
    lifecycle: Lifecycle,
    repository: DataRepository,
    preferences: Preferences,
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
        val scope = rememberCoroutineScope()

        val context = LocalContext.current
        AnimatedNavHost(
            navController,
            startDestination = NavigationRoute.MainScreenGraph.NotificationsList.route
        ) {
            mainScreen(
                lifecycle,
                repository,
                innerPadding,
                onNotificationClick = { notification ->
                    checkNotNull(notification.interactions.main) { "Notification has no main action, shouldn't be clickable" }
                    notification.interactions.main.send()
                },
                onNotificationDismiss = { notification ->
                    scope.launch { handleNotificationSnooze(scope, preferences, scaffoldState, notification, context) }
                }
            )
        }
    }
}

private suspend fun handleNotificationSnooze(
    coroutineScope: CoroutineScope,
    preferences: Preferences,
    scaffoldState: ScaffoldState,
    notification: ActiveNotification,
    context: Context
) {
    val now = LocalDateTime.now()
    val daysSchedule = preferences.getDaysSchedule().first()
    val timeRangesSchedule = preferences.getTimeRangesSchedule().first()

    if (!ScheduleChecker.isSnoozeActive(now, daysSchedule, timeRangesSchedule)) {
        scaffoldState.snackbarHostState.showSnackbar("Can't snooze right now sorry pal")
    } else {
        val dalekSebDurationMillis = ScheduleChecker.calculateSnoozeDelay(now, daysSchedule, timeRangesSchedule)

        // Note: We need the nested launch because showSnackbar is suspending; if we didn't,
        // reordering the calls would cause issues.
        coroutineScope.launch {
            val formattedDelay = DateUtils.getRelativeTimeSpanString(
                context,
                System.currentTimeMillis() + dalekSebDurationMillis,
                false
            )

            scaffoldState.snackbarHostState.showSnackbar("Snoozing until $formattedDelay...")
        }

        BundelNotificationListenerService.snooze(notification, dalekSebDurationMillis)
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
    lifecycle: Lifecycle,
    repository: DataRepository,
    innerPadding: PaddingValues,
    onNotificationClick: (notification: ActiveNotification) -> Unit,
    onNotificationDismiss: (notification: ActiveNotification) -> Unit
) {
    composable(NavigationRoute.MainScreenGraph.NotificationsList.route) {
        NotificationsListScreen(lifecycle, innerPadding, onNotificationClick, onNotificationDismiss)
    }
    composable(NavigationRoute.MainScreenGraph.History.route) {
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
