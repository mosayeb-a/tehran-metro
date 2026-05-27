package com.ma.tehro.domain

data class BilingualName(
    val en: String,
    val fa: String
) {
    override fun toString(): String = "$fa\n${en.uppercase()}"
}