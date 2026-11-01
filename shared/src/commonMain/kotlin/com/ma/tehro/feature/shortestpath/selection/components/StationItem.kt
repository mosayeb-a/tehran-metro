package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.ui.BilingualText
import com.ma.tehro.common.ui.LineBadge
import com.ma.tehro.domain.line.Station

@Composable
fun StationItem(
    station: Station,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier.fillMaxWidth()) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        keyboardController?.hide()
                        onClick()
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BilingualText(
                    modifier = Modifier.weight(1f),
                    fa = station.translations.fa,
                    en = station.name.uppercase(),
                    spaceBetween = 0.dp,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLine = 2,
                    textAlign = TextAlign.Start
                )

                val lineNumbers = station.lines.ifEmpty {
                    station.positionsInLine.map { it.line }.distinct()
                }
                if (lineNumbers.isNotEmpty()) {
                    Spacer(Modifier.width(12.dp))
                    LineBadge(lineNumbers = lineNumbers)
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f)
        )
    }
}