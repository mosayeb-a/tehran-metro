package com.ma.tehro.services

expect class PlatformLocation {
    val latitude: Double
    val longitude: Double
    val accuracy: Float?
    val time: Long?
}