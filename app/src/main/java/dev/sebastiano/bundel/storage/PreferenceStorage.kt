package dev.sebastiano.bundel.storage

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface PreferenceStorage {

    suspend fun isCrashlyticsEnabled(): Boolean
    suspend fun setIsCrashlyticsEnabled(enabled: Boolean): Boolean

    suspend fun isOnboardingSeen(): Boolean
    suspend fun setIsOnboardingSeen(enabled: Boolean): Boolean
}

class SharedPreferencesStorage @Inject constructor(context: Context) : PreferenceStorage {

    private val storage by lazy { context.getSharedPreferences("preferences", Context.MODE_PRIVATE) }

    override suspend fun isCrashlyticsEnabled(): Boolean = withContext(Dispatchers.IO) {
        storage.getBoolean(Keys.CRASHLYTICS_ENABLED, false)
    }

    override suspend fun setIsCrashlyticsEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        storage.edit().putBoolean(Keys.CRASHLYTICS_ENABLED, enabled).commit()
    }

    override suspend fun isOnboardingSeen(): Boolean = withContext(Dispatchers.IO) {
        storage.getBoolean(Keys.ONBOARDING_SEEN, false)
    }

    override suspend fun setIsOnboardingSeen(enabled: Boolean): Boolean = withContext(Dispatchers.IO) {
        storage.edit().putBoolean(Keys.ONBOARDING_SEEN, enabled).commit()
    }

    private object Keys {

        const val CRASHLYTICS_ENABLED = "crashlytics"
        const val ONBOARDING_SEEN = "onboarding_seen"
    }
}
