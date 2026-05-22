package app.ma.scripts.schedule

import app.ma.scripts.common.RES_PATH
import app.ma.scripts.common.convertExcelTimeToString
import app.ma.scripts.common.normalizeName
import app.ma.scripts.schedule.model.ScheduleOutput
import app.ma.scripts.schedule.model.XlsConfig
import kotlinx.serialization.json.Json
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

object XlsFileParser {
    fun parse(config: XlsConfig) {
        val filePath = "${RES_PATH}${config.fileName}.xls"
        val file = File(filePath)
        val workbook = WorkbookFactory.create(file)
        val scheduleOutput = ScheduleOutput()

        println("reading station names from first sheet of ${config.fileName}...")
        val stationNames = readStationNames(workbook, config)
        println("total stations found: ${stationNames.size}")

        config.schedules.forEach { scheduleConfig ->
            println("\n" + "=".repeat(50))
            println("processing sheet ${scheduleConfig.sheetIndex + 1} (${scheduleConfig.name})")
            println("=".repeat(50))

            val sheet = workbook.getSheetAt(scheduleConfig.sheetIndex)
            println("sheetName: ${sheet.sheetName}")

            val row = sheet.getRow(scheduleConfig.firstRow)
            val lastRow = 200

            if (row != null) {
                for (station in stationNames) {
                    println("\nprocessing station: $station for ${scheduleConfig.name}")

                    var stationColIndex = scheduleConfig.firstCol
                    var stationFound = false

                    while (stationColIndex <= row.lastCellNum) {
                        val cell = row.getCell(stationColIndex)
                        if (cell?.cellType == CellType.STRING) {
                            val cellValue = cell.stringCellValue.trim()
                            val normalizedCellValue = normalizeName(cellValue)

                            if (normalizedCellValue == station) {
                                stationFound = true
                                println("found at column ${stationColIndex + 1} (raw: '$cellValue' -> normalized: '$normalizedCellValue')")

                                val timeList = mutableListOf<Double>()
                                val stationSchedules = scheduleOutput.stations.computeIfAbsent(station) { mutableMapOf() }
                                var validTimesCount = 0

                                println("collecting times...")
                                for (currRow in (scheduleConfig.firstRow + 1)..lastRow) {
                                    val nextRow = sheet.getRow(currRow)
                                    val timeCell = nextRow?.getCell(stationColIndex)

                                    when (timeCell?.cellType) {
                                        CellType.NUMERIC -> {
                                            val numericValue = timeCell.numericCellValue
                                            timeList.add(numericValue)
                                            validTimesCount++
                                            if (validTimesCount <= 3 || validTimesCount >= timeList.size - 2) {
                                                println("row ${currRow + 1}: ${convertExcelTimeToString(numericValue)}")
                                            } else if (validTimesCount == 4) {
                                                println("showing first 3 and last 2 times only")
                                            }
                                        }
                                        else -> { }
                                    }
                                }

                                stationSchedules[scheduleConfig.serialName] = timeList
                                scheduleOutput.stations[station] = stationSchedules
                                println("updated ${scheduleConfig.name} for $station with $validTimesCount times")
                                break
                            }
                        }
                        stationColIndex++
                    }
                    if (!stationFound) {
                        println("station not found in this sheet")
                    }
                }
            }
        }

        println("\nsaving data to json..")
        val json = Json {
            prettyPrint = true
            encodeDefaults = true
        }
        File(RES_PATH + config.fileName + ".json").writeText(
            json.encodeToString(ScheduleOutput.serializer(), scheduleOutput)
        )
        println("data saved successfully")

        printStatistics(config, stationNames, scheduleOutput)
    }

    private fun readStationNames(
        workbook: org.apache.poi.ss.usermodel.Workbook,
        config: XlsConfig
    ): List<String> {
        val stationNames = mutableListOf<String>()
        val firstSheet = workbook.getSheetAt(0)
        val firstRow = firstSheet.getRow(config.schedules.first().firstRow)
        var colIndex = config.schedules.first().firstCol

        println("\n--- READING STATION NAMES ---")

        while (colIndex <= firstRow.lastCellNum) {
            val cell = firstRow.getCell(colIndex)
            if (cell != null && cell.cellType == CellType.STRING) {
                val rawName = cell.stringCellValue.trim()
                val normalizedName = normalizeName(rawName)

                if (normalizedName.isNotEmpty() &&
                    !normalizedName.contains("هدوي") &&
                    !normalizedName.contains("كد") &&
                    !normalizedName.contains("شماره") &&
                    !normalizedName.contains("ايستگاهي")) {
                    stationNames.add(normalizedName)
                    println("  raw: '$rawName' -> normalized: '$normalizedName' (column ${colIndex + 1})")
                }
            }
            colIndex++
        }

        return stationNames
    }

    private fun printStatistics(
        config: XlsConfig,
        stationNames: List<String>,
        scheduleOutput: ScheduleOutput
    ) {
        println("\n" + "=".repeat(50))
        println("final schedule statistics for ${config.fileName}")
        println("=".repeat(50))

        for (station in stationNames) {
            println("\nstatistics for $station:")
            scheduleOutput.stations[station]?.let { schedules ->
                config.schedules.forEach { scheduleConfig ->
                    val times = schedules[scheduleConfig.serialName]
                    println("  ${scheduleConfig.name}: ${times?.size ?: 0} times")
                    if (!times.isNullOrEmpty()) {
                        println("    first: ${convertExcelTimeToString(times.first())}")
                        println("    last: ${convertExcelTimeToString(times.last())}")
                    }
                }
            }
        }

        println("=".repeat(50))
    }
}