@file:OptIn(ExperimentalAnimationApi::class, ExperimentalTransitionApi::class)

package dev.sebastiano.bundel.preferences

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.imageloading.rememberDrawablePainter
import dev.sebastiano.bundel.BundelTheme
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.singlePadding

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
                    itemEnabled = !appFilterInfo.isExcluded,
                    onIconClicked = { viewModel.setAppNotificationsExcluded(appFilterInfo.packageName, !appFilterInfo.isExcluded) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun AppToggleItemPreview() {
    var itemEnabled by remember { mutableStateOf(true) }
    BundelTheme {
        Surface {
            AppToggleItem(
                appInfo = AppInfo(packageName = "com.my.package.name", label = "My fancy app"),
                icon = null,
                itemEnabled = itemEnabled,
                onIconClicked = { itemEnabled = !itemEnabled }
            )
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun AppToggleItem(
    appInfo: AppInfo,
    icon: Drawable?,
    itemEnabled: Boolean,
    onIconClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onIconClicked() }
            .padding(singlePadding())
            .alpha(if (itemEnabled) ContentAlpha.high else ContentAlpha.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIcon(
            appIcon = icon,
            contentDescription = stringResource(id = R.string.app_filter_item_icon_content_description, appInfo.displayName)
        )

        Spacer(Modifier.width(singlePadding()))

        Column {
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

@Composable
private fun AppIcon(
    appIcon: Drawable?,
    contentDescription: String
) {
    val icon = appIcon ?: AppCompatResources.getDrawable(LocalContext.current, R.drawable.ic_default_icon)
    val iconPainter = rememberDrawablePainter(drawable = icon)
    Image(
        painter = iconPainter,
        contentDescription = contentDescription,
        modifier = Modifier
            .size(48.dp)
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