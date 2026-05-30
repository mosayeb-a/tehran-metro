package com.ma.tehro.data.podcast.repository.source.local

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.db.SqlDriver
import com.ma.thero.db.TehroDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class PodcastLocalDataSourceImpl(
    driver: SqlDriver
) : PodcastLocalDataSource {

    private val db = TehroDatabase(driver)

    override suspend fun insertFeed(feed: PodcastFeedEntity): Result<Unit> {
        return try {
            db.podcastQueries.insertOrReplaceChannel(
                feed_url = feed.feedUrl,
                title = feed.channel.title,
                description = feed.channel.description,
                author = feed.channel.author,
                artwork_url = feed.channel.artworkUrl
            )
            feed.episodes.forEach { episode ->
                db.podcastQueries.insertOrReplaceEpisode(
                    id = episode.id,
                    feed_url = feed.feedUrl,
                    title = episode.title,
                    description = episode.description,
                    audio_url = episode.audioUrl,
                    duration_sec = episode.durationSec,
                    artwork_url = episode.artworkUrl
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override fun getAllFeeds(): Flow<List<PodcastFeedEntity>> {
        val channelsFlow = db.podcastQueries.getAllChannels()
            .asFlow()
            .map { query ->
                query.executeAsList().associate { entity ->
                    entity.feed_url to PodcastChannelEntity(
                        title = entity.title,
                        description = entity.description,
                        author = entity.author,
                        artworkUrl = entity.artwork_url
                    )
                }
            }

        val episodesFlow = db.podcastQueries.getAllChannels()
            .asFlow()
            .map { query ->
                query.executeAsList().flatMap { channelEntity ->
                    db.podcastQueries.getEpisodesByFeedUrl(channelEntity.feed_url)
                        .executeAsList()
                        .map { episodeEntity ->
                            episodeEntity.feed_url to PodcastEpisodeEntity(
                                id = episodeEntity.id,
                                feedUrl = episodeEntity.feed_url,
                                title = episodeEntity.title,
                                description = episodeEntity.description,
                                audioUrl = episodeEntity.audio_url,
                                durationSec = episodeEntity.duration_sec,
                                artworkUrl = episodeEntity.artwork_url ?: ""
                            )
                        }
                }.groupBy({ it.first }, { it.second })
            }

        return combine(channelsFlow, episodesFlow) { channels, episodes ->
            channels.map { (feedUrl, channel) ->
                PodcastFeedEntity(
                    feedUrl = feedUrl,
                    channel = channel,
                    episodes = episodes[feedUrl] ?: emptyList()
                )
            }
        }
    }

    override fun getFeedByUrl(feedUrl: String): Flow<PodcastFeedEntity?> {
        val channelFlow = db.podcastQueries.getAllChannels()
            .asFlow()
            .map { query ->
                query.awaitAsList()
                    .find { it.feed_url == feedUrl }
                    ?.let { entity ->
                        PodcastChannelEntity(
                            title = entity.title,
                            description = entity.description,
                            author = entity.author,
                            artworkUrl = entity.artwork_url
                        )
                    }
            }

        val episodesFlow = db.podcastQueries.getEpisodesByFeedUrl(feedUrl)
            .asFlow()
            .map { query ->
                query.awaitAsList().map { entity ->
                    PodcastEpisodeEntity(
                        id = entity.id,
                        feedUrl = entity.feed_url,
                        title = entity.title,
                        description = entity.description,
                        audioUrl = entity.audio_url,
                        durationSec = entity.duration_sec,
                        artworkUrl = entity.artwork_url ?: ""
                    )
                }
            }

        return combine(channelFlow, episodesFlow) { channel, episodes ->
            channel?.let {
                PodcastFeedEntity(
                    feedUrl = feedUrl,
                    channel = it,
                    episodes = episodes
                )
            }
        }
    }
}