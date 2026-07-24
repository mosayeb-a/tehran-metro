package com.ma.tehro.feature.schedule

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ma.tehro.common.TimeUtils
import com.ma.tehro.common.ui.TrainScheduleScreen
import com.ma.tehro.domain.common.BilingualName
import com.ma.tehro.domain.schedule.ScheduleGroup
import com.ma.tehro.domain.schedule.ScheduleType
import com.ma.tehro.domain.schedule.repository.ScheduleRepository
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
    val schedules: List<ScheduleGroup> = emptyList(),
    val selectedScheduleTypes: Map<BilingualName, ScheduleType?> = emptyMap(),
    val currentTimeAsDouble: Double = 0.0,
    val currentDayType: ScheduleType? = null,
    val isLoading: Boolean = true,
    val processedSchedules: Map<BilingualName, List<ScheduleSection>> = emptyMap(),
)

class TrainScheduleViewModel(
    private val scheduleRepository: ScheduleRepository,
    station: String,
    lineNumber: Int,
    isBranch: Boolean,
) : ViewModel() {
    private val _state = MutableStateFlow(TrainScheduleState())
    val state = _state.asStateFlow()

    private val timeUpdateJob = Job()
    private val timeScope = CoroutineScope(Dispatchers.Default + timeUpdateJob)

    init {
        loadSchedules(station, lineNumber, isBranch)

        timeScope.launch {
            while (isActive) {
                _state.update { it.copy(currentTimeAsDouble = TimeUtils.getCurrentTimeAsDouble()) }
                delay(1000 - (Clock.System.now().toEpochMilliseconds() % 1000))
            }
        }
    }

    private fun loadSchedules(stationName: String, lineNumber: Int, isBranch: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            val schedules = scheduleRepository.getByStation(stationName, lineNumber, isBranch)

            val currentDayType = TimeUtils.getScheduleTypeForCurrentDay(
                scheduleTypes = schedules.flatMap { it.schedules.keys }
            )
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

    override fun onCleared() {
        super.onCleared()
        timeUpdateJob.cancel()
    }
}