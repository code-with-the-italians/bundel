@file:Suppress("UnusedImports") // TODO bug in detekt 1.17.1 flags unused import incorrectly

package dev.sebastiano.bundel.preferences

import androidx.datastore.core.DataStore
import dev.sebastiano.bundel.preferences.schedule.TimeRange
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import dev.sebastiano.bundel.proto.BundelPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import java.time.Duration
import java.time.LocalTime
import java.time.temporal.ChronoUnit

internal class DataStorePreferences(
    private val dataStore: DataStore<BundelPreferences>
) : Preferences {

    override fun isCrashlyticsEnabled(): Flow<Boolean> =
        dataStore.data.map { it.isCrashlyticsEnabled }
            .catch { throwable ->
                when (throwable) {
                    is IOException -> {
                        Timber.e(throwable, "Error while reading Crashlytics opt-in")
                        emit(DEFAULT_CRASHLYTICS_ENABLED)
                    }
                    else -> {
                        throw throwable
                    }
                }
            }

    override suspend fun setIsCrashlyticsEnabled(enabled: Boolean) {
        try {
            dataStore.updateData { it.toBuilder().setIsCrashlyticsEnabled(enabled).build() }
        } catch (e: IOException) {
            Timber.e(e, "Unable to store new isCrashlyticsEnabled value: $enabled")
        }
    }

    override fun isWinteryEasterEggEnabled(): Flow<Boolean> =
        dataStore.data.map { it.isWinteryEasterEggEnabled }
            .catch { throwable ->
                when (throwable) {
                    is IOException -> {
                        Timber.e(throwable, "Error while reading wintery easter egg enabled state")
                        emit(DEFAULT_WINTERY_EASTER_EGG_ENABLED)
                    }
                    else -> throw throwable
                }
            }

    override suspend fun setWinteryEasterEggEnabled(enabled: Boolean) {
        try {
            dataStore.updateData { it.toBuilder().setIsWinteryEasterEggEnabled(enabled).build() }
        } catch (e: IOException) {
            Timber.e(e, "Unable to store new isWinteryEasterEggEnabled value: $enabled")
        }
    }

    override fun getExcludedPackages(): Flow<Set<String>> =
        dataStore.data.map { it.excludedPackagesList.toSet() }
            .catch { throwable ->
                when (throwable) {
                    is IOException -> {
                        Timber.e(throwable, "Error while reading excluded packages")
                        emit(emptySet())
                    }
                    else -> throw throwable
                }
            }

    override suspend fun setExcludedPackages(excludedPackages: Set<String>) {
        try {
            dataStore.updateData {
                it.toBuilder()
                    .clearExcludedPackages()
                    .addAllExcludedPackages(excludedPackages)
                    .build()
            }
        } catch (e: IOException) {
            Timber.e(e, "Unable to store new excluded packages value: $excludedPackages")
        }
    }

    override suspend fun isOnboardingSeen(): Boolean =
        dataStore.data.first().isOnboardingSeen

    override suspend fun setIsOnboardingSeen(onboardingSeen: Boolean) {
        try {
            dataStore.updateData { it.toBuilder().setIsOnboardingSeen(onboardingSeen).build() }
        } catch (e: IOException) {
            Timber.e(e, "Unable to store new isOnboardingSeen value: $onboardingSeen")
        }
    }

    override fun getDaysSchedule(): Flow<Map<WeekDay, Boolean>> =
        dataStore.data.map { it.scheduleDaysMap }
            .map { rawMap ->
                rawMap.mapKeys { WeekDay.valueOf(it.key) }
                    .takeIf { it.isNotEmpty() } ?: DEFAULT_DAYS_SCHEDULE
            }

    override suspend fun setDaysSchedule(daysSchedule: Map<WeekDay, Boolean>) {
        try {
            dataStore.updateData { preferences ->
                preferences.toBuilder()
                    .putAllScheduleDays(daysSchedule.mapKeys { it.key.name })
                    .build()
            }
        } catch (e: IOException) {
            Timber.e(e, "Unable to store new daysSchedule value: $daysSchedule")
        }
    }

    override fun getTimeRangesSchedule(): Flow<TimeRangesSchedule> =
        dataStore.data.map { it.timeRangesList }
            .map { rawRanges ->
                rawRanges.map { TimeRange(LocalTime.ofSecondOfDay(it.from.toLong()), LocalTime.ofSecondOfDay(it.to.toLong())) }
                    .takeIf { it.isNotEmpty() } ?: DEFAULT_HOURS_SCHEDULE.timeRanges
            }
            .map { TimeRangesSchedule.of(it) }

    override suspend fun setTimeRangesSchedule(timeRangesSchedule: TimeRangesSchedule) {
        try {
            dataStore.updateData { preferences ->
                val protoTimeRanges = timeRangesSchedule.timeRanges
                    .toProtoTimeRanges()

                preferences.toBuilder()
                    .clearTimeRanges()
                    .addAllTimeRanges(protoTimeRanges)
                    .build()
            }
        } catch (e: IOException) {
            Timber.e(e, "Unable to store new timeRangesSchedule value: $timeRangesSchedule")
        }
    }

    override fun getSnoozeWindowDurationSeconds(): Flow<Int> =
        dataStore.data.map { it.snoozeWindowDurationSeconds }

    override suspend fun setSnoozeWindowDurationSeconds(duration: Int) {
        try {
            dataStore.updateData { preferences ->
                preferences.toBuilder()
                    .setSnoozeWindowDurationSeconds(duration)
                    .build()
            }
        } catch (e: IOException) {
            Timber.e(e, "Unable to store new snooze window duration value: $duration")
        }
    }

    companion object {

        internal const val DEFAULT_CRASHLYTICS_ENABLED = false

        internal const val DEFAULT_WINTERY_EASTER_EGG_ENABLED = true

        internal val DEFAULT_DAYS_SCHEDULE = mapOf(
            WeekDay.MONDAY to true,
            WeekDay.TUESDAY to true,
            WeekDay.WEDNESDAY to true,
            WeekDay.THURSDAY to true,
            WeekDay.FRIDAY to true,
            WeekDay.SATURDAY to false,
            WeekDay.SUNDAY to false,
        )

        internal val DEFAULT_HOURS_SCHEDULE = TimeRangesSchedule()

        internal val DEFAULT_SNOOZE_WINDOW_DURATION_SECONDS = Duration.of(1, ChronoUnit.HOURS).seconds.toInt()
    }
}

internal fun List<TimeRange>.toProtoTimeRanges() = map {
    BundelPreferences.ProtoTimeRange.newBuilder()
        .setFrom(it.from.toSecondOfDay())
        .setTo(it.to.toSecondOfDay())
        .build()
}
