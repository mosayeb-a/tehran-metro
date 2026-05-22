package com.ma.tehro.common

import androidx.compose.runtime.Composable

actual class ShareManager {
    actual fun shareText(text: String, title: String?) {
    }
}

@Composable
actual fun rememberShareManager(): ShareManager {
    TODO("Not yet implemented")
}