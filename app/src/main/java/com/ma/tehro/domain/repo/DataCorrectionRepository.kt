package com.ma.tehro.domain.repo

import com.ma.tehro.data.Station

interface DataCorrectionRepository {
    suspend fun submitStationCorrection(station: Station)
    suspend fun submitFeedback(message: String)
}
