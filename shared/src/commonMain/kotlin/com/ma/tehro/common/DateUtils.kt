package com.ma.tehro.common

fun isoToPersianDate(iso: String): String {
    val parts = iso.split("-")
    if (parts.size != 3) return iso
    val gy = parts[0].toIntOrNull() ?: return iso
    val gm = parts[1].toIntOrNull() ?: return iso
    val gd = parts[2].toIntOrNull() ?: return iso

    val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
    val month = JALALI_MONTHS.getOrNull(jm - 1) ?: return iso

    return "${jd.toFarsiNumber()} $month ${jy.toFarsiNumber()}"
}

private val JALALI_MONTHS = listOf(
    "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
    "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
)

private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
    val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    val gy2 = if (gm > 2) gy + 1 else gy

    var days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) +
            ((gy2 + 399) / 400) + gd
    for (i in 0 until gm - 1) days += gDaysInMonth[i]

    var jy = -1595 + (33 * (days / 12053))
    days %= 12053
    jy += 4 * (days / 1461)
    days %= 1461
    if (days > 365) {
        jy += (days - 1) / 365
        days = (days - 1) % 365
    }

    var jm = 1
    var jd = days + 1
    while (jm <= 12) {
        val len = if (jm <= 6) 31 else if (jm <= 11) 30 else if (isLeapJalali(jy)) 30 else 29
        if (jd <= len) break
        jd -= len
        jm++
    }
    return Triple(jy, jm, jd)
}

private fun isLeapJalali(jy: Int): Boolean {
    val mod = (jy + 1595) % 33
    return mod == 1 || mod == 5 || mod == 9 || mod == 13 || mod == 17 ||
            mod == 22 || mod == 26 || mod == 30
}