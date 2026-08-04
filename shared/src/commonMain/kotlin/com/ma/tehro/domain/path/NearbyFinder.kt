package com.ma.tehro.domain.path

import com.ma.tehro.common.calculateDistance
import com.ma.tehro.domain.common.GeoPoint

class NearbyFinder<T : GeoPoint>(
    private val items: List<T>
) {
    fun find(
        latitude: Double,
        longitude: Double,
        limit: Int = 3
    ): List<Nearby<T>> {
        return items
            .asSequence()
            .map { item ->
                Nearby(
                    item = item,
                    distanceInMeters = calculateDistance(
                        latitude,
                        longitude,
                        item.latitude,
                        item.longitude
                    )
                )
            }
            .sortedBy { it.distanceInMeters }
            .take(limit)
            .toList()
    }
}