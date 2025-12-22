package app.ma.scripts.common

import com.ma.tehro.data.Station
import kotlinx.serialization.json.Json
import java.util.Locale

const  val RES_PATH ="scripts/src/main/resources/"

fun readJsonStationsAsText(fileName: String): MutableMap<String, Station> {
    val inputStream = object {}.javaClass.classLoader!!
        .getResourceAsStream("$fileName.json")

    val fileContent = inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    return Json.decodeFromString(fileContent)
}
fun convertExcelTimeToString(excelTime: Double): String {
    val millisInDay = 24 * 60 * 60 * 1000
    val totalMillis = Math.round(excelTime * millisInDay)

    val hours = (totalMillis / (60 * 60 * 1000)) % 24
    val minutes = (totalMillis / (60 * 1000)) % 60
    val seconds = (totalMillis / 1000) % 60

    return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
}