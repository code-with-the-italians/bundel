package dev.sebastiano.bundel.preferences.schedule

import androidx.annotation.StringRes
import dev.sebastiano.bundel.R

internal enum class WeekDay(@StringRes val displayResId: Int) {
    MONDAY(R.string.day_monday),
    TUESDAY(R.string.day_tuesday),
    WEDNESDAY(R.string.day_wednesday),
    THURSDAY(R.string.day_thursday),
    FRIDAY(R.string.day_friday),
    SATURDAY(R.string.day_saturday),
    SUNDAY(R.string.day_sunday)
}
