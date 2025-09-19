package com.ma.tehro.feature.shortestpath.guide.components


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.domain.Step

@Composable
fun StepGuideItem(
    modifier: Modifier,
    step: Step,
    lineColor: Int
) {
    val (symbol, message) = when (step) {
        is Step.FirstStation -> {
            val lineNum = step.lineTitle.substringAfter("خط ").substringBefore(":").trim()
            val direction = step.lineTitle.substringAfter(":").trim().takeIf { it.isNotEmpty() }
            ">" to buildString {
                append("وارد ایستگاه ${step.stationName} (خط $lineNum)")
                if (!direction.isNullOrBlank()) append(" و به سمت $direction")
                append(" سوار قطار شوید")
            }
        }

        is Step.ChangeLine -> {
            val lineNum = step.newLineTitle.substringAfter("خط ").substringBefore(":").trim()
            val direction = step.newLineTitle.substringAfter(":").trim().takeIf { it.isNotEmpty() }
            "<>" to buildString {
                append("در ایستگاه ${step.stationName} از قطار پیاده شوید و به سمت ")
                append(direction ?: step.newLineTitle)
                append(" (خط $lineNum) خط عوض کنید")
            }
        }

        is Step.LastStation -> {
            "<" to "در ایستگاه ${step.stationName} از قطار پیاده شوید"
        }

        Step.Destination -> "*" to "شما به مقصد رسیدید"
    }

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
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W500),
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
    }
}