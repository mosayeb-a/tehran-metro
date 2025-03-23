package com.ma.tehro.data

data class LineBranches(
    val main: Pair<StationName, StationName>,
    val branch: Pair<StationName, StationName>? = null
)

data class StationName(
    val en: String,
    val fa: String
)