package com.ma.tehro.feature.train_schedule

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.common.TimeUtils
import com.ma.tehro.data.BilingualName
import com.ma.tehro.data.ScheduleType
import com.ma.tehro.data.repo.GroupedScheduleInfo
import com.ma.tehro.data.repo.TrainScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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
    val selectedScheduleTypes: Map<BilingualName, ScheduleType?> = emptyMap(),
    val currentTimeAsDouble: Double = 0.0,
    val currentDayType: ScheduleType? = null,
    val isLoading: Boolean = true,
    val processedSchedules: Map<BilingualName, List<ScheduleSection>> = emptyMap(),
)

class TrainScheduleViewModel(
    savedStateHandle: SavedStateHandle,
    private val scheduleRepository: TrainScheduleRepository,
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
        viewModelScope.launch(Dispatchers.Default) {
            val schedules = scheduleRepository.getScheduleByStation(stationName, lineNumber, isBranch)
            val currentDayType = TimeUtils.getScheduleTypeForCurrentDay(schedules.flatMap { it.schedules.keys })
            println(currentDayType)
            val currentTime = TimeUtils.getCurrentTimeAsDouble()

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

                groupInfo.destination to Pair(selectedType, sections)
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
                    currentTimeAsDouble = currentTime
                )
            }
        }
    }

    fun onScheduleTypeSelected(destination: BilingualName, scheduleType: ScheduleType?) {
        viewModelScope.launch(Dispatchers.Default) {
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

                _state.update { state ->
                    state.copy(
                        selectedScheduleTypes = state.selectedScheduleTypes + (destination to scheduleType),
                        processedSchedules = state.processedSchedules + (destination to newSections),
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun startTimeUpdates() {
        timeScope.launch {
            while (isActive) {
                _state.update { it.copy(currentTimeAsDouble = TimeUtils.getCurrentTimeAsDouble()) }
                delay(1000 - (Clock.System.now().toEpochMilliseconds() % 1000))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timeUpdateJob.cancel()
    }
}