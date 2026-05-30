package com.ma.tehro.data.podcast.repository.source.remote

interface PodcastRemoteDataSource {
    suspend fun fetch(url: String): Result<RssFeedDto>
}