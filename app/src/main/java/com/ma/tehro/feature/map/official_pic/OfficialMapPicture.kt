package com.ma.tehro.feature.map.official_pic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.size.Size
import com.ma.tehro.R
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.feature.map.official_pic.zoombox.gesture.condition.WithinXBoundsTouchCondition
import com.ma.tehro.feature.map.official_pic.zoombox.gesture.transform.TransformGestureHandler
import com.ma.tehro.feature.map.official_pic.zoombox.zoomable

@Composable
fun OfficialMapPicture(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val context = LocalContext.current
    Scaffold(
        modifier = modifier,
        topBar = {
            Appbar(
                fa  = "نقشه مترو",
                en  = "metro map",
                backgroundColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                handleBack = true,
                onBackClick = onBack
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(R.drawable.map)
                    .size(Size.ORIGINAL)
                    .build(),
                contentDescription = "metro map pic",
                modifier = Modifier
                    .fillMaxSize()
                    .zoomable(
                        zoomRange = 1f..10f,
                        transformGestureHandler = TransformGestureHandler(
                            onCondition = WithinXBoundsTouchCondition()
                        )
                    )
            )
        }
    }
}
