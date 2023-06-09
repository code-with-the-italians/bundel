package dev.sebastiano.bundel.preferences

import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.NonEmptyList
import dagger.hilt.android.lifecycle.HiltViewModel
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

        @Suppress("InjectDispatcher") // Whatevs for now
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
                                LicensesListItem(licenseInfo, NonEmptyList.fromListUnsafe(depsList))
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
