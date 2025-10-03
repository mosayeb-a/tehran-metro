package com.ma.tehro.feature.line

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ma.tehro.R
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.common.ui.BilingualText
import com.ma.tehro.data.BilingualName

@Composable
fun DrawerContent(
    onCityMapClick: () -> Unit,
    onSubmitFeedbackClick: () -> Unit,
    onPathFinderClick: () -> Unit,
    onLinesClick: () -> Unit,
    onMetroMapClick: () -> Unit,
    onAboutClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(.71f)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        item("appbar") { Appbar(fa = "", en = "") }
        item("lines") {
            DrawerItem(
                label = BilingualName(
                    fa = "فهرست خطوط",
                    en = "LINES"
                ),
                onClick = onLinesClick,
                imageVector = Icons.AutoMirrored.Filled.List
            )
        }
        item("pathfinder") {
            DrawerItem(
                label = BilingualName(
                    fa = "مسیریابی",
                    en = "PATHFINDER"
                ),
                onClick = onPathFinderClick,
                icon = R.drawable.route
            )
        }
        item("station_on_city_map") {
            DrawerItem(
                label = BilingualName(
                    fa = "ایستگاها در نقشه شهر",
                    en = "STATION ON CITY MAP"
                ),
                onClick = onCityMapClick,
                icon = R.drawable.my_location_24px
            )
        }
        item("metro_map") {
            DrawerItem(
                label = BilingualName(
                    fa = "نقشه مترو",
                    en = "METRO MAP"
                ),
                onClick = onMetroMapClick,
                icon = R.drawable.map_24px
            )
        }
        item("about") {
            DrawerItem(
                label = BilingualName(
                    fa = "درباره",
                    en = "ABOUT"
                ),
                onClick = onAboutClick,
                imageVector = Icons.Default.Info
            )
        }
        item("submit_feedback") {
            DrawerItem(
                label = BilingualName(
                    fa = "ارسال پیشنهاد",
                    en = "SUBMIT FEEDBACK"
                ),
                onClick = onSubmitFeedbackClick,
                imageVector = Icons.AutoMirrored.Rounded.Send
            )
        }
    }
}

@Composable
fun DrawerItem(
    label: BilingualName,
    onClick: () -> Unit,
    @DrawableRes icon: Int? = null,
    imageVector: ImageVector? = null
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(end = 16.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            BilingualText(
                fa = label.fa,
                en = label.en,
                style = MaterialTheme.typography.bodyMedium,
                maxLine = 2,
                textAlign = TextAlign.End
            )

            Spacer(Modifier.width(16.dp))

            if (icon != null) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = label.fa,
                    tint = Color.White
                )
            } else if (imageVector != null) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = label.fa,
                    tint = Color.White
                )
            }
        }
        HorizontalDivider()
    }
}