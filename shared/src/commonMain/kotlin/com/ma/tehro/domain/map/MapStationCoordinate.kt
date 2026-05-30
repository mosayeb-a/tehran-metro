package com.ma.tehro.domain.map

import kotlinx.serialization.Serializable

@Serializable
data class MapStationCoordinate(
    val x: Int,
    val y: Int
)

sealed class PathPoint {
    data class Real(val name: String) : PathPoint()
    data class Fake(val x: Int, val y: Int) : PathPoint()
}