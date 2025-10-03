package com.ma.tehro.feature.detail

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
import com.ma.tehro.feature.detail.components.AppbarDetail
import com.ma.tehro.feature.detail.components.FacilityItem

@Composable
fun StationDetail(
    onBack: () -> Unit = {},
    useBranch: Boolean,
    station: Station,
    lineNumber: Int,
    onSubmitInfoStationClicked: (station: Station, line: Int) -> Unit,
    onTrainScheduleClick: (stationName: String, faStationName: String, lineNumber: Int, useBranch: Boolean) -> Unit
) {
    val lineName = remember(lineNumber) { calculateLineName(lineNumber, useBranch) }
    val lineColor = remember { getLineColorByNumber(lineNumber) }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.primary)) {
                Appbar(
                    fa = lineName.fa,
                    en = lineName.en,
                    
                    onBackClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary),
                    content = {
                        IconButton(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(46.dp),
                            onClick = { onSubmitInfoStationClicked(station, lineNumber) },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.help_fill),
                                contentDescription = "submit station info",
                            )
                        }
                    }
                )
                AppbarDetail(
                    text = station.address ?: "آدرس مشخص نشده",
                    fa = station.translations.fa,
                    en = station.name,
                    lineColor = lineColor
                )
            }
        }
    ) { paddingValues ->

        val facilities = listOf(
            FacilityItemData("سرویس بهداشتی", "wc", R.drawable.wash_24px),
            FacilityItemData("فست فود", "fast food", R.drawable.fastfood_24px),
            FacilityItemData("خودپرداز", "atm", R.drawable.local_atm_24px),
            FacilityItemData("بقالی", "grocery store", R.drawable.grocery_24px),
            FacilityItemData("کافی شاپ", "coffee shop", R.drawable.emoji_food_beverage_24px)
        )
        val sortedFacilities = facilities.sortedBy { facility ->
            when (facility.en) {
                "wc" -> station.wc != true
                "fast food" -> station.fastFood != true
                "atm" -> station.atm != true
                "grocery store" -> station.groceryStore != true
                "coffee shop" -> station.coffeeShop != true
                else -> true
            }
        }

        LazyColumn(
            modifier = Modifier.padding(paddingValues),
        ) {
            item("timetable") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onTrainScheduleClick(
                                station.name,
                                station.translations.fa,
                                lineNumber,
                                useBranch
                            )
                        }
                        .background(lineColor)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.directions_subway_24px),
                        contentDescription = "train schedule"
                    )
                    BilingualText(
                        fa = "زمان‌بندی حرکت قطار",
                        en = "TRAIN SCHEDULE",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        enSize = 11.sp
                    )
                }
            }
            item("label") {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FACILITIES",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "امکانات",
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 14.sp
                    )
                }
            }
            items(sortedFacilities, key = { it.en }) { facility ->
                val isDisabled = when (facility.en) {
                    "wc" -> station.wc != true
                    "fast food" -> station.fastFood != true
                    "atm" -> station.atm != true
                    "grocery store" -> station.groceryStore != true
                    "coffee shop" -> station.coffeeShop != true
                    else -> true
                }
                FacilityItem(
                    modifier = Modifier
                        .clickable { },
                    fa = facility.fa,
                    en = facility.en,
                    icon = facility.icon,
                    isDisabled = isDisabled
                )
                Spacer(Modifier.width(4.dp))
                HorizontalDivider()
            }
            item("end_spacer") { Spacer(Modifier.height(58.dp)) }
        }
    }
}

data class FacilityItemData(
    val fa: String,
    val en: String,
    val icon: Int
)