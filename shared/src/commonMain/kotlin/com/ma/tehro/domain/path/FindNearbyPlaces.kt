package com.ma.tehro.domain.path

import com.ma.tehro.common.calculateDistance

class FindNearbyPlaces(
    private val places: List<Place>
) {
    fun getPlaces(
        latitude: Double,
        longitude: Double,
        limit: Int = 5
    ): List<Place> {
        return places
            .asSequence()
            .map { place ->
                val distance = calculateDistance(
                    latitude,
                    longitude,
                    place.latitude,
                    place.longitude
                )
                place to distance
            }
            .sortedBy { it.second }
            .take(limit)
            .map { it.first }
            .toList()
    }
}