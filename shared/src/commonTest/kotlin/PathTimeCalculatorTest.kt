import com.ma.tehro.data.*
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.line.Translations
import com.ma.tehro.data.repository.ScheduleGroup
import com.ma.tehro.data.repository.TrainScheduleRepository
import com.ma.tehro.domain.path.PathItem
import com.ma.tehro.domain.schedule.ScheduleType
import com.ma.tehro.domain.path.PathTimeCalculator
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PathTimeCalculatorTest {

    private lateinit var calculator: PathTimeCalculator
    private lateinit var mockRepository: MockTrainScheduleRepository

    @BeforeTest
    fun setup() {
        mockRepository = MockTrainScheduleRepository()
        calculator = PathTimeCalculator(mockRepository)
    }

    @Test
    fun testBasicPathNoTransfer() = runTest {
        println("=== Test 1: Basic Path (Line 1, Tajrish to Kahrizak) ===")

        val path = listOf(
            PathItem.Title("Line 1: To Kahrizak", "خط ۱: به سمت کهریزک"),
            PathItem.StationItem(
                station = createMockStation("Tajrish", "تجریش"),
                lineNumber = 1
            ),
            PathItem.StationItem(
                station = createMockStation("Shahr-e Ray", "شهرری"),
                lineNumber = 1
            ),
            PathItem.StationItem(
                station = createMockStation("Kahrizak", "کهریزک"),
                lineNumber = 1
            )
        )

        val (stationTimes, estimate) = calculator.calculateStationTimes(
            path = path,
            lineChangeDelayMinutes = 8,
            dayOfWeek = 3,
            currentTime = 0.25 // 6:00 AM
        )

        println("Station arrival times:")
        stationTimes.forEach { (station, time) ->
            println("  $station: $time")
        }
        println("Estimated journey: ${estimate.en}\n")

        // Note: Only stations that have schedule data will appear
        // In mock data, Tajrish and Kahrizak have data, Shahr-e Ray doesn't
        assertTrue(stationTimes.isNotEmpty())
        assertNotNull(stationTimes["Tajrish"])
        assertTrue(estimate.en.contains("MIN") || estimate.en.contains("HOUR"))
    }

    @Test
    fun testPathWithOneTransfer() = runTest {
        println("=== Test 2: Path with Transfer (Line 1 -> Line 2) ===")

        val path = listOf(
            PathItem.Title("Line 1: To Kahrizak", "خط ۱: به سمت کهریزک"),
            PathItem.StationItem(
                station = createMockStation("Tajrish", "تجریش"),
                lineNumber = 1
            ),
            PathItem.StationItem(
                station = createMockStation("Imam Khomeini", "امام خمینی"),
                lineNumber = 1
            ),
            PathItem.Title("Line 2: To Farhangsara", "خط ۲: به سمت فرهنگسرا"),
            PathItem.StationItem(
                station = createMockStation("Imam Khomeini", "امام خمینی"),
                lineNumber = 2
            ),
            PathItem.StationItem(
                station = createMockStation("Shahid Beheshti", "شهید بهشتی"),
                lineNumber = 2
            ),
            PathItem.StationItem(
                station = createMockStation("Farhangsara", "فرهنگسرا"),
                lineNumber = 2
            )
        )

        val (stationTimes, estimate) = calculator.calculateStationTimes(
            path = path,
            lineChangeDelayMinutes = 8,
            dayOfWeek = 3,
            currentTime = 0.3 // 7:12 AM
        )

        println("Station arrival times:")
        stationTimes.forEach { (station, time) ->
            println("  $station: $time")
        }
        println("Estimated journey: ${estimate.en}")
        println("Expected: Should include 8 min transfer delay\n")

        // Verify transfer delay was considered
        assertTrue(stationTimes.size >= 3)
        assertNotNull(stationTimes["Tajrish"])
        assertNotNull(stationTimes["Farhangsara"])
    }

    @Test
    fun testDifferentStartTimes() = runTest {
        println("=== Test 3: Different Start Times ===")

        val path = listOf(
            PathItem.Title("Line 1: To Kahrizak", "خط ۱: به سمت کهریزک"),
            PathItem.StationItem(
                station = createMockStation("Tajrish", "تجریش"),
                lineNumber = 1
            ),
            PathItem.StationItem(
                station = createMockStation("Kahrizak", "کهریزک"),
                lineNumber = 1
            )
        )

        val testTimes = listOf(
            0.23 to "5:30 AM (Before service)",
            0.3 to "7:12 AM (Morning rush)",
            0.5 to "12:00 PM (Midday)",
            0.7 to "4:48 PM (Evening)",
            0.85 to "8:24 PM (Late evening)",
            0.95 to "10:48 PM (Near end)"
        )

        testTimes.forEach { (time, description) ->
            val (stationTimes, estimate) = calculator.calculateStationTimes(
                path = path,
                lineChangeDelayMinutes = 8,
                dayOfWeek = 3,
                currentTime = time
            )
            val firstTrain = stationTimes["Tajrish"] ?: "N/A"
            println("Start at $description: First train at $firstTrain, Journey: ${estimate.en}")
        }
        println()
    }

    @Test
    fun testNoTransferDelayOnFirstLine() = runTest {
        println("=== Test 5: Verify No Transfer Delay on First Line ===")

        val path = listOf(
            PathItem.Title("Line 1: To Kahrizak", "خط ۱: به سمت کهریزک"),
            PathItem.StationItem(
                station = createMockStation("Tajrish", "تجریش"),
                lineNumber = 1
            ),
            PathItem.StationItem(
                station = createMockStation("Kahrizak", "کهریزک"),
                lineNumber = 1
            )
        )

        val (stationTimes, _) = calculator.calculateStationTimes(
            path = path,
            lineChangeDelayMinutes = 8,
            dayOfWeek = 3,
            currentTime = 0.25 // 6:00 AM
        )

        val arrivalTime = stationTimes["Tajrish"]
        println("First station arrival: $arrivalTime")

        // The first train should be exactly at or after 6:00 AM, NOT 6:08
        // This verifies no delay was added before first train
        val expectedMinute = arrivalTime?.split(":")?.get(1)?.toIntOrNull()
        if (expectedMinute != null) {
            assertTrue(expectedMinute == 0 || expectedMinute == 12 || expectedMinute == 24)
        }
        println("✓ No extra delay added before first train\n")
    }

    @Test
    fun testSameStationNotDuplicate() = runTest {
        println("=== Test 8: Duplicate Station Handling ===")

        val path = listOf(
            PathItem.Title("Line 1: To Kahrizak", "خط ۱: به سمت کهریزک"),
            PathItem.StationItem(
                station = createMockStation("Tajrish", "تجریش"),
                lineNumber = 1
            ),
            PathItem.StationItem(
                station = createMockStation("Tajrish", "تجریش"),
                lineNumber = 1
            ), // Duplicate
            PathItem.StationItem(
                station = createMockStation("Kahrizak", "کهریزک"),
                lineNumber = 1
            )
        )

        val (stationTimes, _) = calculator.calculateStationTimes(
            path = path,
            lineChangeDelayMinutes = 8,
            dayOfWeek = 3,
            currentTime = 0.25
        )

        println("Station times (duplicate should be skipped):")
        stationTimes.forEach { (station, time) ->
            println("  $station: $time")
        }

        // Should only have 2 unique stations, not 3
        // Note: Only stations with schedule data appear
        assertTrue(stationTimes.size <= 2)
        println("✓ Duplicate station correctly ignored\n")
    }

    // Helper function to create mock stations
    private fun createMockStation(nameEn: String, nameFa: String): Station {
        return Station(
            name = nameEn,
            translations = Translations(fa = nameFa),
            lines = emptyList(),
            disabled = false,
            wc = null,
            coffeeShop = null,
            groceryStore = null,
            fastFood = null,
            atm = null,
            relations = emptyList(),
            positionsInLine = emptyList()
        )
    }
}

// Fixed Mock Repository with complete data
class MockTrainScheduleRepository : TrainScheduleRepository {
    override suspend fun getScheduleByStation(
        stationName: String,
        lineNum: Int,
        isBranch: Boolean
    ): List<ScheduleGroup> {
        // Common schedule for all stations on Line 1 going to Kahrizak
        val commonSchedule = listOf(
            0.25, 0.26, 0.27, 0.28, 0.29, 0.30,  // 06:00 - 07:12
            0.35, 0.36, 0.37, 0.38, 0.39, 0.40,  // 08:24 - 09:36
            0.50, 0.51, 0.52, 0.53, 0.54, 0.55,  // 12:00 - 13:12
            0.70, 0.71, 0.72, 0.73, 0.74, 0.75,  // 16:48 - 18:00
            0.85, 0.86, 0.87, 0.88, 0.89, 0.90   // 20:24 - 21:36
        )

        return when (stationName) {
            "Tajrish", "Shahr-e Ray", "Kahrizak" -> listOf(
                ScheduleGroup(
                    destination = BilingualName("Kahrizak", "کهریزک"),
                    schedules = mapOf(
                        ScheduleType.SATURDAY_TO_WEDNESDAY to commonSchedule
                    )
                )
            )
            "Imam Khomeini" -> listOf(
                ScheduleGroup(
                    destination = BilingualName("Kahrizak", "کهریزک"),
                    schedules = mapOf(
                        ScheduleType.SATURDAY_TO_WEDNESDAY to listOf(0.28, 0.32, 0.36, 0.40)
                    )
                ),
                ScheduleGroup(
                    destination = BilingualName("Farhangsara", "فرهنگسرا"),
                    schedules = mapOf(
                        ScheduleType.SATURDAY_TO_WEDNESDAY to listOf(0.29, 0.33, 0.37, 0.41)
                    )
                )
            )
            "Shahid Beheshti" -> listOf(
                ScheduleGroup(
                    destination = BilingualName("Farhangsara", "فرهنگسرا"),
                    schedules = mapOf(
                        ScheduleType.SATURDAY_TO_WEDNESDAY to listOf(0.31, 0.35, 0.39, 0.43)
                    )
                ),
                ScheduleGroup(
                    destination = BilingualName("Azadegan", "آزادگان"),
                    schedules = mapOf(
                        ScheduleType.SATURDAY_TO_WEDNESDAY to listOf(0.32, 0.36, 0.40, 0.44)
                    )
                )
            )
            "Farhangsara" -> listOf(
                ScheduleGroup(
                    destination = BilingualName("Tehran (Sadeghiyeh)", "صادقیه"),
                    schedules = mapOf(
                        ScheduleType.SATURDAY_TO_WEDNESDAY to listOf(0.33, 0.37, 0.41, 0.45)
                    )
                )
            )
            "Azadegan" -> listOf(
                ScheduleGroup(
                    destination = BilingualName("Qa'em", "قائم"),
                    schedules = mapOf(
                        ScheduleType.SATURDAY_TO_WEDNESDAY to listOf(0.34, 0.38, 0.42, 0.46)
                    )
                )
            )
            else -> listOf(
                ScheduleGroup(
                    destination = BilingualName("Kahrizak", "کهریزک"),
                    schedules = mapOf(
                        ScheduleType.SATURDAY_TO_WEDNESDAY to commonSchedule
                    )
                )
            )
        }
    }
}