package com.ma.tehro.common

import com.ma.tehro.data.BranchConfig
import com.ma.tehro.data.LineBranches
import com.ma.tehro.data.BilingualName

val lineBranches = mapOf(
    1 to BranchConfig(
        branchPoint = "Shahed - BagherShahr",
        branch = listOf(
            "Namayeshgah-e Shahr-e Aftab",
            "Vavan",
            "Emam Khomeini Airport",
            "Shahr-e Parand"
        )
    ),
    4 to BranchConfig(
        branchPoint = "Bimeh",
        branch = listOf(
            "Mehrabad Airport Terminal 1&2",
            "Mehrabad Airport Terminal 4&6"
        )
    ),
    5 to BranchConfig(
        branchPoint = "Golshahr",
        branch = listOf(
            "Shahid Sepahbod Qasem Soleimani"
        )
    )
)

object LineEndpoints {
    private val get = mapOf(
        1 to LineBranches(
            main = BilingualName("Tajrish", "تجریش") to BilingualName("Kahrizak", "کهریزک"),
            branch = BilingualName("Tajrish", "تجریش") to BilingualName("Shahr-e Parand", "شهر پرند")
        ),
        2 to LineBranches(
            main = BilingualName("Farhangsara", "فرهنگسرا") to BilingualName("Tehran (Sadeghiyeh)", "صادقیه")
        ),
        3 to LineBranches(
            main = BilingualName("Qa'em", "قائم") to BilingualName("Azadegan", "آزادگان")
        ),
        4 to LineBranches(
            main = BilingualName("Kolahdooz", "کلاهدوز") to BilingualName(
                "Allameh Jafari",
                "علامه جعفری"
            ),
            branch = BilingualName(
                "Kolahdooz",
                "کلاهدوز"
            ) to BilingualName("Mehrabad Airport Terminal 4&6", "ترمینال ۴و۶ فرودگاه مهرآباد")
        ),
        5 to LineBranches(
            main = BilingualName("Tehran (Sadeghiyeh)", "صادقیه") to BilingualName("Golshahr", "گلشهر"),
            branch = BilingualName(
                "Tehran (Sadeghiyeh)",
                "صادقیه"
            ) to BilingualName("Shahid Sepahbod Qasem Soleimani", "شهید سپهبد قاسم سلیمانی")
        ),
        6 to LineBranches(
            main = BilingualName("Haram-e Abdol Azim", "حرم عبدالعظیم") to BilingualName(
                "Kouhsar",
                "کوهسار"
            )
        ),
        7 to LineBranches(
            main = BilingualName(
                "Varzeshgah-e Takhti",
                "ورزشگاه تختی"
            ) to BilingualName("Meydan-e Ketab", "میدان کتاب")
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