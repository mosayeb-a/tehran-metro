package com.ma.tehro.data.repo

import com.ma.tehro.data.BranchConfig
import com.ma.tehro.data.Station
import javax.inject.Inject

interface LineRepository {
    fun getOrderedStationsByLine(line: Int, useBranch: Boolean): List<Station>
    fun getLines(): List<Int>
}

class LineRepositoryImpl @Inject constructor(
    private val stations: Map<String, Station>
) : LineRepository {
    private val lineBranches = mapOf(
        1 to BranchConfig(
            branchPoint = "Shahed - BagherShahr",
            branch = listOf(
                "Namayeshgah-e Shahr-e Aftab",
                "Vavan",
                "Emam Khomeini Airport",
                "Shahr-e Parand"
            )
        ),
        4 to BranchConfig(
            branchPoint = "Bimeh",
            branch = listOf(
                "Mehrabad Airport Terminal 1&2",
                "Mehrabad Airport Terminal 4&6"
            )
        ),
        5 to BranchConfig(
            branchPoint = "Golshahr",
            branch = listOf(
                "Shahid Sepahbod Qasem Soleimani"
            )
        )
    )

    override fun getLines(): List<Int> = listOf(1, 2, 3, 4, 5, 6, 7)

    override fun getOrderedStationsByLine(
        line: Int,
        useBranch: Boolean
    ): List<Station> {
        val branchConfig = lineBranches[line] ?: return getDefaultOrderedStations(line)

        val commonStations = stations.values
            .filter { it.lines.contains(line) }
            .sortedBy { station ->
                station.positionsInLine.find { it.line == line }?.position
            }
            .takeWhile { it.name != branchConfig.branchPoint }
            .toMutableList()
            .apply {
                stations[branchConfig.branchPoint]?.let { add(it) }
            }

        val remainingStations = if (useBranch) {
            branchConfig.branch.mapNotNull { stations[it] }
        } else {
            getDefaultOrderedStations(line)
                .dropWhile { it.name != branchConfig.branchPoint }
                .drop(1)
                .filter { it.name !in branchConfig.branch }
        }

        return commonStations + remainingStations
    }

    private fun getDefaultOrderedStations(line: Int): List<Station> =
        stations.values
            .filter { it.lines.contains(line) }
            .sortedBy { station ->
                station.positionsInLine.find { it.line == line }?.position
            }
}