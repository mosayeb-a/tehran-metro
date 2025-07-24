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
import com.ma.tehro.common.ui.BilingualText
import com.ma.tehro.data.Station


@Composable
fun FacilityItem(
    modifier: Modifier = Modifier,
    fa: String,
    en: String,
    icon: Int,
    isDisabled: Boolean
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(52.dp)
            .padding(top = 4.dp)
            .alpha(if (isDisabled) 0.5f else 1f),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {

        BilingualText(
            fa = fa,
            en = en.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.End
        )
        Spacer(Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .padding()
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(icon),
                contentDescription = "facility icon",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}