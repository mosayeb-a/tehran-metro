package com.ma.tehro.data

enum class ScheduleType(val id: Int) {
    SATURDAY_TO_WEDNESDAY(0),
    THURSDAY(1),
    FRIDAY(2),
    ALL_DAY(3),
    SATURDAY_TO_THURSDAY(4),
    HOLIDAYS_AND_FRIDAY(5);

    companion object {
        fun fromScheduleKey(key: String): ScheduleType? {
            return key.last().digitToIntOrNull()?.let { id ->
                entries.find { it.id == id }
            }
        }
    }
}