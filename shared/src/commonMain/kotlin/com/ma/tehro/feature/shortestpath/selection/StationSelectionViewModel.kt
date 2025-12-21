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
import kotlinx.coroutines.flow.StateFlow
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
    val stations: Map<String, Station> = emptyMap(),
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
    val uiState: StateFlow<StationSelectionState> get() = _uiState

    init {
        viewModelScope.launch {
            val result = pathRepository.getStations()
            _uiState.value = _uiState.value.copy(stations = result)
        }
    }

    fun onSelectedChange(isFrom: Boolean, enStation: String, faStation: String) {
        _uiState.update {
            it.copy(
                selectedEnStartStation = if (isFrom) enStation else _uiState.value.selectedEnStartStation,
                selectedFaStartStation = if (isFrom) faStation else _uiState.value.selectedFaStartStation,
                selectedEnDestStation = if (!isFrom) enStation else _uiState.value.selectedEnDestStation,
                selectedFaDestStation = if (!isFrom) faStation else _uiState.value.selectedFaDestStation,
            )
        }
    }

    fun onNearestStationSelected(nearestStation: NearestStation) {
        _uiState.update {
            it.copy(
                selectedEnStartStation = nearestStation.station.name,
                selectedFaStartStation = nearestStation.station.translations.fa,
                selectedNearestStation = nearestStation
            )
        }
    }

    fun onLineChangeDelayChanged(minutes: Int) {
        _uiState.value = _uiState.value.copy(lineChangeDelayMinutes = minutes)
    }

    fun onTimeChanged(time: Double) {
        _uiState.value = _uiState.value.copy(currentTime = time)
    }

    fun onDayOfWeekChanged(day: Int) {
        _uiState.value = _uiState.value.copy(dayOfWeek = day)
    }

    fun findNearestStation(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!forceRefresh && _uiState.value.nearestStations.isNotEmpty()) {
                // for now just do nothing: it should handle try again functionality
                return@launch
            }

            try {
                _uiState.value = _uiState.value.copy(
                    findNearestLocationProgress = true,
                    nearestStations = emptyList(),
                )
                val nearestStations = locationTracker.getNearestStationByCurrentLocation()
                if (nearestStations.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        nearestStations = nearestStations,
                        findNearestLocationProgress = false
                    )
                }
            } catch (_: CancellationException) {
                // do not expose this
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    nearestStations = emptyList(),
                    findNearestLocationProgress = false
                )
                UiMessageManager.sendEvent(
                    event = UiMessage(
                        message = e.message ?: "Location error",
                        action = Action(
                            name = "تلاش مجدد\nRetry",
                            action = { findNearestStation() })
                    )
                )
            }
        }
    }
}