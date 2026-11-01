package com.ma.tehro

import com.ma.tehro.domain.line.NearbyBus
import com.ma.tehro.domain.line.Station
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StationNearbyBusesTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun testNearbyBusDeserialization() {
        val sampleJson = """
        {
          "Tajrish": {
            "name": "Tajrish",
            "translations": {
              "fa": "تجریش"
            },
            "lines": [1],
            "longitude": 51.433487,
            "latitude": 35.804649,
            "nearbyBuses": [
              {
                "id": "1471",
                "name": "مترو تجریش",
                "distance_m": 53.3,
                "lines": [
                  {
                    "line": 218,
                    "is_forward": true
                  },
                  {
                    "line": 220,
                    "is_forward": false
                  }
                ]
              }
            ]
          }
        }
        """.trimIndent()

        val stations = json.decodeFromString<Map<String, Station>>(sampleJson)
        val tajrish = stations["Tajrish"]
        requireNotNull(tajrish)

        assertEquals("Tajrish", tajrish.name)
        assertEquals("تجریش", tajrish.translations.fa)
        assertEquals(1, tajrish.nearbyBuses.size)

        val bus = tajrish.nearbyBuses.first()
        assertEquals("1471", bus.id)
        assertEquals("مترو تجریش", bus.name)
        assertEquals(53.3, bus.distanceMeters)
        assertEquals(2, bus.lines.size)

        val line1 = bus.lines[0]
        assertEquals(218, line1.line)
        assertTrue(line1.isForward)
        assertEquals("رفت", line1.direction)

        val line2 = bus.lines[1]
        assertEquals(220, line2.line)
        assertFalse(line2.isForward)
        assertEquals("برگشت", line2.direction)
    }

    @Test
    fun testStationWithoutNearbyBusesDefaultsToEmptyList() {
        val sampleJson = """
        {
          "Gheytariyeh": {
            "name": "Gheytariyeh",
            "translations": {
              "fa": "قیطریه"
            },
            "lines": [1],
            "longitude": 51.435,
            "latitude": 35.793
          }
        }
        """.trimIndent()

        val stations = json.decodeFromString<Map<String, Station>>(sampleJson)
        val gheytariyeh = stations["Gheytariyeh"]
        requireNotNull(gheytariyeh)
        assertTrue(gheytariyeh.nearbyBuses.isEmpty())
    }

    @Test
    fun testNearbyBusDistanceFormatting() {
        val busClose = NearbyBus(
            id = "1",
            name = "ایستگاه ۱",
            distanceMeters = 53.4,
            lines = emptyList()
        )
        assertEquals("۵۳ متر", busClose.formattedDistanceFa)
        assertEquals("53 m", busClose.formattedDistanceEn)

        val busFar = NearbyBus(
            id = "2",
            name = "ایستگاه ۲",
            distanceMeters = 1450.0,
            lines = emptyList()
        )
        assertEquals("۱.۵ کیلومتر", busFar.formattedDistanceFa)
        assertEquals("1.5 km", busFar.formattedDistanceEn)
    }
}
