package com.ma.tehro.domain

import kotlinx.serialization.Serializable

@Serializable
data class MapStationCoordinate(
    val x: Int,
    val y: Int
)