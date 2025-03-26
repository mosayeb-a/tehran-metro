package com.ma.tehro

import com.ma.tehro.data.Station
import com.ma.tehro.scripts.getOrderedStationsByLine
import com.ma.tehro.scripts.readJsonStationsAsText
import org.junit.Test
import kotlin.test.assertEquals

class DifferenceJsonStationScheduleItemFilesTest {
    private val stations: Map<String, Station> = readJsonStationsAsText("stations_updated")
    private val newStations: Map<String, Station> = readJsonStationsAsText("stations_updated2")

    @Test
    fun `is the sizes are the same`() {

        val stationsList = stations.toList()
        val newStationsList = newStations.toList()

        val stationSize = stationsList.size
        val newStationSize = newStationsList.size


        if (stationSize != newStationSize) {

            val stationNames = stationsList.map { it.second.name }.toSet()
            val newStationNames = newStationsList.map { it.second.name }.toSet()


            val extraInStations = stationNames - newStationNames
            val extraInNewStations = newStationNames - stationNames


            if (extraInStations.isNotEmpty()) {
                println("Extra items in 'stations': $extraInStations")
            }
            if (extraInNewStations.isNotEmpty()) {
                println("Extra items in 'newStations': $extraInNewStations")
            }
        }


        assertEquals(stationSize, newStationSize, "Station sizes do not match.")
    }

    @Test
    fun `check station order for line 1`() {
        checkLineOrder(1)
    }

    @Test
    fun `check station order for line 2`() {
        checkLineOrder(2)
    }

    @Test
    fun `check station order for line 3`() {
        checkLineOrder(3)
    }

    @Test
    fun `check station order for line 4`() {
        checkLineOrder(4)
    }

    @Test
    fun `check station order for line 5`() {
        checkLineOrder(5)
    }

    @Test
    fun `check station order for line 6`() {
        checkLineOrder(6)
    }

    @Test
    fun `check station order for line 7`() {
        checkLineOrder(7)
    }

    @Test
    fun `check if station relations match`() {

        stations.forEach { (stationName, station) ->

            val newStation = newStations[stationName]

            if (newStation == null) {
                println("Station '$stationName' is missing in the new stations map.")
            } else {

                val oldRelations = station.relations.sorted()
                val newRelations = newStation.relations.sorted()

                if (oldRelations != newRelations) {
                    println("Relations mismatch for station '$stationName':")
                    println("Old relations: $oldRelations")
                    println("New relations: $newRelations")
                }


                assertEquals(
                    oldRelations,
                    newRelations,
                    "Relations mismatch detected for station '$stationName'"
                )
            }
        }


        newStations.forEach { (stationName, _) ->
            if (!stations.containsKey(stationName)) {
                println("Station '$stationName' is missing in the original stations map.")
            }
        }
    }

    private fun checkLineOrder(line: Int) {
        val oldOrder = getOrderedStationsByLine(line, stations).map { it.name }
        val newOrder = getOrderedStationsByLine(line, newStations).map { it.name }

        if (oldOrder != newOrder) {
            println("Order mismatch for line $line")
            println("Old order: $oldOrder")
            println("New order: $newOrder")
        }

        assertEquals(oldOrder, newOrder, "Order mismatch detected for line $line")
    }
}

