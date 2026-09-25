package com.ma.tehro.data.path.source.local

import kotlinx.coroutines.flow.Flow

interface PathHistoryLocalDataSource {
    fun getAll(limit: Int): Flow<List<PathHistoryEntity>>
    suspend fun insert(history: PathHistoryEntity, limit: Int): Result<Unit>
    suspend fun delete(history: PathHistoryEntity): Result<Unit>
    suspend fun deleteAll(): Result<Unit>
}
