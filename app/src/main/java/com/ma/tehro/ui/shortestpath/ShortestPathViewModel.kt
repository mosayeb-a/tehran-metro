package com.ma.tehro.ui.shortestpath

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.ma.tehro.common.getLineEndpoints
import com.ma.tehro.data.Station
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.PriorityQueue
import javax.inject.Inject

@Immutable
sealed class PathItem {
    data class Title(val text: String) : PathItem()
    data class StationItem(val station: Station) : PathItem()
}

@Immutable
data class PathUiState(
    val isLoading: Boolean = false,
    val selectedStartStation: String = "",
    val selectedDestStation: String = "",
)

data class PathCost(
    val path: List<String>,
    val cost: Int
)

@HiltViewModel
class ShortestPathViewModel @Inject constructor(
    val stations: Map<String, Station>,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PathUiState())
    val uiState: StateFlow<PathUiState> get() = _uiState

    fun onSelectedChange(isFrom: Boolean, station: String) {
        _uiState.value = _uiState.value.copy(
            selectedStartStation = if (isFrom) station else _uiState.value.selectedStartStation,
            selectedDestStation = if (!isFrom) station else _uiState.value.selectedDestStation
        )
    }


    fun isIndexInTheRange(index: Int, lineAndTitlePosition: List<Pair<Int, Pair<Int, Int>>>): Int? {
        return lineAndTitlePosition.firstOrNull { (_, range) ->
            index in range.first..range.second
        }?.first
    }


    private fun getLineByPath(path: List<PathItem>): List<Pair<Int, Pair<Int, Int>>> {
        val lines = mutableListOf<Pair<Int, Pair<Int, Int>>>()
        var currentLine: Int? = null
        var segmentStartIndex = 0

        path.forEachIndexed { index, item ->
            if (item is PathItem.Title) {
                val lineNumber = item.text.substring(5, 6).toInt()

                if (currentLine != null && lineNumber != currentLine) {
                    lines.add(currentLine!! to (segmentStartIndex to index - 1))
                }

                currentLine = lineNumber
                segmentStartIndex = index
            }
        }

        if (currentLine != null) {
            lines.add(currentLine!! to (segmentStartIndex to path.lastIndex))
        }

        return lines
    }

    private val lineAndTitlePositionCache =
        mutableMapOf<List<PathItem>, List<Pair<Int, Pair<Int, Int>>>>()

    fun getCachedLineByPath(path: List<PathItem>): List<Pair<Int, Pair<Int, Int>>> {
        return lineAndTitlePositionCache.getOrPut(path) {
            getLineByPath(path)
        }
    }

    private val pathCache = mutableMapOf<Pair<String, String>, List<PathItem>>()

    fun findShortestPathWithDirectionCache(from: String, to: String): List<PathItem> {
        val key = Pair(from, to)


        if (pathCache.containsKey(key)) {
            return pathCache[key]!!
        }


        val path = findShortestPathWithDirection(from, to)
        pathCache[key] = path
        return path
    }

    fun findShortestPathWithDirection(from: String, to: String): List<PathItem> {
        val shortestPath = findShortestPath(stations, from, to)
        if (shortestPath.isEmpty()) return emptyList()

        val directions = mutableListOf<PathItem>()
        var currentLine: Int? = null
        var previousStation: Station? = null

        val firstStation = stations[shortestPath.first()] ?: return emptyList()
        directions.add(PathItem.StationItem(firstStation))

        for (i in 0 until shortestPath.size - 1) {
            val currentStationName = shortestPath[i]
            val nextStationName = shortestPath[i + 1]

            val currentStation = stations[currentStationName] ?: continue
            val nextStation = stations[nextStationName] ?: continue

            val currentLinePosition =
                currentStation.positionsInLine.firstOrNull { pos ->
                    nextStation.positionsInLine.any { it.line == pos.line }
                } ?: continue

            val nextLinePosition =
                nextStation.positionsInLine.first { it.line == currentLinePosition.line }

            if (currentLine != currentLinePosition.line) {
                currentLine = currentLinePosition.line
                val endpoints = getLineEndpoints()[currentLine] ?: continue
                val direction = if (currentLinePosition.position < nextLinePosition.position) {
                    "Line $currentLine: To ${endpoints.second}"
                } else {
                    "Line $currentLine: To ${endpoints.first}"
                }
                directions.add(PathItem.Title(direction))

                if (previousStation != null) {
                    directions.add(PathItem.StationItem(currentStation))
                }
            }

            if (previousStation == null || previousStation.name != nextStation.name) {
                directions.add(PathItem.StationItem(nextStation))
            }

            previousStation = nextStation
        }

        // todo quick fix
        if (directions.size > 1) {
            directions.swap(0, 1)
        }

        return directions
    }

    /**
     * Finds the optimal path between two metro stations considering both distance and line changes.
     *
     * This implementation uses Dijkstra's algorithm with a priority queue to find the path that
     * minimizes the total cost, where cost is calculated based on:
     * - Number of stations traveled (stationCost per station)
     * - Number of line changes required (lineChangeCost per change)
     *
     * Cost Calculation:
     * - Base cost per station: 3 points (configurable via stationCost)
     * - Additional cost for line change: 6 points (configurable via lineChangeCost)
     * - Total path cost = (number of stations × stationCost) + (number of line changes × lineChangeCost)
     *
     * Example:
     * Path with 5 stations and 1 line change:
     * - Station cost: 5 × 3 = 15
     * - Line change cost: 1 × 6 = 6
     * - Total cost: 21
     **/
    private fun findShortestPath(
        stations: Map<String, Station>,
        from: String,
        to: String,
        stationCost: Int = 3,
        lineChangeCost: Int = 6
    ): List<String> {
        val queue = PriorityQueue<PathCost>(compareBy { it.cost })
        val visited = mutableSetOf<String>()
        val costMap = mutableMapOf<String, Int>().apply {
            put(from, 0)
        }

        queue.add(PathCost(listOf(from), 0))

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            val currentStation = current!!.path.last()

            if (currentStation == to) return current.path

            if (costMap.getOrDefault(currentStation, Int.MAX_VALUE) < current.cost) continue

            visited.add(currentStation)

            val station = stations[currentStation] ?: continue
            val currentLine = if (current.path.size > 1) {
                val prevStation = stations[current.path[current.path.size - 2]]
                prevStation?.lines?.firstOrNull { line ->
                    station.lines.contains(line)
                }
            } else {
                station.lines.first()
            }

            station.relations
                .filter { stations[it]?.disabled != true }
                .forEach { nextStationName ->
                    val nextStation = stations[nextStationName] ?: return@forEach

                    var newCost = current.cost + stationCost

                    val needsLineChange = currentLine != null &&
                            !nextStation.lines.contains(currentLine)
                    if (needsLineChange) {
                        newCost += lineChangeCost
                    }

                    if (newCost < costMap.getOrDefault(nextStationName, Int.MAX_VALUE)) {
                        costMap[nextStationName] = newCost
                        queue.add(
                            PathCost(
                                path = current.path + nextStationName,
                                cost = newCost
                            )
                        )
                    }
                }
        }

        return emptyList()
    }

    private fun MutableList<PathItem>.swap(index1: Int, index2: Int) {
        val temp = this[index1]
        this[index1] = this[index2]
        this[index2] = temp
    }
}
