package com.ma.tehro.domain

import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.data.Station
import kotlin.math.round

data class NearestStation(
    val station: Station,
    val distanceInMeters: Double,
) {
    val distanceNumberFa: String
        get() = if (distanceInMeters < 1000) {
            distanceInMeters.toInt().toFarsiNumber()
        } else {
            val km = round((distanceInMeters / 1000) * 10) / 10
            km.toString().toFarsiNumber()
        }

    val distanceUnitFa: String
        get() = if (distanceInMeters < 1000) "متر" else "کیلومتر"
}