package com.ma.tehro.domain.path

import com.ma.tehro.common.LineEndpoints
import com.ma.tehro.common.TimeUtils
import com.ma.tehro.common.fractionToTime
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.domain.common.BilingualName
import com.ma.tehro.domain.schedule.repository.ScheduleRepository
import kotlin.collections.get

/**
 * Calculates train arrival times for stations along a given path, including transfer times between lines.
 *
 * @property scheduleRepository Repository for accessing train schedule data
 */
class PathTimeCalculator(
    private val scheduleRepository: ScheduleRepository,
) {
    /**
     * Calculates station arrival times and total estimated journey time for a given path.
     *
     * @param path List of path items (titles and stations)
     * @param transferDelay Time to add for line changes (default: 8 minutes)
     * @return TimeCalculationResult containing station times, total duration, and optional warning
     */
    suspend fun calculate(
        path: List<PathItem>,
        transferDelay: Int,
        dayOfWeek: Int,
        currentTime: Double? = null,
    ): TimeCalculationResult {
        var transferCount = 0
        var currentLine = 0
        var currentDestination = ""
        val stationTimes = mutableListOf<StationTime>()

        var journeyStartTime: Double? = null
        var finalArrivalTime: Double? = null
        var timeTracker = currentTime ?: 0.0

        var isFirstTitle = true
        var warningMessage: String? = null

        path.forEach { item ->
            when (item) {
                is PathItem.Title -> {
                    println("title: ${item.en}")
                    currentLine = item.en.substringAfter("Line ")
                        .substringBefore(":").toIntOrNull() ?: return@forEach

                    currentDestination = item.en
                        .substringAfter(":")
                        .trim()
                        .removePrefix("To ")
                        .trim()

                    println("line: $currentLine, destination: $currentDestination")

                    if (!isFirstTitle) {
                        transferCount++
                        val delayFraction = transferDelay.toDouble() / (24 * 60.0)
                        timeTracker += delayFraction
                        println(
                            "transfer #$transferCount, +${transferDelay}min, " +
                                    "time: ${fractionToTime(timeTracker)}"
                        )
                    }
                    isFirstTitle = false
                }

                is PathItem.StationItem -> {
                    println("station: ${item.station.name}")

                    val availableSchedules = scheduleRepository.getByStation(
                        stationName = item.station.name,
                        lineNum = currentLine,
                        isBranch = false
                    )
                    println("schedules: ${availableSchedules.size}")

                    val destinationPriority = listOfNotNull(
                        currentDestination,
                        LineEndpoints.getEn(currentLine, false)?.second,
                        LineEndpoints.getEn(currentLine, true)?.second
                    )
                    println("priority: $destinationPriority")

                    val scheduleInfo = destinationPriority.firstNotNullOfOrNull { dest ->
                        availableSchedules.find { it.destination.en == dest }
                    } ?: return@forEach
                    println("using: ${scheduleInfo.destination.en}")

                    val todaySchedule = TimeUtils.getScheduleTypeForCurrentDay(
                        scheduleTypes = scheduleInfo.timetable.keys.toList(),
                        dayOfWeek = dayOfWeek
                    )
                    println("schedule type: $todaySchedule")

                    val schedules = scheduleInfo.timetable[todaySchedule]?.sorted()
                        ?: return@forEach
                    println("train times: ${schedules.take(3)}")

                    val referenceTime = if (timeTracker == 0.0) {
                        currentTime ?: TimeUtils.getCurrentTimeAsDouble()
                    } else {
                        timeTracker
                    }
                    println("ref time: ${fractionToTime(referenceTime)}")

                    val nextTime = schedules.firstOrNull { it >= referenceTime }
                    println("next time: ${nextTime?.let { fractionToTime(it) }}")

                    if (nextTime == null && warningMessage == null) {
                        println(
                            "service ended at ${item.station.name} ${fractionToTime(referenceTime)} " +
                                    "last trains ${schedules.takeLast(3).map { fractionToTime(it) }}"
                        )

                        warningMessage = "از ساعت " +
                                fractionToTime(referenceTime).toFarsiNumber() +
                                " به بعد، قطاری برای ایستگاه " + item.station.translations.fa +
                                " وجود ندارد. زمان‌های بعدی برای فردا محاسبه شده‌اند."
                    }

                    val arrivalTime = nextTime ?: schedules.first()
                    println("arrival: ${fractionToTime(arrivalTime)}")

                    if (journeyStartTime == null) {
                        journeyStartTime = arrivalTime
                    }
                    finalArrivalTime = arrivalTime

                    stationTimes.add(
                        StationTime(
                            stationName = item.station.name,
                            line = currentLine,
                            destination = currentDestination,
                            time = arrivalTime,
                        )
                    )

                    timeTracker = arrivalTime
                    println("tracker: ${fractionToTime(timeTracker)}")
                    println("----")
                }
            }
        }

        return TimeCalculationResult(
            stationTimes = stationTimes,
            estimatedTime = calculateFinalEstimateTime(
                journeyStartTime = journeyStartTime,
                finalArrivalTime = finalArrivalTime
            ),
            warning = warningMessage
        )
    }

    /**
     * Calculates the total estimated journey time based on journey start and final arrival times.
     */
    private fun calculateFinalEstimateTime(
        journeyStartTime: Double?,
        finalArrivalTime: Double?,
    ): BilingualName {
        if (journeyStartTime == null || finalArrivalTime == null) {
            return BilingualName("0 MIN", "۰ دقیقه")
        }

        val millisInDay = 24 * 60 * 60 * 1000
        val firstMin = (journeyStartTime * millisInDay / (60 * 1000)).toInt()
        val lastMin = (finalArrivalTime * millisInDay / (60 * 1000)).toInt()

        val diff = if (lastMin >= firstMin) {
            lastMin - firstMin
        } else {
            (lastMin + 24 * 60) - firstMin
        }

        return if (diff >= 60) {
            val h = diff / 60
            val m = diff % 60
            BilingualName(
                "$h HOUR AND $m MINUTES",
                "${h.toFarsiNumber()} ساعت و ${m.toFarsiNumber()} دقیقه"
            )
        } else {
            BilingualName("$diff MIN", "${diff.toFarsiNumber()} دقیقه")
        }
    }
}