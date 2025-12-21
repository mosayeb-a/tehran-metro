package com.ma.tehro.feature.shortestpath.pathfinder

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ma.tehro.common.ui.PathFinderScreen
import com.ma.tehro.data.BilingualName
import com.ma.tehro.domain.PathItem
import com.ma.tehro.domain.Step
import com.ma.tehro.domain.repo.PathRepository
import com.ma.tehro.domain.usecase.PathTimeCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable
data class PathFinderState(
    val shortestPath: List<PathItem> = emptyList(),
    val estimatedTime: BilingualName? = null,
    val stationTimes: Map<String, String> = emptyMap()
)

class PathViewModel(
    private val pathRepository: PathRepository,
    private val pathTimeCalculator: PathTimeCalculator,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow(PathFinderState())
    val state: StateFlow<PathFinderState> get() = _state

    init {
        val args = savedStateHandle.toRoute<PathFinderScreen>()
        viewModelScope.launch {
            val path = pathRepository
                .findShortestPathWithDirection(args.startEnStation, args.enDestination)
            _state.update { it.copy(shortestPath = path) }

            val (stationTimes, estimate) = pathTimeCalculator
                .calculateStationTimes(
                    path = path,
                    lineChangeDelayMinutes = args.lineChangeDelayMinutes,
                    dayOfWeek = args.dayOfWeek,
                    currentTime = args.currentTime
                )
            _state.update {
                it.copy(
                    stationTimes = stationTimes,
                    estimatedTime = estimate
                )
            }
        }
    }


    fun generateGuidSteps(): List<Step> {
        val steps = mutableListOf<Step>()
        var firstStation: PathItem.StationItem? = null
        var lastStation: PathItem.StationItem? = null
        var currentLineTitle: String? = null
        var isFirstSegment = true

        _state.value.shortestPath.forEach { item ->
            when (item) {
                is PathItem.Title -> {
                    lastStation?.let {
                        steps.add(
                            Step.ChangeLine(
                                stationName = it.station.translations.fa,
                                newLineTitle = item.fa
                            )
                        )
                    }
                    firstStation = null
                    lastStation = null
                    currentLineTitle = item.fa
                }

                is PathItem.StationItem -> {
                    if (firstStation == null) {
                        firstStation = item
                        if (isFirstSegment) {
                            steps.add(
                                Step.FirstStation(
                                    stationName = item.station.translations.fa,
                                    lineTitle = currentLineTitle ?: ""
                                )
                            )
                            isFirstSegment = false
                        }
                    }
                    lastStation = item
                }
            }
        }

        lastStation?.let {
            steps.add(Step.LastStation(stationName = it.station.translations.fa))
        }
        steps.add(Step.Destination)
        return steps
    }
}