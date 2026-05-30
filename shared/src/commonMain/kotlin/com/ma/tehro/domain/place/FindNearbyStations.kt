package com.ma.tehro.domain.place

import com.ma.tehro.common.calculateDistance
import com.ma.tehro.domain.common.NearbyStation
import com.ma.tehro.domain.line.Station

class FindNearbyStations(
    val stations: Map<String, Station>
) {
    fun getStations(
        placeLatitude: Double,
        placeLongitude: Double,
    ): List<NearbyStation> {
        return stations.values
            .asSequence()
            .mapNotNull { station ->
                val lat = station.latitude?.toDoubleOrNull()
                val lon = station.longitude?.toDoubleOrNull()

                if (lat != null && lon != null) {
                    val distance = calculateDistance(placeLatitude, placeLongitude, lat, lon)
                    NearbyStation(station = station, distanceInMeters = distance)
                } else {
                    null
                }
            }
            .sortedBy { it.distanceInMeters }
            .take(5)
            .toList()
    }
}