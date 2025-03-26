package com.ma.tehro.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BilingualText(
    fa: String,
    en: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    maxLine: Int = Int.MAX_VALUE,
    spaceBetween: Dp = (-2).dp
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spaceBetween)
    ) {
        Text(
            text = fa,
            style = style,
            textAlign = TextAlign.Center,
            maxLines = maxLine,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = en,
            style = style,
            maxLines = maxLine,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
