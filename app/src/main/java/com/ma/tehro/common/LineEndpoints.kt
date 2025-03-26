package com.ma.tehro.common

import com.ma.tehro.data.LineBranches
import com.ma.tehro.data.StationName

object LineEndpoints {
    private val get = mapOf(
        1 to LineBranches(
            main = StationName("Tajrish", "تجریش") to StationName("Kahrizak", "کهریزک"),
            branch = StationName("Tajrish", "تجریش") to StationName("Shahr-e Parand", "شهر پرند")
        ),
        2 to LineBranches(
            main = StationName("Farhangsara", "فرهنگسرا") to StationName("Tehran (Sadeghiyeh)", "صادقیه")
        ),
        3 to LineBranches(
            main = StationName("Qa'em", "قائم") to StationName("Azadegan", "آزادگان")
        ),
        4 to LineBranches(
            main = StationName("Kolahdooz", "کلاهدوز") to StationName(
                "Allameh Jafari",
                "علامه جعفری"
            ),
            branch = StationName(
                "Kolahdooz",
                "کلاهدوز"
            ) to StationName("Mehrabad Airport Terminal 4&6", "ترمینال ۴و۶ فرودگاه مهرآباد")
        ),
        5 to LineBranches(
            main = StationName("Tehran (Sadeghiyeh)", "صادقیه") to StationName("Golshahr", "گلشهر"),
            branch = StationName(
                "Tehran (Sadeghiyeh)",
                "صادقیه"
            ) to StationName("Shahid Sepahbod Qasem Soleimani", "شهید سپهبد قاسم سلیمانی")
        ),
        6 to LineBranches(
            main = StationName("Haram-e Abdol Azim", "حرم عبدالعظیم") to StationName(
                "Kouhsar",
                "کوهسار"
            )
        ),
        7 to LineBranches(
            main = StationName(
                "Varzeshgah-e Takhti",
                "ورزشگاه تختی"
            ) to StationName("Meydan-e Ketab", "میدان کتاب")
        )
    )

    fun getEn(line: Int, useBranch: Boolean = false): Pair<String, String>? {
        return get[line]?.let { branches ->
            if (useBranch && branches.branch != null) {
                branches.branch.first.en to branches.branch.second.en
            } else {
                branches.main.first.en to branches.main.second.en
            }
        }
    }

    fun getFa(line: Int, useBranch: Boolean = false): Pair<String, String>? {
        return get[line]?.let { branches ->
            if (useBranch && branches.branch != null) {
                branches.branch.first.fa to branches.branch.second.fa
            } else {
                branches.main.first.fa to branches.main.second.fa
            }
        }
    }

    fun hasBranch(line: Int): Boolean = get[line]?.branch != null
}