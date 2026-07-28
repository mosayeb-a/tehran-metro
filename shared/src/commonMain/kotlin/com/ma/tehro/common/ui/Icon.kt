package com.ma.tehro.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ma.thero.resources.Res
import com.ma.thero.resources.icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun TehroIcon(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    cornerRadius: Dp = 16.dp,
    backgroundColor: Color? = null,
) {
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                shape = shape
            )
            .then(
                if (backgroundColor != null) {
                    Modifier.background(backgroundColor)
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.icon),
            contentDescription = null,
            modifier = Modifier
                .padding(2.dp)
                .fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}