package com.ma.tehro

import com.ma.tehro.data.Station
import com.ma.tehro.data.repo.LineRepository
import com.ma.tehro.data.repo.LineRepositoryImpl
import com.ma.tehro.scripts.getOrderedStationsByLine
import com.ma.tehro.scripts.readJsonStationsAsText
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class JsonStationScheduleItemFileTest {

    private val stations: Map<String, Station> = readJsonStationsAsText("stations_updated2")
    private lateinit var repository: LineRepository

    @Before
    fun setup() {
        repository = LineRepositoryImpl(stations)
    }


    @Test
    fun `check the order of stations for line 1`() {
        val line = 1
        val expectedOrder = getOrderedStationsByLine(line, stations)
        val actualOrder = repository.getOrderedStationsByLine(line)

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
        val actualOrder = repository.getOrderedStationsByLine(line)

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
        val actualOrder = repository.getOrderedStationsByLine(line)

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
        val actualOrder = repository.getOrderedStationsByLine(line)

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
        val actualOrder = repository.getOrderedStationsByLine(line)

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
        val actualOrder = repository.getOrderedStationsByLine(line)

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
        val actualOrder = repository.getOrderedStationsByLine(line).map { it.name }

        assertEquals(
            expectedOrder,
            actualOrder,
            "The order of stations for line $line is incorrect."
        )
    }
}