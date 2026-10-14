package com.ma.tehro.domain.path

import androidx.compose.runtime.Immutable
import com.ma.tehro.domain.common.BilingualName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
sealed class PathStep {

    @Serializable
    data class Transfer(
        val line: Int,
        val destination: BilingualName,
        val station: String,
    ) : PathStep()

    @Serializable
    data class Station(
        val station: com.ma.tehro.domain.line.Station,
        val isPassthrough: Boolean = false,
        val line: Int
    ) : PathStep()
}