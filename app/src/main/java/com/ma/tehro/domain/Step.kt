package com.ma.tehro.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class Step {
    @Serializable
    data class FirstStation(val stationName: String, val lineTitle: String) : Step()

    @Serializable
    data class ChangeLine(val stationName: String, val newLineTitle: String) : Step()

    @Serializable
    data class LastStation(val stationName: String) : Step()

    @Serializable
    object Destination : Step()
}
