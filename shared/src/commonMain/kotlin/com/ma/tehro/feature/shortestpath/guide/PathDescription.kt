package com.ma.tehro.feature.shortestpath.guide

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
import androidx.compose.material.icons.rounded.PeopleAlt
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.rememberShareManager
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.domain.path.PathStep
import com.ma.tehro.feature.shortestpath.guide.components.StepGuideItem

data class StepText(val symbol: String, val message: String)

@Composable
fun PathDescription(
    steps: List<PathStep>,
    onBackClick: () -> Unit,
) {
    val lastLine by remember(steps) {
        derivedStateOf {
            steps.lastOrNull { it is PathStep.Transfer || it is PathStep.Station }
                ?.let { step ->
                    when (step) {
                        is PathStep.Transfer -> step.line
                        is PathStep.Station -> step.line
                    }
                } ?: 0
        }
    }

    val stepTexts by remember(steps) {
        derivedStateOf {
            val firstTransferIndex = steps.indexOfFirst { it is PathStep.Transfer }

            steps.mapIndexedNotNull { index, step ->
                val isLast = index == steps.lastIndex

                when {
                    step is PathStep.Station && isLast -> StepText(
                        symbol = "<",
                        message = "در ایستگاه ${step.station.translations.fa} از قطار پیاده شوید",
                    )

                    step is PathStep.Station -> null

                    step is PathStep.Transfer && index == firstTransferIndex -> StepText(
                        symbol = ">",
                        message = "وارد ایستگاه ${step.station} (خط ${step.line.toFarsiNumber()}) و به سمت ${step.destination.fa} سوار قطار شوید",
                    )

                    step is PathStep.Transfer -> StepText(
                        symbol = "<>",
                        message = "در ایستگاه ${step.station} از قطار پیاده شوید و به سمت ${step.destination.fa} (خط ${step.line.toFarsiNumber()}) خط عوض کنید",
                    )

                    else -> null
                }
            } + StepText(symbol = "*", message = "شما به مقصد رسیدید")
        }
    }

    val stepsText by remember(stepTexts) {
        derivedStateOf {
            stepTexts.joinToString("\n") { "${it.symbol} ${it.message}" }
        }
    }

    val shareManager = rememberShareManager()

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
                contentAlignment = Alignment.Center,
            ) {
                Button(
                    onClick = {
                        shareManager.shareText(stepsText, "اشتراک‌گذاری مسیر")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
                    contentPadding = PaddingValues(vertical = 18.dp, horizontal = 16.dp),
                ) {
                    Row {
                        Text(
                            text = "اشتراک‌گذاری مسیر",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.W300,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Rounded.PeopleAlt,
                            contentDescription = "share",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                item("first_spacer") { Spacer(Modifier.height(18.dp)) }
                items(stepTexts) { stepText ->
                    StepGuideItem(
                        modifier = Modifier.clickable {},
                        symbol = stepText.symbol,
                        message = stepText.message,
                        lineColor = lastLine,
                    )
                }
                item("last_spacer") { Spacer(Modifier.height(58.dp)) }
            }
        }
    }
}