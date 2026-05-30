package app.ma.scripts.schedule

import app.ma.scripts.common.RES_PATH
import app.ma.scripts.common.readJsonStationsAsText
import com.ma.tehro.domain.line.Station
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.io.File

fun main() {
    val jsonStations : MutableMap<String, Station> = readJsonStationsAsText("stations")
    val englishNames = jsonStations.values.map { it.name }.toSet()

    val lineGroups = mapOf(
        1 to listOf("train_timetable_1.json", "train_timetable_branch_1.json"),
        2 to listOf("train_timetable_2.json"),
        3 to listOf("train_timetable_3.json"),
        4 to listOf("train_timetable_4.json", "train_timetable_branch_4.json"),
        5 to listOf("train_timetable_5.json", "train_timetable_branch_5.json"),
        6 to listOf("train_timetable_6.json"),
        7 to listOf("train_timetable_7.json")
    )

    for ((lineNum, files) in lineGroups) {
        val allScheduleStations = mutableSetOf<String>()

        for (fileName in files) {
            val file = File("${RES_PATH}$fileName")
            if (!file.exists()) {
                println("$fileName not found")
                continue
            }

            val content = file.readText()
            val json = Json { ignoreUnknownKeys = true }
            val root = json.parseToJsonElement(content).jsonObject
            val stationsObject = root["stations"]?.jsonObject ?: root

            val stationsInJson = stationsObject.keys
                .filter { key -> !key.any { it.isDigit() } }
            allScheduleStations.addAll(stationsInJson)
        }

        val scheduleStations = allScheduleStations.sorted()
        val scheduleStationsStr = scheduleStations.joinToString(", ")

        val jsonNamesForLine = jsonStations.values
            .filter { it.lines.contains(lineNum) }
            .map { it.name }
            .sorted()
        val jsonNamesStr = jsonNamesForLine.joinToString(", ")

        println("=".repeat(60))
        println("line $lineNum")
        println("-".repeat(60))
        println("schedule stations (${scheduleStations.size}): $scheduleStationsStr")
        println()
        println("stations.json (${jsonNamesForLine.size}): $jsonNamesStr")
        println()

        val missingInStationJson = scheduleStations.filter { it !in englishNames }
        if (missingInStationJson.isNotEmpty()) {
            println("missing in stations.json: ${missingInStationJson.joinToString(", ")}")
        }

        val extraInStationJson = jsonNamesForLine.filter { it !in scheduleStations }
        if (extraInStationJson.isNotEmpty()) {
            println("in stations.json but not in schedule: ${extraInStationJson.joinToString(", ")}")
        }

        if (missingInStationJson.isEmpty() && extraInStationJson.isEmpty()) {
            println("all stations match")
        }

        println()
    }
}