package com.ma.tehro.domain.common

import kotlinx.serialization.Serializable

@Serializable
data class BilingualName(
    val en: String,
    val fa: String
)