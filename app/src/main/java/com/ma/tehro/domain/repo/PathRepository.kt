package com.ma.tehro.domain.repo

import com.ma.tehro.data.Station
import com.ma.tehro.domain.PathItem

interface PathRepository {
    suspend fun findShortestPathWithDirection(from: String, to: String): List<PathItem>
    fun getStations(): Map<String, Station>
}