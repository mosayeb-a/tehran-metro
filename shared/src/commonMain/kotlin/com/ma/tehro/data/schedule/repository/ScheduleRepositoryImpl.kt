package com.ma.tehro.data.schedule.repository

import com.ma.tehro.common.LineEndpoints
import com.ma.tehro.domain.schedule.ScheduleType
import com.ma.tehro.domain.common.BilingualName
import com.ma.tehro.domain.schedule.ScheduleGroup
import com.ma.tehro.domain.schedule.repository.ScheduleRepository
import com.ma.thero.resources.Res
import kotlinx.serialization.json.Json

typealias TrainScheduleData = Map<String, Map<String, Map<String, List<Double>>>>

class ScheduleRepositoryImpl(
    private val json: Json
) : ScheduleRepository {

    private val scheduleCache = mutableMapOf<Int, TrainScheduleData>()

    private val scheduleFiles = mapOf(
        1 to "files/train_schedule_1.json",
        2 to "files/train_schedule_2.json",
        3 to "files/train_schedule_3.json",
        4 to "files/train_schedule_4.json",
        5 to "files/train_schedule_5.json",
        6 to "files/train_schedule_6.json",
        7 to "files/train_schedule_7.json",
    )

    override suspend fun getByStation(
        stationName: String,
        lineNum: Int,
        isBranch: Boolean,
    ): List<ScheduleGroup> {
        val schedule = getLineSchedule(lineNum)
        if (schedule.isEmpty()) return emptyList()

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

        if (endpointsEn == null || endpointsFa == null) return emptyList()

        return scheduleData
            .map { (scheduleKey, times) ->
                val scheduleType = ScheduleType.fromScheduleKey(scheduleKey)
                val towardsStation = scheduleKey.substringBefore(scheduleType?.id?.toString() ?: "")
                val (validEn, validFa) = when (towardsStation) {
                    endpointsEn.first -> endpointsEn.first to endpointsFa.first
                    endpointsEn.second -> endpointsEn.second to endpointsFa.second
                    else -> endpointsEn.first to endpointsFa.first
                }

                Triple(
                    BilingualName(validEn, validFa),
                    scheduleType,
                    times
                )
            }
            .groupBy { it.first }
            .map { (destination, schedules) ->
                ScheduleGroup(
                    destination = destination,
                    schedules = schedules.mapNotNull { (_, type, times) ->
                        type?.let { it to times }
                    }.toMap()
                )
            }
    }

    private suspend fun getLineSchedule(lineNum: Int): TrainScheduleData {
        return scheduleCache[lineNum] ?: loadSchedule(lineNum)
    }

    private suspend fun loadSchedule(lineNum: Int): TrainScheduleData {
        val path = scheduleFiles[lineNum] ?: return emptyMap()

        val text = Res.readBytes(path).decodeToString()
        val data = json.decodeFromString<TrainScheduleData>(text)

        scheduleCache[lineNum] = data
        return data
    }
}