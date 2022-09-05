package dev.sebastiano.bundel

import android.content.Context
import android.text.format.DateUtils
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
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

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreenWithBottomNav(
    lifecycle: Lifecycle,
    repository: DataRepository,
    preferences: Preferences,
    windowSizeClass: WindowSizeClass,
    onPreferencesClick: () -> Unit
) {
    val navController = rememberAnimatedNavController()
    SetupTransparentSystemUi(actualBackgroundColor = MaterialTheme.colorScheme.primaryContainer)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = { NotificationsListTopAppBar(onPreferencesClick) },
        bottomBar = { MainScreenBottomNavigation(navController) }
    ) { innerPadding ->
        val scope = rememberCoroutineScope()

        val extraHorizontalPadding = when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 0.dp
            WindowWidthSizeClass.Medium -> 64.dp
            else -> 366.dp
        }

        val layoutDirection = LocalLayoutDirection.current
        val contentPadding = PaddingValues(
            start = innerPadding.calculateStartPadding(layoutDirection) + extraHorizontalPadding,
            top = innerPadding.calculateTopPadding(),
            end = innerPadding.calculateEndPadding(layoutDirection) + extraHorizontalPadding,
            bottom = innerPadding.calculateBottomPadding(),
        )

        val context = LocalContext.current
        AnimatedNavHost(
            navController,
            startDestination = NavigationRoute.MainScreenGraph.NotificationsList.route
        ) {
            mainScreen(
                lifecycle,
                repository,
                contentPadding,
                onNotificationClick = { notification ->
                    checkNotNull(notification.interactions.main) { "Notification has no main action, shouldn't be clickable" }
                    notification.interactions.main.send()
                }
            ) { notification ->
                scope.launch { handleNotificationSnooze(scope, preferences, notification, context) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private suspend fun handleNotificationSnooze(
    coroutineScope: CoroutineScope,
    preferences: Preferences,
    notification: ActiveNotification,
    context: Context
) {
    val now = LocalDateTime.now()
    val daysSchedule = preferences.getDaysSchedule().first()
    val timeRangesSchedule = preferences.getTimeRangesSchedule().first()

    if (!ScheduleChecker.isSnoozeActive(now, daysSchedule, timeRangesSchedule)) {
        // TODO use snackbars once they're available to YOU
        Toast.makeText(context, "Can't snooze right now, sorry pal", Toast.LENGTH_SHORT).show()
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

            // TODO use snackbars once they're available to YOU
            Toast.makeText(context, "Snoozing until $formattedDelay...", Toast.LENGTH_SHORT).show()
        }

        BundelNotificationListenerService.snooze(notification, dalekSebDurationMillis)
    }
}

@Composable
private fun MainScreenBottomNavigation(navController: NavController) {
    val items = listOf(NavigationRoute.MainScreenGraph.NotificationsList, NavigationRoute.MainScreenGraph.History)

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: NavigationRoute.MainScreenGraph.NotificationsList.route
        for (screen in items) {
            val label = stringResource(screen.labelId)
            NavigationBarItem(
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
        val items by repository.getNotificationHistory().collectAsState(initial = emptyList())
        NotificationsHistoryScreen(innerPadding, items)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsListTopAppBar(onPreferencesActionClick: () -> Unit) {
    @Composable
    fun ActionsMenu() {
        IconButton(onClick = onPreferencesActionClick) {
            Icon(
                painter = painterResource(R.drawable.ic_round_settings_24),
                contentDescription = stringResource(id = R.string.menu_preferences_content_description)
            )
        }
    }

    SmallTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bundel_icon),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(stringResource(id = R.string.app_name), style = MaterialTheme.typography.headlineSmall)
            }
        },
        actions = { ActionsMenu() }
    )
}
