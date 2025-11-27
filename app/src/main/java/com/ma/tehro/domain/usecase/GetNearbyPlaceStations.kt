package com.ma.tehro.domain.usecase

import com.ma.tehro.common.calculateDistance
import com.ma.tehro.data.Station
import com.ma.tehro.domain.NearestStation


class GetNearbyPlaceStations(
    val stations: Map<String, Station>
) {
    fun getStations(
        placeLatitude: Double,
        placeLongitude: Double,
    ): List<NearestStation> {
        return stations.values
            .asSequence()
            .mapNotNull { station ->
                val lat = station.latitude?.toDoubleOrNull()
                val lon = station.longitude?.toDoubleOrNull()

                if (lat != null && lon != null) {
                    val distance = calculateDistance(placeLatitude, placeLongitude, lat, lon)
                    NearestStation(station = station, distanceInMeters = distance)
                } else {
                    null
                }
            }
            .sortedBy { it.distanceInMeters }
            .take(5)
            .toList()
    }

}