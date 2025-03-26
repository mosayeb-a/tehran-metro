package com.ma.tehro.data.repo

import android.content.Context
import com.ma.tehro.R
import com.ma.tehro.common.LineEndpoints
import com.ma.tehro.data.ScheduleType
import com.ma.tehro.data.StationName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

interface TrainScheduleRepository {
    suspend fun getScheduleByStation(
        stationName: String,
        lineNum: Int,
        isBranch: Boolean
    ): List<GroupedScheduleInfo>
}

data class GroupedScheduleInfo(
    val destination: StationName,
    val schedules: Map<ScheduleType, List<Double>>
)

class TrainScheduleRepositoryImpl @Inject constructor(
    private val context: Context,
    private val json: Json
) : TrainScheduleRepository {

    private val scheduleCache =
        mutableMapOf<Int, Map<String, Map<String, Map<String, Map<String, List<Double>>>>>>()

    private val scheduleResources = mapOf(
        1 to R.raw.train_schedule_1,
        2 to R.raw.train_schedule_2,
        3 to R.raw.train_schedule_3,
        4 to R.raw.train_schedule_4,
        5 to R.raw.train_schedule_5,
        6 to R.raw.train_schedule_6,
        7 to R.raw.train_schedule_7,
    )

    override suspend fun getScheduleByStation(
        stationName: String,
        lineNum: Int,
        isBranch: Boolean,
    ): List<GroupedScheduleInfo> {
        val schedule = getLineSchedule(lineNum)
        val mainPathSchedule = schedule["1"]?.get(stationName)
        val branchPathSchedule = schedule["2"]?.get(stationName)

        val (scheduleData, useBranchEndpoints) = when {
            isBranch && branchPathSchedule != null -> branchPathSchedule to true
            isBranch && mainPathSchedule != null -> mainPathSchedule to false
            !isBranch && mainPathSchedule != null -> mainPathSchedule to false
            !isBranch && branchPathSchedule != null -> branchPathSchedule to true
            else -> return emptyList()
        }
        val endpointsEn = LineEndpoints.getEn(lineNum, useBranchEndpoints)
        val endpointsFa = LineEndpoints.getFa(lineNum, useBranchEndpoints)

        if (endpointsEn == null || endpointsFa == null) { return emptyList() }

        return scheduleData
            .map { (scheduleKey, schedule) ->
                val scheduleType = ScheduleType.fromScheduleKey(scheduleKey)
                val towardsStation = scheduleKey.substringBefore(scheduleType?.id?.toString() ?: "")
                val (validEn, validFa) = when (towardsStation) {
                    endpointsEn.first -> endpointsEn.first to endpointsFa.first
                    endpointsEn.second -> endpointsEn.second to endpointsFa.second
                    else -> endpointsEn.first to endpointsFa.first
                }

                Triple(
                    StationName(validEn, validFa),
                    scheduleType,
                    schedule["timetable"] ?: emptyList()
                )
            }
            .groupBy { it.first }
            .map { (destination, schedules) ->
                GroupedScheduleInfo(
                    destination = destination,
                    schedules = schedules.mapNotNull { (_, type, times) ->
                        type?.let { it to times }
                    }.toMap()
                )
            }
    }

    private suspend fun getLineSchedule(lineNum: Int): Map<String, Map<String, Map<String, Map<String, List<Double>>>>> {
        return scheduleCache[lineNum] ?: loadScheduleInBackground(lineNum)
    }

    private suspend fun loadScheduleInBackground(lineNum: Int): Map<String, Map<String, Map<String, Map<String, List<Double>>>>> {
        return withContext(Dispatchers.IO) {
            val inputStream = context.resources.openRawResource(scheduleResources[lineNum]!!)
            val schedule =
                json.decodeFromString<Map<String, Map<String, Map<String, Map<String, List<Double>>>>>>(
                    inputStream.bufferedReader().use { it.readText() }
                )
            scheduleCache[lineNum] = schedule
            schedule
        }
    }
}