package dev.sebastiano.bundel.preferences.schedule

import androidx.annotation.StringRes
import dev.sebastiano.bundel.R
import java.time.DayOfWeek

internal enum class WeekDay(
    @StringRes val displayResId: Int,
    val dayOfWeek: DayOfWeek
) {

    MONDAY(displayResId = R.string.day_monday, dayOfWeek = DayOfWeek.MONDAY),
    TUESDAY(displayResId = R.string.day_tuesday, dayOfWeek = DayOfWeek.TUESDAY),
    WEDNESDAY(displayResId = R.string.day_wednesday, dayOfWeek = DayOfWeek.WEDNESDAY),
    THURSDAY(displayResId = R.string.day_thursday, dayOfWeek = DayOfWeek.THURSDAY),
    FRIDAY(displayResId = R.string.day_friday, dayOfWeek = DayOfWeek.FRIDAY),
    SATURDAY(displayResId = R.string.day_saturday, dayOfWeek = DayOfWeek.SATURDAY),
    SUNDAY(displayResId = R.string.day_sunday, dayOfWeek = DayOfWeek.SUNDAY)
}
