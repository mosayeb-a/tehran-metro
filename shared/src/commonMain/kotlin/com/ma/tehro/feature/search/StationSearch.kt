package com.ma.tehro.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.common.ui.EmptyStatesFaces
import com.ma.tehro.common.ui.LineBadge
import com.ma.tehro.common.ui.Message
import com.ma.tehro.common.ui.TehroHorizontalDivider
import com.ma.tehro.common.ui.TehroSearchBar
import com.ma.tehro.domain.line.Station

@Composable
fun StationSearch(
    modifier: Modifier = Modifier,
    viewModel: StationSearchViewModel,
    onBack: () -> Unit,
    onStationClick: (Station) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondary)
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                        .padding(top = 14.dp, end = 16.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = Color.White
                        )
                    }
                    TehroSearchBar(
                        value = state.query,
                        onValueChange = viewModel::setQuery,
                        placeholder = "جستجوی ایستگاه...",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                TehroHorizontalDivider(alpha = .7f)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.query.isBlank() -> Message(
                    modifier = Modifier.fillMaxSize(),
                    message = "نام ایستگاه را جستجو کنید",
                    faces = EmptyStatesFaces.suggestion
                )

                state.results.isEmpty() -> Message(
                    modifier = Modifier.fillMaxSize(),
                    message = "نتیجه‌ای برای «${state.query}» پیدا نشد",
                    faces = EmptyStatesFaces.sad
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(state.results, key = { it.name }) { station ->
                        StationResultItem(
                            station = station,
                            onClick = { onStationClick(station) }
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StationResultItem(
    station: Station,
    onClick: () -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = station.translations.fa,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Start
                )
                if (station.name.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = station.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Start
                    )
                }
            }

            val lineNumbers = station.lines.ifEmpty {
                station.positionsInLine.map { it.line }.distinct()
            }
            if (lineNumbers.isNotEmpty()) {
                Spacer(Modifier.width(12.dp))
                LineBadge(lineNumbers = lineNumbers)
            }
        }
    }
}