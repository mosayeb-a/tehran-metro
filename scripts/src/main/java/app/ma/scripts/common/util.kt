package app.ma.scripts.common

import app.ma.scripts.common.model.Station
import kotlinx.serialization.json.Json
import java.io.File

const  val RES_PATH ="scripts/src/main/resources/"

fun readJsonStationsAsText(fileName: String): MutableMap<String, Station> {
    val path = "$RES_PATH$fileName.json"
    val file = File(path).readText(Charsets.UTF_8)
    val stations: MutableMap<String, Station> = Json.decodeFromString(file)
    return stations
}