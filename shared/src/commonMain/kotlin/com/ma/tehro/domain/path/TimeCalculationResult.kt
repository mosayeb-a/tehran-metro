package com.ma.tehro.domain.path

import com.ma.tehro.domain.common.BilingualName

/**
 * Represents the result of calculating train arrival times for a path.
 *
 * @property stationTimes Map of station names to their arrival times (formatted as HH:MM)
 * @property estimatedTime Total estimated journey time in hours and minutes (bilingual)
 * @property warning Optional warning message in shown when the search time
 *   is after the last train of the day. In this case, times are calculated using the next day's schedule.
 *
 */
data class TimeCalculationResult(
    val stationTimes: Map<String, String>,
    val estimatedTime: BilingualName,
    val warning: String? = null
)