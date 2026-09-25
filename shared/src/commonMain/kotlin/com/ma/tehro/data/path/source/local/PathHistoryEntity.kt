package com.ma.tehro.data.path.source.local

data class PathHistoryEntity(
    val fromStationId: String,
    val toStationId: String,
    val timestamp: Long,
)