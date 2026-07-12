package app.ma.scripts.stations

import com.ma.tehro.domain.line.PositionInLine
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.line.StationAccessibility
import com.ma.tehro.domain.line.StationFacilities
import com.ma.tehro.domain.line.StationSafety
import com.ma.tehro.domain.line.Translations

@kotlinx.serialization.Serializable
data class StationFlat(
    val name: String,
    val translations: Translations,
    val lines: List<Int> = emptyList(),
    val longitude: Double? = null,
    val latitude: Double? = null,
    val address: String? = null,
    val disabled: Boolean = false,
    val wc: Boolean? = null,
    val coffeeShop: Boolean? = null,
    val groceryStore: Boolean? = null,
    val fastFood: Boolean? = null,
    val atm: Boolean? = null,
    val elevator: Boolean? = null,
    val bicycleParking: Boolean? = null,
    val waterCooler: Boolean? = null,
    val cleanFood: Boolean? = null,
    val blindPath: Boolean? = null,
    val fireSuppressionSystem: Boolean? = null,
    val fireExtinguisher: Boolean? = null,
    val metroPolice: Boolean? = null,
    val creditTicketSales: Boolean? = null,
    val waitingChair: Boolean? = null,
    val camera: Boolean? = null,
    val trashCan: Boolean? = null,
    val smoking: Boolean? = null,
    val petsAllowed: Boolean? = null,
    val freeWifi: Boolean? = null,
    val prayerRoom: Boolean? = null,
    val colors: List<String> = emptyList(),
    val relations: List<String> = emptyList(),
    val positionsInLine: List<PositionInLine> = emptyList(),
)

fun StationFlat.toStation(): Station {
    return Station(
        name = name,
        translations = translations,
        lines = lines,
        longitude = longitude,
        latitude = latitude,
        address = address,
        disabled = disabled,
        facilities = StationFacilities(
            wc = wc,
            coffeeShop = coffeeShop,
            groceryStore = groceryStore,
            fastFood = fastFood,
            atm = atm,
            bicycleParking = bicycleParking,
            waterCooler = waterCooler,
            waitingChair = waitingChair,
            prayerRoom = prayerRoom,
            freeWifi = freeWifi,
        ),
        accessibility = StationAccessibility(
            elevator = elevator,
            blindPath = blindPath,
            cleanFood = cleanFood,
        ),
        safety = StationSafety(
            fireSuppressionSystem = fireSuppressionSystem,
            fireExtinguisher = fireExtinguisher,
            metroPolice = metroPolice,
            creditTicketSales = creditTicketSales,
            camera = camera,
            trashCan = trashCan,
            smoking = smoking,
            petsAllowed = petsAllowed,
        ),
        relations = relations,
        positionsInLine = positionsInLine,
    )
}