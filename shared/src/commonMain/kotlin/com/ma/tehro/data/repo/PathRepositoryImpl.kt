package com.ma.tehro.data.repo

import com.ma.tehro.common.LineEndpoints
import com.ma.tehro.common.PriorityQueue
import com.ma.tehro.common.lineBranches
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.data.Station
import com.ma.tehro.domain.PathItem
import com.ma.tehro.domain.repo.PathRepository
import kotlin.comparisons.compareBy

data class PathResult(
    val path: List<String>,
    val lineChanges: Int,
)

data class PathCost(
    val path: List<String>,
    val cost: Int,
    val currentLine: Int?
)

class PathRepositoryImpl(
    private val stations: Map<String, Station>,
) : PathRepository {

    override fun getStations(): Map<String, Station> = stations

    /**
     * Finds the shortest path between two metro stations with direction and line titles.
     *
     * This function uses the shortest path computed by [findShortestPath], which employs Dijkstra's
     * algorithm to minimize the total cost. The cost is calculated as:
     * - **Station Cost**: Each station traversal costs 3 points (configurable via stationCost).
     * - **Line Change Cost**: Each line change costs 6 points (configurable via lineChangeCost).
     * - **Total Cost Formula**: (number of stations × stationCost) + (number of line changes × lineChangeCost)
     *
     * The function enhances the path by:
     * - adding titles for each line segment (e.g: "Line 1: To Kahrizak").
     * - handling branch stations:
     *   - If the first station is a branch station, the title uses the branch point (e.g., "To Shahed - BagherShahr").
     *   - If the last station is a branch station, a new segment is created (e.g., "To Shahr-e Parand").
     * - determining direction (forward/backward) using station positions in positionsInLine.
     *
     * @param from The starting station name.
     * @param to The destination station name.
     * @return A list of [PathItem] objects representing the path with titles and stations.
     */
    override suspend fun findShortestPathWithDirection(from: String, to: String): List<PathItem> {
        val path = findShortestPath(stations, from, to).path
        val result = mutableListOf<PathItem>()
        var currentLine: Int? = null
        var isBranchSegment = false

        fun createTitle(line: Int, enDirection: String, faDirection: String) = PathItem.Title(
            en = "Line $line: To $enDirection",
            fa = "خط ${line.toFarsiNumber()}: به سمت $faDirection"
        )

        fun getDirectionEndpoints(
            line: Int,
            useBranch: Boolean,
            isForward: Boolean
        ): Pair<String, String>? {
            val enEndpoints = LineEndpoints.getEn(line, useBranch) ?: return null
            val faEndpoints = LineEndpoints.getFa(line, useBranch) ?: return null
            return if (isForward) enEndpoints.second to faEndpoints.second
            else enEndpoints.first to faEndpoints.first
        }

        path.forEachIndexed { index, stationName ->
            val currentStation = stations[stationName]!!
            val nextStation = path.getOrNull(index + 1)?.let { stations[it] }
            val sharedLines = nextStation?.lines?.intersect(currentStation.lines)?.takeIf { it.isNotEmpty() }

            // initialize the line and title if not set
            // Formula: If no line is set and there are shared lines, select the first line and determine
            // the direction. For branch starts, use the branch point; otherwise, use main/branch endpoints.
            if (currentLine == null && sharedLines != null) {
                currentLine = sharedLines.first()
                val branchConfig = lineBranches[currentLine]
                val isBranchStart = branchConfig?.branch?.contains(currentStation.name) == true
                isBranchSegment = isBranchStart && LineEndpoints.hasBranch(currentLine)

                val isForward = currentStation.positionsInLine.firstOrNull { it.line == currentLine }
                    ?.let { currentPos ->
                        nextStation.positionsInLine.firstOrNull { it.line == currentLine }
                            ?.let { nextPos -> currentPos.position < nextPos.position }
                    } != false

                val (enDirection, faDirection) = if (isBranchStart) {
                    branchConfig.branchPoint.en to branchConfig.branchPoint.fa
                } else {
                    getDirectionEndpoints(currentLine, isBranchSegment, isForward) ?: return@forEachIndexed
                }

                result.add(createTitle(currentLine, enDirection, faDirection))
            }

            // add the current station to the path
            // formula: Each station is added with its line number and passthrough status (disabled stations
            // are marked as passthrough). if no line is set, use -1 as a fallback.
            result.add(
                PathItem.StationItem(
                    station = currentStation,
                    isPassthrough = currentStation.disabled,
                    lineNumber = currentLine ?: -1
                )
            )

            // handle branch transitions (branch to main or main to branch)
            // formula: check if the current station is a branch point and adjust the segment type
            // (branch or main). add a new title and re-add the branch point for the new segment.
            if (currentLine != null && nextStation != null) {
                val branchConfig = lineBranches[currentLine]
                val isLastBranch = branchConfig?.branch?.contains(nextStation.name) == true
                val isBranchPoint = branchConfig?.branchPoint?.en == currentStation.name

                // transition from branch to main line (e.g: from "Namayeshgah-e Shahr-e Aftab" to main line)
                if (isBranchSegment && isBranchPoint) {
                    isBranchSegment = false
                    val isForward = currentStation.positionsInLine.firstOrNull { it.line == currentLine }
                        ?.let { currentPos ->
                            nextStation.positionsInLine.firstOrNull { it.line == currentLine }
                                ?.let { nextPos -> currentPos.position < nextPos.position }
                        } != false

                    val (enDirection, faDirection) = getDirectionEndpoints(currentLine, false, isForward)
                        ?: return@forEachIndexed

                    result.add(createTitle(currentLine, enDirection, faDirection))
                    result.add(
                        PathItem.StationItem(
                            station = currentStation,
                            isPassthrough = currentStation.disabled,
                            lineNumber = currentLine
                        )
                    )
                }

                // transition to branch (e.g: to "Shahr-e Parand" when reaching "Shahed - BagherShahr")
                if (isLastBranch && isBranchPoint) {
                    isBranchSegment = true
                    val (enDirection, faDirection) = getDirectionEndpoints(currentLine, true, true)
                        ?: return@forEachIndexed

                    result.add(createTitle(currentLine, enDirection, faDirection))
                    result.add(
                        PathItem.StationItem(
                            station = currentStation,
                            isPassthrough = currentStation.disabled,
                            lineNumber = currentLine
                        )
                    )
                }
            }

            // handle line changes (non-branch)
            // formula: If the next station is on a different line, switch to the new line, determine
            // the direction, and add a new title. for branch starts, use the branch point; otherwise,
            // use main/branch endpoints.
            if (nextStation != null && sharedLines?.contains(currentLine) != true) {
                val newLine = sharedLines?.firstOrNull() ?: return@forEachIndexed
                currentLine = newLine

                val branchConfig = lineBranches[newLine]
                val isBranchStart = branchConfig?.branch?.contains(currentStation.name) == true
                isBranchSegment = isBranchStart && LineEndpoints.hasBranch(newLine)

                val isForward = currentStation.positionsInLine.firstOrNull { it.line == newLine }
                    ?.let { currentPos ->
                        nextStation.positionsInLine.firstOrNull { it.line == newLine }
                            ?.let { nextPos -> currentPos.position < nextPos.position }
                    } != false

                val (enDirection, faDirection) = if (isBranchStart) {
                    branchConfig.branchPoint.en to branchConfig.branchPoint.fa
                } else {
                    getDirectionEndpoints(newLine, isBranchSegment, isForward) ?: return@forEachIndexed
                }

                result.add(createTitle(newLine, enDirection, faDirection))
                result.add(
                    PathItem.StationItem(
                        station = currentStation,
                        isPassthrough = currentStation.disabled,
                        lineNumber = newLine
                    )
                )
            }
        }

        return result
    }

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

    /**
     * The pathfinding formula is implemented in two stages:
     *  -shortest path calculation:
     *      -uses dijkstra's algorithm to find the path with the minimum total cost.
     * -cost formula (number of stations × stationCost) + (number of line changes × lineChangeCost).
     * example: for a path with 5 stations and 1 line change:
     *  station cost: 5 × 3 = 15
     *  line change cost: 1 × 6 = 6
     *  total cost: 15 + 6 = 21
     * the algorithm considers all possible lines between stations and penalizes line changes to prefer paths with fewer transfers.
     */
    fun findShortestPath(
        stations: Map<String, Station>,
        from: String,
        to: String,
        stationCost: Int = 3,
        lineChangeCost: Int = 6
    ): PathResult {
        val queue = PriorityQueue<PathCost>(compareBy { it.cost })

        val visited = mutableMapOf<Pair<String, Int?>, Int>()

        queue.add(PathCost(path = listOf(from), cost = 0, currentLine = null))

        while (queue.isNotEmpty()) {
            val current = queue.poll()!!
            val currentStationName = current.path.last()

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

                    queue.add(
                        PathCost(
                            path = current.path + nextStationName,
                            cost = current.cost + stationCost + if (isLineChange) lineChangeCost else 0,
                            currentLine = line
                        )
                    )
                }
            }
        }

        return PathResult(path = emptyList(), lineChanges = 0)
    }
}