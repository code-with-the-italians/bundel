@file:OptIn(ExperimentalAnimationApi::class, ExperimentalTransitionApi::class)

package dev.sebastiano.bundel.preferences

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.ui.BundelTheme
import kotlinx.coroutines.flow.map

@Preview
@Composable
private fun PreferencesScreenPreview() {
    BundelTheme {
        PreferencesScreen(
            onSelectAppsClicked = {},
            onSelectDaysClicked = {},
            onSelectTimeRangesClicked = {},
        ) { }
    }
}

@Composable
internal fun PreferencesScreen(
    activeDaysViewModel: ActiveDaysViewModel = hiltViewModel(),
    activeTimeRangesViewModel: EnglebertViewModel = hiltViewModel(),
    onSelectAppsClicked: () -> Unit,
    onSelectDaysClicked: () -> Unit,
    onSelectTimeRangesClicked: () -> Unit,
    onBackPress: () -> Unit
) {
    Scaffold(
        topBar = { PreferencesTopAppBar(onBackPress) }
    ) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectDaysClicked() }
                    .padding(16.dp)
            ) {
                Text(text = "Active days")

                val daysResIds by activeDaysViewModel.daysScheduleFlow
                    .map { daysMap ->
                        daysMap.entries.filter { it.value }
                            .map { it.key.displayResId }
                    }
                    .collectAsState(initial = emptyList())

                AnimatedVisibility(visible = daysResIds.isNotEmpty()) {
                    @Suppress("SimplifiableCallChain") // joinToString is not inline so noooope
                    Text(
                        text = daysResIds.map { stringResource(id = it) }.joinToString(", "),
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.alpha(ContentAlpha.disabled)
                    )
                }
            }
            Divider()
            Text(
                text = "Active time ranges",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectTimeRangesClicked() }
                    .padding(16.dp)
            )
            Divider()
            Text(
                text = "Select apps",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectAppsClicked() }
                    .padding(16.dp)
            )
            Divider()
            Text(text = "About app", modifier = Modifier.padding(16.dp))
        }
    }
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
