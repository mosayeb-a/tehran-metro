package com.ma.tehro.feature.shortestpath.selection

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.common.TimeUtils
import com.ma.tehro.common.ui.Action
import com.ma.tehro.common.ui.UiMessage
import com.ma.tehro.common.ui.UiMessageManager
import com.ma.tehro.data.Station
import com.ma.tehro.domain.NearestStation
import com.ma.tehro.domain.repo.PathRepository
import com.ma.tehro.services.LocationTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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
    val selectedEnStartStation: String = "",
    val selectedFaStartStation: String = "",
    val selectedEnDestStation: String = "",
    val selectedFaDestStation: String = "",
    val findNearestLocationProgress: Boolean = false,
    val nearestStations: List<NearestStation> = emptyList(),
    val lineChangeDelayMinutes: Int = 8,
    val dayOfWeek: Int = Clock.System.now()
        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        .dayOfWeek.isoDayNumber,
    val currentTime: Double = TimeUtils.getCurrentTimeAsDouble(),
    val selectedNearestStation: NearestStation? = null,
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

    fun onSelectedChange(isFrom: Boolean, enStation: String, faStation: String) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedEnStartStation = if (isFrom) enStation else currentState.selectedEnStartStation,
                selectedFaStartStation = if (isFrom) faStation else currentState.selectedFaStartStation,
                selectedEnDestStation = if (!isFrom) enStation else currentState.selectedEnDestStation,
                selectedFaDestStation = if (!isFrom) faStation else currentState.selectedFaDestStation,
            )
        }
        _searchQuery.update { "" }
    }

    fun onNearestStationSelected(nearestStation: NearestStation) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedEnStartStation = nearestStation.station.name,
                selectedFaStartStation = nearestStation.station.translations.fa,
                selectedNearestStation = nearestStation
            )
        }
    }

    fun onLineChangeDelayChanged(minutes: Int) {
        _uiState.update { it.copy(lineChangeDelayMinutes = minutes) }
    }

    fun onTimeChanged(time: Double) {
        _uiState.update { it.copy(currentTime = time) }
    }

    fun onDayOfWeekChanged(day: Int) {
        _uiState.update { it.copy(dayOfWeek = day) }
    }

    fun findNearestStation(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!forceRefresh && _uiState.value.nearestStations.isNotEmpty()) {
                return@launch
            }

            try {
                _uiState.update {
                    it.copy(
                        findNearestLocationProgress = true,
                        nearestStations = emptyList(),
                    )
                }
                val nearestStations = locationTracker.getNearestStationByCurrentLocation()
                if (nearestStations.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            nearestStations = nearestStations,
                            findNearestLocationProgress = false
                        )
                    }
                }
            }
            catch (_: CancellationException) {
            }
            catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        nearestStations = emptyList(),
                        findNearestLocationProgress = false
                    )
                }
                UiMessageManager.sendEvent(
                    event = UiMessage(
                        message = e.message ?: "مشکلی رخ داده",
                        action = Action(
                            name = "تلاش مجدد",
                            action = { findNearestStation() })
                    )
                )
            }
        }
    }
}