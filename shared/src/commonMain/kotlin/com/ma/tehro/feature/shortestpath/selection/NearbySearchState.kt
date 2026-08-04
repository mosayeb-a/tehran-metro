package com.ma.tehro.feature.shortestpath.selection

import androidx.compose.runtime.Immutable
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.path.Nearby
import com.ma.tehro.domain.path.Place

sealed interface NearbySource {
    data object CurrentLocation : NearbySource
    data class Place(val place: com.ma.tehro.domain.path.Place) : NearbySource
}

enum class NearbyType {
    Stations,
    Places
}

enum class NearbyError {
    PermissionDenied,
    LocationDisabled,
    Timeout,
    Unknown
}

@Immutable
data class NearbySearchState(
    val source: NearbySource? = null,
    val type: NearbyType = NearbyType.Stations,
    val isLoading: Boolean = false,
    val stations: List<Nearby<Station>> = emptyList(),
    val places: List<Nearby<Place>> = emptyList(),
    val error: NearbyError? = null,
)