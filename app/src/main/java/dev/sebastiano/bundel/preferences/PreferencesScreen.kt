@file:OptIn(ExperimentalAnimationApi::class, ExperimentalTransitionApi::class)

package dev.sebastiano.bundel.preferences

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.imageloading.rememberDrawablePainter
import dev.sebastiano.bundel.ui.BundelTheme
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.composables.MaterialPill
import dev.sebastiano.bundel.ui.overlay.StrikethroughOverlay
import dev.sebastiano.bundel.ui.overlay.animatedOverlay
import dev.sebastiano.bundel.ui.pillBackground
import dev.sebastiano.bundel.ui.singlePadding
import kotlin.math.roundToInt

@Composable
internal fun PreferencesScreen(
    viewModel: PreferencesViewModel = hiltViewModel(),
    onBackPress: () -> Unit
) {
    Scaffold(
        topBar = { PreferencesTopAppBar(onBackPress) }
    ) {
        val appFilterInfo by viewModel.appFilterInfoFlow.collectAsState(initial = emptyList())

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = appFilterInfo, key = { it.packageName }) { appFilterInfo ->
                AppToggleItem(
                    appInfo = appFilterInfo.appInfo,
                    icon = appFilterInfo.appIcon,
                    filterState = if (appFilterInfo.isExcluded) AppFilterState.Excluded else AppFilterState.Included,
                    onIconClicked = { viewModel.setAppNotificationsExcluded(appFilterInfo.packageName, !appFilterInfo.isExcluded) }
                )
            }
        }
    }
}

private enum class AppFilterState {
    Included,
    Excluded
}

@Preview
@Composable
private fun AppToggleItemPreview() {
    var filterState by remember { mutableStateOf(AppFilterState.Included) }
    BundelTheme {
        Surface {
            AppToggleItem(
                appInfo = AppInfo(packageName = "com.my.package.name", label = "My fancy app"),
                icon = null,
                filterState = filterState,
                onIconClicked = {
                    filterState = if (filterState == AppFilterState.Included) AppFilterState.Excluded else AppFilterState.Included
                }
            )
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun AppToggleItem(
    appInfo: AppInfo,
    icon: Drawable?,
    filterState: AppFilterState,
    onIconClicked: () -> Unit
) {
    val excludedTransition = updateTransition(filterState, label = "excludedTransition")
    val rowAlpha by excludedTransition.animateFloat(label = "rowAlpha") { targetFilterState ->
        if (targetFilterState == AppFilterState.Included) ContentAlpha.high else ContentAlpha.medium
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onIconClicked() }
            .padding(singlePadding())
            .alpha(rowAlpha),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIcon(
            appIcon = icon,
            contentDescription = stringResource(id = R.string.app_filter_item_icon_content_description, appInfo.displayName),
            excludedTransition = excludedTransition
        )

        Spacer(Modifier.width(singlePadding()))

        Column {
            ExcludedLabel(excludedTransition)

            Text(text = appInfo.displayName)

            if (appInfo.label != null) {
                Text(
                    text = appInfo.packageName,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.alpha(ContentAlpha.medium)
                )
            }
        }
    }
}

private const val ANIMATION_DURATION_MILLIS = 200
private const val ALPHA_ANIMATION_DURATION_MILLIS = ANIMATION_DURATION_MILLIS / 3
private const val HEIGHT_ANIMATION_DURATION_MILLIS = ANIMATION_DURATION_MILLIS - ALPHA_ANIMATION_DURATION_MILLIS

@Composable
private fun ExcludedLabel(
    transition: Transition<AppFilterState>
) {
    val labelAlpha by transition.animateFloat(
        label = "labelAlpha",
        transitionSpec = {
            if (targetState == AppFilterState.Excluded) {
                tween(durationMillis = ALPHA_ANIMATION_DURATION_MILLIS, delayMillis = HEIGHT_ANIMATION_DURATION_MILLIS)
            } else {
                tween(durationMillis = ALPHA_ANIMATION_DURATION_MILLIS)
            }
        }
    ) { targetFilterState ->
        if (targetFilterState == AppFilterState.Included) 0f else 1f
    }
    val labelHeightPercent by transition.animateFloat(
        label = "labelHeightPercent",
        transitionSpec = {
            if (targetState == AppFilterState.Excluded) {
                tween(durationMillis = ALPHA_ANIMATION_DURATION_MILLIS)
            } else {
                tween(durationMillis = ALPHA_ANIMATION_DURATION_MILLIS, delayMillis = ALPHA_ANIMATION_DURATION_MILLIS)
            }
        }
    ) { targetFilterState ->
        if (targetFilterState == AppFilterState.Included) 0f else 1f
    }

    MaterialPill(
        backgroundColor = MaterialTheme.colors.pillBackground,
        modifier = Modifier
            .padding(bottom = 4.dp)
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)

                val height = placeable.height * labelHeightPercent
                layout(placeable.width, height.roundToInt()) {
                    placeable.placeRelative(0, 0)
                }
            }
            .alpha(labelAlpha)
    ) {
        Text(
            text = stringResource(id = R.string.label_excluded),
            fontSize = MaterialTheme.typography.caption.fontSize,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun AppIcon(
    appIcon: Drawable?,
    contentDescription: String,
    excludedTransition: Transition<AppFilterState>
) {
    val icon = appIcon ?: AppCompatResources.getDrawable(LocalContext.current, R.drawable.ic_default_icon)
    val iconPainter = rememberDrawablePainter(drawable = icon)

    val strikethroughProgress by excludedTransition.animateFloat(label = "strikethroughProgress") { targetFilterState ->
        if (targetFilterState == AppFilterState.Included) 0f else 1f
    }
    val overlay = StrikethroughOverlay(
        color = MaterialTheme.colors.onSurface,
        widthDp = 4.dp,
        getProgress = { strikethroughProgress }
    )

    Image(
        painter = iconPainter,
        contentDescription = contentDescription,
        modifier = Modifier
            .size(48.dp)
            .animatedOverlay(overlay)
            .padding(6.dp)
    )
}

@Composable
private fun PreferencesTopAppBar(
    onBackPress: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackPress) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_24),
                    contentDescription = stringResource(id = R.string.menu_back_content_description)
                )
            }
        },
        title = { Text(stringResource(id = R.string.app_name), style = MaterialTheme.typography.h4) }
    )
}
