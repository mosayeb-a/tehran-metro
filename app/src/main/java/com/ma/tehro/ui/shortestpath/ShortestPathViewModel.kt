package com.ma.tehro.ui.shortestpath

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.data.Station
import com.ma.tehro.ui.detail.repo.PathItem
import com.ma.tehro.ui.detail.repo.PathRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class PathUiState(
    val isLoading: Boolean = false,
    val selectedEnStartStation: String = "",
    val selectedFaStartStation: String = "",
    val selectedEnDestStation: String = "",
    val selectedFaDestStation: String = "",
    val stations: Map<String, Station> = emptyMap(),
)

@HiltViewModel
class ShortestPathViewModel @Inject constructor(
    private val repository: PathRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PathUiState())
    val uiState: StateFlow<PathUiState> get() = _uiState

    init {
        viewModelScope.launch {
          val result =  repository.getStations()
            _uiState.value = _uiState.value.copy(stations = result)
        }
    }

    fun onSelectedChange(isFrom: Boolean, enStation: String, faStation: String) {
        _uiState.value = _uiState.value.copy(
            selectedEnStartStation = if (isFrom) enStation else _uiState.value.selectedEnStartStation,
            selectedFaStartStation = if (isFrom) faStation else _uiState.value.selectedFaStartStation,
            selectedEnDestStation = if (!isFrom) enStation else _uiState.value.selectedEnDestStation,
            selectedFaDestStation = if (!isFrom) faStation else _uiState.value.selectedFaDestStation
        )
    }

    fun findShortestPathWithDirection(from: String, to: String): List<PathItem> =
        repository.findShortestPathWithDirection(from = from, to = to)
}

