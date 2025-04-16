package com.ma.tehro.feature.shortestpath.pathfinder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.Appbar
import com.ma.tehro.common.createBilingualMessage

@Composable
fun Appbar(
    modifier: Modifier = Modifier,
    fromEn: String,
    toEn: String,
    fromFa: String,
    toFa: String,
    onBack: () -> Unit
) {
    Column(modifier) {
        Appbar(
            title = createBilingualMessage(
                fa = "مسیر پیشنهادی",
                en = "Suggested Path"
            ),
            handleBack = true,
            onBackClick = onBack
        )
        AppbarDetail(fromEn = fromEn, toEn = toEn, fromFa = fromFa, toFa = toFa)
        HorizontalDivider()
    }
}

@Composable
fun AppbarDetail(
    modifier: Modifier = Modifier,
    fromEn: String,
    toEn: String,
    fromFa: String,
    toFa: String,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = toFa + "\n" + toEn.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Icon(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .size(16.dp),
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = "Going to .."
        )
        Text(
            text = fromFa + "\n" + fromEn.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
