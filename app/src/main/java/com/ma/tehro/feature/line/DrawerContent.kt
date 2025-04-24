package com.ma.tehro.feature.line

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.R
import com.ma.tehro.common.Appbar
import com.ma.tehro.data.BilingualName

@Composable
fun DrawerContent(
    onMapClick: () -> Unit,
    onSubmitFeedbackClick: () -> Unit,
    onPathFinderClick: () -> Unit,
    onLinesClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(.71f)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        item("appbar") { Appbar("") }
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
                onClick = onMapClick,
                icon = R.drawable.my_location_24px
            )
        }
        item("metro_map") {
            DrawerItem(
                label = BilingualName(
                    fa = "نقشه مترو",
                    en = "METRO MAP"
                ),
                onClick = {},
                icon = R.drawable.map_24px
            )
        }
        item("submit_feedback") {
            DrawerItem(
                label = BilingualName(
                    fa = "ارسال پیشنهاد",
                    en = "SUBMIT FEEDBACK"
                ),
                onClick = onSubmitFeedbackClick,
                icon = R.drawable.send
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClick) {
                if (icon != null) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = label.fa,
                        tint = Color.White
                    )
                } else {
                    Icon(
                        imageVector = imageVector!!,
                        contentDescription = label.fa,
                        tint = Color.White
                    )
                }
            }
            Spacer(Modifier.width(4.dp))
            Column {
                Text(
                    text = label.fa,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.W500,
                    )
                )
                Text(
                    text = label.en,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSecondary.copy(alpha = .8f)
                    )
                )
            }
        }
        HorizontalDivider()
    }
}
