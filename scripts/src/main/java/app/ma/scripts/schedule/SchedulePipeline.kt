package app.ma.scripts.schedule

import app.ma.scripts.common.RES_PATH
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonObject
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.add
import java.io.File

private val LINE_FILES: Map<Int, List<String>> = linkedMapOf(
    1 to listOf("train_timetable_1.json", "train_timetable_branch_1.json"),
    2 to listOf("train_timetable_2.json"),
    3 to listOf("train_timetable_3.json"),
    4 to listOf("train_timetable_4.json", "train_timetable_branch_4.json"),
    5 to listOf("train_timetable_5.json", "train_timetable_branch_5.json"),
    6 to listOf("train_timetable_6.json"),
    7 to listOf("train_timetable_7.json"),
)

fun main() {
    log("downloading schedules")
//    runDownloader()

    log("extracting xls -> json")
    runExtractor()

    log("mapping station names")
    runMapper()

    log("merging into line files")
    mergeIntoLineFiles()

    log("copying to shared module")
    copyToShared()

    log("done")
}

private fun log(msg: String) = println("> $msg")

private fun wipeAndCreateStagingDir(): File =
    File(System.getProperty("java.io.tmpdir"), "ma-schedule-staging").apply {
        if (exists()) deleteRecursively()
        mkdirs()
    }

private fun stagingDir(): File =
    File(System.getProperty("java.io.tmpdir"), "ma-schedule-staging")

private fun mergeIntoLineFiles() {
    val resDir = File(RES_PATH)
    val outDir = wipeAndCreateStagingDir()
    val json = Json { ignoreUnknownKeys = true; prettyPrint = true; prettyPrintIndent = "  " }

    for ((lineNum, sources) in LINE_FILES) {
        // sectionId -> station -> scheduleKey -> times
        val mergedSections = linkedMapOf<String, MutableMap<String, MutableMap<String, List<Double>>>>()

        for ((idx, src) in sources.withIndex()) {
            val file = File(resDir, src)
            if (!file.exists()) {
                log("  skip missing: $src")
                continue
            }
            val root = json.parseToJsonElement(file.readText()).jsonObject
            val stationsNode = root["stations"]?.jsonObject ?: root
            val sectionId = (idx + 1).toString()
            val section = mergedSections.getOrPut(sectionId) { linkedMapOf() }

            for ((station, schedNode) in stationsNode) {
                val target = section.getOrPut(station) { linkedMapOf() }
                for ((serial, timesNode) in schedNode.jsonObject) {
                    val times = (timesNode as JsonArray).map { it.jsonPrimitive.content.toDouble() }
                    target[serial] = times
                }
            }
        }

        if (mergedSections.isEmpty()) continue

        val out = buildJsonObject {
            for ((sectionId, stations) in mergedSections) {
                putJsonObject(sectionId) {
                    for ((station, scheds) in stations) {
                        putJsonObject(station) {
                            for ((serial, times) in scheds) {
                                putJsonArray(serial) { times.forEach { add(it) } }
                            }
                        }
                    }
                }
            }
        }

        val dst = File(outDir, "train_schedule_$lineNum.json")
        dst.writeText(json.encodeToString(JsonObject.serializer(), out))
        log("  train_schedule_$lineNum.json (${mergedSections.size} section(s), ${dst.length()} bytes)")
    }
}

private fun copyToShared() {
    val srcDir = stagingDir()
    if (!srcDir.exists()) return log("  skip copy: staging dir missing")

    val repoRoot = findRepoRoot(File(RES_PATH))
        ?: return log("  skip copy: repo root not found")

    val sharedDir = File(repoRoot, "shared/src/commonMain/composeResources/files")
    if (!sharedDir.exists()) return log("  skip copy: shared dir missing")

    var n = 0
    for (line in LINE_FILES.keys) {
        val src = File(srcDir, "train_schedule_$line.json")
        if (!src.exists()) continue
        src.copyTo(File(sharedDir, src.name), overwrite = true)
        n++
    }
    log("  copied $n line file(s)")
}

private fun findRepoRoot(start: File): File? {
    var f: File? = start.canonicalFile
    while (f != null) {
        if (File(f, "settings.gradle.kts").exists() || File(f, "settings.gradle").exists()) return f
        f = f.parentFile
    }
    return null
}
