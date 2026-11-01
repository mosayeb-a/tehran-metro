package com.ma.tehro.common.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.toFarsiNumber


@Composable
fun LineBadge(
    lineNumbers: List<Int>,
    modifier: Modifier = Modifier,
    showPrefix: Boolean = true,
    containerColor: Color = Color.White.copy(alpha = 0.08f),
    contentColor: Color = Color.White.copy(alpha = 0.75f),
) {
    if (lineNumbers.isEmpty()) return

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = containerColor
    ) {
        Text(
            text = buildString {
                if (showPrefix) append("خط ")
                append(lineNumbers.joinToString("، ") { it.toFarsiNumber() })
            },
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = contentColor,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}