package com.ma.tehro.feature.map.city.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun MapTopBar(
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 38.dp, end = 16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Row(
            modifier = Modifier
                .clip(
                    if (isExpanded) RoundedCornerShape(24.dp) else CircleShape
                )
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                .clickable { isExpanded = !isExpanded }
                .animateContentSize(tween(durationMillis = 300)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                modifier = Modifier.weight(1f),
                visible = isExpanded,
                enter = expandHorizontally(tween(300)) + fadeIn(tween(200)),
                exit = shrinkHorizontally(tween(300)) + fadeOut(tween(200))
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = "نقشه را حرکت دهید، مکان مورد نظر را انتخاب کنید و برای یافتن ایستگاه‌های نزدیک، دکمه پایین را بزنید.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = Icons.Rounded.Info,
                contentDescription = if (isExpanded) "بستن راهنما" else "نمایش راهنما",
            )
        }
    }
}