package com.ma.tehro.services

import com.ma.tehro.common.Position

actual class PlatformLocation() {
    private var _latitude: Double = 0.0
    private var _longitude: Double = 0.0
    private var _accuracy: Float? = null
    private var _time: Long? = null

    actual val latitude: Double get() = _latitude
    actual val longitude: Double get() = _longitude
    actual val accuracy: Float? get() = _accuracy
    actual val time: Long? get() = _time

    constructor(
        latitude: Double,
        longitude: Double,
        accuracy: Float?,
        time: Long?
    ) : this() {
        _latitude = latitude
        _longitude = longitude
        _accuracy = accuracy
        _time = time
    }
}

fun Position.toPlatformLocation(): PlatformLocation {
    val c = coords
    return PlatformLocation(
       latitude =  c.latitude,
       longitude =  c.longitude,
        accuracy = c.accuracy.toFloat(),
        time = timestamp.toLong()
    )
}