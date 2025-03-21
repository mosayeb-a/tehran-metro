package com.ma.tehro.scripts

import com.ma.tehro.data.Station
import kotlinx.serialization.json.Json
import java.io.File

const val BASE_RAW_PATH =""

fun readJsonStationsAsText(fileName: String): MutableMap<String, Station> {
    val path = "$BASE_RAW_PATH$fileName.json"
    val file = File(path).readText(Charsets.UTF_8)
    val stations: MutableMap<String, Station> = Json.decodeFromString(file)
    return stations
}