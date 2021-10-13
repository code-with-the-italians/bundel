package dev.sebastiano.bundel.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.preferences.PreferencesTopAppBar

@Composable
internal fun LicensesScreen(onBackPress: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = { PreferencesTopAppBar(title = stringResource(id = R.string.preferences_licenses_title), onBackPress = onBackPress) }
    ) {
        Text("There's licenses here, in theory") // TODO implement this screen
    }
}
