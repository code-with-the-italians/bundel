package dev.sebastiano.bundel.preferences

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

internal data class AppFilterInfo(
    val appInfo: AppInfo,
    val appIcon: Drawable?,
    val isExcluded: Boolean
) {

    val packageName = appInfo.packageName
    val label = appInfo.label
    val displayName = appInfo.displayName

    constructor(
        applicationInfo: ApplicationInfo,
        packageManager: PackageManager,
        isExcluded: Boolean
    ) : this(
        appInfo = AppInfo(applicationInfo, packageManager),
        appIcon = packageManager.getApplicationIcon(applicationInfo),
        isExcluded = isExcluded
    )
}
