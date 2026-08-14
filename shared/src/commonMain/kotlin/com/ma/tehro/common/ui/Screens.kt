package com.ma.tehro.common.ui

import com.ma.tehro.domain.common.BilingualName
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.path.Step
import kotlinx.serialization.Serializable

@Serializable
object LinesScreen

@Serializable
data class StationsScreen(val lineNumber: Int, val useBranch: Boolean)

@Serializable
object StationSelectorScreen

@Serializable
data class PathFinderScreen(
    val from: BilingualName,
    val to: BilingualName,
    val dayOfWeek: Int,
    val departureTime: Double,
    val transferDelayMinutes: Int
)

@Serializable
data class StationDetailScreen(val station: Station, val lineNumber: Int, val useBranch: Boolean)

@Serializable
data class MapScreen(
    val isSelection: Boolean = false,
    val isFrom: Boolean = false
)

@Serializable
data class TrainScheduleScreen(
    val station: BilingualName,
    val lineNumber: Int,
    val useBranch: Boolean
)

@Serializable
object SubmitFeedbackScreen

@Serializable
data class PathDescriptionScreen(val steps: List<Step>)

@Serializable
data class MapViewerScreen(val shortestPath: List<String>?)

@Serializable
object MoreScreen

@Serializable
object MetroGuideScreen

@Serializable
object PodcastListScreen