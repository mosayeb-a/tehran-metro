package com.ma.tehro

import com.ma.tehro.common.readJsonStationsAsText
import com.ma.tehro.data.Station
import com.ma.tehro.ui.detail.repo.PathItem
import com.ma.tehro.ui.detail.repo.PathRepository
import com.ma.tehro.ui.detail.repo.PathRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ShortestPathTest {

    private lateinit var repository: PathRepository
    private lateinit var stations: Map<String, Station>

    @Before
    fun setup() {
        stations = readJsonStationsAsText("stations_updated")
        repository = PathRepositoryImpl(stations)
    }

    @Test
    fun `from Darvazeh Shemiran to Ayatollah Taleghani`() {
        val from = "Darvazeh Shemiran"
        val to = "Ayatollah Taleghani"
        val expectedPath = listOf("Darvazeh Shemiran", "Darvazeh Dolat", "Ayatollah Taleghani")

        assertShortestPath(from, to, expectedPath)
    }
//
//    @Test
//    fun `from Roudaki to Ostad Mo'in`() {
//        val from = "Roudaki"
//        val to = "Ostad Mo'in"
//        val expectedPath = listOf(
//            "Roudaki",
//            "Shahid Navab-e Safavi",
//            "Towhid",
//            "Shademan",
//            "Doctor Habibollah",
//            "Ostad Mo'in"
//        )
//
//        assertShortestPath(from, to, expectedPath)
//    }

    @Test
    fun `from Kahrizak to Tajrish`() {
        val from = "Kahrizak"
        val to = "Tajrish"
        val expectedPath = listOf(
            "Kahrizak",
            "Holy Shrine of Imam Khomeini",
            "Shahed - BagherShahr",
            "Palayeshgah",
            "Shahr-e Rey",
            "Javanmard-e Ghassab",
            "Aliabad",
            "Shahid Bokharaei",
            "Payaneh Jonoub(Jonoub Terminal)",
            "Shoush",
            "Meydan-e Mohammadiyeh",
            "Khayyam",
            "Panzdah-e Khordad",
            "Imam Khomeini",
            "Sa'adi",
            "Darvazeh Dolat",
            "Ayatollah Taleghani",
            "Shohada-ye Haftom-e Tir",
            "Shahid Mofattah",
            "Shahid Beheshti",
            "Mosalla-ye Imam Khomeini",
            "Shahid Hemmat",
            "Shahid Haghani",
            "Mirdamad",
            "Doctor Shariati",
            "Qolhak",
            "Shahid Sadr",
            "Gheytariyeh",
            "Tajrish"
        )

        assertShortestPath(from, to, expectedPath)
    }

    @Test
    fun `from Bahar Shiraz (Khanevadeh Hospital) to Allameh Jafari`() {
        // it need a refacotr
        val from = "Bahar Shiraz (Khanevadeh Hospital)"
        val to = "Allameh Jafari"
        val expectedPath = listOf(
            "Bahar Shiraz (Khanevadeh Hospital)",
            "Shohada-ye Haftom-e Tir",
            "Shahid Nejatollahi", // disabled
            "Meydan-e Hazrat Vali Asr",
            "Teatr-e Shahr",
            "Meydan-e Enghelab-e Eslami",
            "Towhid",
            "Shademan",
            "Doctor Habibollah",
            "Ostad Mo'in",
            "Meydan-e Azadi",
            "Bimeh",
            "Shahrak-e Ekbatan",
            "Eram-e Sabz",
            "Allameh Jafari"
        )
        assertShortestPath(from, to, expectedPath)
    }

//    @Test
//    fun `from Javadiyeh to Ayatollah Taleghani`() {
//        val from = "Javadiyeh"
//        val to = "Ayatollah Taleghani"
//        val expectedPath = listOf(
//            "Roudaki",
//            "Shahid Navab-e Safavi",
//            "Towhid",
//            "Shademan",
//            "Doctor Habibollah",
//            "Ostad Mo'in"
//        )
//        val actualPath = viewModel.findShortestPathWithDirection(from, to)
//            .filterIsInstance<PathItem.StationItem>()
//            .map { it.station.name }
//            .toSet()
//            .toList()
//        println(actualPath)
////        assertShortestPath(from, to, expectedPath)
//    }

    private fun assertShortestPath(from: String, to: String, expectedStations: List<String>) {
        val result = repository.findShortestPathWithDirection(from, to)
        val actualStations = result
            .filterIsInstance<PathItem.StationItem>()
            .map { it.station.name }
            .toSet()
            .toList()

        assertEquals(
            "Path from $from to $to should match expected stations",
            expectedStations,
            actualStations
        )
    }
}