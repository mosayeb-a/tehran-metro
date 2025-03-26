package com.ma.tehro.ui.train_schedule

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ScheduleDaySelection(
    isSelected: Boolean,
    onClick: () -> Unit,
    label: String,
    enabled: Boolean = true
) {
    val indication: Indication = LocalIndication.current
    val interactionSource =
        remember { MutableInteractionSource() }

    val screenWidth = LocalConfiguration.current.screenWidthDp
    val horizontalPadding = (screenWidth * 0.04).dp
    val verticalPadding = (screenWidth * 0.015).dp
    val cornerRadius = (screenWidth * 0.065).dp

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    !enabled -> MaterialTheme.colorScheme.onPrimary.copy(alpha = .2f)
                    else -> Color.Transparent
                }
            )
            .border(
                width = 1.dp,
                color = when {
                    isSelected -> Color.Transparent
                    !enabled -> MaterialTheme.colorScheme.onPrimary.copy(alpha = .6f)
                    else -> MaterialTheme.colorScheme.onPrimary.copy(alpha = .14f)
                },
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = indication
            ) { onClick() }
        ,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = when {
                isSelected -> MaterialTheme.colorScheme.primary
                !enabled -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onPrimary
            },
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding
            )
        )
    }
}