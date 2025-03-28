package com.ma.tehro.ui.shortestpath.pathfinder

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.common.LineEndpoints
import com.ma.tehro.common.TimeUtils
import com.ma.tehro.common.fractionToTime
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.data.BilingualName
import com.ma.tehro.data.repo.PathItem
import com.ma.tehro.data.repo.PathRepository
import com.ma.tehro.data.repo.TrainScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@Stable
data class PathFinderState(
    val shortestPath: List<PathItem> = emptyList(),
    val estimatedTime: BilingualName? = null,
    val stationTimes: Map<String, String> = emptyMap()
)

@HiltViewModel
class PathViewModel @Inject constructor(
    private val pathRepository: PathRepository,
    savedStateHandle: SavedStateHandle,
    private val trainScheduleRepository: TrainScheduleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PathFinderState())
    val state: StateFlow<PathFinderState> get() = _state

    init {
        val from = savedStateHandle.get<String>("startEnStation")!!
        val to = savedStateHandle.get<String>("enDestination")!!
        viewModelScope.launch {
            val path = pathRepository.findShortestPathWithDirection(from = from, to = to)
            println("PathViewModel PathRepo: path with title-> $path ")
            _state.update { it.copy(shortestPath = path) }

            val estimatedTimes = calculateStationTimes(path)
            println(estimatedTimes)
            _state.update { it.copy(stationTimes = estimatedTimes) }
        }
    }

    private suspend fun calculateStationTimes(path: List<PathItem>): Map<String, String> {
        val stationTimes = mutableMapOf<String, Double>()
        var currentTime = 0.0
        var lineChanges = 0
        var currentLine = 0
        var currentDestination = ""
        path.forEach { item ->
            when (item) {
                is PathItem.Title -> {
                    currentLine = item.en.substringAfter("Line ")
                        .substringBefore(":")
                        .toIntOrNull() ?: return@forEach

                    currentDestination = item.en.substringAfter(":")
                        .trim()
                        .removePrefix("To ")
                        .trim()

                    lineChanges++
                }

                is PathItem.StationItem -> {
                    if (stationTimes.containsKey(item.station.name)) return@forEach
                    val stationName = item.station.name
                    val scheduleInfo = trainScheduleRepository.getScheduleByStation(
                        stationName,
                        currentLine,
                        false
                    ).run {
                        find { it.destination.en == currentDestination }
                            ?: find {
                                it.destination.en == LineEndpoints.getEn(
                                    currentLine,
                                    false
                                )?.second
                            }
                            ?: find {
                                it.destination.en == LineEndpoints.getEn(
                                    currentLine,
                                    true
                                )?.second
                            }
                    } ?: return@forEach
                    println("PathViewModel scheduleInfo: ${scheduleInfo.schedules.keys.firstOrNull()} for $stationName")

                    val todaySchedule =
                        TimeUtils.getScheduleTypeForCurrentDay(scheduleInfo.schedules.keys.toList())

                    val schedules =
                        scheduleInfo.schedules[todaySchedule]?.sorted() ?: return@forEach
                    println("PathViewModel scheduleInfo: ${schedules.firstOrNull()} for $stationName")

                    if (schedules.isEmpty()) return@forEach

                    val referenceTime = if (currentTime == 0.0) {
                        TimeUtils.getCurrentTimeAsDouble()
                    } else {
                        currentTime
                    }

                    val nextTime = when {
                        referenceTime > schedules.last() -> schedules.first()
                        else -> schedules.firstOrNull { it > referenceTime } ?: schedules.first()
                    }
                    stationTimes[stationName] = nextTime
                    currentTime = nextTime
                }
            }
        }


        val finalEstimate = calculateFinalEstimateTime(stationTimes, lineChanges - 1)
        _state.update { it.copy(estimatedTime = finalEstimate) }


        return stationTimes.mapValues { fractionToTime(it.value) }
    }

    private fun calculateFinalEstimateTime(
        stationTimes: Map<String, Double>,
        lineChanges: Int
    ): BilingualName {
        if (stationTimes.isEmpty()) return BilingualName("0 min", "۰ دقیقه")

        val times = stationTimes.values
        val firstTime = times.minOrNull() ?: 0.0
        val lastTime = times.maxOrNull() ?: 0.0

        val millisInDay = 24 * 60 * 60 * 1000
        val firstMinutes = (firstTime * millisInDay / (60 * 1000)).toInt()
        val lastMinutes = (lastTime * millisInDay / (60 * 1000)).toInt()

        val timeDiffMinutes = if (lastMinutes >= firstMinutes) {
            lastMinutes - firstMinutes
        } else {
            (lastMinutes + 24 * 60) - firstMinutes
        }

        val totalMinutes = timeDiffMinutes + (lineChanges * 8)

        return if (totalMinutes >= 60) {
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            BilingualName(
                en = "$hours HOUR AND $minutes MINUTES",
                fa = "${hours.toFarsiNumber()} ساعت و ${minutes.toFarsiNumber()} دقیقه"
            )
        } else {
            BilingualName(
                en = "$totalMinutes min",
                fa = "${totalMinutes.toFarsiNumber()} دقیقه"
            )
        }
    }
}