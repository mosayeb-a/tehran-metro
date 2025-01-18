package com.ma.tehro.ui.line

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ma.tehro.R
import com.ma.tehro.common.Appbar
import com.ma.tehro.common.StationsScreen
import com.ma.tehro.common.calculateLineName
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.common.getLineEnEndpoints
import com.ma.tehro.common.getLineFaEndpoints

@Composable
fun Lines(
    navController: NavHostController,
    lines: List<Int>,
    onFindPathClicked: () -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val itemHeight = ((screenHeight / (lines.size + 1).coerceAtLeast(1)) * 1.2f)
        .coerceAtLeast(74f)

    Scaffold(
        topBar = {
            Appbar("فهرست خطوط\nlines list")
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(62.dp),
                onClick = onFindPathClicked,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = FloatingActionButtonDefaults.largeShape,
            ) {
                Icon(
                    painter = painterResource(R.drawable.route),
                    contentDescription = "Find Shortest Path",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            items(lines, key = { it }) { line ->
                println("itemHeight: $itemHeight")
                LineItem(
                    lineNumber = line,
                    lineColor = getLineColorByNumber(line),
                    onClick = { navController.navigate(StationsScreen(line)) },
                    itemHeight = itemHeight
                )
            }
            item("spacer") {
                Spacer(modifier = Modifier.height(itemHeight.dp - (itemHeight.dp / 3)))
            }
        }
    }
}

@Composable
fun LineItem(
    lineNumber: Int,
    lineColor: Color,
    onClick: () -> Unit,
    itemHeight: Float
) {
    val lineName = calculateLineName(lineNumber)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight.dp)
            .background(color = lineColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = lineName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            painter = painterResource(R.drawable.arrow_forward_24px),
            contentDescription = "See stations by line",
            tint = Color.White
        )
    }
}

