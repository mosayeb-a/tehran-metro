package com.ma.tehro.data

data class LineBranches(
    val main: Pair<BilingualName, BilingualName>,
    val branch: Pair<BilingualName, BilingualName>? = null
)

data class BilingualName(
    val en: String,
    val fa: String
)