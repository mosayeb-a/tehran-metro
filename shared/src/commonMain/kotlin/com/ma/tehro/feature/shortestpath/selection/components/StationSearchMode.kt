package com.ma.tehro.feature.shortestpath.selection.components

import com.ma.tehro.feature.shortestpath.selection.NearbyType
import com.ma.tehro.feature.shortestpath.selection.NearbySource

sealed interface StationSearchMode {
    data object Search : StationSearchMode
    data class Nearby(
        val source: NearbySource,
        val type: NearbyType,
        val locationName: String
    ) : StationSearchMode
}