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
     * @param lineChangeDelayMinutes Time to add for line changes (default: 8 minutes)
     * @return TimeCalculationResult containing station times, total duration, and optional warning
     */
    suspend fun calculateStationTimes(
        path: List<PathItem>,
        lineChangeDelayMinutes: Int,
        dayOfWeek: Int,
        currentTime: Double? = null,
    ): TimeCalculationResult {
        var transferCount = 0
        var currentLine = 0
        var currentDestination = ""
        val stationTimes = mutableMapOf<String, Double>()
        var timeTracker = currentTime ?: 0.0
        var isFirstTitle = true
        var warningMessage: String? = null

        path.forEach { item ->
            when (item) {
                is PathItem.Title -> {
                    println("title: ${item.en}")
                    currentLine = item.en.substringAfter("Line ")
                        .substringBefore(":").toIntOrNull() ?: return@forEach

                    currentDestination = item.en.substringAfter(":")
                        .removePrefix("To ").trim()
                    println("line: $currentLine, destination: $currentDestination")

                    if (!isFirstTitle) {
                        transferCount++
                        val delayFraction = lineChangeDelayMinutes.toDouble() / (24 * 60.0)
                        timeTracker += delayFraction
                        println(
                            "transfer #$transferCount, +${lineChangeDelayMinutes}min, " +
                                    "time: ${fractionToTime(timeTracker)}"
                        )
                    }
                    isFirstTitle = false
                }

                is PathItem.StationItem -> {
                    println("station: ${item.station.name}")
                    if (stationTimes.containsKey(item.station.name)) return@forEach

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
                        scheduleTypes = scheduleInfo.schedules.keys.toList(),
                        dayOfWeek = dayOfWeek
                    )
                    println("schedule type: $todaySchedule")

                    val schedules = scheduleInfo.schedules[todaySchedule]?.sorted()
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

                    stationTimes[item.station.name] = arrivalTime
                    timeTracker = arrivalTime
                    println("tracker: ${fractionToTime(timeTracker)}")
                    println("----")
                }
            }
        }

        return TimeCalculationResult(
            stationTimes = stationTimes.mapValues { fractionToTime(it.value) },
            estimatedTime = calculateFinalEstimateTime(stationTimes, transferCount),
            warning = warningMessage
        )
    }

    /**
     * Calculates the total estimated journey time based on station times and line changes.
     *
     * @param stationTimes Map of station names to their arrival times (as day fractions)
     * @param transferCount Number of line changes in the journey
     * @return Bilingual estimated time (English and Farsi)
     */
    private fun calculateFinalEstimateTime(
        stationTimes: Map<String, Double>,
        transferCount: Int
    ): BilingualName {
        if (stationTimes.isEmpty()) return BilingualName("0 MIN", "۰ دقیقه")

        val times = stationTimes.values
        val first = times.minOrNull() ?: 0.0
        val last = times.maxOrNull() ?: 0.0

        val millisInDay = 24 * 60 * 60 * 1000
        val firstMin = (first * millisInDay / (60 * 1000)).toInt()
        val lastMin = (last * millisInDay / (60 * 1000)).toInt()

        val diff = if (lastMin >= firstMin) {
            lastMin - firstMin
        } else {
            (lastMin + 24 * 60) - firstMin
        }

        val totalMin = diff + (transferCount * 8)

        return if (totalMin >= 60) {
            val h = totalMin / 60
            val m = totalMin % 60
            BilingualName(
                "$h HOUR AND $m MINUTES",
                "${h.toFarsiNumber()} ساعت و ${m.toFarsiNumber()} دقیقه"
            )
        } else {
            BilingualName("$totalMin MIN", "${totalMin.toFarsiNumber()} دقیقه")
        }
    }
}