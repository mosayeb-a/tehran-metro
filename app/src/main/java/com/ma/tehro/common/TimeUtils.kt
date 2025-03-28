package com.ma.tehro.common

import android.icu.util.Calendar
import com.ma.tehro.data.ScheduleType

object TimeUtils {
    fun getCurrentTimeAsDouble(): Double {
        return Calendar.getInstance().run {
            (get(Calendar.HOUR_OF_DAY) * 3600 +
                    get(Calendar.MINUTE) * 60 +
                    get(Calendar.SECOND)).toDouble() / 86400.0
        }
    }

    fun getScheduleTypeForCurrentDay(scheduleTypes: List<ScheduleType>): ScheduleType? {
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
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
}