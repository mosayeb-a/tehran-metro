package com.ma.tehro.domain.podcast

import kotlinx.serialization.Serializable

@Serializable
data class PodcastFeed(
    val feedUrl: String,
    val channel: PodcastChannel,
    val episodes: List<PodcastEpisode>
)

@Serializable
data class PodcastChannel(
    val title: String,
    val description: String,
    val author: String,
    val artworkUrl: String
)

@Serializable
data class PodcastEpisode(
    val id: String,
    val title: String,
    val description: String,
    val audioUrl: String,
    val artworkUrl: String,
    val durationSec: Int
)