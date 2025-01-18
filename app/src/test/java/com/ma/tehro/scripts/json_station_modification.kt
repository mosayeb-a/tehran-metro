package com.ma.tehro.scripts

import com.ma.tehro.common.readJsonStationsAsText
import com.ma.tehro.data.PositionInLine
import com.ma.tehro.data.Station
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun main() {
    val stations: MutableMap<String, Station> = readJsonStationsAsText("stations2")
    val updatedStations = addPositionsInLine(stations)

    File("station_updated.json").writeText(Json.encodeToString(updatedStations))
    println("Updated stations.json successfully!")
}

fun addPositionsInLine(stations: MutableMap<String, Station>): Map<String, Station> {
    val lines = stations.values.flatMap { it.lines }.toSet()

    for (line in lines) {
        val orderedStations = getOrderedStationsByLine(line, stations)
        orderedStations.forEachIndexed { index, station ->
            val updatedStation = station.copy(
                positionsInLine = station.positionsInLine + PositionInLine(index + 1, line)
            )
            stations[station.name] = updatedStation
        }
    }

    return stations
}

fun getOrderedStationsByLine(line: Int, stations: Map<String, Station>): List<Station> {
    if (stations.isEmpty()) return emptyList()

    val visited = mutableSetOf<String>()
    val orderedStations = mutableListOf<Station>()

    val startStation = stations.entries.find { entry ->
        entry.value.lines.contains(line) &&
                entry.value.relations.count { stations[it]?.lines?.contains(line) == true } == 1
    }?.key ?: return emptyList()

    fun dfs(stationName: String) {
        if (stationName in visited) return
        visited.add(stationName)
        stations[stationName]?.let { station ->
            orderedStations.add(station)
            station.relations
                .filter { stations[it]?.lines?.contains(line) == true && it !in visited }
                .forEach { dfs(it) }
        }
    }

    dfs(startStation)
    return orderedStations
}
