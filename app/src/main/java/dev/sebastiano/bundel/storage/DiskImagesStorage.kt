package dev.sebastiano.bundel.storage

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import dev.sebastiano.bundel.notifications.ActiveNotification
import dev.sebastiano.bundel.storage.ImagesStorage.NotificationIconSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DiskImagesStorage @Inject constructor(
    private val application: Application
) : ImagesStorage {

    private val cacheFolder = application.cacheDir

    override suspend fun saveIconsFrom(activeNotification: ActiveNotification) {
        activeNotification.icons.small?.let { icon ->
            val iconSize = NotificationIconSize.SMALL
            saveIcon(activeNotification.persistableNotification.uniqueId, iconSize, icon)
        }
        activeNotification.icons.large?.let { icon ->
            val iconSize = NotificationIconSize.LARGE
            saveIcon(activeNotification.persistableNotification.uniqueId, iconSize, icon)
        }
        activeNotification.icons.extraLarge?.let { icon ->
            val iconSize = NotificationIconSize.EXTRA_LARGE
            saveIcon(activeNotification.persistableNotification.uniqueId, iconSize, icon)
        }
    }

    private suspend fun saveIcon(notificationUniqueId: String, iconSize: NotificationIconSize, icon: Icon) {
        val iconFile = getIconFile(notificationUniqueId, iconSize)
        if (iconFile.exists()) return

        withContext(Dispatchers.IO) {
            val iconBitmap = icon.loadDrawable(application).toBitmap()
            iconBitmap.compress(getCachedImageFormat().format, 0, iconFile.outputStream())
        }
    }

    override suspend fun deleteIconsFor(notificationUniqueId: String) {
        withContext(Dispatchers.IO) {
            for (iconSize in NotificationIconSize.values()) {
                getIconFile(notificationUniqueId, iconSize).takeIf { it.exists() }
                    ?.delete()
            }
        }
    }

    override fun getIconPath(notificationUniqueId: String, iconSize: NotificationIconSize): String =
        getIconFile(notificationUniqueId, iconSize).path

    private fun getIconFile(notificationUniqueId: String, iconSize: NotificationIconSize): File {
        val extension = getCachedImageFormat().extension
        return File(cacheFolder, "${notificationUniqueId}_icon_${iconSize.cacheKey}.$extension")
    }

    override suspend fun saveAppIcon(packageName: String, icon: Icon) {
        val iconFile = getAppIconFile(packageName)

        // TODO check if the file as it exists is already the same as the icon (maybe check size, hash, ...?)
        if (iconFile.exists()) return

        withContext(Dispatchers.IO) {
            val iconBitmap = icon.loadDrawable(application).toBitmap()
            iconBitmap.compress(getCachedImageFormat().format, 0, iconFile.outputStream())
        }
    }

    override suspend fun deleteAppIcon(packageName: String) {
        withContext(Dispatchers.IO) {
            getAppIconFile(packageName).takeIf { it.exists() }
                ?.delete()
        }
    }

    override suspend fun clear() {
        val extension = getCachedImageFormat().extension
        withContext(Dispatchers.IO) {
            cacheFolder.listFiles()
                ?.filter { it.extension == extension }
                ?.forEach { it.delete() }
        }
    }

    override fun getAppIconPath(packageName: String): String = getAppIconFile(packageName).path

    private fun getAppIconFile(packageName: String): File {
        val extension = getCachedImageFormat().extension
        return File(cacheFolder, "app_${packageName}_icon.$extension")
    }

    private fun getCachedImageFormat() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ImageFormat.WEBP else ImageFormat.PNG

    private enum class ImageFormat(
        val format: Bitmap.CompressFormat,
        val extension: String
    ) {

        PNG(Bitmap.CompressFormat.PNG, "png"),

        @RequiresApi(Build.VERSION_CODES.R)
        WEBP(Bitmap.CompressFormat.WEBP_LOSSLESS, "webp")
    }
}
