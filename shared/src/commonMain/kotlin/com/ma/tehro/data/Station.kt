package com.ma.tehro.data

import kotlinx.serialization.Serializable

@Serializable
data class Station(
    val name: String,
    val translations: Translations,
    val lines: List<Int> = emptyList(),
    val longitude: String? = null,
    val latitude: String? = null,
    val address: String? = null,
    val disabled: Boolean = false,
    val facilities: StationFacilities = StationFacilities(),
    val accessibility: StationAccessibility = StationAccessibility(),
    val safety: StationSafety = StationSafety(),
    val relations: List<String> = emptyList(),
    val positionsInLine: List<PositionInLine> = emptyList(),
)

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