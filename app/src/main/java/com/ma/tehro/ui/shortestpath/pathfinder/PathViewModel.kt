package com.ma.tehro.ui.shortestpath.pathfinder

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.data.repo.PathItem
import com.ma.tehro.data.repo.PathRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class PathFinderState(
    val shortestPath: List<PathItem> = emptyList()
)

@HiltViewModel
class PathViewModel @Inject constructor(
    private val repository: PathRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow(PathFinderState())
    val state: StateFlow<PathFinderState> get() = _state

    init {
        val from = savedStateHandle.get<String>("startEnStation")!!
        val to  = savedStateHandle.get<String>("enDestination")!!

        viewModelScope.launch {
            val result = repository.findShortestPathWithDirection(from = from, to = to)
            _state.update { it.copy(shortestPath = result) }
        }
    }
}


