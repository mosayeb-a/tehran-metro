package com.ma.tehro.services

import com.ma.tehro.common.calculateDistance
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.common.NearbyStation


interface LocationTracker {
    suspend fun getNearestStationByCurrentLocation(): List<NearbyStation>
}

class LocationTrackerImpl(
    private val locationClient: LocationClient,
    private val stations: Map<String, Station>
) : LocationTracker {

    override suspend fun getNearestStationByCurrentLocation(): List<NearbyStation> {
        val location = locationClient.getCurrentLocation()
        return findNearestStations(location)
    }

    private fun findNearestStations(location: PlatformLocation): List<NearbyStation> {
        val validStations = stations.values.filter { it.latitude != null && it.longitude != null }
        return validStations
            .map { station ->
                val distance = calculateDistance(
                    location.latitude,
                    location.longitude,
                    station.latitude!!.toDouble(),
                    station.longitude!!.toDouble()
                )
                NearbyStation(station, distance)
            }
            .sortedBy { it.distanceInMeters }
            .take(3)
    }
}
