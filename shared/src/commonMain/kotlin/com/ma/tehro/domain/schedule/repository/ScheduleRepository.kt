package com.ma.tehro.domain.schedule.repository

import com.ma.tehro.domain.schedule.ScheduleGroup

interface ScheduleRepository {
    suspend fun getByStation(
        stationName: String,
        lineNum: Int,
        isBranch: Boolean
    ): List<ScheduleGroup>
}