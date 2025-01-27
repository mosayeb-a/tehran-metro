package com.ma.tehro.common.messenger

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class UiMessage(
    val message: String,
    val action: Action? = null
)

data class Action(
    val name: String,
    val action: suspend () -> Unit
)

object UiMessageManager {
    private val _events = Channel<UiMessage>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: UiMessage) {
        _events.send(event)
    }
}