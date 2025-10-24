package com.ma.tehro.feature.line

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.ma.tehro.R
import com.ma.tehro.common.LineEndpoints
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.common.ui.ExtendableFab
import com.ma.tehro.feature.line.components.BranchSelectionDialog
import com.ma.tehro.feature.line.components.DrawerContent
import com.ma.tehro.feature.line.components.LineItem
import kotlinx.coroutines.launch

@Composable
fun Lines(
    modifier: Modifier = Modifier,
    lines: List<Int>,
    onFindPathClicked: () -> Unit,
    onlineClick: (line: Int, seeBranchStations: Boolean) -> Unit,
    onMapClick: () -> Unit,
    onSubmitFeedbackClick: () -> Unit,
    onPathFinderClick: () -> Unit,
    onMetroMapClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val itemHeight = remember(screenHeight, lines.size) {
        ((screenHeight / (lines.size + 1)).coerceAtLeast(1) * 1.2f).coerceAtLeast(74f)
    }

    val lazyListState = rememberLazyListState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var showBranchDialog by remember { mutableStateOf(false) }
    var selectedLine by remember { mutableStateOf<Int?>(null) }

    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onCityMapClick = {
                    coroutineScope.launch { drawerState.close() }
                    onMapClick()
                },
                onSubmitFeedbackClick = {
                    coroutineScope.launch { drawerState.close() }
                    onSubmitFeedbackClick()
                },
                onPathFinderClick = {
                    coroutineScope.launch { drawerState.close() }
                    onPathFinderClick()
                },
                onLinesClick = {
                    coroutineScope.launch { drawerState.close() }
                },
                onMetroMapClick = {
                    coroutineScope.launch { drawerState.close() }
                    onMetroMapClick()
                },
                onMoreClick = {
                    coroutineScope.launch { drawerState.close() }
                    onMoreClick()
                }
            )
        }
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                Appbar(
                    modifier = Modifier.fillMaxWidth(),
                    fa = "فهرست خطوط",
                    en = "lines list",
                    startIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch { drawerState.open() }
                            },
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .fillMaxHeight()
                                .width(46.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "open drawer"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendableFab(
                    lazyListState = lazyListState,
                    containerColor = MaterialTheme.colorScheme.primary,
                    iconRes = R.drawable.route,
                    faText = "مسیریابی",
                    enText = "ROUTING",
                    onClick = onFindPathClicked,
                    textColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        ) { innerPadding ->
            LazyColumn(
                state = lazyListState,
                contentPadding = innerPadding
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
                item {
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
}