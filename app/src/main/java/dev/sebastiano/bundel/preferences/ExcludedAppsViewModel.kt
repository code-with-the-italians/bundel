package dev.sebastiano.bundel.preferences

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class ExcludedAppsViewModel @Inject constructor(
    private val preferences: Preferences,
    packageManager: PackageManager
) : ViewModel() {

    private val excludedPackagesFlow = preferences.getExcludedPackages()
    private val installedApps = packageManager.getInstalledApplications(0)

    val excludedAppsCountFlow = excludedPackagesFlow.map { it.count() }

    val appFilterInfoFlow = excludedPackagesFlow.map { excludedPackages ->
        computeApps(installedApps, excludedPackages, packageManager)
            .sortedBy { it.displayName }
    }

    private fun computeApps(
        installedApps: List<ApplicationInfo>,
        excludedPackages: Set<String>,
        packageManager: PackageManager
    ) =
        installedApps.map { applicationInfo ->
            val isExcluded = excludedPackages.contains(applicationInfo.packageName)
            AppFilterInfo(applicationInfo, packageManager, isExcluded)
        }

    fun setAppNotificationsExcluded(packageName: String, excluded: Boolean) {
        Timber.d("Setting app '$packageName' notifications as excluded: $excluded")

        viewModelScope.launch {
            val excludedPackages = excludedPackagesFlow.first().toMutableSet()
            val succeeded = if (excluded) {
                excludedPackages.add(packageName)
            } else {
                excludedPackages.remove(packageName)
            }
            check(succeeded) { "Unable to ${if (excluded) "add" else "remove"} $packageName to/from exclusions" }

            preferences.setExcludedPackages(excludedPackages)
        }
    }
}
