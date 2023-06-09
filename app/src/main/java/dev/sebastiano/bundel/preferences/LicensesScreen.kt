package dev.sebastiano.bundel.preferences

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
import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import dev.sebastiano.bundel.R
import dev.sebastiano.bundel.ui.BundelYouTheme
import dev.sebastiano.bundel.util.Lce

@Preview
@Composable
private fun LicensesScreenErrorPreview() {
    BundelYouTheme {
        LicensesScreen(onBackPress = { /* Nothing to do */ }, Lce.Error(IllegalArgumentException("test")))
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
            items(item.dependencies, key = { it.coordinates }) { dependency ->
                Text(text = dependency.name ?: dependency.coordinates)
            }
        }
    }
}
