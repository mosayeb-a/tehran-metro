package app.ma.scripts.schedule

import app.ma.scripts.schedule.model.ScheduleConfig
import app.ma.scripts.schedule.model.XlsConfig

const val ROW_LINE1_TAJRISH = 4
const val COL_LINE1_TAJRISH = 2
const val ROW_LINE1_KAHRIZAK = 5
const val COL_LINE1_KAHRIZAK = 2
const val ROW_LINE1_BRANCH = 5
const val COL_LINE1_BRANCH = 1

const val ROW_LINE2 = 4
const val COL_LINE2 = 2

const val ROW_LINE3 = 5
const val COL_LINE3 = 3

const val ROW_LINE4 = 5
const val COL_LINE4_MAIN = 2
const val COL_LINE4_BRANCH = 3

const val ROW_LINE5 = 4
const val ROW_LINE5_BRANCH = 5
const val COL_LINE5 = 3

const val ROW_LINE6 = 5
const val COL_LINE6 = 3

const val ROW_LINE7 = 5
const val COL_LINE7 = 3

val xlsConfigs = listOf(
    // line 1
    XlsConfig(
        fileName = "train_timetable_1",
        schedules = listOf(
            ScheduleConfig(
                sheetIndex = 0,
                name = "تجريش عادي",
                serialName = "Tajrish0",
                firstRow = ROW_LINE1_TAJRISH,
                firstCol = COL_LINE1_TAJRISH
            ),
            ScheduleConfig(
                sheetIndex = 1,
                name = "تجريش پنجشنبه",
                serialName = "Tajrish1",
                firstRow = ROW_LINE1_TAJRISH,
                firstCol = COL_LINE1_TAJRISH
            ),
            ScheduleConfig(
                sheetIndex = 2,
                name = "تجریش جمعه",
                serialName = "Tajrish2",
                firstRow = ROW_LINE1_TAJRISH,
                firstCol = COL_LINE1_TAJRISH
            ),
            ScheduleConfig(
                sheetIndex = 3,
                name = "كهريزك عادي",
                serialName = "Kahrizak0",
                firstRow = ROW_LINE1_KAHRIZAK,
                firstCol = COL_LINE1_KAHRIZAK
            ),
            ScheduleConfig(
                sheetIndex = 4,
                name = "كهريزك پنجشنبه",
                serialName = "Kahrizak1",
                firstRow = ROW_LINE1_KAHRIZAK,
                firstCol = COL_LINE1_KAHRIZAK
            ),
            ScheduleConfig(
                sheetIndex = 5,
                name = "کهریزک جمعه",
                serialName = "Kahrizak2",
                firstRow = ROW_LINE1_KAHRIZAK,
                firstCol = COL_LINE1_KAHRIZAK
            ),
        )
    ),
    XlsConfig(
        fileName = "train_timetable_branch_1",
        schedules = listOf(
            ScheduleConfig(
                sheetIndex = 0,
                name = "پرند",
                serialName = "Shahr-e Parand3",
                firstRow = ROW_LINE1_BRANCH,
                firstCol = COL_LINE1_BRANCH
            ),
            ScheduleConfig(
                sheetIndex = 1,
                name = "شاهد",
                serialName = "Shahed - BagherShahr3",
                firstRow = ROW_LINE1_BRANCH,
                firstCol = COL_LINE1_BRANCH
            ),
        )
    ),
    // line 2
    XlsConfig(
        fileName = "train_timetable_2",
        schedules = listOf(
            ScheduleConfig(
                sheetIndex = 0,
                name = "صادقيه عادي",
                serialName = "Tehran (Sadeghiyeh)0",
                firstRow = ROW_LINE2,
                firstCol = COL_LINE2
            ),
            ScheduleConfig(
                sheetIndex = 1,
                name = "صادقيه پنج شنبه",
                serialName = "Tehran (Sadeghiyeh)1",
                firstRow = ROW_LINE2,
                firstCol = COL_LINE2
            ),
            ScheduleConfig(
                sheetIndex = 2,
                name = "صادقيه جمعه",
                serialName = "Tehran (Sadeghiyeh)2",
                firstRow = ROW_LINE2,
                firstCol = COL_LINE2
            ),
            ScheduleConfig(
                sheetIndex = 3,
                name = "فرهنگسرا عادي",
                serialName = "Farhangsara0",
                firstRow = ROW_LINE2,
                firstCol = COL_LINE2
            ),
            ScheduleConfig(
                sheetIndex = 4,
                name = "فرهنگسرا پنج شنبه",
                serialName = "Farhangsara1",
                firstRow = ROW_LINE2,
                firstCol = COL_LINE2
            ),
            ScheduleConfig(
                sheetIndex = 5,
                name = "فرهنگسرا جمعه",
                serialName = "Farhangsara2",
                firstRow = ROW_LINE2,
                firstCol = COL_LINE2
            ),
        )
    ),
    // line 3
    XlsConfig(
        fileName = "train_timetable_3",
        schedules = listOf(
            ScheduleConfig(
                sheetIndex = 0,
                name = "آزادگان - عادی",
                serialName = "Azadegan0",
                firstRow = ROW_LINE3,
                firstCol = COL_LINE3
            ),
            ScheduleConfig(
                sheetIndex = 1,
                name = "آزادگان - پنجشنبه",
                serialName = "Azadegan1",
                firstRow = ROW_LINE3,
                firstCol = COL_LINE3
            ),
            ScheduleConfig(
                sheetIndex = 2,
                name = "آزادگان - جمعه",
                serialName = "Azadegan2",
                firstRow = ROW_LINE3,
                firstCol = COL_LINE3
            ),
            ScheduleConfig(
                sheetIndex = 3,
                name = "قائم - عادی",
                serialName = "Qa'em0",
                firstRow = ROW_LINE3,
                firstCol = COL_LINE3
            ),
            ScheduleConfig(
                sheetIndex = 4,
                name = "قائم - پنجشنبه",
                serialName = "Qa'em1",
                firstRow = ROW_LINE3,
                firstCol = COL_LINE3
            ),
            ScheduleConfig(
                sheetIndex = 5,
                name = "قائم - جمعه",
                serialName = "Qa'em2",
                firstRow = ROW_LINE3,
                firstCol = COL_LINE3
            ),
        )
    ),
    // line4
    XlsConfig(
        fileName = "train_timetable_4",
        schedules = listOf(
            ScheduleConfig(
                sheetIndex = 0,
                name = "علامه - عادي",
                serialName = "Allameh Jafari0",
                firstRow = ROW_LINE4,
                firstCol = COL_LINE4_MAIN
            ),
            ScheduleConfig(
                sheetIndex = 1,
                name = "علامه - پنجشنبه",
                serialName = "Allameh Jafari1",
                firstRow = ROW_LINE4,
                firstCol = COL_LINE4_MAIN
            ),
            ScheduleConfig(
                sheetIndex = 2,
                name = "علامه - جمعه",
                serialName = "Allameh Jafari2",
                firstRow = ROW_LINE4,
                firstCol = COL_LINE4_MAIN
            ),
            ScheduleConfig(
                sheetIndex = 3,
                name = "شهيد كلاهدوز - عادي",
                serialName = "Kolahdooz0",
                firstRow = ROW_LINE4,
                firstCol = COL_LINE4_MAIN
            ),
            ScheduleConfig(
                sheetIndex = 4,
                name = "شهيد كلاهدوز - پنجشنبه",
                serialName = "Kolahdooz1",
                firstRow = ROW_LINE4,
                firstCol = COL_LINE4_MAIN
            ),
            ScheduleConfig(
                sheetIndex = 5,
                name = "شهيد كلاهدوز - جمعه",
                serialName = "Kolahdooz2",
                firstRow = ROW_LINE4,
                firstCol = COL_LINE4_MAIN
            ),
        )
    ),
    XlsConfig(
        fileName = "train_timetable_branch_4",
        schedules = listOf(
            ScheduleConfig(
                sheetIndex = 0,
                name = "بيمه عادي",
                serialName = "Bimeh4",
                firstRow = ROW_LINE4,
                firstCol = COL_LINE4_BRANCH
            ),
            ScheduleConfig(
                sheetIndex = 1,
                name = "بيمه تعطيل",
                serialName = "Bimeh5",
                firstRow = ROW_LINE4,
                firstCol = COL_LINE4_BRANCH
            ),
            ScheduleConfig(
                sheetIndex = 2,
                name = "مهرآباد- عادي",
                serialName = "Mehrabad Airport Terminal 4&64",
                firstRow = ROW_LINE4,
                firstCol = COL_LINE4_MAIN
            ),
            ScheduleConfig(
                sheetIndex = 3,
                name = "مهرآباد تعطيل",
                serialName = "Mehrabad Airport Terminal 4&65",
                firstRow = ROW_LINE4,
                firstCol = COL_LINE4_MAIN
            ),
        )
    ),
    // line 5
    XlsConfig(
        fileName = "train_timetable_5",
        schedules = listOf(
            ScheduleConfig(
                sheetIndex = 0,
                name = "گلشهر- عادي",
                serialName = "Golshahr0",
                firstRow = ROW_LINE5,
                firstCol = COL_LINE5
            ),
            ScheduleConfig(
                sheetIndex = 1,
                name = "گلشهر- پنجشنبه",
                serialName = "Golshahr1",
                firstRow = ROW_LINE5,
                firstCol = COL_LINE5
            ),
            ScheduleConfig(
                sheetIndex = 2,
                name = "گلشهر - جمعه و تعطيلات",
                serialName = "Golshahr5",
                firstRow = ROW_LINE5,
                firstCol = COL_LINE5
            ),
            ScheduleConfig(
                sheetIndex = 3,
                name = "صادقيه - عادي",
                serialName = "Tehran (Sadeghiyeh)0",
                firstRow = ROW_LINE5,
                firstCol = COL_LINE5
            ),
            ScheduleConfig(
                sheetIndex = 4,
                name = "صادقيه - پنجشنبه",
                serialName = "Tehran (Sadeghiyeh)1",
                firstRow = ROW_LINE5,
                firstCol = COL_LINE5
            ),
            ScheduleConfig(
                sheetIndex = 5,
                name = "صادقيه - تعطيل",
                serialName = "Tehran (Sadeghiyeh)5",
                firstRow = ROW_LINE5,
                firstCol = COL_LINE5
            ),
        )
    ),
    XlsConfig(
        fileName = "train_timetable_branch_5",
        schedules = listOf(
            ScheduleConfig(
                sheetIndex = 0,
                name = "هشتگرد - عادي",
                serialName = "Sepahbod Qasem Soleimani4",
                firstRow = ROW_LINE5_BRANCH,
                firstCol = COL_LINE5
            ),
            ScheduleConfig(
                sheetIndex = 1,
                name = "هشتگرد - تعطيل",
                serialName = "Sepahbod Qasem Soleimani5",
                firstRow = ROW_LINE5_BRANCH,
                firstCol = COL_LINE5
            ),
            ScheduleConfig(
                sheetIndex = 2,
                name = "گلشهر - عادي",
                serialName = "Golshahr4",
                firstRow = ROW_LINE5_BRANCH,
                firstCol = COL_LINE5
            ),
            ScheduleConfig(
                sheetIndex = 3,
                name = "گلشهر - تعطيل",
                serialName = "Golshahr5",
                firstRow = ROW_LINE5_BRANCH,
                firstCol = COL_LINE5
            ),
        )
    ),
    // line 6
    XlsConfig(
        fileName = "train_timetable_6",
        schedules = listOf(
            ScheduleConfig(
                sheetIndex = 0,
                name = "شهيدآرمان - عادي",
                serialName = "Kouhsar0",
                firstRow = ROW_LINE6,
                firstCol = COL_LINE6
            ),
            ScheduleConfig(
                sheetIndex = 1,
                name = "شهید آرمان - پنجشنبه",
                serialName = "Kouhsar1",
                firstRow = ROW_LINE6,
                firstCol = COL_LINE6
            ),
            ScheduleConfig(
                sheetIndex = 2,
                name = "شهيدآرمان - تعطيل",
                serialName = "Kouhsar5",
                firstRow = ROW_LINE6,
                firstCol = COL_LINE6
            ),
            ScheduleConfig(
                sheetIndex = 3,
                name = "دولت آباد - عادي",
                serialName = "Shohada-ye Dowlat Abad0",
                firstRow = ROW_LINE6,
                firstCol = COL_LINE6
            ),
            ScheduleConfig(
                sheetIndex = 4,
                name = "دولت آّباد  - پنج شنبه",
                serialName = "Shohada-ye Dowlat Abad1",
                firstRow = ROW_LINE6,
                firstCol = COL_LINE6
            ),
            ScheduleConfig(
                sheetIndex = 5,
                name = "دولت آباد - تعطيل",
                serialName = "Shohada-ye Dowlat Abad5",
                firstRow = ROW_LINE6,
                firstCol = COL_LINE6
            ),
        )
    ),
    // line 7
    XlsConfig(
        fileName = "train_timetable_7",
        schedules = listOf(
            ScheduleConfig(
                sheetIndex = 0,
                name = "كتاب - عادي",
                serialName = "Meydan-e Ketab0",
                firstRow = ROW_LINE7,
                firstCol = COL_LINE7
            ),
            ScheduleConfig(
                sheetIndex = 1,
                name = "کتاب- پنجشنبه",
                serialName = "Meydan-e Ketab1",
                firstRow = ROW_LINE7,
                firstCol = COL_LINE7
            ),
            ScheduleConfig(
                sheetIndex = 2,
                name = "كتاب - تعطیل",
                serialName = "Meydan-e Ketab5",
                firstRow = ROW_LINE7,
                firstCol = COL_LINE7
            ),
            ScheduleConfig(
                sheetIndex = 3,
                name = "بسيج - عادي",
                serialName = "Basij0",
                firstRow = ROW_LINE7,
                firstCol = COL_LINE7
            ),
            ScheduleConfig(
                sheetIndex = 4,
                name = "بسیج- پنجشنبه",
                serialName = "Basij1",
                firstRow = ROW_LINE7,
                firstCol = COL_LINE7
            ),
            ScheduleConfig(
                sheetIndex = 5,
                name = "بسيج - تعطيل",
                serialName = "Basij5",
                firstRow = ROW_LINE7,
                firstCol = COL_LINE7
            ),
        )
    ),
)

fun main() {
    xlsConfigs.forEach { config ->
        XlsFileParser.parse(config)
    }
}