package com.ma.tehro.data.repo

import com.ma.tehro.BuildConfig
import com.ma.tehro.data.Station
import com.ma.tehro.domain.repo.DataCorrectionRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlin.time.ExperimentalTime

@Serializable
data class FeedbackEntry(
    val message: String,
    val timestamp: String
)

class DataCorrectionRepositoryImpl(
    private val httpClient: HttpClient,
    private val json: Json
) : DataCorrectionRepository {

    private val token = BuildConfig.github_token
    private val stationsGistId = BuildConfig.stations_gist_id
    private val feedbacksGistId = BuildConfig.feedbacks_gist_id

    override suspend fun submitStationCorrection(station: Station) {
        updateGist(stationsGistId, station, "gistfile1.txt")
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun submitFeedback(message: String) {
        val now = kotlin.time.Clock.System.now()
            .toLocalDateTime(TimeZone.of("Asia/Tehran"))
            .toString()
            .substringBefore('.')
            .replace('T', ' ')

        val feedback = FeedbackEntry(message = message, timestamp = now)
        updateGist(feedbacksGistId, feedback, "feedbacks.json")
    }

    private suspend inline fun <reified T> updateGist(
        gistId: String,
        newItem: T,
        fileName: String
    ) = withContext(Dispatchers.IO) {
        val response = httpClient.get("https://api.github.com/gists/$gistId") {
            bearerAuth(token)
            accept(ContentType.Application.Json)
        }

        val gistJson = json.parseToJsonElement(response.bodyAsText()).jsonObject
        val currentContent = gistJson["files"]
            ?.jsonObject
            ?.get(fileName)
            ?.jsonObject
            ?.get("content")
            ?.jsonPrimitive
            ?.content ?: "[]"

        val currentList = try {
            json.decodeFromString<JsonArray>(currentContent).toMutableList()
        } catch (e: Exception) {
            mutableListOf()
        }

        currentList.add(json.encodeToJsonElement(newItem))

        val updateResponse: HttpResponse =
            httpClient.patch("https://api.github.com/gists/$gistId") {
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)

                setBody(
                    buildJsonObject {
                        put("files", buildJsonObject {
                            put(fileName, buildJsonObject {
                                put("content", json.encodeToString(currentList))
                            })
                        })
                    }
                )
            }

        if (!updateResponse.status.isSuccess()) {
            throw Exception()
        }
    }
}