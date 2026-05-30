package com.ma.tehro.data.podcast

import com.ma.tehro.data.podcast.repository.source.local.PodcastChannelEntity
import com.ma.tehro.data.podcast.repository.source.local.PodcastEpisodeEntity
import com.ma.tehro.data.podcast.repository.source.local.PodcastFeedEntity
import com.ma.tehro.data.podcast.repository.source.remote.ChannelDto
import com.ma.tehro.data.podcast.repository.source.remote.ItemDto
import com.ma.tehro.data.podcast.repository.source.remote.RssFeedDto
import com.ma.tehro.domain.podcast.PodcastChannel
import com.ma.tehro.domain.podcast.PodcastEpisode
import com.ma.tehro.domain.podcast.PodcastFeed

fun RssFeedDto.toPodcastFeed(feedUrl: String): PodcastFeed {
    return PodcastFeed(
        feedUrl = feedUrl,
        channel = channel.toPodcastChannel(),
        episodes = channel.item.map { it.toPodcastEpisode() }
    )
}

private fun ChannelDto.toPodcastChannel(): PodcastChannel {
    return PodcastChannel(
        title = title,
        description = description,
        author = author ?: "",
        artworkUrl = image?.href ?: ""
    )
}

private fun ItemDto.toPodcastEpisode(): PodcastEpisode {
    return PodcastEpisode(
        id = guid.ifEmpty { link },
        title = title,
        description = description,
        audioUrl = enclosure?.url ?: "",
        durationSec = duration.toDurationSeconds(),
        artworkUrl = image?.href ?: ""
    )
}

private fun String?.toDurationSeconds(): Int {
    if (this.isNullOrBlank()) return 0

    return when {
        contains(":") -> {
            val parts = split(":").mapNotNull { it.toIntOrNull() }
            when (parts.size) {
                3 -> parts[0] * 3600 + parts[1] * 60 + parts[2]
                2 -> parts[0] * 60 + parts[1]
                else -> 0
            }
        }
        else -> this.toIntOrNull() ?: 0
    }
}

fun PodcastFeedEntity.toDomain(): PodcastFeed {
    return PodcastFeed(
        feedUrl = feedUrl,
        channel = PodcastChannel(
            title = channel.title,
            description = channel.description,
            author = channel.author,
            artworkUrl = channel.artworkUrl
        ),
        episodes = episodes.map { it.toDomain() }
    )
}

fun PodcastEpisodeEntity.toDomain(): PodcastEpisode {
    return PodcastEpisode(
        id = id,
        title = title,
        description = description,
        audioUrl = audioUrl,
        durationSec = durationSec.toInt(),
        artworkUrl = artworkUrl
    )
}

fun PodcastFeed.toEntity(): PodcastFeedEntity {
    return PodcastFeedEntity(
        feedUrl = feedUrl,
        channel = PodcastChannelEntity(
            title = channel.title,
            description = channel.description,
            author = channel.author,
            artworkUrl = channel.artworkUrl
        ),
        episodes = episodes.map { episode ->
            PodcastEpisodeEntity(
                id = episode.id,
                feedUrl = feedUrl,
                title = episode.title,
                description = episode.description,
                audioUrl = episode.audioUrl,
                durationSec = episode.durationSec.toLong(),
                artworkUrl = episode.artworkUrl
            )
        }
    )
}