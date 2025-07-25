package com.ma.tehro.feature.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.R
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.common.calculateLineName
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.data.Station


@Composable
fun AppbarDetail(
    modifier: Modifier = Modifier,
    text: String,
    fa: String,
    en: String,
    lineColor: Color
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(lineColor)
                .padding(26.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier) {
                Text(
                    modifier = Modifier
                        .wrapContentWidth()
                        .fillMaxWidth(),
                    text = fa,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.W500,
                        fontSize = 20.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier
                        .wrapContentWidth()
                        .fillMaxWidth(),
                    text = en.uppercase(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    textAlign = TextAlign.Center
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
                .fillMaxWidth()
                .height(26.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
                fontSize = 14.sp,
                modifier = Modifier.wrapContentWidth()
            )
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painterResource(R.drawable.location_on_24px),
                contentDescription = "address"
            )
        }
    }
}