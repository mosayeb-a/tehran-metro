package com.ma.tehro.ui.line

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ma.tehro.R
import com.ma.tehro.common.Appbar
import com.ma.tehro.common.LineEndpoints
import com.ma.tehro.common.calculateLineName
import com.ma.tehro.common.createBilingualMessage
import com.ma.tehro.common.getLineColorByNumber

@Composable
fun Lines(
    lines: List<Int>,
    onFindPathClicked: () -> Unit,
    onlineClick: (line: Int, seeBranchStations: Boolean) -> Unit,
    onMapClick: () -> Unit,
    onNewSubmitInfoStationClicked: () -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val itemHeight = remember(screenHeight, lines.size) {
        ((screenHeight / (lines.size + 1)
            .coerceAtLeast(1)) * 1.2f).coerceAtLeast(74f)
    }
    var showBranchDialog by remember { mutableStateOf(false) }
    var selectedLine by remember { mutableStateOf<Int?>(null) }


    if (showBranchDialog && selectedLine != null) {
        BranchSelectionDialog(
            line = selectedLine!!,
            onDismiss = { showBranchDialog = false },
            onSelect = { useBranch ->
                onlineClick(selectedLine!!, useBranch)
                showBranchDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Appbar(title = "فهرست خطوط\nlines list", modifier = Modifier.weight(1f))
                IconButton(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .size(46.dp),
                    onClick = onMapClick,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.map_24px),
                        contentDescription = "show map screen",
                    )
                }
                IconButton(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .size(46.dp),
                    onClick = { onNewSubmitInfoStationClicked() },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.clarify_24px),
                        contentDescription = "show map screen",
                    )
                }
            }
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
                LineItem(
                    lineNumber = line,
                    lineColor = getLineColorByNumber(line),
                    onClick = {
                        if (LineEndpoints.hasBranch(line)) {
                            selectedLine = line
                            showBranchDialog = true
                        } else {
                            onlineClick(line, false)
                        }
                    },
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


@Composable
fun BranchSelectionDialog(
    line: Int,
    onDismiss: () -> Unit,
    onSelect: (useBranch: Boolean) -> Unit
) {
    val mainInteractionSource = remember { MutableInteractionSource() }
    val branchInteractionSource = remember { MutableInteractionSource() }
    val indication: Indication = LocalIndication.current

    val mainEnEndpoints = remember(line) { LineEndpoints.getEn(line, false) }
    val mainFaEndpoints = remember(line) { LineEndpoints.getFa(line, false) }

    val branchEnEndpoints = remember(line) { LineEndpoints.getEn(line, true) }
    val branchFaEndpoints = remember(line) { LineEndpoints.getFa(line, true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(32.dp),
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = createBilingualMessage(
                    fa = "مسیرتان را انتخاب کنید",
                    en = "SELECT YOUR PATH"
                ),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        },
        containerColor = MaterialTheme.colorScheme.outlineVariant,
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = createBilingualMessage(
                        fa = "${mainFaEndpoints?.first} / ${mainFaEndpoints?.second}",
                        en = "${mainEnEndpoints?.first} / ${mainEnEndpoints?.second}"
                    ),
                    style = MaterialTheme.typography.titleSmall.copy(color = Color.White),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(getLineColorByNumber(line).copy(alpha = .6f))
                        .clickable(
                            interactionSource = mainInteractionSource,
                            indication = indication
                        ) {
                            onSelect(false)
                            onDismiss()
                        }
                        .padding(16.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = createBilingualMessage(
                        fa = "${branchFaEndpoints?.first} / ${branchFaEndpoints?.second}",
                        en = "${branchEnEndpoints?.first} / ${branchEnEndpoints?.second}"
                    ),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.White,
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(getLineColorByNumber(line).copy(alpha = .6f))
                        .clickable(
                            interactionSource = branchInteractionSource,
                            indication = indication
                        ) {
                            onSelect(true)
                            onDismiss()
                        }
                        .padding(16.dp)
                )
            }
        },
        confirmButton = { },
        dismissButton = { }
    )
}