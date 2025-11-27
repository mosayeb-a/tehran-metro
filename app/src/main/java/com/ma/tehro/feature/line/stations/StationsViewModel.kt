package com.ma.tehro.feature.line.stations

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.ma.tehro.common.ui.StationsScreen
import com.ma.tehro.data.Station
import com.ma.tehro.domain.repo.LineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class StationsState(
    val stations: List<Station> = emptyList()
)

class StationsViewModel(
    private val lineRepository: LineRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(StationsState())
    val uiState: StateFlow<StationsState> get() = _uiState

    init {
        val args = savedStateHandle.toRoute<StationsScreen>()
        _uiState.update {
            it.copy(
                stations = lineRepository.getOrderedStationsByLine(
                    line = args.lineNumber,
                    useBranch = args.useBranch
                )
            )
        }
    }
}