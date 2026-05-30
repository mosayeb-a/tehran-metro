package com.ma.tehro.feature.podcast.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselDefaults
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.ma.tehro.common.formatDuration
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.domain.podcast.PodcastEpisode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastSlider(
    modifier: Modifier = Modifier,
    episodes: List<PodcastEpisode>,
    onItemClick: (Int) -> Unit
) {
    val state = rememberCarouselState { episodes.size }
    val context = LocalPlatformContext.current

    HorizontalCenteredHeroCarousel(
        state = state,
        modifier = modifier.fillMaxWidth(),
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 24.dp),
        flingBehavior = CarouselDefaults.singleAdvanceFlingBehavior(state = state)
    ) { index ->
        val episode = episodes[index]

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onItemClick(index) },
            contentAlignment = Alignment.BottomStart
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(context)
                    .crossfade(true)
                    .data(episode.artworkUrl)
                    .build(),
                contentDescription = episode.title,
                contentScale = ContentScale.Crop,
                onError = { println("image error for: ${episode.title}") }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .align(Alignment.BottomEnd)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.2f),
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.7f),
                                Color.Black.copy(alpha = 0.9f)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = episode.title,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Text(
                        text = formatDuration(episode.durationSec).toFarsiNumber(),
                        color = Color.White,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}