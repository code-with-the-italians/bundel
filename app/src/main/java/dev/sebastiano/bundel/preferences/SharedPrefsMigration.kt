@file:Suppress("UnusedImports") // TODO bug in detekt 1.17.1 flags unused import incorrectly

package dev.sebastiano.bundel.preferences

import android.content.Context
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.migrations.SharedPreferencesView
import dev.sebastiano.bundel.preferences.schedule.DaysScheduleSerializer
import dev.sebastiano.bundel.preferences.schedule.HoursScheduleSerializer
import dev.sebastiano.bundel.proto.BundelPreferences
import timber.log.Timber

internal fun sharedPrefsMigration(context: Context) = SharedPreferencesMigration(
    context = context,
    sharedPreferencesName = "preferences",
    keysToMigrate = setOf(
        Keys.CRASHLYTICS_ENABLED,
        Keys.ONBOARDING_SEEN,
        Keys.DAYS_SCHEDULE,
        Keys.HOURS_SCHEDULE
    )
) { sharedPreferencesView: SharedPreferencesView, bundelPrefs: BundelPreferences ->
    if (bundelPrefs.isMigratedFromSharedPrefs) return@SharedPreferencesMigration bundelPrefs

    Timber.i("Migrating shared prefs to datastore...")

    val timeRanges = sharedPreferencesView.getString(Keys.HOURS_SCHEDULE)
        ?.let {
            HoursScheduleSerializer.deserializeFromString(it)
                .timeRanges
        } ?: DataStorePreferenceStorage.DEFAULT_HOURS_SCHEDULE.timeRanges

    val daysMap = sharedPreferencesView.getString(Keys.DAYS_SCHEDULE)
        ?.let { DaysScheduleSerializer.deserializeFromString(it) }
        ?: DataStorePreferenceStorage.DEFAULT_DAYS_SCHEDULE

    bundelPrefs.toBuilder()
        .clearTimeRanges()
        .addAllTimeRanges(timeRanges.toProtoTimeRanges())
        .clearScheduleDays()
        .putAllScheduleDays(daysMap.mapKeys { it.key.name })
        .setIsCrashlyticsEnabled(sharedPreferencesView.getBoolean(Keys.CRASHLYTICS_ENABLED, defValue = false))
        .setIsOnboardingSeen(sharedPreferencesView.getBoolean(Keys.ONBOARDING_SEEN, defValue = false))
        .setIsMigratedFromSharedPrefs(true)
        .build()
}

internal object Keys {

    const val CRASHLYTICS_ENABLED = "crashlytics"
    const val ONBOARDING_SEEN = "onboarding_seen"
    const val DAYS_SCHEDULE = "days_schedule"
    const val HOURS_SCHEDULE = "hours_schedule"
}
