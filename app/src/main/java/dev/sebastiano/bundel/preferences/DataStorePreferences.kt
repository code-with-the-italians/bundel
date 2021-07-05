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
import java.time.LocalTime

internal class DataStorePreferences(
    private val dataStore: DataStore<BundelPreferences>
) : Preferences {

    override fun isCrashlyticsEnabled(): Flow<Boolean> =
        dataStore.data.map { it.isCrashlyticsEnabled }
            .catch { throwable ->
                when (throwable) {
                    is IOException -> {
                        Timber.e(throwable)
                        emit(true)
                    }
                    else -> {
                        throw throwable
                    }
                }
            }

    override suspend fun setIsCrashlyticsEnabled(enabled: Boolean) {
        try {
            dataStore.updateData { it.toBuilder().setIsCrashlyticsEnabled(enabled).build() }
        } catch (ignored: IOException) {
            Timber.e(ignored, "Unable to store new isCrashlyticsEnabled value: $enabled")
        }
    }

    override suspend fun isOnboardingSeen(): Boolean =
        dataStore.data.first().isOnboardingSeen

    override suspend fun setIsOnboardingSeen(onboardingSeen: Boolean) {
        try {
            dataStore.updateData { it.toBuilder().setIsOnboardingSeen(onboardingSeen).build() }
        } catch (ignored: IOException) {
            Timber.e(ignored, "Unable to store new isOnboardingSeen value: $onboardingSeen")
        }
    }

    override fun getDaysSchedule(): Flow<Map<WeekDay, Boolean>> =
        dataStore.data.map { it.scheduleDaysMap }
            .map { rawMap ->
                rawMap.mapKeys { WeekDay.valueOf(it.key) }.toMap()
                    .takeIf { it.isNotEmpty() } ?: DEFAULT_DAYS_SCHEDULE
            }

    override suspend fun setDaysSchedule(daysSchedule: Map<WeekDay, Boolean>) {
        try {
            dataStore.updateData { preferences ->
                preferences.toBuilder()
                    .putAllScheduleDays(daysSchedule.mapKeys { it.key.name })
                    .build()
            }
        } catch (ignored: IOException) {
            Timber.e(ignored, "Unable to store new daysSchedule value: $daysSchedule")
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
        } catch (ignored: IOException) {
            Timber.e(ignored, "Unable to store new timeRangesSchedule value: $timeRangesSchedule")
        }
    }

    companion object {

        internal val DEFAULT_DAYS_SCHEDULE = WeekDay.values().map { it to true }.toMap()

        internal val DEFAULT_HOURS_SCHEDULE = TimeRangesSchedule()
    }
}

internal fun List<TimeRange>.toProtoTimeRanges() = map {
    BundelPreferences.ProtoTimeRange.newBuilder()
        .setFrom(it.from.toSecondOfDay())
        .setTo(it.to.toSecondOfDay())
        .build()
}
