package com.ma.tehro.data.feedback.repository

import com.ma.tehro.domain.feedback.repository.FeedbackRepository
import com.ma.tehro.shared.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlin.time.Clock

class FeedbackRepositoryImpl(
    private val httpClient: HttpClient,
    private val json: Json
) : FeedbackRepository {

    private val token = BuildKonfig.GITHUB_TOKEN
    private val gistId = BuildKonfig.FEEDBACKS_GIST_ID
    private val fileName = "feedbacks.json"

    override suspend fun send(message: String) {
        val timestamp = Clock.System.now()
            .toLocalDateTime(TimeZone.Companion.of("Asia/Tehran"))
            .toString()
            .substringBefore('.')
            .replace('T', ' ')

        val response = httpClient.get("https://api.github.com/gists/$gistId") {
            bearerAuth(token)
            accept(ContentType.Application.Json)
        }.bodyAsText()

        val currentContent = json.parseToJsonElement(response)
            .jsonObject["files"]
            ?.jsonObject?.get(fileName)
            ?.jsonObject?.get("content")
            ?.jsonPrimitive?.content ?: "[]"

        val feedbacks = try {
            json.decodeFromString<JsonArray>(currentContent).toMutableList()
        } catch (e: Exception) {
            mutableListOf()
        }

        feedbacks.add(
            buildJsonObject {
                put("message", message)
                put("timestamp", timestamp)
            }
        )

        val result = httpClient.patch("https://api.github.com/gists/$gistId") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(
                buildJsonObject {
                    put("files", buildJsonObject {
                        put(fileName, buildJsonObject {
                            put("content", json.encodeToString(feedbacks))
                        })
                    })
                }
            )
        }

        if (!result.status.isSuccess()) {
            throw Exception()
        }
    }
}