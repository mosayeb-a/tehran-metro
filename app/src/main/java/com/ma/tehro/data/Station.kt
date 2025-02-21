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
    val disabled: Boolean,
    val wc: Boolean? = null,
    val coffeeShop: Boolean? = null,
    val groceryStore: Boolean? = null,
    val fastFood: Boolean? = null,
    val atm: Boolean? = null,
    val relations: List<String> = emptyList(),
    val positionsInLine: List<PositionInLine> = emptyList(),
)

@Serializable
data class Translations(
    val fa: String
)

@Serializable
data class PositionInLine(
    val position: Int,
    val line: Int
)