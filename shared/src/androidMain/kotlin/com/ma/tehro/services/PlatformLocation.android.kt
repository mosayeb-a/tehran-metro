package com.ma.tehro.services

import android.location.Location

actual class PlatformLocation(
    location: Location
) {
    actual val latitude: Double = location.latitude
    actual val longitude: Double = location.longitude
    actual val accuracy: Float? = location.accuracy.takeIf { it > 0 }
    actual val time: Long? = location.time
}

fun Location.toPlatformLocation(): PlatformLocation = PlatformLocation(this)