package app.ma.scripts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.util.Locale

const val FIRST_ROW = 4
const val FIRST_COL = 2
const val NORMAL_0 = "تجريش عادي"
const val THURSDAY_1 = "تجريش پنجشنبه"
const val FRIDAY_2 = "تجريش جمعه"
const val NORMAL_3 = "كهريزك عادي"
const val THURSDAY_4 = "كهريزك پنجشنبه"
const val FRIDAY_5 = "كهريزك جمعه"
const val FILE_NAME = "train_timetable_1"

@Serializable
data class Timetable(val timetable: List<Double> = emptyList())

@Serializable
data class Schedule(
    @SerialName(NORMAL_0) var normal0: Timetable = Timetable(),
    @SerialName(THURSDAY_1) var thursday1: Timetable = Timetable(),
    @SerialName(FRIDAY_2) var friday2: Timetable = Timetable(),
    @SerialName(NORMAL_3) var normal3: Timetable = Timetable(),
    @SerialName(THURSDAY_4) var thursday4: Timetable = Timetable(),
    @SerialName(FRIDAY_5) var friday6: Timetable = Timetable()
)

@Serializable
data class ScheduleOutput(val stations: MutableMap<String, Schedule> = mutableMapOf())

fun main() {
    val filePath = "$BASE_RAW_PATH$FILE_NAME.xls"
    val file = File(filePath)
    val workbook = WorkbookFactory.create(file)
    val scheduleOutput = ScheduleOutput()

    println("reading station names from first sheet...")
    val stationNames = mutableListOf<String>()
    val firstSheet = workbook.getSheetAt(0)
    // todo based on xls file it changed
    val firstRow = firstSheet.getRow(FIRST_ROW)
    // todo based on xls file it changed
    // a:0 b:1 c:2 d:3
    var colIndex = FIRST_COL

    // reading the first row to get all the station names
    while (colIndex <= firstRow.lastCellNum) {
        val cell = firstRow.getCell(colIndex)
        if (cell != null && cell.cellType == CellType.STRING) {
            val stationName = cell.stringCellValue
                .trim()
                .replace(" ", "")
            stationNames.add(stationName)
            println("found station: $stationName (column ${colIndex + 1})")
        }
        colIndex++
    }
    println("total stations found: ${stationNames.size}")

    for (sheetIndex in 0..5) {
        println("\n" + "=".repeat(50))
        println("processing sheet ${sheetIndex + 1} (${getScheduleNameForSheet(sheetIndex)})")
        println("=".repeat(50))

        val sheet = workbook.getSheetAt(sheetIndex)
        // todo based on xls file it changed
        val rowIndex = if (sheetIndex <= 1) 4 else 5
        val row = sheet.getRow(rowIndex)
        val lastRow = 200

        if (row != null) {
            for (station in stationNames) {
                println("\nprocessing station: $station for ${getScheduleNameForSheet(sheetIndex)}")

                // todo based on xls file it changed
                var stationColIndex = FIRST_COL
                var stationFound = false
                while (stationColIndex <= row.lastCellNum) {
                    val cell = row.getCell(stationColIndex)
                    if (cell?.cellType == CellType.STRING && cell.stringCellValue
                            .trim()
                            .replace(" ", "") == station
                    ) {
                        stationFound = true
                        println("found at column ${stationColIndex + 1}")

                        val timeList = mutableListOf<Double>()
                        val existingSchedule = scheduleOutput.stations[station] ?: Schedule()
                        var validTimesCount = 0

                        println("collecting times...")
                        for (currRow in (rowIndex + 1)..lastRow) {
                            val nextRow = sheet.getRow(currRow)
                            val timeCell = nextRow?.getCell(stationColIndex)

                            when (timeCell?.cellType) {
                                CellType.NUMERIC -> {
                                    val numericValue = timeCell.numericCellValue
                                    timeList.add(numericValue)
                                    validTimesCount++
                                    if (validTimesCount <= 3 || validTimesCount >= timeList.size - 2) {
                                        println(
                                            "row ${currRow + 1}: ${
                                                convertExcelTimeToString(
                                                    numericValue
                                                )
                                            }"
                                        )
                                    } else if (validTimesCount == 4) {
                                        println("showing first 3 and last 2 times only")
                                    }
                                }

                                else -> {
                                    if (timeCell != null) {
                                        println("skipped non-numeric value at row ${currRow + 1}")
                                    }
                                }
                            }
                        }

                        val updatedSchedule = when (sheetIndex) {
                            0 -> existingSchedule.copy(normal0 = Timetable(timeList))
                            1 -> existingSchedule.copy(thursday1 = Timetable(timeList))
                            2 -> existingSchedule.copy(friday2 = Timetable(timeList))
                            3 -> existingSchedule.copy(normal3 = Timetable(timeList))
                            4 -> existingSchedule.copy(thursday4 = Timetable(timeList))
                            5 -> existingSchedule.copy(friday6 = Timetable(timeList))
                            else -> existingSchedule
                        }

                        scheduleOutput.stations[station] = updatedSchedule
                        println("updated ${getScheduleNameForSheet(sheetIndex)} for $station with $validTimesCount times")
                        break
                    }
                    stationColIndex++
                }
                if (!stationFound) {
                    println("station not found in this sheet")
                }
            }
        }

        println("\nsaving data to json...")
        val json = Json {
            prettyPrint = true
            encodeDefaults = true
        }
        File(BASE_RAW_PATH + FILE_NAME + "_json_test.json").writeText(
            json.encodeToString(
                scheduleOutput
            )
        )
        println("data saved successfully")
    }

    println("\n" + "=".repeat(50))
    println("final schedule statistics")
    println("=".repeat(50))

    for (station in stationNames) {
        println("\nstatistics for $station:")
        scheduleOutput.stations[station]?.let { schedule ->
            println("$NORMAL_0: ${schedule.normal0.timetable.size} times")
            println("$THURSDAY_1: ${schedule.thursday1.timetable.size} times")
            println("$FRIDAY_2: ${schedule.friday2.timetable.size} times")
            println("$NORMAL_3: ${schedule.normal3.timetable.size} times")
            println("$THURSDAY_4: ${schedule.thursday4.timetable.size} times")
            println("$FRIDAY_5: ${schedule.friday6.timetable.size} times")
        }
    }

    println("=".repeat(50))
}

fun getScheduleNameForSheet(sheetIndex: Int): String = when (sheetIndex) {
    0 -> NORMAL_0
    1 -> THURSDAY_1
    2 -> FRIDAY_2
    3 -> NORMAL_3
    4 -> THURSDAY_4
    5 -> FRIDAY_5
    else -> "unknown"
}

fun convertExcelTimeToString(excelTime: Double): String {
    val millisInDay = 24 * 60 * 60 * 1000
    val totalMillis = Math.round(excelTime * millisInDay)

    val hours = (totalMillis / (60 * 60 * 1000)) % 24
    val minutes = (totalMillis / (60 * 1000)) % 60
    val seconds = (totalMillis / 1000) % 60

    return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
}