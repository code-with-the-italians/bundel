package dev.sebastiano.bundel.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.preferences.DependenciesModel
import dev.sebastiano.bundel.preferences.PreferencesTopAppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSerializationApi::class)
@Composable
internal fun LicensesScreen(onBackPress: () -> Unit) {
    var licenses by remember { mutableStateOf(listOf<DependenciesModel.Dependency>()) }
    val assetManager = LocalContext.current.resources.assets
    LaunchedEffect(key1 = Unit) {
        // TODO THIS IS STUPID. Move to a VM and save your soul
        launch(Dispatchers.IO) {
            assetManager.open("licences.json").use {
                val json: Json = Json.Default
                licenses = json.decodeFromStream(it) as List<DependenciesModel.Dependency>
            }
        }
    }

    // TODO deshittify this
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = { PreferencesTopAppBar(title = stringResource(id = R.string.preferences_licenses_title), onBackPress = onBackPress) }
    ) { paddingValues ->
        if (licenses == null) {
            Box(modifier = Modifier.padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(text = "Nothing?!?!")
            }
        } else {
            LazyColumn(Modifier.padding(paddingValues)) {
                items(licenses) {
                    Text(it.name ?: it.coordinates)
                }
            }
        }
    }
}
