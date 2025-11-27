package com.ma.tehro.feature.shortestpath.places

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.common.normalizeWords
import com.ma.tehro.domain.CategorizedPlaces
import com.ma.tehro.domain.NearestStation
import com.ma.tehro.domain.usecase.GetNearbyPlaceStations
import com.ma.tehro.domain.usecase.ShowPlacesByCategory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class PlaceSelectionState(
    val isLoading: Boolean = true,
    val places: List<CategorizedPlaces> = emptyList(),
    val nearbyStations: List<NearestStation> = emptyList(),
    val searchQuery: String = ""
)

@OptIn(FlowPreview::class)
class PlaceSelectionViewModel(
    private val showPlacesByCategory: ShowPlacesByCategory,
    private val getNearbyPlaceStations: GetNearbyPlaceStations
) : ViewModel() {

    private val _state = MutableStateFlow(PlaceSelectionState())
    val state: StateFlow<PlaceSelectionState> get() = _state

    private val searchQueryFlow = MutableStateFlow("")
    private var originalPlaces: List<CategorizedPlaces> = emptyList()

    init {
        viewModelScope.launch {
            val result = showPlacesByCategory.getPlacesByCategory()
            originalPlaces = result
            _state.update { it.copy(isLoading = false, places = result) }
        }

        searchQueryFlow
            .debounce(300.milliseconds)
            .distinctUntilChanged()
            .onEach { query ->
                search(query)
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        searchQueryFlow.value = query.trim()
        _state.update { it.copy(searchQuery = query) }
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val queryWords = normalizeWords(query)

            val filtered = if (queryWords.isEmpty()) {
                originalPlaces
            } else {
                originalPlaces.map { category ->
                    val filteredPlaces = category.places.filter { place ->
                        val targetWords = normalizeWords(place.name)

                        queryWords.all { queryWord ->
                            targetWords.any { targetWord ->
                                targetWord.contains(queryWord)
                            }
                        }
                    }
                    category.copy(places = filteredPlaces)
                }.filter { it.places.isNotEmpty() }
            }

            _state.update { it.copy(isLoading = false, places = filtered) }
        }
    }

    fun getNearbyStations(lat: Double, long: Double) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val stations = getNearbyPlaceStations.getStations(
                placeLatitude = lat,
                placeLongitude = long
            )
            _state.update { it.copy(isLoading = false, nearbyStations = stations) }
        }
    }
}