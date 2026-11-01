package com.ma.tehro.feature.bus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.common.ui.EmptyStatesFaces
import com.ma.tehro.common.ui.Message
import com.ma.tehro.common.ui.drawVerticalScrollbar
import com.ma.tehro.domain.common.BilingualName
import com.ma.tehro.domain.line.NearbyBus
import com.ma.tehro.feature.bus.components.NearbyBusItem

@Composable
fun NearbyBuses(
    modifier: Modifier = Modifier,
    station: BilingualName,
    lineNumber: Int,
    nearbyBuses: List<NearbyBus>,
    onBack: () -> Unit,
) {
    val lineColor = remember(lineNumber) { getLineColorByNumber(lineNumber) }
    val listState = rememberLazyListState()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .background(lineColor)
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
            ) {
                Appbar(
                    fa = "اتوبوس‌های نزدیک ${station.fa}",
                    en = "nearby buses for ${station.en}",
                    onBackClick = onBack,
                    modifier = Modifier.height(56.dp),
                    backgroundColor = lineColor
                )
            }
        }
    ) { paddingValues ->
        if (nearbyBuses.isEmpty()) {
            Message(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                message = "ایستگاه اتوبوسی در نزدیکی این ایستگاه ثبت نشده است.",
                faces = EmptyStatesFaces.sad
            )
        } else {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .drawVerticalScrollbar(listState),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(nearbyBuses) { index, bus ->
                        NearbyBusItem(
                            bus = bus,
                            index = index
                        )
                    }
                }
            }
        }
    }
}