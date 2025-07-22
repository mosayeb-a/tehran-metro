package com.ma.tehro.common.ui

import com.ma.tehro.data.Station
import kotlinx.serialization.Serializable

@Serializable
object LinesScreen

@Serializable
data class StationsScreen(val lineNumber: Int, val useBranch: Boolean)

@Serializable
object StationSelectorScreen

@Serializable
data class PathFinderScreen(
    val startEnStation: String,
    val startFaStation: String,
    val enDestination: String,
    val faDestination: String,
    val dayOfWeek: Int,
    val currentTime: Double,
    val lineChangeDelayMinutes: Int
)

@Serializable
data class StationDetailScreen(val station: Station, val lineNumber: Int, val useBranch: Boolean)

@Serializable
object MapScreen

@Serializable
data class SubmitStationInfoScreen(val station: Station, val lineNumber: Int)

@Serializable
data class TrainScheduleScreen(
    val enStationName: String,
    val faStationName: String,
    val lineNumber: Int,
    val useBranch: Boolean
)

@Serializable
object SubmitFeedbackScreen

/**
 * order of path list->
 * static:
 * f: first station of list
 * repetitive:
 * l: last station of the line
 * t: title of line changes contains destination
 * static:
 * l: last station of list
 */
@Serializable
data class PathDescriptionScreen(val path: List<String>)

@Serializable
object OfficialMetroMapScreen

@Serializable
object AboutScreen

@Serializable
object NearbyPlaceStationsScreen