package com.ma.tehro.data.path.source.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import com.ma.thero.db.TehroDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PathHistoryLocalDataSourceImpl(
    driver: SqlDriver,
) : PathHistoryLocalDataSource {

    private val db = TehroDatabase(driver)

    override fun getAll(limit: Int): Flow<List<PathHistoryEntity>> =
        db.pathHistoryQueries
            .getAll(limit.toLong())
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows ->
                rows.map { row ->
                    PathHistoryEntity(
                        fromStationId = row.from_station_id,
                        toStationId = row.to_station_id,
                        timestamp = row.timestamp,
                    )
                }
            }

    override suspend fun insert(
        history: PathHistoryEntity,
        limit: Int,
    ): Result<Unit> = runCatching {
        db.pathHistoryQueries.insertOrReplace(
            from_station_id = history.fromStationId,
            to_station_id = history.toStationId,
            timestamp = history.timestamp,
        )
        db.pathHistoryQueries.trim(limit.toLong())
    }

    override suspend fun delete(history: PathHistoryEntity): Result<Unit> =
        runCatching {
            db.pathHistoryQueries.delete(
                from_station_id = history.fromStationId,
                to_station_id = history.toStationId,
            )
        }

    override suspend fun deleteAll(): Result<Unit> =
        runCatching {
            db.pathHistoryQueries.deleteAll()
        }
}
