package app.ma.scripts.schedule.model

data class XlsConfig(
    val fileName: String,
    val schedules: List<ScheduleConfig>
)