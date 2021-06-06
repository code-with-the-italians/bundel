package dev.sebastiano.bundel.storage

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface PreferenceStorage {

    suspend fun isCrashlyticsEnabled(): Boolean
    suspend fun setIsCrashlyticsEnabled(enabled: Boolean): Boolean
}

class SharedPreferencesStorage @Inject constructor(context: Context) : PreferenceStorage {

    private val storage by lazy { context.getSharedPreferences("preferences", Context.MODE_PRIVATE) }

    override suspend fun isCrashlyticsEnabled(): Boolean = withContext(Dispatchers.IO) {
        storage.getBoolean("crashlytics", false)
    }

    override suspend fun setIsCrashlyticsEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        storage.edit().putBoolean("crashlytics", enabled).commit()
    }
}
