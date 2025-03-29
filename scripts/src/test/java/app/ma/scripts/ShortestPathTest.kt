package app.ma.scripts

import com.ma.tehro.data.Station
import com.ma.tehro.data.repo.PathItem
import com.ma.tehro.data.repo.PathRepository
import com.ma.tehro.data.repo.PathRepositoryImpl
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse

class ShortestPathTest {

    /**
     * Note: The test cases are tightly coupled to the data in the `stations.json` file.
     * any changes to the file may cause the tests to fail.
     *
     * a potential solution is to avoid hardcoding the paths as static data. Instead,
     * we could dynamically traverse the stations until we reach an intersection point
     * or list only the intersecting stations.
     */

    private lateinit var repository: PathRepository
    private lateinit var stations: Map<String, Station>

    @Before
    fun setup() {
        stations = readJsonStationsAsText("stations_updated2")
        repository = PathRepositoryImpl(stations)
    }

    @Test
    fun `from Darvazeh Shemiran to Ayatollah Taleghani`() {
        val from = "Darvazeh Shemiran"
        val to = "Ayatollah Taleghani"
        val expectedPath = listOf("Darvazeh Shemiran", "Darvazeh Dolat", "Ayatollah Taleghani")

        assertShortestPath(from, to, expectedPath)
    }

    @Test
    fun `from Roudaki to Ostad Mo'in`() {
        val from = "Roudaki"
        val to = "Ostad Mo'in"
        val expectedPath = listOf(
            "Roudaki",
            "Shahid Navab-e Safavi",
            "Towhid",
            "Shademan",
            "Doctor Habibollah",
            "Ostad Mo'in"
        )

        assertShortestPath(from, to, expectedPath)
    }

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

    @Test
    fun `from Shoush to Meydan-e Enghelab-e Eslami`() {
        val from = "Shoush"
        val to = "Meydan-e Enghelab-e Eslami"
        val expectedPath = listOf(
            "Shoush",
            "Meydan-e Mohammadiyeh",
            "Khayyam",
            "Panzdah-e Khordad",
            "Imam Khomeini",
            "Sa'adi",
            "Darvazeh Dolat",
            "Ferdowsi",
            "Teatr-e Shahr",
            "Meydan-e Enghelab-e Eslami",
        )
        assertShortestPath(from, to, expectedPath)
    }

    @Test
    fun `from Mowlavi to Darvazeh Shemiran, disabled station shouldn't cause line change`() = runTest {
        val from = "Mowlavi"
        val to = "Darvazeh Shemiran"
        val result = repository.findShortestPathWithDirection(from, to)
        val actualStations = result
            .filterIsInstance<PathItem.StationItem>()
            .map { it.station.name }
            .toSet()
            .toList()

        println(actualStations)
        assertFalse { actualStations.contains("Shohada-ye Hefdah-e Shahrivar") }
    }

    private fun assertShortestPath(from: String, to: String, expectedStations: List<String>) = runBlocking {
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