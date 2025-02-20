package com.ma.tehro.common

import com.ma.tehro.data.Station
import kotlinx.serialization.Serializable

@Serializable
object LinesScreen

@Serializable
data class StationsScreen(val lineNumber: Int)

@Serializable
object StationSelectorScreen

@Serializable
data class PathFinderScreen(
    val startEnStation: String,
    val startFaStation: String,
    val enDestination: String,
    val faDestination: String
)

@Serializable
data class StationDetailScreen(val station: Station, val lineNumber: Int)

@Serializable
object MapScreen

@Serializable
data class SubmitStationInfoScreen(val station: Station, val lineNumber: Int)
