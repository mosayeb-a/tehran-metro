package com.ma.tehro.domain.line

import com.ma.tehro.domain.common.GeoPoint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
) : GeoPoint {
    override val latitude: Double
        get() = requireNotNull(_latitude) { "station $name has no latitude" }

    override val longitude: Double
        get() = requireNotNull(_longitude) { "station $name has no longitude" }
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