package com.ma.tehro.services

import android.location.Location
import com.ma.tehro.common.calculateDistance
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.data.Station
import com.ma.tehro.domain.NearestStation
import javax.inject.Inject


interface LocationTracker {
    suspend fun getNearestStationByCurrentLocation(): List<NearestStation>
}

class LocationTrackerImpl @Inject constructor(
    private val locationClient: LocationClient,
    private val stations: Map<String, Station>
) : LocationTracker {

    override suspend fun getNearestStationByCurrentLocation(): List<NearestStation> {
        val location = locationClient.getCurrentLocation()
        return findNearestStations(location)
    }

    private fun findNearestStations(location: Location): List<NearestStation> {
        val validStations = stations.values.filter { it.latitude != null && it.longitude != null }
        return validStations
            .map { station ->
                val distance = calculateDistance(
                    location.latitude,
                    location.longitude,
                    station.latitude!!.toDouble(),
                    station.longitude!!.toDouble()
                )
                NearestStation(station, distance)
            }
            .sortedBy { it.distanceInMeters }
            .take(3)
    }
}
