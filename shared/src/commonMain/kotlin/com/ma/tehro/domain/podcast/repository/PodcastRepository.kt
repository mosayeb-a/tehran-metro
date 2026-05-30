package com.ma.tehro.domain.podcast.repository

import com.ma.tehro.domain.podcast.PodcastFeed
import kotlinx.coroutines.flow.Flow

interface PodcastRepository {
    suspend fun get(feedUrl: String): Flow<PodcastFeed?>
    suspend fun getAll(feedUrls: List<String>): Flow<Result<List<PodcastFeed>>>
}