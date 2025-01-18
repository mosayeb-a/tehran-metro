    package com.ma.tehro

    import com.ma.tehro.common.readJsonStationsAsText
    import com.ma.tehro.data.Station
    import com.ma.tehro.ui.shortestpath.PathItem
    import com.ma.tehro.ui.shortestpath.ShortestPathViewModel
    import org.junit.Assert.assertEquals
    import org.junit.Test

    class ShortestPathViewModelTest {
        private var viewModel: ShortestPathViewModel
        private var stations: Map<String, Station> = readJsonStationsAsText("stations_updated")

        init {
            viewModel = ShortestPathViewModel(stations)
        }

        @Test
        fun `find shortest path with direction from Darvazeh Shemiran to Ayatollah Taleghani`() {
            val from = "Darvazeh Shemiran"
            val to = "Ayatollah Taleghani"

            val actualPath = viewModel.findShortestPathWithDirection(from, to)

            val expectedPath = actualPath.map {
                when (it) {
                    is PathItem.Title -> PathItem.Title(it.en)
                    is PathItem.StationItem -> PathItem.StationItem(stations[it.station.name]!!)
                }
            }

            assertEquals(expectedPath, actualPath)
        }

        @Test
        fun `find shortest path with direction when no path exists`() {
            val from = "Darvazeh Shemiran"
            val to = "Invalid Station"

            val expectedPath = emptyList<PathItem>()
            val actualPath = viewModel.findShortestPathWithDirection(from, to)
            assertEquals(expectedPath, actualPath)
        }
    }
