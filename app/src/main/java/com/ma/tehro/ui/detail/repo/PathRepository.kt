package com.ma.tehro.ui.detail.repo

import androidx.compose.runtime.Immutable
import com.ma.tehro.common.getLineEnEndpoints
import com.ma.tehro.common.getLineFaEndpoints
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.data.Station
import java.util.PriorityQueue
import javax.inject.Inject

@Immutable
sealed class PathItem {
    data class Title(val en: String, val fa: String) : PathItem()
    data class StationItem(
        val station: Station,
        val isPassthrough: Boolean = false,
        val lineNumber: Int
    ) : PathItem()
}

data class PathResult(
    val path: List<String>,
    val lineChanges: Int,
)

data class PathCost(
    val path: List<String>,
    val cost: Int
)

interface PathRepository {
    fun findShortestPathWithDirection(from: String, to: String): List<PathItem>
    fun getStations(): Map<String, Station>
}

class PathRepositoryImpl @Inject constructor(
    private val stations: Map<String, Station>,
) : PathRepository {

    override fun getStations(): Map<String, Station> = stations

    override fun findShortestPathWithDirection(from: String, to: String): List<PathItem> {
        val result = findShortestPath(stations, from, to)
        if (result.path.isEmpty()) return emptyList()

        val directions = mutableListOf<PathItem>()
        var currentLine: Int? = null
        var previousStation: Station? = null
        val firstStation = stations[result.path.first()] ?: return emptyList()
        val secondStation = if (result.path.size > 1) stations[result.path[1]] else null

        val initialLine = if (secondStation != null) {
            firstStation.positionsInLine.firstOrNull { firstPos ->
                secondStation.positionsInLine.any { it.line == firstPos.line }
            }?.line ?: firstStation.lines.first()
        } else {
            firstStation.lines.first()
        }

        directions.add(
            PathItem.StationItem(
                station = firstStation,
                isPassthrough = firstStation.disabled,
                lineNumber = initialLine
            )
        )

        for (i in 0 until result.path.size - 1) {
            val currentStationName = result.path[i]
            val nextStationName = result.path[i + 1]

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
                val enEndpoints = getLineEnEndpoints()[currentLine] ?: continue
                val faEndpoints = getLineFaEndpoints()[currentLine] ?: continue

                if (currentLinePosition.position < nextLinePosition.position) {
                    directions.add(
                        PathItem.Title(
                            fa = "خط ${currentLine.toFarsiNumber()}: به سمت ${faEndpoints.second}",
                            en = "Line $currentLine: To ${enEndpoints.second}"
                        )
                    )
                } else {
                    directions.add(
                        PathItem.Title(
                            fa = "خط ${currentLine.toFarsiNumber()}: به سمت ${faEndpoints.first}",
                            en = "Line $currentLine: To ${enEndpoints.first}"
                        )
                    )
                }

                if (previousStation != null) {
                    directions.add(
                        PathItem.StationItem(
                            station = currentStation,
                            isPassthrough = currentStation.disabled,
                            lineNumber = currentLinePosition.line
                        )
                    )
                }
            }

            if (previousStation == null || previousStation.name != nextStation.name) {
                directions.add(
                    PathItem.StationItem(
                        station = nextStation,
                        isPassthrough = nextStation.disabled,
                        lineNumber = currentLinePosition.line
                    )
                )
            }

            previousStation = nextStation
        }

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
    ): PathResult {
        val queue = PriorityQueue<PathCost>(compareBy { it.cost })
        val visited = mutableSetOf<String>()
        val costMap = mutableMapOf<String, Int>().apply {
            put(from, 0)
        }

        queue.add(PathCost(listOf(from), 0))

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            val currentStation = current!!.path.last()

            if (currentStation == to) {

                val lastStation = stations[currentStation]

                if (lastStation?.disabled == true) {

                    continue
                }

                val lineChanges = countLineChanges(current.path, stations)
                return PathResult(
                    path = current.path,
                    lineChanges = lineChanges
                )
            }

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

            station.relations.forEach { nextStationName ->
                val nextStation = stations[nextStationName] ?: return@forEach


                if (nextStation.disabled && (nextStationName == to ||
                            nextStation.relations.any { it == to })
                ) {
                    return@forEach
                }

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

        return PathResult(emptyList(), 0)
    }

    private fun countLineChanges(path: List<String>, stations: Map<String, Station>): Int {
        var changes = 0
        var currentLine: Int? = null

        for (stationName in path) {
            val station = stations[stationName] ?: continue

            if (currentLine == null) {
                currentLine = station.lines.first()
                continue
            }

            if (!station.lines.contains(currentLine)) {
                changes++
                currentLine = station.lines.first()
            }
        }
        return changes
    }

    private fun MutableList<PathItem>.swap(index1: Int, index2: Int) {
        val temp = this[index1]
        this[index1] = this[index2]
        this[index2] = temp
    }
}