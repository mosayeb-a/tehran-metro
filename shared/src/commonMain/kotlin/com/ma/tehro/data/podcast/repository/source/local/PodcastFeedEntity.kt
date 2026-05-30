package com.ma.tehro.data.podcast.repository.source.local

data class PodcastFeedEntity(
    val feedUrl: String,
    val channel: PodcastChannelEntity,
    val episodes: List<PodcastEpisodeEntity>
)

data class PodcastChannelEntity(
    val title: String,
    val description: String,
    val author: String,
    val artworkUrl: String
)

data class PodcastEpisodeEntity(
    val id: String,
    val feedUrl: String,
    val title: String,
    val description: String,
    val audioUrl: String,
    val durationSec: Long,
    val artworkUrl: String
)