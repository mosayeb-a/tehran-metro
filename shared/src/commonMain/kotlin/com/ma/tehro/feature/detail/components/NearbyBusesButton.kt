package com.ma.tehro.feature.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.ui.BilingualText

@Composable
fun NearbyBusesButton(
    modifier: Modifier = Modifier,
    lineColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(lineColor)
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.DirectionsBus,
                contentDescription = "اتوبوس‌های نزدیک",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        BilingualText(
            fa = "اتوبوس‌های نزدیک",
            en = "NEARBY BUSES",
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            enSize = 10.sp
        )
    }
}
