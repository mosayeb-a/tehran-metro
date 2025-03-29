package com.ma.tehro.feature.shortestpath.selection

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.common.createBilingualMessage
import com.ma.tehro.common.messenger.Action
import com.ma.tehro.common.messenger.UiMessage
import com.ma.tehro.common.messenger.UiMessageManager
import com.ma.tehro.data.Station
import com.ma.tehro.data.repo.PathRepository
import com.ma.tehro.services.LocationTracker
import com.ma.tehro.services.NearestStation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@Immutable
data class StationSelectionState(
    val selectedEnStartStation: String = "Tajrish",
    val selectedFaStartStation: String = "",
    val selectedEnDestStation: String = "Shahr-e Parand",
    val selectedFaDestStation: String = "",
    val stations: Map<String, Station> = emptyMap(),
    val findNearestLocationProgress: Boolean = false,
    val nearestStations: List<NearestStation> = emptyList(),
)

@HiltViewModel
class StationSelectionViewModel @Inject constructor(
    private val repository: PathRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(StationSelectionState())
    val uiState: StateFlow<StationSelectionState> get() = _uiState

    init {
        viewModelScope.launch {
            val result = repository.getStations()
            _uiState.value = _uiState.value.copy(stations = result)
        }
    }

    fun onSelectedChange(isFrom: Boolean, enStation: String, faStation: String) {
        _uiState.value = _uiState.value.copy(
            selectedEnStartStation = if (isFrom) enStation else _uiState.value.selectedEnStartStation,
            selectedFaStartStation = if (isFrom) faStation else _uiState.value.selectedFaStartStation,
            selectedEnDestStation = if (!isFrom) enStation else _uiState.value.selectedEnDestStation,
            selectedFaDestStation = if (!isFrom) faStation else _uiState.value.selectedFaDestStation
        )
    }


    fun findNearestStation(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!forceRefresh && _uiState.value.nearestStations.isNotEmpty()) {
                val message = createBilingualMessage(
                    fa = "محاسبه دوباره نزدیک‌ترین ایستگاه؟",
                    en = "Recalculate nearest station?"
                )

                val actionLabel = createBilingualMessage(
                    fa = "محاسبه دوباره",
                    en = "Recalculate"
                )

                UiMessageManager.sendEvent(
                    UiMessage(
                        message = message,
                        action = Action(
                            name = actionLabel,
                            action = { findNearestStation(forceRefresh = true) }
                        )
                    )
                )
                return@launch
            }

            try {
                _uiState.value = _uiState.value.copy(
                    findNearestLocationProgress = true,
                    nearestStations = emptyList(),
                )
                val nearestStations = locationTracker.getNearestStationByCurrentLocation()
                if (nearestStations.isNotEmpty()) {
                    val closestStation = nearestStations.first()
                    _uiState.value = _uiState.value.copy(
                        nearestStations = nearestStations,
                        selectedEnStartStation = closestStation.station.name,
                        selectedFaStartStation = closestStation.station.translations.fa,
                        findNearestLocationProgress = false
                    )
                }
            } catch (e: CancellationException) {
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