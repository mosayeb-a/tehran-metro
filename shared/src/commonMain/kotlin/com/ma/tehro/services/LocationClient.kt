package com.ma.tehro.services

import kotlinx.coroutines.flow.Flow

interface LocationClient {
    suspend fun getCurrentLocation(): PlatformLocation
    suspend fun observeLocationUpdates(interval: Long): Flow<PlatformLocation>
}