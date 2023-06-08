package dev.sebastiano.bundel.preferences

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

data class AppInfo(
    val packageName: String,
    val label: String?
) {

    val displayName = label ?: packageName

    constructor(applicationInfo: ApplicationInfo, packageManager: PackageManager) : this(
        packageName = applicationInfo.packageName,
        label = packageManager.getApplicationLabel(applicationInfo)
            .toString()
            .takeIf { it != applicationInfo.packageName }
    )
}
