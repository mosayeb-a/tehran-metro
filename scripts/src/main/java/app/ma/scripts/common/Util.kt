package app.ma.scripts.common

import kotlinx.serialization.json.Json
import java.util.Locale

const val RES_PATH = "scripts/src/main/resources/"

inline fun <reified T> readJsonStationsAsText(fileName: String): T {
    val inputStream = object {}.javaClass.classLoader!!
        .getResourceAsStream("$fileName.json")

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    val fileContent = inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    return json.decodeFromString(fileContent)
}

fun convertExcelTimeToString(excelTime: Double): String {
    val millisInDay = 24 * 60 * 60 * 1000
    val totalMillis = Math.round(excelTime * millisInDay)

    val hours = (totalMillis / (60 * 60 * 1000)) % 24
    val minutes = (totalMillis / (60 * 1000)) % 60
    val seconds = (totalMillis / 1000) % 60

    return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
}

fun normalizeName(rawName: String): String {
    return rawName
        .trim()
        .replace("\n", "")
        .replace("\r", "")
        .replace(" ", "")
        .replace("ي", "ی")
        .replace("ك", "ک")
        .replace("آّ", "آ")
        .replace("ّ", "")
        .replace("ـ", "")
        .replace("ة", "ه")
        .replace("اعزاماز", "")
        .replace("اعزام", "")
        .replace("دریافت", "")
        .replace("دريافت", "")
}