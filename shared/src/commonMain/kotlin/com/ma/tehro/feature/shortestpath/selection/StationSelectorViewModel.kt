package com.ma.tehro.feature.shortestpath.selection

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.common.TimeUtils
import com.ma.tehro.domain.common.BilingualName
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.path.NearbyFinder
import com.ma.tehro.domain.path.Place
import com.ma.tehro.domain.path.repository.PathRepository
import com.ma.tehro.domain.place.repository.PlacesRepository
import com.ma.tehro.services.LocationClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock

@Immutable
data class StationSelectorState(
    val fromStation: BilingualName? = null,
    val toStation: BilingualName? = null,
    val nearby: NearbySearchState = NearbySearchState(),
    val transferDelay: Int = 8,
    val dayOfWeek: Int = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .dayOfWeek.isoDayNumber,
    val departureTime: Double = TimeUtils.getCurrentTimeAsDouble(),
)

@Immutable
data class SearchResult(
    val stations: List<Station> = emptyList(),
    val places: List<Place> = emptyList()
)

class StationSelectorViewModel(
    private val pathRepository: PathRepository,
    private val locationClient: LocationClient,
    private val placeRepository: PlacesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StationSelectorState())
    val uiState: StateFlow<StationSelectorState> = _uiState

    private val _stations = MutableStateFlow<Map<String, Station>>(emptyMap())
    private val _places = MutableStateFlow<List<Place>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val searchResults: StateFlow<SearchResult> = combine(
        _stations,
        _places,
        _searchQuery
    ) { stationsMap, places, query ->
        val stations = stationsMap.values.toList()
        if (query.isBlank()) {
            SearchResult(
                stations = stations,
                places = emptyList()
            )
        } else {
            SearchResult(
                stations = stations.filter { station ->
                    station.name.contains(query, ignoreCase = true) ||
                            station.translations.fa.contains(query, ignoreCase = true)
                },
                places = places.filter { place ->
                    place.name.contains(query, ignoreCase = true)
                }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchResult()
    )

    private val stationFinder: NearbyFinder<Station> by lazy {
        NearbyFinder(_stations.value.values.toList())
    }

    private val placeFinder: NearbyFinder<Place> by lazy {
        NearbyFinder(_places.value)
    }

    init {
        viewModelScope.launch {
            _stations.update { pathRepository.getStations() }

            _places.update { placeRepository.getAll }
        }
    }

    fun setSearchQuery(query: String) = _searchQuery.update { query }

    fun setFromStation(station: BilingualName) {
        _uiState.update { it.copy(fromStation = station) }
        _searchQuery.update { "" }
    }

    fun setToStation(station: BilingualName) {
        _uiState.update { it.copy(toStation = station) }
        _searchQuery.update { "" }
    }

    fun setTransferDelay(minutes: Int) = _uiState.update { it.copy(transferDelay = minutes) }

    fun setDepartureTime(time: Double) = _uiState.update { it.copy(departureTime = time) }

    fun setDayOfWeek(day: Int) = _uiState.update { it.copy(dayOfWeek = day) }

    fun searchNearby(
        request: NearbySource,
        content: NearbyType,
        forceRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            val currentNearby = _uiState.value.nearby

            if (
                !forceRefresh &&
                currentNearby.source == request &&
                currentNearby.type == content &&
                when (content) {
                    NearbyType.Stations -> currentNearby.stations.isNotEmpty()
                    NearbyType.Places -> currentNearby.places.isNotEmpty()
                }
            ) {
                return@launch
            }

            _uiState.update {
                it.copy(
                    nearby = NearbySearchState(
                        isLoading = true,
                        source = request,
                        type = content
                    )
                )
            }

            try {
                val (latitude, longitude) = when (request) {
                    is NearbySource.CurrentLocation -> {
                        val location = locationClient.getCurrentLocation()
                        location.latitude to location.longitude
                    }

                    is NearbySource.Place -> {
                        request.place.latitude to request.place.longitude
                    }
                }

                when (content) {
                    NearbyType.Stations -> {
                        val stations = stationFinder.find(latitude, longitude)
                        _uiState.update {
                            it.copy(
                                nearby = NearbySearchState(
                                    source = request,
                                    type = content,
                                    stations = stations,
                                    places = it.nearby.places
                                )
                            )
                        }
                    }

                    NearbyType.Places -> {
                        val places = placeFinder.find(latitude, longitude)
                        _uiState.update {
                            it.copy(
                                nearby = NearbySearchState(
                                    source = request,
                                    type = content,
                                    places = places,
                                    stations = it.nearby.stations
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                val error = when (e) {
                    else -> NearbyError.Unknown
                }

                _uiState.update {
                    it.copy(
                        nearby = NearbySearchState(
                            source = request,
                            type = content,
                            error = error
                        )
                    )
                }
            }
        }
    }
}