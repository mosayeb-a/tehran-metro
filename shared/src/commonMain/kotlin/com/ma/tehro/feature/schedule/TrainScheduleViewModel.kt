package com.ma.tehro.feature.schedule

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.common.TimeUtils
import com.ma.tehro.domain.common.BilingualName
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
import kotlin.collections.mapValues
import kotlin.time.Clock

@Immutable
data class ScheduleSection(
    val type: ScheduleType,
    val times: List<Double>,
    val isCurrentDay: Boolean
)

@Immutable
data class TrainScheduleState(
    val schedules: Map<BilingualName, List<ScheduleSection>> = emptyMap(),
    val selectedTypes: Map<BilingualName, ScheduleType?> = emptyMap(),
    val currentTime: Double = 0.0,
    val isLoading: Boolean = true,
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
                _state.update { it.copy(currentTime = TimeUtils.getCurrentTimeAsDouble()) }
                delay(1000 - (Clock.System.now().toEpochMilliseconds() % 1000))
            }
        }
    }

    private fun loadSchedules(stationName: String, lineNumber: Int, isBranch: Boolean) {
        viewModelScope.launch {
            val rawSchedules = scheduleRepository.getByStation(stationName, lineNumber, isBranch)

            val currentDayType = TimeUtils.getScheduleTypeForCurrentDay(
                scheduleTypes = rawSchedules.flatMap { it.timetable.keys }
            )
            val currentTime = TimeUtils.getCurrentTimeAsDouble()

            val scheduleData = rawSchedules.associate { stationSchedule ->
                val sections = stationSchedule.timetable.map { (type, times) ->
                    ScheduleSection(
                        type = type,
                        times = times,
                        isCurrentDay = type == currentDayType || type == ScheduleType.ALL_DAY
                    )
                }

                val defaultSelectedType = stationSchedule.timetable.keys.run {
                    find { it == currentDayType }
                        ?: find { it == ScheduleType.ALL_DAY }
                        ?: firstOrNull()
                }

                stationSchedule.destination to Pair(sections, defaultSelectedType)
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    schedules = scheduleData.mapValues { it.value.first },
                    selectedTypes = scheduleData.mapValues { it.value.second },
                    currentTime = currentTime
                )
            }
        }
    }

    fun setScheduleType(destination: BilingualName, scheduleType: ScheduleType?) {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    selectedTypes = state.selectedTypes + (destination to scheduleType)
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timeUpdateJob.cancel()
    }
}