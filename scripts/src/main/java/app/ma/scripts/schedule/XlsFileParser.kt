package app.ma.scripts.schedule

import app.ma.scripts.common.RES_PATH
import app.ma.scripts.common.convertExcelTimeToString
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
        val stationNames = mutableListOf<String>()
        val firstSheet = workbook.getSheetAt(0)
        val firstRow = firstSheet.getRow(config.schedules.first().firstRow)
        var colIndex = config.schedules.first().firstCol

        while (colIndex <= firstRow.lastCellNum) {
            val cell = firstRow.getCell(colIndex)
            if (cell != null && cell.cellType == CellType.STRING) {
                val stationName = cell.stringCellValue.trim().replace(" ", "")
                stationNames.add(stationName)
                println("found station: $stationName (column ${colIndex + 1})")
            }
            colIndex++
        }
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
                        if (cell?.cellType == CellType.STRING && cell.stringCellValue.trim().replace(" ", "") == station) {
                            stationFound = true
                            println("found at column ${stationColIndex + 1}")

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
                                    else -> {
                                        if (timeCell != null) {
                                            println("skipped non-numeric value at row ${currRow + 1}")
                                        }
                                    }
                                }
                            }

                            stationSchedules[scheduleConfig.serialName] = timeList
                            scheduleOutput.stations[station] = stationSchedules
                            println("updated ${scheduleConfig.name} for $station with $validTimesCount times")
                            break
                        }
                        stationColIndex++
                    }
                    if (!stationFound) {
                        println("station not found in this sheet")
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
        }

        println("\n" + "=".repeat(50))
        println("final schedule statistics for ${config.fileName}")
        println("=".repeat(50))

        for (station in stationNames) {
            println("\nstatistics for $station:")
            scheduleOutput.stations[station]?.let { schedules ->
                config.schedules.forEach { scheduleConfig ->
                    println("${scheduleConfig.name}: ${schedules[scheduleConfig.serialName]?.size ?: 0} times")
                }
            }
        }

        println("=".repeat(50))
    }
}