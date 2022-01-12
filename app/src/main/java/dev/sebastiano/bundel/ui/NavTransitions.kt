package dev.sebastiano.bundel.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import dev.sebastiano.bundel.navigation.NavigationRoute

// Note: these transitions have been kindly donated by Chris Banes, and are the
// same you can find in his app Tivi https://github.com/chrisbanes/tivi

@ExperimentalAnimationApi
internal fun AnimatedContentScope<*>.defaultEnterTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): EnterTransition {
    if (initial.destination.route == NavigationRoute.SplashoScreenButWithAWeirdNameNotToTriggerLint.route) {
        return fadeIn(tween(durationMillis = 0))
    }

    val initialNavGraph = initial.destination.hostNavGraph
    val targetNavGraph = target.destination.hostNavGraph
    // If we're crossing nav graphs (bottom navigation graphs), we crossfade
    if (initialNavGraph.id != targetNavGraph.id) {
        return fadeIn()
    }
    // Otherwise we're in the same nav graph, we can imply a direction
    return fadeIn() + slideIntoContainer(AnimatedContentScope.SlideDirection.Start)
}

@ExperimentalAnimationApi
internal fun AnimatedContentScope<*>.defaultExitTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): ExitTransition {
    if (initial.destination.route == NavigationRoute.SplashoScreenButWithAWeirdNameNotToTriggerLint.route) {
        return fadeOut(tween(durationMillis = 0))
    }

    val initialNavGraph = initial.destination.hostNavGraph
    val targetNavGraph = target.destination.hostNavGraph
    // If we're crossing nav graphs (bottom navigation graphs), we crossfade
    if (initialNavGraph.id != targetNavGraph.id) {
        return fadeOut()
    }
    // Otherwise we're in the same nav graph, we can imply a direction
    return fadeOut() + slideOutOfContainer(AnimatedContentScope.SlideDirection.Start)
}

private val NavDestination.hostNavGraph: NavGraph
    get() = hierarchy.first { it is NavGraph } as NavGraph

@ExperimentalAnimationApi
internal fun AnimatedContentScope<*>.defaultPopEnterTransition(): EnterTransition =
    fadeIn() + slideIntoContainer(AnimatedContentScope.SlideDirection.End)

@ExperimentalAnimationApi
internal fun AnimatedContentScope<*>.defaultPopExitTransition(): ExitTransition =
    fadeOut() + slideOutOfContainer(AnimatedContentScope.SlideDirection.End)
