package com.ma.tehro.feature.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.domain.BilingualName

@Composable
fun FacilitySection(
    modifier: Modifier = Modifier,
    title: BilingualName,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title.en,
                style = MaterialTheme.typography.labelMedium,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = title.fa,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(Modifier.height(10.dp))
        content()
    }
}