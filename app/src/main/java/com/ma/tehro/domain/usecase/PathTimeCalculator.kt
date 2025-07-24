package com.ma.tehro.domain.usecase

import com.ma.tehro.common.LineEndpoints
import com.ma.tehro.common.TimeUtils
import com.ma.tehro.common.fractionToTime
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.data.BilingualName
import com.ma.tehro.data.repo.PathItem
import com.ma.tehro.data.repo.TrainScheduleRepository
import javax.inject.Inject

/**
 * Calculates train arrival times for stations along a given path, including transfer times between lines.
 *
 * @property trainScheduleRepository Repository for accessing train schedule data
 */
class PathTimeCalculator @Inject constructor(
    private val trainScheduleRepository: TrainScheduleRepository,
) {
    /**
     * Calculates station arrival times and total estimated journey time for a given path.
     *
     * @param path List of path items (titles and stations)
     * @param lineChangeDelayMinutes Time to add for line changes (default: 8 minutes)
     * @return Pair of station arrival times and total estimated journey time
     */
    suspend fun calculateStationTimes(
        path: List<PathItem>,
        lineChangeDelayMinutes: Int,
        dayOfWeek: Int,
        currentTime: Double? = null,
    ): Pair<Map<String, String>, BilingualName> {
        var lineChanges = 0
        var currentLine = 0
        var currentDestination = ""
        val stationTimes = mutableMapOf<String, Double>()
        var timeTracker = currentTime ?: 0.0

        path.forEachIndexed { index, item ->
            when (item) {
                is PathItem.Title -> {
                    currentLine = item.en.substringAfter("Line ")
                        .substringBefore(":").toIntOrNull() ?: return@forEachIndexed

                    currentDestination = item.en.substringAfter(":")
                        .removePrefix("To ").trim()
                    lineChanges++

                    val delayFraction = lineChangeDelayMinutes.toDouble() / (24 * 60.0)
                    timeTracker += delayFraction
                }

                is PathItem.StationItem -> {
                    if (stationTimes.containsKey(item.station.name)) return@forEachIndexed

                    val scheduleInfo = trainScheduleRepository.getScheduleByStation(
                        item.station.name, currentLine, false
                    ).run {
                        find { it.destination.en == currentDestination }
                            ?: find {
                                it.destination.en == LineEndpoints.getEn(currentLine, false)?.second
                            }
                            ?: find {
                                it.destination.en == LineEndpoints.getEn(currentLine, true)?.second
                            }
                    } ?: return@forEachIndexed

                    val todaySchedule =
                        TimeUtils.getScheduleTypeForCurrentDay(
                            scheduleTypes = scheduleInfo.schedules.keys.toList(),
                            dayOfWeek = dayOfWeek
                        )

                    val schedules = scheduleInfo.schedules[todaySchedule]?.sorted()
                        ?: return@forEachIndexed

                    val referenceTime = if (timeTracker == 0.0) {
                        currentTime ?: TimeUtils.getCurrentTimeAsDouble()
                    } else {
                        timeTracker
                    }

                    val nextTime =
                        schedules.firstOrNull { it >= referenceTime } ?: schedules.first()
                    stationTimes[item.station.name] = nextTime
                    timeTracker = nextTime
                }
            }
        }

        val estimate = calculateFinalEstimateTime(stationTimes, lineChanges - 1)
        return stationTimes.mapValues { fractionToTime(it.value) } to estimate
    }

    /**
     * Calculates the total estimated journey time based on station times and line changes.
     *
     * @param stationTimes Map of station names to their arrival times (as day fractions)
     * @param lineChanges Number of line changes in the journey
     * @return Bilingual estimated time (English and Farsi)
     */
    private fun calculateFinalEstimateTime(
        stationTimes: Map<String, Double>,
        lineChanges: Int
    ): BilingualName {
        if (stationTimes.isEmpty()) return BilingualName("0 MIN", "۰ دقیقه")

        val times = stationTimes.values
        val first = times.minOrNull() ?: 0.0
        val last = times.maxOrNull() ?: 0.0

        val millisInDay = 24 * 60 * 60 * 1000
        val firstMin = (first * millisInDay / (60 * 1000)).toInt()
        val lastMin = (last * millisInDay / (60 * 1000)).toInt()

        val diff = if (lastMin >= firstMin) lastMin - firstMin else (lastMin + 24 * 60) - firstMin
        val totalMin = diff + (lineChanges * 8)

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