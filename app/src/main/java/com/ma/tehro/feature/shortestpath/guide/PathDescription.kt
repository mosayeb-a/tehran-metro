package com.ma.tehro.feature.shortestpath.guide

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.common.getLineColorByNumber

@Composable
fun PathDescription(viewState: PathDescriptionState, onBackClick: () -> Unit) {
    Column {
        Appbar(
            fa = "راهنمای مسیر",
            en = "Path Description",
            handleBack = true,
            onBackClick = onBackClick,
        )
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            LazyColumn(modifier = Modifier) {
                item("first_spacer") { Spacer(Modifier.height(18.dp)) }
                items(viewState.steps) { step ->
                    StepGuideItem(
                        modifier = Modifier.clickable {},
                        step = step,
                        lineColor = viewState.lastLine
                    )
                }
                item("last_spacer") { Spacer(Modifier.height(58.dp)) }
            }
        }
    }
}


@Composable
fun StepGuideItem(modifier: Modifier, step: String, lineColor: Int) {
    val symbol = step.take(if (step.startsWith("<>")) 2 else 1)
    val message = step.drop(if (step.startsWith("<>")) 2 else 1).trim()

    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = symbol,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 28.sp
            ),
            color = getLineColorByNumber(lineColor),
            modifier = Modifier
                .width(48.dp)
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.W500),
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
    }
}
