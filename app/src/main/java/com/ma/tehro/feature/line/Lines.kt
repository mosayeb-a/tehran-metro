package com.ma.tehro.feature.line

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.R
import com.ma.tehro.common.Appbar
import com.ma.tehro.common.LineEndpoints
import com.ma.tehro.common.calculateBilingualLineName
import com.ma.tehro.common.createBilingualMessage
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.feature.theme.Blue
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun Lines(
    modifier: Modifier = Modifier,
    lines: List<Int>,
    onFindPathClicked: () -> Unit,
    onlineClick: (line: Int, seeBranchStations: Boolean) -> Unit,
    onMapClick: () -> Unit,
    onSubmitFeedbackClick: () -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val itemHeight = remember(screenHeight, lines.size) {
        ((screenHeight / (lines.size + 1)
            .coerceAtLeast(1)) * 1.2f).coerceAtLeast(74f)
    }
    var showBranchDialog by remember { mutableStateOf(false) }
    var selectedLine by remember { mutableStateOf<Int?>(null) }

    val lazyListState = rememberLazyListState()
    var isExtended by remember { mutableStateOf(true) }
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { isScrolling ->
                isExtended = !isScrolling
            }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Appbar(
                title = "فهرست خطوط\nlines list",
                backgroundColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                content = {
                    IconButton(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .fillMaxHeight()
                            .width(46.dp),
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
                            .fillMaxHeight()
                            .width(46.dp),
                        onClick = { onSubmitFeedbackClick() },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.help_fill),
                            contentDescription = "submit feedback",
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onFindPathClicked,
                containerColor = Blue,
                expanded = isExtended,
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.route),
                        contentDescription = "path finder",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                },
                text = {
                    if (isExtended) {
                        Text(
                            text = createBilingualMessage(fa = "مسیریابی", en = "ROUTING"),
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            )
        }
    ) { innerpadding ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.padding(innerpadding)
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
                Spacer(modifier = Modifier.height(itemHeight.dp - (itemHeight.dp / 4)))
            }
        }
    }

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
}

@Composable
fun LineItem(
    lineNumber: Int,
    lineColor: Color,
    onClick: () -> Unit,
    itemHeight: Float
) {
    val lineName = calculateBilingualLineName(lineNumber)
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
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = lineName.fa,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600
                ),
                color = Color.White
            )
            Text(
                text = lineName.en,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.W400
                ),
                color = Color.White.copy(alpha = 0.9f)
            )
        }
        Icon(
            painter = painterResource(R.drawable.arrow_forward_24px),
            contentDescription = "See stations by line",
            tint = Color.White
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
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

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 22.dp, horizontal = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = createBilingualMessage(
                        fa = "انتخاب مسیر",
                        en = "SELECT PATH"
                    ),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                PathSelectionItem(
                    faEndpoints = mainFaEndpoints,
                    enEndpoints = mainEnEndpoints,
                    backgroundColor = getLineColorByNumber(line).copy(alpha = .9f),
                    interactionSource = mainInteractionSource,
                    indication = indication,
                    onClick = {
                        onSelect(false)
                        onDismiss()
                    }
                )

                PathSelectionItem(
                    faEndpoints = branchFaEndpoints,
                    enEndpoints = branchEnEndpoints,
                    backgroundColor = getLineColorByNumber(line),
                    interactionSource = branchInteractionSource,
                    indication = indication,
                    onClick = {
                        onSelect(true)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun PathSelectionItem(
    faEndpoints: Pair<String, String>?,
    enEndpoints: Pair<String, String>?,
    backgroundColor: Color,
    interactionSource: MutableInteractionSource,
    indication: Indication,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(36.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = indication
            ) {
                onClick()
            }
            .padding(10.dp)
    ) {
        Text(
            text = createBilingualMessage(
                fa = "${faEndpoints?.first} / ${faEndpoints?.second}",
                en = "${enEndpoints?.first} / ${enEndpoints?.second}"
            ),
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Icon(
            painter = painterResource(R.drawable.arrow_forward_24px),
            contentDescription = "See stations by line",
            tint = Color.White,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(24.dp)
                .align(Alignment.CenterEnd)
        )
    }
}