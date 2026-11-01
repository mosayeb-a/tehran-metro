package com.ma.tehro.feature.search

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.path.repository.PathRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class StationSearchState(
    val query: String = "",
    val results: List<Station> = emptyList(),
)

class StationSearchViewModel(
    private val pathRepository: PathRepository
) : ViewModel() {

    private val _stations = MutableStateFlow<Map<String, Station>>(emptyMap())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val uiState: StateFlow<StationSearchState> = combine(
        _stations,
        _query
    ) { stationsMap, q ->
        val all = stationsMap.values.toList()
        if (q.isBlank()) {
            StationSearchState(query = q, results = emptyList())
        } else {
            StationSearchState(
                query = q,
                results = all.filter { station ->
                    station.name.contains(q, ignoreCase = true) ||
                        station.translations.fa.contains(q, ignoreCase = true)
                }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StationSearchState()
    )

    init {
        viewModelScope.launch {
            _stations.update { pathRepository.getStations() }
        }
    }

    fun setQuery(q: String) = _query.update { q }
}
