package com.ma.tehro.services

import android.location.Location
import com.ma.tehro.common.calculateDistance
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.data.Station
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

data class NearestStation(
    val station: Station,
    val distanceInMeters: Double,
) {
    val distanceTextFa: String
        get() = when {
            distanceInMeters < 1000 -> "${distanceInMeters.toFarsiNumber()} متر"
            else -> "${"%.1f".format(distanceInMeters / 1000).toFarsiNumber()} کیلومتر"
        }

    val distanceTextEn: String
        get() = when {
            distanceInMeters < 1000 -> "${distanceInMeters.toInt()} m"
            else -> "%.1f km".format(distanceInMeters / 1000)
        }
}
