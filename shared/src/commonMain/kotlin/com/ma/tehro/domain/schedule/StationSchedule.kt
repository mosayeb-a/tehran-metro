package com.ma.tehro.domain.schedule

import com.ma.tehro.domain.common.BilingualName

data class StationSchedule(
    val destination: BilingualName,
    val timetable: Map<ScheduleType, List<Double>>
)