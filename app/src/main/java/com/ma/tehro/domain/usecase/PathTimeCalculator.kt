package com.ma.tehro.domain.usecase

import com.ma.tehro.common.LineEndpoints
import com.ma.tehro.common.TimeUtils
import com.ma.tehro.common.fractionToTime
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.data.BilingualName
import com.ma.tehro.data.repo.PathItem
import com.ma.tehro.data.repo.TrainScheduleRepository
import javax.inject.Inject
import kotlin.collections.get

class PathTimeCalculator @Inject constructor(
    private val trainScheduleRepository: TrainScheduleRepository
) {
    suspend fun calculateStationTimes(
        path: List<PathItem>,
    ): Pair<Map<String, String>, BilingualName> {
        val stationTimes = mutableMapOf<String, Double>()
        var currentTime = 0.0
        var lineChanges = 0
        var currentLine = 0
        var currentDestination = ""

        path.forEach { item ->
            when (item) {
                is PathItem.Title -> {
                    currentLine = item.en.substringAfter("Line ")
                        .substringBefore(":").toIntOrNull() ?: return@forEach
                    currentDestination = item.en.substringAfter(":")
                        .removePrefix("To ").trim()
                    lineChanges++
                }

                is PathItem.StationItem -> {
                    if (stationTimes.containsKey(item.station.name)) return@forEach
                    val stationName = item.station.name

                    val scheduleInfo = trainScheduleRepository.getScheduleByStation(
                        stationName, currentLine, false
                    ).run {
                        find { it.destination.en == currentDestination }
                            ?: find { it.destination.en == LineEndpoints.getEn(currentLine, false)?.second }
                            ?: find { it.destination.en == LineEndpoints.getEn(currentLine, true)?.second }
                    } ?: return@forEach

                    val todaySchedule =
                        TimeUtils.getScheduleTypeForCurrentDay(scheduleInfo.schedules.keys.toList())

                    val schedules = scheduleInfo.schedules[todaySchedule]?.sorted() ?: return@forEach

                    val referenceTime = if (currentTime == 0.0)
                        TimeUtils.getCurrentTimeAsDouble()
                    else currentTime

                    val nextTime = schedules.firstOrNull { it > referenceTime } ?: schedules.first()
                    stationTimes[stationName] = nextTime
                    currentTime = nextTime
                }
            }
        }

        val estimate = calculateFinalEstimateTime(stationTimes, lineChanges - 1)
        return stationTimes.mapValues { fractionToTime(it.value) } to estimate
    }

    private fun calculateFinalEstimateTime(
        stationTimes: Map<String, Double>,
        lineChanges: Int
    ): BilingualName {
        if (stationTimes.isEmpty()) return BilingualName("0 min", "۰ دقیقه")

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
            BilingualName("$totalMin min", "${totalMin.toFarsiNumber()} دقیقه")
        }
    }
}