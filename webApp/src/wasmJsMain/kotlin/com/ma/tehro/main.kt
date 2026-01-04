package com.ma.tehro

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.ma.tehro.app.App
import com.ma.tehro.common.WasmPreloadedData
import com.ma.tehro.di.initKoin
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    MainScope().launch {
        val json = Json { ignoreUnknownKeys = true }
        WasmPreloadedData.load(json)

        initKoin()

        ComposeViewport(document.body!!) {
                App()
        }
    }
}