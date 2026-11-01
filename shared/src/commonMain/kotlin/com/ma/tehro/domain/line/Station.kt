package com.ma.tehro.domain.line

import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.domain.common.GeoPoint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.round

@Serializable
data class Station(
    val name: String,
    val translations: Translations,
    val lines: List<Int> = emptyList(),
    @SerialName("longitude")
    private val _longitude: Double? = null,
    @SerialName("latitude")
    private val _latitude: Double? = null,
    val address: String? = null,
    val disabled: Boolean = false,
    val facilities: StationFacilities = StationFacilities(),
    val accessibility: StationAccessibility = StationAccessibility(),
    val safety: StationSafety = StationSafety(),
    val relations: List<String> = emptyList(),
    val positionsInLine: List<PositionInLine> = emptyList(),
    val nearbyBuses: List<NearbyBus> = emptyList(),
) : GeoPoint {
    override val latitude: Double
        get() = requireNotNull(_latitude) { "station $name has no latitude" }

    override val longitude: Double
        get() = requireNotNull(_longitude) { "station $name has no longitude" }
}

@Serializable
data class NearbyBus(
    val id: String,
    val name: String,
    @SerialName("distance_m")
    val distanceMeters: Double,
    val lines: List<NearbyBusLine> = emptyList(),
) {
    val distanceNumber: String
        get() = if (distanceMeters < 1000) {
            distanceMeters.toInt().toFarsiNumber()
        } else {
            val km = round((distanceMeters / 1000) * 10) / 10
            km.toString().toFarsiNumber()
        }

    val distanceUnit: String
        get() = if (distanceMeters < 1000) "متر" else "کیلومتر"

    val formattedDistanceFa: String
        get() = "$distanceNumber $distanceUnit"

    val formattedDistanceEn: String
        get() = if (distanceMeters < 1000) "${distanceMeters.toInt()} m" else "${round((distanceMeters / 1000) * 10) / 10} km"
}

@Serializable
data class NearbyBusLine(
    val line: Int,
    @SerialName("is_forward")
    val isForward: Boolean,
    val destination: String? = null,
) {
    val direction: String
        get() = if (isForward) "رفت" else "برگشت"

    val displayLabel: String
        get() = if (!destination.isNullOrBlank()) {
            "به سمت $destination"
        } else {
            ""
        }
}

@Serializable
data class StationFacilities(
    val wc: Boolean? = null,
    val coffeeShop: Boolean? = null,
    val groceryStore: Boolean? = null,
    val fastFood: Boolean? = null,
    val atm: Boolean? = null,
    val bicycleParking: Boolean? = null,
    val waterCooler: Boolean? = null,
    val waitingChair: Boolean? = null,
    val prayerRoom: Boolean? = null,
    val freeWifi: Boolean? = null,
)

@Serializable
data class StationAccessibility(
    val elevator: Boolean? = null,
    val blindPath: Boolean? = null,
    val cleanFood: Boolean? = null,
)

@Serializable
data class StationSafety(
    val fireSuppressionSystem: Boolean? = null,
    val fireExtinguisher: Boolean? = null,
    val metroPolice: Boolean? = null,
    val creditTicketSales: Boolean? = null,
    val camera: Boolean? = null,
    val trashCan: Boolean? = null,
    val smoking: Boolean? = null,
    val petsAllowed: Boolean? = null,
)

@Serializable
data class Translations(val fa: String)

@Serializable
data class PositionInLine(
    val position: Int,
    val line: Int
)