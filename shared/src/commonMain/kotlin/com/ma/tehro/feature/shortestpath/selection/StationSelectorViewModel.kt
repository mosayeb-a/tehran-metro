package com.ma.tehro.feature.shortestpath.selection

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.common.TimeUtils
import com.ma.tehro.common.ui.Action
import com.ma.tehro.common.ui.UiMessage
import com.ma.tehro.common.ui.UiMessageManager
import com.ma.tehro.data.place.Place
import com.ma.tehro.domain.common.BilingualName
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.common.NearbyStation
import com.ma.tehro.domain.path.repository.PathRepository
import com.ma.tehro.domain.place.FindNearbyStations
import com.ma.tehro.domain.place.GetPlacesByCategory
import com.ma.tehro.services.LocationTracker
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
    val isLoadingNearbyStations: Boolean = false,
    val isLoadingStationsByPlace: Boolean = false,
    val nearbyStations: List<NearbyStation> = emptyList(),
    val placeNearbyStations: List<NearbyStation> = emptyList(),
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
    private val locationTracker: LocationTracker,
    private val getPlacesByCategory: GetPlacesByCategory,
    private val findNearbyStations: FindNearbyStations
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

    init {
        viewModelScope.launch {
            _stations.update { pathRepository.getStations() }

            val placeGroups = getPlacesByCategory.getPlaces()
            _places.update { placeGroups.flatMap { it.places } }
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

    fun findNearbyStations(forceRefresh: Boolean = false, onError: () -> Unit) {
        viewModelScope.launch {
                if (!forceRefresh && _uiState.value.nearbyStations.isNotEmpty()) {
                return@launch
            }

            try {
                _uiState.update {
                    it.copy(
                        isLoadingNearbyStations = true,
                        nearbyStations = emptyList(),
                    )
                }
                val nearestStations = locationTracker.getNearestStationByCurrentLocation()
                if (nearestStations.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            nearbyStations = nearestStations,
                            isLoadingNearbyStations = false
                        )
                    }
                }
            } catch (_: CancellationException) {
            } catch (e: Exception) {
                onError()
                _uiState.update {
                    it.copy(
                        nearbyStations = emptyList(),
                        isLoadingNearbyStations = false
                    )
                }
                UiMessageManager.sendEvent(
                    event = UiMessage(
                        message = e.message ?: "مشکلی رخ داده",
                        action = Action(
                            name = "باشه",
                            action = {}
                        )
                    )
                )
            }
        }
    }

    fun findStationsNear(lat: Double, long: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingStationsByPlace = true) }
            val stations = findNearbyStations.getStations(
                placeLatitude = lat,
                placeLongitude = long
            )
            _uiState.update {
                it.copy(
                    isLoadingStationsByPlace = false,
                    placeNearbyStations = stations
                )
            }
        }
    }
}