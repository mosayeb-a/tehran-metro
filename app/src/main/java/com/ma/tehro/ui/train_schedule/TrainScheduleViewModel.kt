package com.ma.tehro.ui.train_schedule

import android.icu.util.Calendar
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.data.ScheduleType
import com.ma.tehro.data.StationName
import com.ma.tehro.data.repo.GroupedScheduleInfo
import com.ma.tehro.data.repo.TrainScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Immutable
data class ScheduleSection(
    val type: ScheduleType,
    val times: List<Double>,
    val isCurrentDay: Boolean
)

@Immutable
data class TrainScheduleState(
    val stationName: String = "",
    val lineNumber: Int = 0,
    val schedules: List<GroupedScheduleInfo> = emptyList(),
    val selectedScheduleTypes: Map<StationName, ScheduleType?> = emptyMap(),
    val currentTimeAsDouble: Double = 0.0,
    val currentDayType: ScheduleType? = null,
    val isLoading: Boolean = true,
    val processedSchedules: Map<StationName, List<ScheduleSection>> = emptyMap(),
    val initialScrollPositions: Map<StationName, Int?> = emptyMap()
)

@HiltViewModel
class TrainScheduleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TrainScheduleRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(TrainScheduleState())
    val state = _state.asStateFlow()

    private val timeUpdateJob = Job()
    private val timeScope = CoroutineScope(Dispatchers.Default + timeUpdateJob)

    init {
        savedStateHandle.get<String>("enStationName")?.let { stationName ->
            savedStateHandle.get<Int>("lineNumber")?.let { lineNumber ->
                savedStateHandle.get<Boolean>("useBranch")?.let { isBranch ->
                    loadSchedules(stationName, lineNumber, isBranch)
                    startTimeUpdates()
                }
            }
        }
    }

    private fun loadSchedules(stationName: String, lineNumber: Int, isBranch: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val schedules = repository.getScheduleByStation(stationName, lineNumber, isBranch)
            val currentDayType =
                getScheduleTypeForCurrentDay(schedules.flatMap { it.schedules.keys })
            val currentTime = getCurrentTimeAsDouble()

            val processedData = schedules.associate { groupInfo ->
                val selectedType = groupInfo.schedules.keys.run {
                    find { it == currentDayType }
                        ?: find { it == ScheduleType.ALL_DAY }
                        ?: firstOrNull()
                }

                val sections = groupInfo.schedules.map { (type, times) ->
                    ScheduleSection(
                        type = type,
                        times = times,
                        isCurrentDay = type == currentDayType || type == ScheduleType.ALL_DAY
                    )
                }

                val scrollPosition = sections.findInitialScrollPosition(currentTime)

                groupInfo.destination to Triple(selectedType, sections, scrollPosition)
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    schedules = schedules,
                    stationName = stationName,
                    lineNumber = lineNumber,
                    selectedScheduleTypes = processedData.mapValues { it.value.first },
                    currentDayType = currentDayType,
                    processedSchedules = processedData.mapValues { it.value.second },
                    initialScrollPositions = processedData.mapValues { it.value.third },
                    currentTimeAsDouble = currentTime
                )
            }
        }
    }

    fun onScheduleTypeSelected(destination: StationName, scheduleType: ScheduleType?) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentState = state.value
            val schedule = currentState.schedules.find { it.destination == destination }

            schedule?.let {
                val newSections = it.schedules.map { (type, times) ->
                    ScheduleSection(
                        type = type,
                        times = times,
                        isCurrentDay = type == currentState.currentDayType ||
                                type == ScheduleType.ALL_DAY
                    )
                }

                val newScrollPosition = if (scheduleType != null) {
                    newSections.findInitialScrollPosition(currentState.currentTimeAsDouble)
                } else null

                _state.update { state ->
                    state.copy(
                        selectedScheduleTypes = state.selectedScheduleTypes + (destination to scheduleType),
                        processedSchedules = state.processedSchedules + (destination to newSections),
                        initialScrollPositions = state.initialScrollPositions + (destination to newScrollPosition)
                    )
                }
            }
        }
    }

    private fun startTimeUpdates() {
        timeScope.launch {
            while (isActive) {
                _state.update { it.copy(currentTimeAsDouble = getCurrentTimeAsDouble()) }
                delay(1000 - (System.currentTimeMillis() % 1000))
            }
        }
    }

    private fun List<ScheduleSection>.findInitialScrollPosition(currentTime: Double): Int? {
        var position = 0
        forEach { section ->
            position++
            if (section.isCurrentDay) {
                section.times.indexOfFirst { it > currentTime }
                    .takeIf { it != -1 }
                    ?.let { return position + it }
            }
            position += section.times.size + 1
        }
        return null
    }

    override fun onCleared() {
        super.onCleared()
        timeUpdateJob.cancel()
    }

    private fun getScheduleTypeForCurrentDay(types: List<ScheduleType>): ScheduleType? {
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            in Calendar.SATURDAY downTo Calendar.WEDNESDAY -> types.firstOrNull {
                it in listOf(
                    ScheduleType.SATURDAY_TO_WEDNESDAY,
                    ScheduleType.ALL_DAY,
                    ScheduleType.SATURDAY_TO_THURSDAY
                )
            }

            Calendar.THURSDAY -> types.firstOrNull {
                it in listOf(
                    ScheduleType.THURSDAY,
                    ScheduleType.ALL_DAY,
                    ScheduleType.SATURDAY_TO_THURSDAY
                )
            }

            Calendar.FRIDAY -> types.firstOrNull {
                it in listOf(
                    ScheduleType.FRIDAY,
                    ScheduleType.ALL_DAY,
                    ScheduleType.HOLIDAYS_AND_FRIDAY
                )
            }

            else -> null
        }
    }

    private fun getCurrentTimeAsDouble(): Double {
        return Calendar.getInstance().run {
            (get(Calendar.HOUR_OF_DAY) * 3600 +
                    get(Calendar.MINUTE) * 60 +
                    get(Calendar.SECOND)).toDouble() / 86400.0
        }
    }
}