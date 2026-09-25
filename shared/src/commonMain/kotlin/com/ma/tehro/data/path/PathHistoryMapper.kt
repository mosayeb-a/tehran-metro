package com.ma.tehro.data.path

import com.ma.tehro.data.path.source.local.PathHistoryEntity
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.path.PathHistory

fun PathHistoryEntity.toDomain(from: Station, to: Station): PathHistory =
    PathHistory(
        from = from,
        to = to,
        timestamp = timestamp,
    )

fun PathHistory.toEntity() = PathHistoryEntity(
    fromStationId = fromStationId,
    toStationId = toStationId,
    timestamp = timestamp,
)
