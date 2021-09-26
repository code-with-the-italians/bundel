package dev.sebastiano.bundel.onboarding

import dev.sebastiano.bundel.preferences.schedule.WeekDay

internal class DaysSchedulePageState(
    val daysSchedule: Map<WeekDay, Boolean>,
    val onDayCheckedChange: (day: WeekDay, checked: Boolean) -> Unit
) {

    constructor() : this(daysSchedule = WeekDay.values().associate { it to true }, onDayCheckedChange = { _, _ -> })
}
