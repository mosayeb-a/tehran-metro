package com.ma.tehro.data.feedback.repository

import com.ma.tehro.domain.feedback.repository.FeedbackRepository
import com.ma.tehro.shared.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class FeedbackRepositoryImpl(private val httpClient: HttpClient) : FeedbackRepository {

    private val token = BuildKonfig.BALE_BOT_TOKEN
    private val chatId = BuildKonfig.BALE_CHAT_ID
    private val appVersion = BuildKonfig.VERSION_NAME

    override suspend fun send(message: String) {
        if (token.isBlank() || chatId.isBlank()) {
            throw Exception("Bale bot token or chat id is not configured")
        }

        val response = httpClient.post("https://tapi.bale.ai/bot$token/sendMessage") {
            contentType(ContentType.Application.Json)
            setBody(
                buildJsonObject {
                    put("chat_id", chatId)
                    put(
                        "text",
                        buildString {
                            append(message)
                            append("\n\n---\n")
                            append("version: ")
                            append(appVersion)
                        }
                    )
                }
            )
        }

        if (!response.status.isSuccess()) {
            throw Exception("Feedback submission failed: ${response.status}")
        }
    }
}