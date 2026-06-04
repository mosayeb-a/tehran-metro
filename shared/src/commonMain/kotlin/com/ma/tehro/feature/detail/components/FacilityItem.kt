package com.ma.tehro.feature.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.ui.BilingualText

@Composable
fun FacilityChip(
    fa: String,
    en: String,
    icon: ImageVector,
    isAvailable: Boolean
) {
    val alpha = if (isAvailable) 1f else 0.5f

    Surface(
        modifier = Modifier.alpha(alpha),
        shape = RoundedCornerShape(42.dp),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 0.dp,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = icon,
                    contentDescription = fa,
                    tint = MaterialTheme.colorScheme.background
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            BilingualText(
                fa = fa,
                en = en.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.End,
                textColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}