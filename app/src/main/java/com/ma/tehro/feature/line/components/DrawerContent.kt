package com.ma.tehro.feature.line.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.HdrStrong
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ma.tehro.BuildConfig
import com.ma.tehro.R
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.data.BilingualName

@Composable
fun DrawerContent(
    onCityMapClick: () -> Unit,
    onSubmitFeedbackClick: () -> Unit,
    onPathFinderClick: () -> Unit,
    onLinesClick: () -> Unit,
    onMetroMapClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(.71f)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.secondary)
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            item { Spacer(Modifier.height(56.dp)) }

            item("lines") {
                DrawerItem(
                    BilingualName(fa = "فهرست خطوط", en = "LINES"),
                    onLinesClick,
                    imageVector = Icons.Rounded.FormatListNumbered
                )
            }
            item("pathfinder") {
                DrawerItem(
                    BilingualName(fa = "مسیریابی", en = "PATHFINDER"),
                    onPathFinderClick,
                    icon = R.drawable.route
                )
            }
            item("station_on_city_map") {
                DrawerItem(
                    BilingualName(
                        fa = "ایستگاها در نقشه شهر",
                        en = "STATION ON CITY MAP"
                    ), onCityMapClick, imageVector = Icons.Rounded.MyLocation
                )
            }
            item("metro_map") {
                DrawerItem(
                    BilingualName(fa = "نقشه مترو", en = "METRO MAP"),
                    onMetroMapClick,
                    imageVector = Icons.Rounded.Map
                )
            }
            item("submit_feedback") {
                DrawerItem(
                    BilingualName(fa = "ارسال پیشنهاد", en = "SUBMIT FEEDBACK"),
                    onSubmitFeedbackClick,
                    imageVector = Icons.AutoMirrored.Rounded.Send
                )
            }
            item("more") {
                DrawerItem(
                    BilingualName(fa = "بیشتر", en = "MORE"),
                    onMoreClick,
                    imageVector = Icons.Default.HdrStrong
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(.06f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.23f))
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "آخرین به‌روزرسانی",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = "۲ آبان ۱۴۰۴ - نسخه ${BuildConfig.VERSION_NAME.toFarsiNumber()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        }
    }
}