package com.ma.tehro.common

import java.util.Calendar
import com.ma.tehro.data.ScheduleType

object TimeUtils {
    fun getCurrentTimeAsDouble(): Double {
        return Calendar.getInstance().run {
            (get(Calendar.HOUR_OF_DAY) * 3600 +
                    get(Calendar.MINUTE) * 60 +
                    get(Calendar.SECOND)).toDouble() / 86400.0
        }
    }

    fun getScheduleTypeForCurrentDay(
        scheduleTypes: List<ScheduleType>,
        dayOfWeek: Int? = null
    ): ScheduleType? {
        val actualDayOfWeek = dayOfWeek ?: Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return when (actualDayOfWeek) {
            Calendar.SATURDAY, Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY -> {
                scheduleTypes.find { it == ScheduleType.SATURDAY_TO_WEDNESDAY || it == ScheduleType.ALL_DAY || it == ScheduleType.SATURDAY_TO_THURSDAY }
            }

            Calendar.THURSDAY -> {
                scheduleTypes.find { it == ScheduleType.THURSDAY || it == ScheduleType.ALL_DAY || it == ScheduleType.SATURDAY_TO_THURSDAY }
            }

            Calendar.FRIDAY -> {
                scheduleTypes.find { it == ScheduleType.FRIDAY || it == ScheduleType.ALL_DAY || it == ScheduleType.HOLIDAYS_AND_FRIDAY }
            }

            else -> null
        }
    }

    fun remainingTime(target: Double): String {
        val calendar = Calendar.getInstance()

        val currentSeconds = calendar.get(Calendar.HOUR_OF_DAY) * 3600 +
                calendar.get(Calendar.MINUTE) * 60 +
                calendar.get(Calendar.SECOND)
        val currentTimeFraction = currentSeconds / 86400.0

        val remaining = target - currentTimeFraction
        if (remaining <= 0) return "00:00:00"

        val totalSeconds = (remaining * 86400).toInt()
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return StringBuilder(8)
            .append(hours.toString().padStart(2, '0'))
            .append(':')
            .append(minutes.toString().padStart(2, '0'))
            .append(':')
            .append(seconds.toString().padStart(2, '0'))
            .toString()
    }
}