package com.ma.tehro.data.repo

import com.ma.tehro.BuildConfig
import com.ma.tehro.common.AppException
import com.ma.tehro.data.Station
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.HttpURLConnection
import java.net.URL

interface DataCorrectionRepository {
    suspend fun submitStationCorrection(station: Station)
    suspend fun submitSimpleCorrection(message: String)
}

class DataCorrectionRepositoryImpl : DataCorrectionRepository {
    private val token = BuildConfig.github_token
    private val gistId = BuildConfig.gist_id

    override suspend fun submitStationCorrection(station: Station) {
        sendCorrectionToGist(Json.encodeToString(station))
    }

    override suspend fun submitSimpleCorrection(message: String) {
        sendCorrectionToGist(message)
    }

    private suspend fun sendCorrectionToGist(newStationJson: String) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://api.github.com/gists/$gistId")
                val getConnection = url.openConnection() as HttpURLConnection
                getConnection.requestMethod = "GET"
                getConnection.setRequestProperty("Authorization", "Bearer $token")
                getConnection.setRequestProperty("Accept", "application/vnd.github.v3+json")

                val existingContent = getConnection.inputStream.bufferedReader().use { it.readText() }
                getConnection.disconnect()
                val currentStationsJson = Json.parseToJsonElement(existingContent)
                    .jsonObject["files"]?.jsonObject?.get("gistfile1.txt")
                    ?.jsonObject?.get("content")?.jsonPrimitive?.content ?: "[]"

                val currentStations = try {
                    Json.decodeFromString<MutableList<Station>>(currentStationsJson)
                } catch (e: Exception) {
                    println("Error parsing JSON: ${e.localizedMessage}")
                    mutableListOf()
                }

                val newStation = Json.decodeFromString<Station>(newStationJson)
                currentStations.add(newStation)
                val updatedContent = Json.encodeToString(currentStations)

                val patchConnection = url.openConnection() as HttpURLConnection
                patchConnection.requestMethod = "PATCH"
                patchConnection.setRequestProperty("Authorization", "Bearer $token")
                patchConnection.setRequestProperty("Accept", "application/vnd.github.v3+json")
                patchConnection.setRequestProperty("Content-Type", "application/json")
                patchConnection.doOutput = true

                val payload = Json.encodeToString(
                    mapOf(
                        "files" to mapOf(
                            "gistfile1.txt" to mapOf(
                                "content" to updatedContent
                            )
                        )
                    )
                )
                println("updated payload: $payload")

                patchConnection.outputStream.use { outputStream ->
                    outputStream.write(payload.toByteArray())
                    outputStream.flush()
                }

                val responseCode = patchConnection.responseCode
                val responseMessage = patchConnection.inputStream.bufferedReader().use { it.readText() }
                println("response code: $responseCode")
                println("response message: $responseMessage")

                patchConnection.disconnect()
            } catch (e: Exception) {
                throw AppException(e.message.toString())
            }
        }
    }
}
