package com.ma.tehro.ui.detail.repo

import com.ma.tehro.data.Station
import javax.inject.Inject

interface LineRepository {
    fun getOrderedStationsByLine(line: Int): List<Station>
    fun getLines(): List<Int>
}

class LineRepositoryImpl @Inject constructor(
    private val stations: Map<String, Station>
) : LineRepository {
    override fun getLines(): List<Int> =
        listOf(1, 2, 3, 4, 5, 6, 7)

    // todo: showing stations line in branches for line 1 & 2
    override fun getOrderedStationsByLine(
        line: Int,
    ): List<Station> {
        return stations.values
            .filter { it.lines.contains(line) }
            .sortedBy { station ->
                station.positionsInLine.find { it.line == line }?.position
            }
    }

}