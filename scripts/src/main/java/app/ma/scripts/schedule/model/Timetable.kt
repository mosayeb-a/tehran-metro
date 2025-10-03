package app.ma.scripts.schedule.model

import kotlinx.serialization.Serializable

@Serializable
data class Timetable(val timetable: List<Double> = emptyList())
