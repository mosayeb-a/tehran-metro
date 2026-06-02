package com.ma.tehro.feature.podcast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.common.ui.EmptyStatesFaces
import com.ma.tehro.common.ui.Message
import com.ma.tehro.domain.podcast.PodcastFeed
import com.ma.tehro.feature.podcast.components.ChannelCard
import com.ma.tehro.feature.podcast.components.EpisodeSheet
import com.ma.tehro.feature.podcast.components.FailureBar
import com.ma.tehro.feature.podcast.components.PodcastSlider
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastList(
    onBack: () -> Unit,
    viewModel: PodcastViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedFeed by remember { mutableStateOf<PodcastFeed?>(null) }
    var sheetSearchQuery by remember { mutableStateOf("") }
    var isSheetOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                Appbar(
                    fa = "پادکست‌ها",
                    en = "podcasts",
                    onBackClick = onBack
                )
                AnimatedVisibility(
                    visible = !state.isLoading && state.failedCount > 0 && state.failedCount < allFeedUrls.size,
                    enter = expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = tween(250, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(250)),
                    exit = shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(200, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(200))
                ) {
                    FailureBar(
                        failedCount = state.failedCount,
                        onRetry = { viewModel.refresh() }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                !state.isLoading && state.failedCount >= allFeedUrls.size && state.feeds.isEmpty() -> {
                    Message(
                        faMessage = "خطایی رخ داده، لطفا دوباره تلاش کنید!",
                        faces = EmptyStatesFaces.sad,
                        actionText = "تلاش دوباره",
                        onAction = { viewModel.refresh() }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    ) {
                        if (state.randomEpisodes.isNotEmpty()) {
                            item {
                                PodcastSlider(
                                    modifier = Modifier.padding(top = 16.dp),
                                    episodes = state.randomEpisodes,
                                    onItemClick = { index ->
                                        val episode = state.randomEpisodes[index]
                                    }
                                )
                            }

                            item { Spacer(modifier = Modifier.height(18.dp)) }
                        }

                        if (state.feeds.isNotEmpty()) {
                            item {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    text = "کانال‌ها",
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.End
                                )
                            }

                            items(state.feeds) { feed ->
                                ChannelCard(
                                    feed = feed,
                                    onClick = {
                                        selectedFeed = feed
                                        sheetSearchQuery = ""
                                        isSheetOpen = true
                                    },
                                )
                            }
                        }
                    }
                }
            }

            when {
                state.feeds.isEmpty() && state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.feeds.isNotEmpty() && state.isLoading -> {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = paddingValues.calculateTopPadding()),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    if (isSheetOpen && selectedFeed != null) {
        EpisodeSheet(
            feed = selectedFeed!!,
            isOpen = isSheetOpen,
            onDismiss = {
                isSheetOpen = false
                sheetSearchQuery = ""
                selectedFeed = null
            },
            onEpisodeSelected = { episode ->
                isSheetOpen = false
                sheetSearchQuery = ""
                selectedFeed = null
            },
            searchQuery = sheetSearchQuery,
            onSearchQueryChanged = { sheetSearchQuery = it }
        )
    }
}