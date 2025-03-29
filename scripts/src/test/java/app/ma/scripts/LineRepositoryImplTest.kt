package app.ma.scripts

import com.ma.tehro.data.Station
import com.ma.tehro.data.repo.LineRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// should run the test every time a station added
class LineRepositoryImplTest {
    private lateinit var lineRepository: LineRepositoryImpl
    private lateinit var stations: Map<String, Station>

    @Before
    fun setup() {
        stations = readJsonStationsAsText("stations")
        lineRepository = LineRepositoryImpl(stations)
    }

    @Test
    fun `test line 1 main branch order`() {
        val expected = listOf(
            "Tajrish",
            "Gheytariyeh",
            "Shahid Sadr",
            "Qolhak",
            "Doctor Shariati",
            "Mirdamad",
            "Shahid Haghani",
            "Shahid Hemmat",
            "Mosalla-ye Imam Khomeini",
            "Shahid Beheshti",
            "Shahid Mofattah",
            "Shohada-ye Haftom-e Tir",
            "Ayatollah Taleghani",
            "Darvazeh Dolat",
            "Sa'adi",
            "Imam Khomeini",
            "Panzdah-e Khordad",
            "Khayyam",
            "Meydan-e Mohammadiyeh",
            "Shoush",
            "Payaneh Jonoub(Jonoub Terminal)",
            "Shahid Bokharaei",
            "Aliabad",
            "Javanmard-e Ghassab",
            "Shahr-e Rey",
            "Palayeshgah",
            "Shahed - BagherShahr",
            "Holy Shrine of Imam Khomeini",
            "Kahrizak"
        )

        val actual = lineRepository.getOrderedStationsByLine(
            line = 1,
            useBranch = false
        ).map { it.name }

        assertEquals(expected, actual)
    }

    @Test
    fun `test line 1  branch order`() {
        val expected = listOf(
            "Tajrish",
            "Gheytariyeh",
            "Shahid Sadr",
            "Qolhak",
            "Doctor Shariati",
            "Mirdamad",
            "Shahid Haghani",
            "Shahid Hemmat",
            "Mosalla-ye Imam Khomeini",
            "Shahid Beheshti",
            "Shahid Mofattah",
            "Shohada-ye Haftom-e Tir",
            "Ayatollah Taleghani",
            "Darvazeh Dolat",
            "Sa'adi",
            "Imam Khomeini",
            "Panzdah-e Khordad",
            "Khayyam",
            "Meydan-e Mohammadiyeh",
            "Shoush",
            "Payaneh Jonoub(Jonoub Terminal)",
            "Shahid Bokharaei",
            "Aliabad",
            "Javanmard-e Ghassab",
            "Shahr-e Rey",
            "Palayeshgah",
            "Shahed - BagherShahr",
            "Namayeshgah-e Shahr-e Aftab",
            "Vavan",
            "Emam Khomeini Airport",
            "Shahr-e Parand"
        )

        val actual = lineRepository.getOrderedStationsByLine(
            line = 1,
            useBranch = true
        ).map { it.name }

        assertEquals(expected, actual)
    }

    @Test
    fun `test line 4 main branch order`() {
        val expected = listOf(
            "Shahid Kolahdooz",
            "Niroohavaii",
            "Nabard",
            "Piroozi",
            "Ebn-e Sina",
            "Meydan-e Shohada",
            "Darvazeh Shemiran",
            "Darvazeh Dolat",
            "Ferdowsi",
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

        val actual = lineRepository.getOrderedStationsByLine(
            line = 4,
            useBranch = false
        ).map { it.name }

        assertEquals(expected, actual)
    }

    @Test
    fun `test line 4  branch order`() {
        val expected = listOf(
            "Shahid Kolahdooz",
            "Niroohavaii",
            "Nabard",
            "Piroozi",
            "Ebn-e Sina",
            "Meydan-e Shohada",
            "Darvazeh Shemiran",
            "Darvazeh Dolat",
            "Ferdowsi",
            "Teatr-e Shahr",
            "Meydan-e Enghelab-e Eslami",
            "Towhid",
            "Shademan",
            "Doctor Habibollah",
            "Ostad Mo'in",
            "Meydan-e Azadi",
            "Bimeh",
            "Mehrabad Airport Terminal 1&2",
            "Mehrabad Airport Terminal 4&6"
        )

        val actual = lineRepository.getOrderedStationsByLine(
            line = 4,
            useBranch = true
        ).map { it.name }

        assertEquals(expected, actual)
    }

    @Test
    fun `test line 5 main branch order`() {
        val expected = listOf(
            "Tehran (Sadeghiyeh)",
            "Eram-e Sabz",
            "Varzeshgah-e Azadi",
            "Chitgar",
            "Iran Khodro",
            "Vardavard",
            "Garmdareh",
            "Atmosphere",
            "Karaj",
            "Mohammadshahr",
            "Golshahr"
        )

        val actual = lineRepository.getOrderedStationsByLine(
            line = 5,
            useBranch = false
        ).map { it.name }

        assertEquals(expected, actual)
    }

    @Test
    fun `test line 5  branch order`() {
        val expected = listOf(
            "Tehran (Sadeghiyeh)",
            "Eram-e Sabz",
            "Varzeshgah-e Azadi",
            "Chitgar",
            "Iran Khodro",
            "Vardavard",
            "Garmdareh",
            "Atmosphere",
            "Karaj",
            "Mohammadshahr",
            "Golshahr",
            "Shahid Sepahbod Qasem Soleimani"
        )

        val actual = lineRepository.getOrderedStationsByLine(
            line = 5,
            useBranch = true
        ).map { it.name }

        assertEquals(expected, actual)
    }
}