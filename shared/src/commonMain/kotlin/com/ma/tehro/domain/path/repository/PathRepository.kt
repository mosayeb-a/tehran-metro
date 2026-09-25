package com.ma.tehro.domain.path.repository

import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.path.PathStep

interface PathRepository {
    suspend fun findShortestPath(from: String, to: String): List<PathStep>
    fun getStations(): Map<String, Station>
}