package com.ma.tehro.domain.schedule

import com.ma.tehro.domain.common.BilingualName

data class ScheduleGroup(
    val destination: BilingualName,
    val schedules: Map<ScheduleType, List<Double>>
)