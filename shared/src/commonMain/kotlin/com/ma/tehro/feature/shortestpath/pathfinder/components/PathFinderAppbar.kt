package com.ma.tehro.feature.shortestpath.pathfinder.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.common.ui.BilingualText
import com.ma.tehro.common.ui.TehroHorizontalDivider
import com.ma.tehro.domain.common.BilingualName

@Composable
fun PathFinderAppbar(
    modifier: Modifier = Modifier,
    from: BilingualName,
    to: BilingualName,
    onBack: () -> Unit,
    estimatedTime: BilingualName?,
    lineChangeDelayMinutes: Int,
    warningMessage: String?
) {
    Column(modifier) {
        Appbar(
            fa = "مسیر پیشنهادی",
            en = "Suggested Path",
            onBackClick = onBack
        )
        RouteHeader(from = from, to = to)
        TehroHorizontalDivider()

        warningMessage?.let {
            WarningBanner(message = it)
        }

        estimatedTime?.let {
            EstimatedTimeDisplay(
                estimatedTime = estimatedTime,
                lineChangeDelayMinutes = lineChangeDelayMinutes,
            )
        }
    }
}

@Composable
private fun WarningBanner(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = message,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EstimatedTimeDisplay(
    estimatedTime: BilingualName?,
    lineChangeDelayMinutes: Int
) {
    estimatedTime?.let {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "ESTIMATED TIME",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .9f)
                    ),
                )
                Text(
                    text = estimatedTime.en,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .9f),
                        fontSize = 11.sp,
                    ),
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "زمان تقریبی (تعویض خط ${lineChangeDelayMinutes.toFarsiNumber()} دقیقه)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = .9f),
                    style = MaterialTheme.typography.labelSmall,
                )
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = estimatedTime.fa,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .9f),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun RouteHeader(
    modifier: Modifier = Modifier,
    from: BilingualName,
    to: BilingualName
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(bottom = 4.dp, start = 4.dp, end = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BilingualText(
            fa = to.fa,
            en = to.en.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            enAlpha = .7f,
            enSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLine = 1
        )
        Icon(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .size(16.dp),
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
        )
        BilingualText(
            fa = from.fa,
            en = from.en.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            enAlpha = .7f,
            enSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLine = 1
        )
    }
}
