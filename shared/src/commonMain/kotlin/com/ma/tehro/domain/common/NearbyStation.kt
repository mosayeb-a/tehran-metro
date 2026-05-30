package com.ma.tehro.domain.common

import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.domain.line.Station
import kotlin.math.round

data class NearbyStation(
    val station: Station,
    val distanceInMeters: Double,
) {
    val distanceNumber: String
        get() = if (distanceInMeters < 1000) {
            distanceInMeters.toInt().toFarsiNumber()
        } else {
            val km = round((distanceInMeters / 1000) * 10) / 10
            km.toString().toFarsiNumber()
        }

    val distanceUnit: String
        get() = if (distanceInMeters < 1000) "متر" else "کیلومتر"
}