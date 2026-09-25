package com.ma.tehro.domain.path

import androidx.compose.runtime.Immutable
import com.ma.tehro.domain.line.Station

@Immutable
data class PathHistory(
    val from: Station,
    val to: Station,
    val timestamp: Long,
) {
    val fromStationId: String get() = from.name
    val toStationId: String get() = to.name
}