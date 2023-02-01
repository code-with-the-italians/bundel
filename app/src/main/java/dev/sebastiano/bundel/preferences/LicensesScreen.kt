package dev.sebastiano.bundel.preferences

import android.content.res.AssetManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.ui.BundelYouTheme
import dev.sebastiano.bundel.util.Lce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
internal class LicensesPreferencesViewModel @Inject constructor(
    private val json: Json,
    private val assetManager: AssetManager
) : ViewModel() {

    private val licenses = MutableStateFlow<Lce<NonEmptyList<LicensesListItem>>>(Lce.Loading())
    val licensesFlow: Flow<Lce<NonEmptyList<LicensesListItem>>> = licenses

    @OptIn(ExperimentalSerializationApi::class)
    fun loadLicenses() {
        if (licenses.value is Lce.Data<*>) return

        licenses.value = Lce.Loading()

        viewModelScope.launch(Dispatchers.IO) {
            assetManager.open("licences.json").use {
                try {
                    val dependencyList = json.decodeFromStream(it) as List<DependenciesModel.Dependency>
                    if (dependencyList.isNotEmpty()) {
                        val map = dependencyList.groupBy { dependency ->
                            dependency.spdxLicenses?.firstOrNull()?.identifier
                                ?: dependency.unknownLicenses?.firstOrNull()?.url
                        }
                            .mapValues { (key, depsList) ->
                                if (key == null) return@mapValues null

                                val license = depsList.first()
                                    .let { dep ->
                                        dep.spdxLicenses?.firstOrNull()
                                            ?: dep.unknownLicenses?.firstOrNull()
                                    }

                                val licenseInfo = when (license) {
                                    is DependenciesModel.Dependency.SpdxLicense -> {
                                        LicenseInfo(license.name, license.identifier, license.url)
                                    }

                                    is DependenciesModel.Dependency.UnknownLicense -> {
                                        LicenseInfo(license.name, null, license.url)
                                    }

                                    else -> error("This should never happen LOL CIAO CULO")
                                }
                                LicensesListItem(licenseInfo, Nel.fromListUnsafe(depsList))
                            }
                            .values
                            .filterNotNull()

                        licenses.value = Lce.Data(NonEmptyList.fromListUnsafe(map))
                    } else {
                        licenses.value = Lce.Error(IOException("The licenses file is empty OHNO.jpg"))
                    }
                } catch (e: IllegalArgumentException) {
                    licenses.value = Lce.Error(e)
                } catch (e: IOException) {
                    licenses.value = Lce.Error(e)
                }
            }
        }
    }

    data class LicenseInfo(val name: String, val spdxId: String?, val url: String?)

    data class LicensesListItem(val licenseInfo: LicenseInfo, val dependencies: NonEmptyList<DependenciesModel.Dependency>)
}

@Preview
@Composable
private fun LicensesScreenErrorPreview() {
    BundelYouTheme {
        LicensesScreen(onBackPress = { /* Nothing to do */ }, Lce.Error(IllegalArgumentException()))
    }
}

@Preview
@Composable
private fun LicensesScreenLoadingPreview() {
    BundelYouTheme {
        LicensesScreen(onBackPress = { /* Nothing to do */ }, Lce.Loading())
    }
}

@Preview
@Composable
private fun LicensesScreenDataPreview() {
    BundelYouTheme {
        val dependency = DependenciesModel.Dependency(
            artifactId = "com.ivan",
            groupId = "ivano",
            name = "El Ivano",
            spdxLicenses = listOf(
                DependenciesModel.Dependency.SpdxLicense(
                    identifier = "Ivano-PL",
                    name = "El Ivano Public License",
                    url = "https://codewiththeitalians.it"
                )
            ),
            version = "culo"
        )
        val lli = LicensesPreferencesViewModel.LicensesListItem(
            LicensesPreferencesViewModel.LicenseInfo(
                spdxId = "Ivano-PL",
                name = "El Ivano Public License",
                url = "https://codewiththeitalians.it"
            ),
            dependencies = nonEmptyListOf(dependency)
        )
        LicensesScreen(onBackPress = { /* Nothing to do */ }, Lce.Data(nonEmptyListOf(lli)))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LicensesScreen(
    onBackPress: () -> Unit,
    viewModel: LicensesPreferencesViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = Unit) {
        // (re)Load licenses when the screen is shown
        viewModel.loadLicenses()
    }

    val licensesState by viewModel.licensesFlow.collectAsState(initial = Lce.Loading())
    val licenses = licensesState

    LicensesScreen(onBackPress = onBackPress, state = licenses)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LicensesScreen(
    onBackPress: () -> Unit,
    state: Lce<NonEmptyList<LicensesPreferencesViewModel.LicensesListItem>>
) {
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = { PreferencesTopAppBar(title = stringResource(id = R.string.preferences_licenses_title), onBackPress = onBackPress) }
    ) { paddingValues ->
        val modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)

        when (state) {
            is Lce.Data<NonEmptyList<LicensesPreferencesViewModel.LicensesListItem>> -> {
                LicensesList(state.value, modifier)
            }

            is Lce.Error<*, *> -> {
                Box(modifier = modifier, contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Ruh roh!", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Seb broke it. Sorry!")
                    }
                }
            }

            is Lce.Loading -> {
                Box(modifier = modifier, contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LicensesList(licenses: NonEmptyList<LicensesPreferencesViewModel.LicensesListItem>, modifier: Modifier) {
    LazyColumn(modifier = modifier) {
        licenses.forEach { item ->
            stickyHeader(key = item.licenseInfo.url) {
                Text(
                    text = item.licenseInfo.name,
                    Modifier
                        .fillMaxWidth()
                        .background(Color.Yellow)
                        .padding(8.dp)
                )
            }
            items(item.dependencies, key = { it.coordinates }) { item ->
                Text(text = item.name ?: item.coordinates)
            }
        }
    }
}
