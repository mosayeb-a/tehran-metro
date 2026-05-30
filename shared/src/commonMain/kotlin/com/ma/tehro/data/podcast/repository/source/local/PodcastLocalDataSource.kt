package com.ma.tehro.data.podcast.repository.source.local

import kotlinx.coroutines.flow.Flow

interface PodcastLocalDataSource {
    suspend fun insertFeed(feed: PodcastFeedEntity): Result<Unit>
    fun getAllFeeds(): Flow<List<PodcastFeedEntity>>
    fun getFeedByUrl(feedUrl: String): Flow<PodcastFeedEntity?>
}