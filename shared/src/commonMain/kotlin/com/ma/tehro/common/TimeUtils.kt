package com.ma.tehro.common

import com.ma.tehro.data.ScheduleType
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime
import kotlin.math.floor
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
object TimeUtils {

    fun getCurrentTimeAsDouble(): Double {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val secondsOfDay = now.hour * 3600 + now.minute * 60 + now.second
        return secondsOfDay.toDouble() / 86400.0
    }

    fun getScheduleTypeForCurrentDay(
        scheduleTypes: List<ScheduleType>,
        dayOfWeek: Int? = null
    ): ScheduleType? {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val isoDayOfWeek = dayOfWeek ?: now.dayOfWeek.isoDayNumber

        return when (isoDayOfWeek) {
            1, 2, 3, 4, 5 -> scheduleTypes.find {
                it == ScheduleType.SATURDAY_TO_WEDNESDAY ||
                        it == ScheduleType.ALL_DAY ||
                        it == ScheduleType.SATURDAY_TO_THURSDAY
            }

            6 -> scheduleTypes.find {
                it == ScheduleType.THURSDAY ||
                        it == ScheduleType.ALL_DAY ||
                        it == ScheduleType.SATURDAY_TO_THURSDAY
            }

            7 -> scheduleTypes.find {
                it == ScheduleType.FRIDAY ||
                        it == ScheduleType.ALL_DAY ||
                        it == ScheduleType.HOLIDAYS_AND_FRIDAY
            }

            else -> null
        }
    }

    fun remainingTime(target: Double): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentSeconds = now.hour * 3600 + now.minute * 60 + now.second
        val currentFraction = currentSeconds.toDouble() / 86400.0

        val remainingFraction = target - currentFraction
        if (remainingFraction <= 0) return "00:00:00"

        val totalSeconds = floor(remainingFraction * 86400).toInt()
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        val hoursStr = hours.toString().padStart(2, '0')
        val minutesStr = minutes.toString().padStart(2, '0')
        val secondsStr = seconds.toString().padStart(2, '0')

        return "$hoursStr:$minutesStr:$secondsStr"
    }
}
