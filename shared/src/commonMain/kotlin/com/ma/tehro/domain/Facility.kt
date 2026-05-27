package com.ma.tehro.domain

import androidx.compose.ui.graphics.vector.ImageVector

data class Facility(
    val fa: String,
    val en: String,
    val icon: ImageVector,
    val isAvailable: Boolean
)