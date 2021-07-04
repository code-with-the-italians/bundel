package dev.sebastiano.bundel

import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.migrations.SharedPreferencesView
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sebastiano.bundel.preferences.schedule.DaysScheduleSerializer
import dev.sebastiano.bundel.preferences.schedule.HoursScheduleSerializer
import dev.sebastiano.bundel.proto.BundelPreferences
import dev.sebastiano.bundel.storage.BundelPreferencesSerializer
import dev.sebastiano.bundel.storage.DataStorePreferenceStorage
import dev.sebastiano.bundel.storage.PreferenceStorage
import dev.sebastiano.bundel.storage.SharedPreferencesStorage
import dev.sebastiano.bundel.storage.toProtoTimeRanges
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class ApplicationModule {

    private val Context.dataStore by dataStore(
        fileName = "bundelprefs.pb",
        serializer = BundelPreferencesSerializer,
        produceMigrations = { context ->
            listOf(
                SharedPreferencesMigration(
                    context = context,
                    sharedPreferencesName = "preferences",
                    keysToMigrate = setOf(
                        SharedPreferencesStorage.Keys.CRASHLYTICS_ENABLED,
                        SharedPreferencesStorage.Keys.ONBOARDING_SEEN,
                        SharedPreferencesStorage.Keys.DAYS_SCHEDULE,
                        SharedPreferencesStorage.Keys.HOURS_SCHEDULE
                    )
                ) { sharedPreferencesView: SharedPreferencesView, bundelPrefs: BundelPreferences ->
                    if (bundelPrefs.isMigratedFromSharedPrefs) return@SharedPreferencesMigration bundelPrefs

                    Timber.i("Migrating shared prefs to datastore...")
                    bundelPrefs.toBuilder()
                        .clearTimeRanges()
                        .addAllTimeRanges(
                            (sharedPreferencesView.getString(SharedPreferencesStorage.Keys.HOURS_SCHEDULE)
                                ?.let {
                                    HoursScheduleSerializer.deserializeFromString(it)
                                        .timeRanges
                                } ?: DataStorePreferenceStorage.DEFAULT_HOURS_SCHEDULE.timeRanges)
                                .toProtoTimeRanges()
                        )
                        .clearScheduleDays()
                        .putAllScheduleDays(
                            (sharedPreferencesView.getString(SharedPreferencesStorage.Keys.DAYS_SCHEDULE)
                                ?.let { DaysScheduleSerializer.deserializeFromString(it) }
                                ?: DataStorePreferenceStorage.DEFAULT_DAYS_SCHEDULE)
                                .mapKeys { it.key.name }
                        )
                        .setIsCrashlyticsEnabled(
                            sharedPreferencesView.getBoolean(
                                SharedPreferencesStorage.Keys.CRASHLYTICS_ENABLED,
                                defValue = false
                            )
                        )
                        .setIsOnboardingSeen(sharedPreferencesView.getBoolean(SharedPreferencesStorage.Keys.ONBOARDING_SEEN, defValue = false))
                        .setIsMigratedFromSharedPrefs(true)
                        .build()
                }
            )
        }
    )

    @Provides
    @Singleton
    fun providePreferenceStorage(@ApplicationContext context: Context): PreferenceStorage = DataStorePreferenceStorage(context.dataStore)
}
