package dev.sebastiano.bundel.preferences

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.imageloading.rememberDrawablePainter
import dev.sebastiano.bundel.R

@Composable
internal fun PreferencesScreen(viewModel: PreferencesViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    Scaffold(
        topBar = { PreferencesTopAppBar(onBackPress = { navController.popBackStack() }) }
    ) {
        val apps by viewModel.appsFlow.collectAsState(initial = emptyMap())
        val packageManager = LocalContext.current.packageManager

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = apps.entries.toList(), key = { it.key.packageName }) { (appInfo, excluded) ->
                val appLabel = appInfo.loadLabel(packageManager).toString()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    IconButton(onClick = { viewModel.setAppNotificationsExcluded(appInfo.packageName, excluded) }) {
                        val iconPainter = rememberDrawablePainter(drawable = appInfo.loadIcon(packageManager))
                        Icon(painter = iconPainter, contentDescription = appLabel, modifier = Modifier.size(48.dp))
                    }

                    Spacer(Modifier.width(16.dp))

                    Text(text = appLabel)
                }
            }
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
