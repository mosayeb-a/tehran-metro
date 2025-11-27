package com.ma.tehro.data.repo

import com.ma.tehro.BuildConfig
import com.ma.tehro.common.AppException
import com.ma.tehro.data.Station
import com.ma.tehro.domain.repo.DataCorrectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.HttpURLConnection
import java.net.URL
import kotlin.time.ExperimentalTime

@Serializable
data class FeedbackEntry(
    val message: String,
    val timestamp: String
)

class DataCorrectionRepositoryImpl(
    val json: Json
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

        val feedback = FeedbackEntry(
            message = message,
            timestamp = now
        )
        updateGist(feedbacksGistId, feedback, "feedbacks.json")
    }

    private suspend inline fun <reified T> updateGist(
        gistId: String,
        content: T,
        fileName: String
    ) = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.github.com/gists/$gistId")

            val existingContent = getExistingContent(url)

            val currentItems: MutableList<T> = try {
                val fileContent = json.parseToJsonElement(existingContent)
                    .jsonObject["files"]?.jsonObject?.get(fileName)
                    ?.jsonObject?.get("content")?.jsonPrimitive?.content ?: "[]"

                json.decodeFromString<MutableList<T>>(fileContent)
            } catch (e: Exception) {
                println(e)
                mutableListOf()
            }

            currentItems.add(content)

            updateGistContent(url, fileName, currentItems)
        } catch (e: Exception) {
            println(e)
            throw AppException(e.message ?: "Unknown error occurred")
        }
    }

    private fun getExistingContent(url: URL): String {
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Authorization", "Bearer $token")
            setRequestProperty("Accept", "application/vnd.github.v3+json")
        }

        return connection.inputStream.bufferedReader().readText().also {
            connection.disconnect()
        }
    }

    private inline fun <reified T> updateGistContent(
        url: URL,
        fileName: String,
        currentItems: List<T>
    ) {
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "PATCH"
            setRequestProperty("Authorization", "Bearer $token")
            setRequestProperty("Accept", "application/vnd.github.v3+json")
            setRequestProperty("Content-Type", "application/json")
            doOutput = true
        }

        try {
            val payload = json.encodeToString(
                mapOf(
                    "files" to mapOf(
                        fileName to mapOf(
                            "content" to json.encodeToString(currentItems)
                        )
                    )
                )
            )

            connection.outputStream.use { output ->
                output.write(payload.toByteArray())
            }

            if (connection.responseCode !in 200..299) {
                val errorResponse =
                    connection.errorStream?.bufferedReader()?.readText() ?: "unknown error"
                throw AppException("HTTP Error: ${connection.responseCode} - $errorResponse")
            }
        } finally {
            connection.disconnect()
        }
    }
}