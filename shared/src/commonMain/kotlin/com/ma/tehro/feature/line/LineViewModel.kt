package com.ma.tehro.feature.line

import androidx.lifecycle.ViewModel
import com.ma.tehro.domain.repo.LineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class LineState(
    val lines: List<Int> = emptyList()
)

class LineViewModel(
    private val lineRepository: LineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LineState())
    val uiState: StateFlow<LineState> get() = _uiState

    init {
        _uiState.update { it.copy(lines = lineRepository.getLines) }
    }
}