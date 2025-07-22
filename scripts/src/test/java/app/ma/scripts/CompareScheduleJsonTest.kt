package app.ma.scripts

import app.ma.scripts.schedule.ScheduleOutput
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CompareScheduleJsonTest {

    private val jsonFile1 = File(BASE_RAW_PATH + "test.json")
    private val existingJson = jsonFile1.readText()
    private val existingData = Json.decodeFromString<ScheduleOutput>(existingJson)

    private val jsonFile2 = File(BASE_RAW_PATH + "testxls.json")
    private val existingJson2 = jsonFile2.readText()
    private val existingData2 = Json.decodeFromString<ScheduleOutput>(existingJson2)

    @Test
    fun testIsSizeSame() {
        assertEquals("station sizes are not the same", existingData.stations.size, existingData2.stations.size)
    }

    @Test
    fun testIsStationsTheSame() {
        assertTrue("stations are not the same", existingData.stations.keys == existingData2.stations.keys)
    }

    @Test
    fun testIsTimetableOfEachStationTheSame() {
        existingData.stations.forEach { (station, schedule) ->
            val schedule2 = existingData2.stations[station]
            assertNotNull("Station $station not found in the second dataset", schedule2)
            assertEquals("normal0 timetable for $station does not match", schedule.normal0.timetable, schedule2?.normal0?.timetable)
            assertEquals("thursday1  timetable for $station does not match", schedule.thursday1.timetable, schedule2?.thursday1?.timetable)
            assertEquals("friday2 timetable for $station does not match", schedule.friday2.timetable, schedule2?.friday2?.timetable)
            assertEquals("normal3 timetable for $station does not match", schedule.normal3.timetable, schedule2?.normal3?.timetable)
            assertEquals("thursday4 timetable for $station does not match", schedule.thursday4.timetable, schedule2?.thursday4?.timetable)
            assertEquals("friday5 timetable for $station does not match", schedule.friday6.timetable, schedule2?.friday6?.timetable)
        }
    }
}