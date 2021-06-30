package dev.sebastiano.bundel.storage

import androidx.datastore.core.DataStore
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.preferences.schedule.WeekDay
import dev.sebastiano.bundel.proto.BundelPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException

internal class DataStorePreferenceStorage(
    private val dataStore: DataStore<BundelPreferences>
) : PreferenceStorage {

    override suspend fun isCrashlyticsEnabled(): Boolean =
        dataStore.data.first().isCrashlyticsEnabled

    override fun isCrashlyticsEnabledFlow(): Flow<Boolean> =
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
        } catch (e: Exception) {
            Timber.e(e)
            false
        }

    override suspend fun isOnboardingSeen(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setIsOnboardingSeen(enabled: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getScheduleActiveDays(): Map<WeekDay, Boolean> {
        val rawMap = dataStore.data.first().scheduleDaysMap
        return rawMap.mapKeys { WeekDay.valueOf(it.key) }.toMap()
    }

    override suspend fun setScheduleActiveDays(daysSchedule: Map<WeekDay, Boolean>): Boolean =
        try {
            dataStore.updateData { preferences ->
                preferences.toBuilder()
                    .putAllScheduleDays(daysSchedule.mapKeys { it.key.name })
                    .build()
            }
            true
        } catch (e: Exception) {
            false
        }

    override suspend fun getScheduleActiveHours(): TimeRangesSchedule {
        TODO("Not yet implemented")
    }

    override suspend fun setScheduleActiveHours(hoursSchedule: TimeRangesSchedule): Boolean {
        TODO("Not yet implemented")
    }

    companion object {

        private val DEFAULT_DAYS_SCHEDULE = WeekDay.values().map { it to true }.toMap()

        private val DEFAULT_HOURS_SCHEDULE = TimeRangesSchedule()
    }
}
