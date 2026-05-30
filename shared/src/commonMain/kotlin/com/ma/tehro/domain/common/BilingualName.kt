package com.ma.tehro.domain.common

data class BilingualName(
    val en: String,
    val fa: String
) {
    override fun toString(): String = "$fa\n${en.uppercase()}"
}