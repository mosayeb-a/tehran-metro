package com.ma.tehro.feature.shortestpath.selection.components

sealed interface StationSearchMode {
    data object Search : StationSearchMode
    data class Nearby(
        val source: NearbySource,
        val locationName: String
    ) : StationSearchMode
}

enum class NearbySource {
    CurrentLocation,
    Place
}