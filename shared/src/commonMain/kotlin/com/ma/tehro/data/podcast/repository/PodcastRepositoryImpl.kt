package com.ma.tehro.data.podcast.repository

import com.ma.tehro.data.podcast.repository.source.local.PodcastLocalDataSource
import com.ma.tehro.data.podcast.repository.source.remote.PodcastRemoteDataSource
import com.ma.tehro.data.podcast.toDomain
import com.ma.tehro.data.podcast.toEntity
import com.ma.tehro.data.podcast.toPodcastFeed
import com.ma.tehro.domain.podcast.PodcastFeed
import com.ma.tehro.domain.podcast.repository.PodcastRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class PodcastRepositoryImpl(
    private val remoteDataSource: PodcastRemoteDataSource,
    private val localDataSource: PodcastLocalDataSource
) : PodcastRepository {

    override suspend fun get(
        feedUrl: String
    ): Flow<PodcastFeed?> = flow {
        val localFeed = localDataSource
            .getFeedByUrl(feedUrl)
            .first()

        if (localFeed != null) {
            emit(localFeed.toDomain())
            return@flow
        }

        remoteDataSource.fetch(feedUrl)
            .onSuccess { dto ->
                val feed = dto.toPodcastFeed(feedUrl)

                localDataSource.insertFeed(
                    feed.toEntity()
                )
                emit(feed)
            }
            .onFailure {
                emit(null)
            }
    }

    override suspend fun getAll(
        feedUrls: List<String>
    ): Flow<Result<List<PodcastFeed>>> = flow {
        feedUrls.forEach { feedUrl ->
            try {
                val localFeed = localDataSource
                    .getFeedByUrl(feedUrl)
                    .first()

                if (localFeed == null) {
                    remoteDataSource.fetch(feedUrl)
                        .onSuccess { dto ->
                            localDataSource.insertFeed(
                                dto.toPodcastFeed(feedUrl)
                                    .toEntity()
                            )
                        }
                        .onFailure { error ->
                            emit(
                                Result.failure(
                                    Exception(error)
                                )
                            )
                            return@forEach
                        }
                }

                emit(
                    Result.success(
                        localDataSource
                            .getAllFeeds()
                            .first()
                            .map { it.toDomain() }
                    )
                )
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }
    }
}