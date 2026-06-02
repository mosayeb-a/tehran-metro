package com.ma.tehro.feature.podcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.domain.podcast.PodcastEpisode
import com.ma.tehro.domain.podcast.PodcastFeed
import com.ma.tehro.domain.podcast.repository.PodcastRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PodcastUiState(
    val isLoading: Boolean = false,
    val feeds: List<PodcastFeed> = emptyList(),
    val randomEpisodes: List<PodcastEpisode> = emptyList(),
    val failedCount: Int = 0
)

val allFeedUrls = listOf(
    "https://rss.castbox.fm/everest/79e872f2347e451ca4d061d73ba1b4c8.xml", // dialoguebox
    "https://anchor.fm/s/35a3e08c/podcast/rss", // b plus
    "https://rss.castbox.fm/everest/a5e1031c4a5c4dc39564f2978c2bb837.xml", // farcast
    "https://feeds.acast.com/public/shows/651e9d60130f3400115df9a0", // channel b
    "https://feeds.acast.com/public/shows/69c76b5188f1e89132a239bd", // kashkool?
    "https://feeds.acast.com/public/shows/6722689777f0e7cbfb9e3a37", // fuse
    "https://feeds.acast.com/public/shows/625aee4922dee40012cfd01e", // alef
    "https://rss.iono.fm/rss/chan/2303",
    "https://anchor.fm/s/91e1a78/podcast/rss",
    "https://anchor.fm/s/6b2b51cc/podcast/rss",
    "https://feeds.acast.com/public/shows/640dc802ae21760011765ff6"
)

class PodcastViewModel(
    private val repository: PodcastRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PodcastUiState(isLoading = true))
    val state: StateFlow<PodcastUiState> = _state.asStateFlow()

    private val failedUrls = mutableSetOf<String>()
    private val processedFeedUrls = mutableSetOf<String>()

    init {
        loadAllFeeds()
    }

    fun loadAllFeeds() {
        viewModelScope.launch {
            failedUrls.clear()
            _state.update { it.copy(isLoading = true) }

            var processedCount = 0
            var failedCount = 0

            repository.getAll(allFeedUrls).collect { result ->
                val currentUrl = allFeedUrls.getOrNull(processedCount)
                processedCount++

                result.onSuccess { feeds ->
                    println("success: ${feeds.size} feeds")

                    val finished = processedCount >= allFeedUrls.size
                    val newFeeds = feeds.filter { feed -> feed.feedUrl !in processedFeedUrls }
                    newFeeds.forEach { feed -> processedFeedUrls.add(feed.feedUrl) }

                    val newEpisodes = newFeeds
                        .flatMap { it.episodes }
                        .shuffled()
                        .take(6)

                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = !finished,
                            feeds = feeds,
                            randomEpisodes = currentState.randomEpisodes + newEpisodes,
                            failedCount = if (finished) failedCount else 0
                        )
                    }
                }

                result.onFailure { _ ->
                    failedCount++
                    currentUrl?.let { failedUrls += it }
                    val finished = processedCount >= allFeedUrls.size

                    println("failed: $currentUrl")

                    _state.update {
                        it.copy(
                            isLoading = !finished,
                            failedCount = if (finished) failedCount else 0
                        )
                    }
                }
            }
        }
    }

    fun refresh() {
        loadAllFeeds()
    }
}