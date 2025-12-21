package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.toFarsiNumber
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineChangeDelaySlider(
    lineChangeDelay: Int,
    onLineChangeDelayChanged: (Int) -> Unit,
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "مدت زمان لازم برای تعویض خط",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.End
    )
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Slider(
            modifier = Modifier
                .weight(0.8f)
                .scale(1f, 0.75f),
            value = lineChangeDelay.toFloat(),
            onValueChange = { onLineChangeDelayChanged(it.roundToInt()) },
            valueRange = 1f..20f,
            steps = 18,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.onBackground,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                inactiveTickColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .5f),
                activeTickColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = " دقیقه",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = " ${lineChangeDelay.toFarsiNumber()}",
            style = MaterialTheme.typography.bodyLarge
                .copy(color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold,fontSize = 18.sp)
        )
    }
}