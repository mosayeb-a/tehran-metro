package com.ma.tehro.data.repo

import androidx.compose.runtime.Immutable
import com.ma.tehro.common.LineEndpoints
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
    val cost: Int,
    val currentLine: Int?
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
                val enEndpoints = LineEndpoints.getEn(currentLine) ?: continue
                val faEndpoints = LineEndpoints.getFa(currentLine) ?: continue

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
    private fun countLineChanges(path: List<String>, stations: Map<String, Station>): Int {
        var lineChanges = 0
        var currentLine: Int? = null

        for (i in 0 until path.size - 1) {
            val currentStation = stations[path[i]] ?: continue
            val nextStation = stations[path[i + 1]] ?: continue

            val possibleLines = currentStation.positionsInLine.map { it.line }
                .intersect(nextStation.positionsInLine.map { it.line })

            val newLine = possibleLines.firstOrNull()
            if (newLine != null && currentLine != null && newLine != currentLine) {
                lineChanges++
            }

            currentLine = newLine
        }

        return lineChanges
    }


    private fun findShortestPath(
        stations: Map<String, Station>,
        from: String,
        to: String,
        stationCost: Int = 3,
        lineChangeCost: Int = 6 // we can set it to 10
    ): PathResult {
        val queue = PriorityQueue<PathCost>(compareBy { it.cost })
        val visited = mutableMapOf<Pair<String, Int?>, Int>()

        queue.add(PathCost(path = listOf(from), cost = 0, currentLine = null))

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            val currentStationName = current!!.path.last()

            if (currentStationName == to) {
                val lineChanges = countLineChanges(current.path, stations)
                return PathResult(path = current.path, lineChanges = lineChanges)
            }

            val currentStation = stations[currentStationName] ?: continue

            val visitedKey = currentStationName to current.currentLine
            if (visited[visitedKey] != null && visited[visitedKey]!! <= current.cost) continue
            visited[visitedKey] = current.cost

            for (nextStationName in currentStation.relations) {
                val nextStation = stations[nextStationName] ?: continue

                // consider the disables
//                if (nextStation.disabled && nextStationName != to) continue

                val possibleLines = currentStation.positionsInLine.map { it.line }
                    .intersect(nextStation.positionsInLine.map { it.line })

                for (line in possibleLines) {
                    val isLineChange = (current.currentLine != null && line != current.currentLine)

                    if (isLineChange && currentStation.disabled) continue

                    val newCost =
                        current.cost + stationCost + if (isLineChange) lineChangeCost else 0

                    queue.add(
                        PathCost(
                            path = current.path + nextStationName,
                            cost = newCost,
                            currentLine = line
                        )
                    )
                }
            }
        }

        return PathResult(path = emptyList(), lineChanges = 0)
    }


    private fun MutableList<PathItem>.swap(index1: Int, index2: Int) {
        val temp = this[index1]
        this[index1] = this[index2]
        this[index2] = temp
    }
}