package com.ma.tehro.domain

import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.data.Station

data class NearestStation(
    val station: Station,
    val distanceInMeters: Double,
) {
    val distanceNumberFa: String
        get() = if (distanceInMeters < 1000) {
            distanceInMeters.toInt().toFarsiNumber()
        } else {
            "%.1f".format(distanceInMeters / 1000).toFarsiNumber()
        }

    val distanceUnitFa: String
        get() = if (distanceInMeters < 1000) "متر" else "کیلومتر"
}