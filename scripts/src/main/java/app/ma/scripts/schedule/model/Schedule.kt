package app.ma.scripts.schedule.model

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleOutput(
    val stations: MutableMap<String, MutableMap<String, List<Double>>> = mutableMapOf()
)

data class ScheduleConfig(
    val sheetIndex: Int,
    val name: String,
    val serialName: String,
    val firstRow: Int,
    val firstCol: Int
)