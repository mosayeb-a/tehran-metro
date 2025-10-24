package com.ma.tehro.feature.shortestpath.guide

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.domain.Step
import com.ma.tehro.feature.shortestpath.guide.components.StepGuideItem

data class StepText(val symbol: String, val message: String)

@Composable
fun PathDescription(steps: List<Step>, onBackClick: () -> Unit) {
    val lastLine by remember(steps) {
        derivedStateOf {
            val lastChangeOrFirst = steps.lastOrNull {
                it is Step.ChangeLine || it is Step.FirstStation
            }
            when (lastChangeOrFirst) {
                is Step.ChangeLine -> lastChangeOrFirst.newLineTitle
                is Step.FirstStation -> lastChangeOrFirst.lineTitle
                else -> ""
            }
                .substringAfter("خط ")
                .substringBefore(":")
                .trim()
                .toIntOrNull() ?: 0
        }
    }
    val stepTexts by remember(steps) {
        derivedStateOf {
            steps.map { step ->
                when (step) {
                    is Step.FirstStation -> {
                        val lineNum =
                            step.lineTitle.substringAfter("خط ").substringBefore(":").trim()
                        val direction = step.lineTitle.substringAfter(":").trim()
                            .replace("به سمت ", "")
                            .takeIf { it.isNotEmpty() }
                        StepText(
                            symbol = ">",
                            message = buildString {
                                append("وارد ایستگاه ${step.stationName} (خط $lineNum)")
                                if (!direction.isNullOrBlank()) append(" و به سمت $direction")
                                append(" سوار قطار شوید")
                            }
                        )
                    }

                    is Step.ChangeLine -> {
                        val lineNum =
                            step.newLineTitle.substringAfter("خط ").substringBefore(":").trim()
                        val direction = step.newLineTitle.substringAfter(":").trim()
                            .replace("به سمت ", "")
                            .takeIf { it.isNotEmpty() }
                        StepText(
                            symbol = "<>",
                            message = buildString {
                                append("در ایستگاه ${step.stationName} از قطار پیاده شوید و به سمت ")
                                append(direction ?: step.newLineTitle)
                                append(" (خط $lineNum) خط عوض کنید")
                            }
                        )
                    }

                    is Step.LastStation -> StepText(
                        symbol = "<",
                        message = "در ایستگاه ${step.stationName} از قطار پیاده شوید"
                    )

                    Step.Destination -> StepText(
                        symbol = "*",
                        message = "شما به مقصد رسیدید"
                    )
                }
            }
        }
    }

    val stepsText by remember(stepTexts) {
        derivedStateOf {
            stepTexts.joinToString("\n") { "${it.symbol} ${it.message}" }
        }
    }

    val context = LocalContext.current
    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondary,
        topBar = {
            Appbar(
                fa = "راهنمای مسیر",
                en = "Path Description",
                onBackClick = onBackClick,
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, stepsText)
                            type = "text/plain"
                        }
                        context.startActivity(
                            Intent.createChooser(sendIntent, "share path")
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
                    contentPadding = PaddingValues(vertical = 18.dp, horizontal = 16.dp)
                ) {
                    Row {
                        Text(
                            text = "اشتراک‌گذاری مسیر",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.W300,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.PeopleAlt,
                            contentDescription = "share",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                item("first_spacer") { Spacer(Modifier.height(18.dp)) }
                items(stepTexts) { stepText ->
                    StepGuideItem(
                        modifier = Modifier.clickable {},
                        symbol = stepText.symbol,
                        message = stepText.message,
                        lineColor = lastLine
                    )
                }
                item("last_spacer") { Spacer(Modifier.height(58.dp)) }
            }
        }
    }
}