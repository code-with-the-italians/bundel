package dev.sebastiano.bundel.preferences

import java.time.LocalDate
import java.time.Month

internal fun isWinteryEasterEggEnabled(): Boolean {
    val now = LocalDate.now()
    return now.month == Month.DECEMBER && now.dayOfMonth in 21..31
}
