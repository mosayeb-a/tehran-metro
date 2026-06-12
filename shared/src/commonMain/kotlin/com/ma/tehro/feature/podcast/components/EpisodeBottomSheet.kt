package com.ma.tehro.feature.podcast.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.ma.tehro.common.formatDuration
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.common.ui.SearchableBottomSheet
import com.ma.tehro.domain.podcast.PodcastEpisode
import com.ma.tehro.domain.podcast.PodcastFeed

@Composable
fun EpisodeSheet(
    feed: PodcastFeed,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onEpisodeSelected: (PodcastEpisode) -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit
) {
    val context = LocalPlatformContext.current
    val filteredEpisodes = if (searchQuery.isBlank()) {
        feed.episodes
    } else {
        feed.episodes.filter { episode ->
            episode.title.contains(searchQuery, ignoreCase = true)
        }
    }

    SearchableBottomSheet(
        items = filteredEpisodes,
        searchQuery = searchQuery,
        onSearchQueryChanged = onSearchQueryChanged,
        onItemSelected = { episode ->
            onEpisodeSelected(episode)
            onDismiss()
        },
        searchPlaceholder = "جستجوی اپیزود...",
        itemKey = { it.id },
        onDismiss = onDismiss,
        itemContent = { episode ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = episode.title,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        textAlign = TextAlign.End
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = formatDuration(episode.durationSec).toFarsiNumber(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End
                    )
                }

                Spacer(Modifier.width(12.dp))

                AsyncImage(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    model = ImageRequest.Builder(context)
                        .crossfade(true)
                        .data(episode.artworkUrl)
                        .build(),
                    contentDescription = episode.title,
                    contentScale = ContentScale.Crop
                )
            }
        }
    )
}