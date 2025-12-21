package com.ma.tehro.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.skiko.ClipboardManager

actual class ShareManager( val clipboardManager: ClipboardManager = ClipboardManager()) {
    actual fun shareText(text: String, title: String?) {
        clipboardManager.setText(text)
        println("path copied to clipboard")
    }
}

@Composable
actual fun rememberShareManager(): ShareManager {
    return remember { ShareManager() }
}