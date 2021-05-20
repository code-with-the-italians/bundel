package dev.sebastiano.bundel.storage

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface PreferenceStorage {

    suspend fun loadCrashlytics(): Boolean
    suspend fun storeCrashlytics(enabled: Boolean): Boolean
}

class PreferenceStorageSP @Inject constructor(context: Context) : PreferenceStorage {

    private val storage by lazy { context.getSharedPreferences("preferences", Context.MODE_PRIVATE) }

    override suspend fun loadCrashlytics(): Boolean = withContext(Dispatchers.IO) {
        storage.getBoolean("crashlytics", false)
    }

    override suspend fun storeCrashlytics(enabled: Boolean) = withContext(Dispatchers.IO) {
        storage.edit().putBoolean("crashlytics", enabled).commit()
    }
}
