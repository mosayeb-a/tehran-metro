package com.ma.tehro.domain.path

import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.domain.common.GeoPoint
import kotlin.math.round

data class Nearby<T : GeoPoint>(
    val item: T,
    val distanceInMeters: Double
) {
    val latitude: Double get() = item.latitude
    val longitude: Double get() = item.longitude

    val distanceNumber: String
        get() = when {
            distanceInMeters < 1000 -> distanceInMeters.toInt().toFarsiNumber()
            distanceInMeters < 10000 -> {
                val km = round(distanceInMeters / 100) / 10
                km.toString().toFarsiNumber()
            }
            else -> {
                val km = round(distanceInMeters / 1000)
                km.toInt().toFarsiNumber()
            }
        }

    val distanceUnit: String
        get() = if (distanceInMeters < 1000) "متر" else "کیلومتر"
}