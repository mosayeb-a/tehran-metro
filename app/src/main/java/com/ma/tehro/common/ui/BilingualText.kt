package com.ma.tehro.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BilingualText(
    fa: String,
    en: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    maxLine: Int = Int.MAX_VALUE,
    spaceBetween: Dp = (-2).dp,
    enSize: TextUnit = 11.sp,
    enAlpha: Float = .9f,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
) {
    val linesPerText =
        if (maxLine == Int.MAX_VALUE) Int.MAX_VALUE else (maxLine / 2).coerceAtLeast(1)

    val horizontalAlignment = when (textAlign) {
        TextAlign.Center -> Alignment.CenterHorizontally
        TextAlign.Start, TextAlign.Left -> Alignment.Start
        TextAlign.End, TextAlign.Right -> Alignment.End
        else -> Alignment.CenterHorizontally
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spaceBetween),
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            text = fa,
            style = style.copy(
                color = Color.White,
                fontWeight = FontWeight.W500
            ),
            textAlign = textAlign,
            maxLines = linesPerText,
            overflow = overflow,
        )
        Text(
            text = en,
            style = style.copy(
                fontSize = enSize,
                color = Color.White.copy(alpha = enAlpha)
            ),
            textAlign = textAlign,
            maxLines = linesPerText,
            overflow = overflow,
        )
    }
}