package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.ui.BilingualText
import com.ma.tehro.domain.line.Station

@Composable
fun StationItem(
    station: Station,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    keyboardController?.hide()
                    onClick()
                }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            BilingualText(
                modifier = Modifier
                    .fillMaxWidth(),
                fa = station.translations.fa,
                en = station.name.uppercase(),
                spaceBetween = 0.dp,
                style = MaterialTheme.typography.bodyLarge,
                maxLine = 2,
                textAlign = TextAlign.End
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f)
        )
    }
}