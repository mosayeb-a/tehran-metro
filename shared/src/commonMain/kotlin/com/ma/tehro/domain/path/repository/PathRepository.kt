package com.ma.tehro.domain.path.repository

import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.path.PathItem

interface PathRepository {
    suspend fun findShortestPathWithDirection(from: String, to: String): List<PathItem>
    fun getStations(): Map<String, Station>
}