package com.ma.tehro.feature.line

import androidx.lifecycle.ViewModel
import com.ma.tehro.data.Station
import com.ma.tehro.data.repo.LineRepository
import com.ma.tehro.feature.shortestpath.selection.StationSelectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class LineState(
    val lines: List<Int> = emptyList()
)

@HiltViewModel
class LineViewModel @Inject constructor(
    private val repository: LineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LineState())
    val uiState: StateFlow<LineState> get() = _uiState

    init {
        _uiState.update { it.copy(lines = repository.getLines) }
    }
}