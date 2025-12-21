package com.ma.tehro.feature.train_schedule.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.TimeUtils.remainingTime
import com.ma.tehro.common.fractionToTime
import com.ma.tehro.common.toFarsiNumber

@Composable
fun TimeListItem(
    time: Double,
    currentTimeAsDouble: Double,
    isCurrentDaySchedule: Boolean,
    isFirstActiveTime: Boolean,
    modifier: Modifier = Modifier,
) {
    val isPastTime = time <= currentTimeAsDouble
    val contentAlpha = when {
        !isCurrentDaySchedule -> 0.5f
        isPastTime -> 0.5f
        else -> 1f
    }

    val remainingTimeText = when {
        !isCurrentDaySchedule -> ""
        isPastTime -> ""
        isFirstActiveTime -> {
            LaunchedEffect(Unit) {
                snapshotFlow { remainingTime(time) }

                    .collect { }
            }
            remainingTime(time)
        }

        else -> {
            remember(time) {
                remainingTime(time)
            }
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isFirstActiveTime && isCurrentDaySchedule)
                    MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                else Color.Transparent
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "ARRIVES AT ${fractionToTime(time)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
            )
            Spacer(Modifier.height(4.dp))
            if (remainingTimeText.isNotEmpty()) {
                Text(
                    text = "$remainingTimeText REMAINING",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isFirstActiveTime)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "ساعت ${fractionToTime(time).toFarsiNumber()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
            )
            Spacer(Modifier.height(4.dp))
            if (remainingTimeText.isNotEmpty()) {
                Text(
                    text = "زمان باقی مانده ${remainingTimeText.toFarsiNumber()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isFirstActiveTime)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
                )
            }
        }
    }
    HorizontalDivider()
}