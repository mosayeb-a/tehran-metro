package com.ma.tehro.common.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.flow.distinctUntilChanged


@Composable
fun ExtendedFab(
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.tertiary,
    disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    iconRes: Int,
    faText: String,
    enText: String,
    onClick: () -> Unit,
) {
    var isExtended by remember { mutableStateOf(true) }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { isScrolling -> isExtended = !isScrolling }
    }

    ExtendedFloatingActionButton(
        modifier = modifier,
        onClick = { if (enabled) onClick() },
        containerColor = if (enabled) containerColor else disabledContainerColor,
        expanded = isExtended,
        icon = {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        },
        text = {
            if (isExtended) {
                BilingualText(
                    fa = faText,
                    en = enText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLine = 2,
                    textAlign = TextAlign.Center,
                )
            }
        }
    )
}
