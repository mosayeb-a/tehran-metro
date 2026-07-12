package app.ma.scripts.stations

import app.ma.scripts.common.readJsonStationsAsText
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.line.StationAccessibility
import com.ma.tehro.domain.line.StationFacilities
import com.ma.tehro.domain.line.StationSafety
import kotlinx.serialization.json.Json
import java.io.File

fun main() {
    val oldStations: MutableMap<String, Station> = readJsonStationsAsText("stations")
    val newStationsFlat: MutableMap<String, StationFlat> = readJsonStationsAsText("stations_0.1.0")

    val updatedStations = mutableMapOf<String, Station>()
    val notFoundStations = mutableListOf<String>()

    oldStations.forEach { (stationKey, oldStation) ->
        val newStationFlat = newStationsFlat[stationKey]

        if (newStationFlat == null) {
            notFoundStations.add(stationKey)
            updatedStations[stationKey] = oldStation
        } else {
            val newStation = newStationFlat.toStation()

            val updatedStation = Station(
                name = oldStation.name,
                translations = oldStation.translations,
                lines = oldStation.lines,
                longitude = updateIfNotNull(oldStation.longitude, newStation.longitude),
                latitude = updateIfNotNull(oldStation.latitude, newStation.latitude),
                address = updateIfNotNull(oldStation.address, newStation.address),
                disabled = oldStation.disabled,
                facilities = StationFacilities(
                    wc = updateIfTrue(oldStation.facilities.wc, newStation.facilities.wc),
                    coffeeShop = updateIfTrue(
                        oldStation.facilities.coffeeShop,
                        newStation.facilities.coffeeShop
                    ),
                    groceryStore = updateIfTrue(
                        oldStation.facilities.groceryStore,
                        newStation.facilities.groceryStore
                    ),
                    fastFood = updateIfTrue(
                        oldStation.facilities.fastFood,
                        newStation.facilities.fastFood
                    ),
                    atm = updateIfTrue(oldStation.facilities.atm, newStation.facilities.atm),
                    bicycleParking = updateIfTrue(
                        oldStation.facilities.bicycleParking,
                        newStation.facilities.bicycleParking
                    ),
                    waterCooler = updateIfNotNull(
                        oldStation.facilities.waterCooler,
                        newStation.facilities.waterCooler
                    ),
                    waitingChair = updateIfTrue(
                        oldStation.facilities.waitingChair,
                        newStation.facilities.waitingChair
                    ),
                    prayerRoom = updateIfTrue(
                        oldStation.facilities.prayerRoom,
                        newStation.facilities.prayerRoom
                    ),
                    freeWifi = updateIfTrue(
                        oldStation.facilities.freeWifi,
                        newStation.facilities.freeWifi
                    ),
                ),
                accessibility = StationAccessibility(
                    elevator = updateIfTrue(
                        oldStation.accessibility.elevator,
                        newStation.accessibility.elevator
                    ),
                    blindPath = updateIfTrue(
                        oldStation.accessibility.blindPath,
                        newStation.accessibility.blindPath
                    ),
                    cleanFood = updateIfTrue(
                        oldStation.accessibility.cleanFood,
                        newStation.accessibility.cleanFood
                    ),
                ),
                safety = StationSafety(
                    fireSuppressionSystem = updateIfTrue(
                        oldStation.safety.fireSuppressionSystem,
                        newStation.safety.fireSuppressionSystem
                    ),
                    fireExtinguisher = updateIfTrue(
                        oldStation.safety.fireExtinguisher,
                        newStation.safety.fireExtinguisher
                    ),
                    metroPolice = updateIfTrue(
                        oldStation.safety.metroPolice,
                        newStation.safety.metroPolice
                    ),
                    creditTicketSales = updateIfTrue(
                        oldStation.safety.creditTicketSales,
                        newStation.safety.creditTicketSales
                    ),
                    camera = updateIfTrue(oldStation.safety.camera, newStation.safety.camera),
                    trashCan = updateIfTrue(oldStation.safety.trashCan, newStation.safety.trashCan),
                    smoking = updateIfTrue(oldStation.safety.smoking, newStation.safety.smoking),
                    petsAllowed = updateIfTrue(
                        oldStation.safety.petsAllowed,
                        newStation.safety.petsAllowed
                    ),
                ),
                relations = oldStation.relations,
                positionsInLine = oldStation.positionsInLine,
            )
            updatedStations[stationKey] = updatedStation
        }
    }

    if (notFoundStations.isNotEmpty()) {
        println("stations not found in version 0.1.0:")
        notFoundStations.forEach { println("  - $it") }
        println()
    }

    println("statistics:")
    println("total stations: ${updatedStations.size}")
    println()

    printStats("facilities", updatedStations.values.toList()) { station ->
        listOf(
            "wc" to station.facilities.wc,
            "coffeeShop" to station.facilities.coffeeShop,
            "groceryStore" to station.facilities.groceryStore,
            "fastFood" to station.facilities.fastFood,
            "atm" to station.facilities.atm,
            "bicycleParking" to station.facilities.bicycleParking,
            "waterCooler" to station.facilities.waterCooler,
            "waitingChair" to station.facilities.waitingChair,
            "prayerRoom" to station.facilities.prayerRoom,
            "freeWifi" to station.facilities.freeWifi,
        )
    }

    printStats("accessibility", updatedStations.values.toList()) { station ->
        listOf(
            "elevator" to station.accessibility.elevator,
            "blindPath" to station.accessibility.blindPath,
            "cleanFood" to station.accessibility.cleanFood,
        )
    }

    printStats("safety", updatedStations.values.toList()) { station ->
        listOf(
            "fireSuppressionSystem" to station.safety.fireSuppressionSystem,
            "fireExtinguisher" to station.safety.fireExtinguisher,
            "metroPolice" to station.safety.metroPolice,
            "creditTicketSales" to station.safety.creditTicketSales,
            "camera" to station.safety.camera,
            "trashCan" to station.safety.trashCan,
            "smoking" to station.safety.smoking,
            "petsAllowed" to station.safety.petsAllowed,
        )
    }

    println()
    val json = Json { prettyPrint = true; encodeDefaults = true }
    val jsonString = json.encodeToString(updatedStations)

    val currentDir = System.getProperty("user.dir")
    val sourceResourcesPath = "$currentDir/scripts/src/main/resources/stations_updated.json"
    val outputFile = File(sourceResourcesPath)
    outputFile.parentFile.mkdirs()
    outputFile.writeText(jsonString)

    println("updated stations saved to: ${outputFile.absolutePath}")
    println("total: ${updatedStations.size} | updated: ${oldStations.size - notFoundStations.size} | not found: ${notFoundStations.size}")
}

fun printStats(
    title: String,
    stations: List<Station>,
    getFields: (Station) -> List<Pair<String, Boolean?>>
) {
    println("$title:")
    val counts = mutableMapOf<String, Int>()

    stations.forEach { station ->
        getFields(station).forEach { (fieldName, value) ->
            if (value == true) {
                counts[fieldName] = (counts[fieldName] ?: 0) + 1
            }
        }
    }

    counts.toSortedMap().forEach { (fieldName, count) ->
        println("  $fieldName: $count")
    }
}

fun updateIfNotNull(oldValue: Boolean?, newValue: Boolean?): Boolean? {
    return newValue ?: oldValue
}

fun updateIfNotNull(oldValue: String?, newValue: String?): String? {
    return newValue ?: oldValue
}

fun updateIfNotNull(oldValue: Double?, newValue: Double?): Double? {
    return newValue ?: oldValue
}

fun updateIfTrue(oldValue: Boolean?, newValue: Boolean?): Boolean? {
    return when {
        newValue == true -> true
        oldValue == true -> true
        else -> false
    }
}