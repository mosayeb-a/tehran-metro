package com.ma.tehro.feature.shortestpath.pathfinder

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ma.tehro.common.ui.PathFinderScreen
import com.ma.tehro.common.fractionToTime
import com.ma.tehro.data.BilingualName
import com.ma.tehro.data.repo.PathItem
import com.ma.tehro.data.repo.PathRepository
import com.ma.tehro.domain.usecase.PathTimeCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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


    fun generateGuidSteps(): List<String> {
        val pathDescription = mutableListOf<String>()
        var firstStation: PathItem.StationItem? = null
        var lastStation: PathItem.StationItem? = null
        var isFirstSegment = true

        _state.value.shortestPath.forEach { item ->
            when (item) {
                is PathItem.Title -> {
                    lastStation?.let { pathDescription.add("l: ${it.station.translations.fa}") }
                    firstStation = null
                    lastStation = null
                    pathDescription.add("t: ${item.fa}")
                }

                is PathItem.StationItem -> {
                    if (firstStation == null) {
                        firstStation = item
                        if (isFirstSegment) {
                            pathDescription.add("f: ${item.station.translations.fa}")
                            isFirstSegment = false
                        }
                    }
                    lastStation = item
                }
            }
        }

        lastStation?.let { pathDescription.add("l: ${it.station.translations.fa}") }

        return pathDescription
    }
}