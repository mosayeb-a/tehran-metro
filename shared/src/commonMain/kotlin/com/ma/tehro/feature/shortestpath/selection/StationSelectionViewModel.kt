package com.ma.tehro.feature.shortestpath.selection

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.common.TimeUtils
import com.ma.tehro.common.ui.Action
import com.ma.tehro.common.ui.UiMessage
import com.ma.tehro.common.ui.UiMessageManager
import com.ma.tehro.domain.common.BilingualName
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.common.NearbyStation
import com.ma.tehro.domain.path.repository.PathRepository
import com.ma.tehro.services.LocationTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Immutable
data class StationSelectionState @OptIn(ExperimentalTime::class) constructor(
    val fromStation: BilingualName? = null,
    val toStation: BilingualName? = null,
    val isSearchingNearby: Boolean = false,
    val nearbyStations: List<NearbyStation> = emptyList(),
    val transferDelay: Int = 8,
    val dayOfWeek: Int = Clock.System.now()
        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        .dayOfWeek.isoDayNumber,
    val currentTime: Double = TimeUtils.getCurrentTimeAsDouble(),
    val nearestStation: NearbyStation? = null,
)

class StationSelectionViewModel(
    private val pathRepository: PathRepository,
    private val locationTracker: LocationTracker,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StationSelectionState())
    val uiState: StateFlow<StationSelectionState> = _uiState

    private val _stations = MutableStateFlow<Map<String, Station>>(emptyMap())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val filteredStations: StateFlow<List<Station>> = combine(
        flow = _stations,
        flow2 = _searchQuery
    ) { stationsMap, query ->
        val stationsList = stationsMap.values.toList()
        if (query.isBlank()) {
            stationsList
        } else {
            stationsList.filter { station ->
                station.name.contains(query, ignoreCase = true) ||
                        station.translations.fa.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            _stations.update { pathRepository.getStations() }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.update { query }
    }

    fun onSelectedChange(isFrom: Boolean, station: BilingualName) {
        _uiState.update { currentState ->
            if (isFrom) {
                currentState.copy(fromStation = station)
            } else {
                currentState.copy(toStation = station)
            }
        }
        _searchQuery.update { "" }
    }

    fun onNearestStationSelected(nearbyStation: NearbyStation) {
        _uiState.update { currentState ->
            currentState.copy(
                fromStation = BilingualName(
                    en = nearbyStation.station.name,
                    fa = nearbyStation.station.translations.fa
                ),
                nearestStation = nearbyStation
            )
        }
    }

    fun onLineChangeDelayChanged(minutes: Int) {
        _uiState.update { it.copy(transferDelay = minutes) }
    }

    fun onTimeChanged(time: Double) {
        _uiState.update { it.copy(currentTime = time) }
    }

    fun onDayOfWeekChanged(day: Int) {
        _uiState.update { it.copy(dayOfWeek = day) }
    }

    fun findNearestStation(forceRefresh: Boolean = false, onError: () -> Unit) {
        viewModelScope.launch {
            if (!forceRefresh && _uiState.value.nearbyStations.isNotEmpty()) {
                return@launch
            }

            try {
                _uiState.update {
                    it.copy(
                        isSearchingNearby = true,
                        nearbyStations = emptyList(),
                    )
                }
                val nearestStations = locationTracker.getNearestStationByCurrentLocation()
                if (nearestStations.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            nearbyStations = nearestStations,
                            isSearchingNearby = false
                        )
                    }
                }
            } catch (_: CancellationException) {
            } catch (e: Exception) {
                onError()
                _uiState.update {
                    it.copy(
                        nearbyStations = emptyList(),
                        isSearchingNearby = false
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
}