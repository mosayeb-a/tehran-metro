package com.ma.tehro

import com.ma.tehro.common.readJsonStationsAsText
import com.ma.tehro.data.Station
import com.ma.tehro.ui.shortestpath.PathItem
import com.ma.tehro.ui.shortestpath.ShortestPathViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ShortestPathViewModelTest {

    private lateinit var viewModel: ShortestPathViewModel
    private lateinit var stations: Map<String, Station>

    @Before
    fun setup() {
        stations = readJsonStationsAsText("stations_updated")
        viewModel = ShortestPathViewModel(stations)
    }

    @Test
    fun `find shortest path with direction from Darvazeh Shemiran to Ayatollah Taleghani`() {
        val from = "Darvazeh Shemiran"
        val to = "Ayatollah Taleghani"

        val actualPath = viewModel.findShortestPathWithDirection(from, to)
        val expectedPath = actualPath.map {
            when (it) {
                is PathItem.Title -> PathItem.Title(it.en, it.fa)
                is PathItem.StationItem -> PathItem.StationItem(stations[it.station.name]!!, it.isPassthrough, it.lineNumber)
            }
        }

        assertEquals(expectedPath, actualPath)
    }

    @Test
    fun `find shortest path with direction when no path exists`() {
        val from = "Darvazeh Shemiran"
        val to = "Invalid Station"

        val actualPath = viewModel.findShortestPathWithDirection(from, to)

        assertEquals(emptyList<PathItem>(), actualPath)
    }
}
