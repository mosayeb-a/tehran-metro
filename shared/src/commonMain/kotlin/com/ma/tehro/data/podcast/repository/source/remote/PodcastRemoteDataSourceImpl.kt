package com.ma.tehro.data.podcast.repository.source.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withTimeout

class PodcastRemoteDataSourceImpl(
    private val httpClient: HttpClient
) : PodcastRemoteDataSource {
    override suspend fun fetch(url: String): Result<RssFeedDto> {
        return try {
            val response = withTimeout(7000) {
                httpClient.get(url)
            }
            val xmlString = response.body<String>()

            val dto = PodcastXmlParser.parseFeed(xmlString)
            Result.success(dto)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}