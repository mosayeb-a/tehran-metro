package com.ma.tehro

import com.ma.tehro.common.readJsonStationsAsText
import com.ma.tehro.data.Station
import com.ma.tehro.scripts.getOrderedStationsByLine
import com.ma.tehro.ui.line.LineViewModel
import org.junit.Test
import kotlin.test.assertEquals

class JsonStationItemFileTest {

    private val stations: Map<String, Station> = readJsonStationsAsText("station_updated")

    @Test
    fun `check the order of stations for line 1`() {
        val line = 1
        val expectedOrder = getOrderedStationsByLine(line, stations)
        val actualOrder = LineViewModel(stations).getOrderedStationsInLineByPosition(line)

        assertEquals(
            expectedOrder,
            actualOrder,
            "The order of stations for line $line is incorrect."
        )
    }

    @Test
    fun `check the order of stations for line 2`() {
        val line = 2
        val expectedOrder = getOrderedStationsByLine(line, stations)
        val actualOrder = LineViewModel(stations).getOrderedStationsInLineByPosition(line)

        assertEquals(
            expectedOrder,
            actualOrder,
            "The order of stations for line $line is incorrect."
        )
    }

    @Test
    fun `check the order of stations for line 3`() {
        val line = 3
        val expectedOrder = getOrderedStationsByLine(line, stations)
        val actualOrder = LineViewModel(stations).getOrderedStationsInLineByPosition(line)

        assertEquals(
            expectedOrder,
            actualOrder,
            "The order of stations for line $line is incorrect."
        )
    }

    @Test
    fun `check the order of stations for line 4`() {
        val line = 4
        val expectedOrder = getOrderedStationsByLine(line, stations)
        val actualOrder = LineViewModel(stations).getOrderedStationsInLineByPosition(line)

        assertEquals(
            expectedOrder,
            actualOrder,
            "The order of stations for line $line is incorrect."
        )
    }

    @Test
    fun `check the order of stations for line 5`() {
        val line = 5
        val expectedOrder = getOrderedStationsByLine(line, stations)
        val actualOrder = LineViewModel(stations).getOrderedStationsInLineByPosition(line)

        assertEquals(
            expectedOrder,
            actualOrder,
            "The order of stations for line $line is incorrect."
        )
    }

    @Test
    fun `check the order of stations for line 6`() {
        val line =6
        val expectedOrder = getOrderedStationsByLine(line, stations)
        val actualOrder = LineViewModel(stations).getOrderedStationsInLineByPosition(line)

        assertEquals(
            expectedOrder,
            actualOrder,
            "The order of stations for line $line is incorrect."
        )
    }

    @Test
    fun `check the order of stations for line 7`() {
        val line = 7
        val expectedOrder = getOrderedStationsByLine(line, stations).map { it.name }
        val actualOrder = LineViewModel(stations).getOrderedStationsInLineByPosition(line).map { it.name }

        assertEquals(
            expectedOrder,
            actualOrder,
            "The order of stations for line $line is incorrect."
        )
    }
}