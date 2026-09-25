package com.ma.tehro.domain.path.repository

import com.ma.tehro.domain.path.PathHistory
import kotlinx.coroutines.flow.Flow

interface PathHistoryRepository {
    fun getAll(limit: Int = 5): Flow<List<PathHistory>>
    suspend fun add(history: PathHistory, limit: Int = 5)
    suspend fun delete(history: PathHistory)
    suspend fun deleteAll()
}