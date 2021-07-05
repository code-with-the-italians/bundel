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

    override suspend fun setIsCrashlyticsEnabled(enabled: Boolean): Boolean =
        try {
            dataStore.updateData { it.toBuilder().setIsCrashlyticsEnabled(enabled).build() }
            true
        } catch (ignored: IOException) {
            Timber.e(ignored)
            false
        }

    override suspend fun isOnboardingSeen(): Boolean =
        dataStore.data.first().isOnboardingSeen

    override suspend fun setIsOnboardingSeen(enabled: Boolean): Boolean =
        try {
            dataStore.updateData { it.toBuilder().setIsOnboardingSeen(enabled).build() }
            true
        } catch (ignored: IOException) {
            Timber.e(ignored)
            false
        }

    override fun getScheduleActiveDays(): Flow<Map<WeekDay, Boolean>> =
        dataStore.data.map { it.scheduleDaysMap }
            .map { rawMap ->
                rawMap.mapKeys { WeekDay.valueOf(it.key) }.toMap()
                    .takeIf { it.isNotEmpty() } ?: DEFAULT_DAYS_SCHEDULE
            }

    override suspend fun setScheduleActiveDays(daysSchedule: Map<WeekDay, Boolean>): Boolean =
        try {
            dataStore.updateData { preferences ->
                preferences.toBuilder()
                    .putAllScheduleDays(daysSchedule.mapKeys { it.key.name })
                    .build()
            }
            true
        } catch (ignored: IOException) {
            Timber.e(ignored)
            false
        }

    override fun getScheduleActiveHours(): Flow<TimeRangesSchedule> =
        dataStore.data.map { it.timeRangesList }
            .map { rawRanges ->
                rawRanges.map { TimeRange(LocalTime.ofSecondOfDay(it.from.toLong()), LocalTime.ofSecondOfDay(it.to.toLong())) }
                    .takeIf { it.isNotEmpty() } ?: DEFAULT_HOURS_SCHEDULE.timeRanges
            }
            .map { TimeRangesSchedule.of(it) }

    override suspend fun setScheduleActiveHours(hoursSchedule: TimeRangesSchedule): Boolean =
        try {
            dataStore.updateData { preferences ->
                val protoTimeRanges = hoursSchedule.timeRanges
                    .toProtoTimeRanges()

                preferences.toBuilder()
                    .clearTimeRanges()
                    .addAllTimeRanges(protoTimeRanges)
                    .build()
            }
            true
        } catch (ignored: IOException) {
            Timber.e(ignored)
            false
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
