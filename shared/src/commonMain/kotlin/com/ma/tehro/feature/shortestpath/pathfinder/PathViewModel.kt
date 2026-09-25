package com.ma.tehro.feature.shortestpath.pathfinder

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.domain.common.BilingualName
import com.ma.tehro.domain.path.PathStep
import com.ma.tehro.domain.path.repository.PathRepository
import com.ma.tehro.domain.path.PathTimeCalculator
import com.ma.tehro.domain.path.StationTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable
data class PathFinderState(
    val shortestPath: List<PathStep> = emptyList(),
    val totalTravelTime: BilingualName? = null,
    val arrivals: List<StationTime> = emptyList(),
    val warningMessage: String? = null,
)

class PathViewModel(
    private val from: BilingualName,
    private val to: BilingualName,
    private val dayOfWeek: Int,
    private val departureTime: Double,
    private val transferDelayMinutes: Int,
    private val pathRepository: PathRepository,
    private val pathTimeCalculator: PathTimeCalculator,
) : ViewModel() {

    val state: StateFlow<PathFinderState>
        field = MutableStateFlow(PathFinderState())

    init {
        viewModelScope.launch {
            val path = pathRepository
                .findShortestPath(from.en, to.en)
            state.update { it.copy(shortestPath = path) }

            val result = pathTimeCalculator.calculate(
                path = path,
                transferDelay = transferDelayMinutes,
                dayOfWeek = dayOfWeek,
                currentTime = departureTime
            )
            state.update {
                it.copy(
                    arrivals = result.stationTimes,
                    totalTravelTime = result.estimatedTime,
                    warningMessage = result.warning
                )
            }
        }
    }
}