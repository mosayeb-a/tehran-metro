package com.ma.tehro.domain

import androidx.compose.runtime.Immutable
import com.ma.tehro.data.Station

@Immutable
sealed class PathItem {
    data class Title(val en: String, val fa: String) : PathItem()
    data class StationItem(
        val station: Station,
        val isPassthrough: Boolean = false,
        val lineNumber: Int
    ) : PathItem()
}