package com.ma.tehro.data.path.repository

import com.ma.tehro.data.path.source.local.PathHistoryLocalDataSource
import com.ma.tehro.data.path.toDomain
import com.ma.tehro.data.path.toEntity
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.path.PathHistory
import com.ma.tehro.domain.path.repository.PathHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PathHistoryRepositoryImpl(
    private val localDataSource: PathHistoryLocalDataSource,
    private val stations: Map<String, Station>,
) : PathHistoryRepository {

    override fun getAll(limit: Int): Flow<List<PathHistory>> =
        localDataSource.getAll(limit).map { entities ->
            entities.mapNotNull { entity ->
                val from = stations[entity.fromStationId] ?: return@mapNotNull null
                val to = stations[entity.toStationId] ?: return@mapNotNull null
                entity.toDomain(from, to)
            }
        }

    override suspend fun add(history: PathHistory, limit: Int) {
        localDataSource.insert(history.toEntity(), limit)
    }

    override suspend fun delete(history: PathHistory) {
        localDataSource.delete(history.toEntity())
    }

    override suspend fun deleteAll() {
        localDataSource.deleteAll()
    }
}