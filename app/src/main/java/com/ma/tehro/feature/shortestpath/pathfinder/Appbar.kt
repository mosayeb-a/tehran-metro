package com.ma.tehro.feature.shortestpath.pathfinder

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.R
import com.ma.tehro.common.Appbar
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.data.BilingualName

@Composable
fun Appbar(
    modifier: Modifier = Modifier,
    fromEn: String,
    toEn: String,
    fromFa: String,
    toFa: String,
    onBack: () -> Unit,
    onPathGuideClick: () -> Unit,
    estimatedTime: BilingualName?,
    lineChangeDelayMinutes: Int
) {
    Column(modifier) {
        Appbar(
            fa = "مسیر پیشنهادی",
            en = "Suggested Path",
            handleBack = true,
            onBackClick = onBack
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable { onPathGuideClick() }
                    .padding(end = 16.dp)
                    .padding(start = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "راهنمای مسیر",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "info",
                    modifier = Modifier.size(22.dp),
                )
            }
        }
        AppbarDetail(fromEn = fromEn, toEn = toEn, fromFa = fromFa, toFa = toFa)
        HorizontalDivider()
        estimatedTime?.let {
            EstimatedTimeDisplay(
                estimatedTime = estimatedTime,
                lineChangeDelayMinutes = lineChangeDelayMinutes,
            )
        }
    }
}

@Composable
fun EstimatedTimeDisplay(estimatedTime: BilingualName?, lineChangeDelayMinutes: Int) {
    estimatedTime?.let {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
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
                        color = MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = .9f
                        )
                    ),
                )
                Text(
                    text = estimatedTime.en,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = .9f
                        ),
                    ),
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "زمان تقریبی (تعویض خط ${lineChangeDelayMinutes.toFarsiNumber()} دقیقه)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = .9f
                        )
                    ),
                )
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = estimatedTime.fa,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onPrimary.copy(
                                alpha = .9f
                            )
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun AppbarDetail(
    modifier: Modifier = Modifier,
    fromEn: String,
    toEn: String,
    fromFa: String,
    toFa: String,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(bottom = 4.dp, start = 4.dp, end = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = toFa,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = toEn.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .size(16.dp),
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Going to ..",
            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = fromFa,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = fromEn.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
