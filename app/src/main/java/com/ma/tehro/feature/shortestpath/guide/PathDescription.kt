package com.ma.tehro.feature.shortestpath.guide

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.domain.Step
import com.ma.tehro.feature.shortestpath.guide.components.StepGuideItem

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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondary,
        topBar = {
            Appbar(
                fa = "راهنمای مسیر",
                en = "Path Description",
                handleBack = true,
                onBackClick = onBackClick,
            ) {
                IconButton(onClick ={} ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "share",
                        tint = Color.White
                    )
                }
            }
        },
    ) { innerPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                item("first_spacer") { Spacer(Modifier.height(18.dp)) }
                items(steps) { step ->
                    StepGuideItem(
                        modifier = Modifier.clickable {},
                        step = step,
                        lineColor = lastLine
                    )
                }
                item("last_spacer") { Spacer(Modifier.height(58.dp)) }
            }
        }
    }
}