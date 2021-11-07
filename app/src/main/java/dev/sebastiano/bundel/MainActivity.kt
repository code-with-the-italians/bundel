package dev.sebastiano.bundel

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import dev.sebastiano.bundel.navigation.NavigationRoute
import dev.sebastiano.bundel.navigation.mainScreenGraph
import dev.sebastiano.bundel.navigation.onboardingGraph
import dev.sebastiano.bundel.navigation.settingsGraph
import dev.sebastiano.bundel.navigation.splashScreenGraph
import dev.sebastiano.bundel.notifications.needsNotificationsPermission
import dev.sebastiano.bundel.preferences.Preferences
import dev.sebastiano.bundel.storage.DataRepository
import dev.sebastiano.bundel.ui.BundelYouTheme
import dev.sebastiano.bundel.ui.defaultEnterTransition
import dev.sebastiano.bundel.ui.defaultExitTransition
import dev.sebastiano.bundel.ui.defaultPopEnterTransition
import dev.sebastiano.bundel.ui.defaultPopExitTransition
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import androidx.compose.material.MaterialTheme as MaterialTheme2

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

    @OptIn(
        ExperimentalAnimationApi::class,
        ExperimentalMaterialNavigationApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val bottomSheetNavigator = rememberBottomSheetNavigator()
            val navController = rememberAnimatedNavController(bottomSheetNavigator)
            val systemUiController = rememberSystemUiController()

            BundelYouTheme {
                SetupSystemUi(systemUiController)

                ModalBottomSheetLayout(
                    bottomSheetNavigator,
                    sheetShape = MaterialTheme2.shapes.small
                ) {
                    AnimatedNavHost(
                        navController = navController,
                        startDestination = NavigationRoute.SplashoScreenButWithAWeirdNameNotToTriggerLint.route,
                        enterTransition = { defaultEnterTransition(initialState, targetState) },
                        exitTransition = { defaultExitTransition(initialState, targetState) },
                        popEnterTransition = { defaultPopEnterTransition() },
                        popExitTransition = { defaultPopExitTransition() }
                    ) {
                        splashScreenGraph(preferences) { navigationRoute ->
                            navController.navigate(navigationRoute.route)
                        }
                        onboardingGraph(
                            navController = navController,
                            needsNotificationsPermissionFlow = needsNotificationsPermission,
                            preferences = preferences,
                            onOpenSettingsClick = { openNotificationsPreferences() }
                        )
                        mainScreenGraph(navController, lifecycle, repository, preferences)
                        settingsGraph(navController, ::onOpenUrlClick)
                    }
                }
            }
        }
    }

    private fun onOpenUrlClick(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    @Composable
    private fun SetupSystemUi(systemUiController: SystemUiController) {
        val barsColor = MaterialTheme.colorScheme.tertiary
        SideEffect {
            systemUiController.setStatusBarColor(color = barsColor)
            systemUiController.setNavigationBarColor(color = barsColor)
        }
    }

    private fun openNotificationsPreferences() {
        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }
}

private fun Lifecycle.eventsAsFlow(): Flow<Lifecycle.Event> = callbackFlow {
    val observer = LifecycleEventObserver { _, event -> trySend(event) }
    addObserver(observer)

    awaitClose { removeObserver(observer) }
}
