package com.ma.tehro.feature.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CoffeeMaker
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Elevator
import androidx.compose.material.icons.outlined.EventSeat
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.FireExtinguisher
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalPolice
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.PedalBike
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.SmokingRooms
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.calculateLineName
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.data.Station
import com.ma.tehro.domain.BilingualName
import com.ma.tehro.domain.Facility
import com.ma.tehro.feature.detail.components.AppbarDetail
import com.ma.tehro.feature.detail.components.FacilityChip
import com.ma.tehro.feature.detail.components.FacilitySection
import com.ma.tehro.feature.detail.components.TrainScheduleButton

@Composable
fun StationDetail(
    onBack: () -> Unit = {},
    useBranch: Boolean,
    station: Station,
    lineNumber: Int,
    onTrainScheduleClick: (stationName: String, faStationName: String, lineNumber: Int, useBranch: Boolean) -> Unit
) {
    val lineName = remember(lineNumber) { calculateLineName(lineNumber, useBranch) }
    val lineColor = remember { getLineColorByNumber(lineNumber) }

    val allFacilities = remember(station) {
        listOf(
            Facility(
                fa = "سرویس بهداشتی",
                en = "WC",
                icon = Icons.Filled.Wc,
                isAvailable = station.facilities.wc == true
            ),
            Facility(
                fa = "فست فود",
                en = "FAST FOOD",
                icon = Icons.Outlined.Fastfood,
                isAvailable = station.facilities.fastFood == true
            ),
            Facility(
                fa = "خودپرداز",
                en = "ATM",
                icon = Icons.Outlined.AttachMoney,
                isAvailable = station.facilities.atm == true
            ),
            Facility(
                fa = "بقالی",
                en = "GROCERY",
                icon = Icons.Outlined.Storefront,
                isAvailable = station.facilities.groceryStore == true
            ),
            Facility(
                fa = "کافی شاپ",
                en = "COFFEE",
                icon = Icons.Outlined.CoffeeMaker,
                isAvailable = station.facilities.coffeeShop == true
            ),
            Facility(
                fa = "پارکینگ دوچرخه",
                en = "BIKE PARKING",
                icon = Icons.Outlined.PedalBike,
                isAvailable = station.facilities.bicycleParking == true
            ),
            Facility(
                fa = "صندلی انتظار",
                en = "WAITING CHAIR",
                icon = Icons.Outlined.EventSeat,
                isAvailable = station.facilities.waitingChair == true
            ),
            Facility(
                fa = "نمازخانه",
                en = "PRAYER ROOM",
                icon = Icons.Outlined.Mosque,
                isAvailable = station.facilities.prayerRoom == true
            ),
            Facility(
                fa = "وای فای",
                en = "FREE WIFI",
                icon = Icons.Outlined.Wifi,
                isAvailable = station.facilities.freeWifi == true
            ),
            Facility(
                fa = "آسانسور",
                en = "ELEVATOR",
                icon = Icons.Outlined.Elevator,
                isAvailable = station.accessibility.elevator == true
            ),
            Facility(
                fa = "مسیر نابینایان",
                en = "BLIND PATH",
                icon = Icons.Outlined.Visibility,
                isAvailable = station.accessibility.blindPath == true
            ),
            Facility(
                fa = "غذای سالم",
                en = "CLEAN FOOD",
                icon = Icons.Outlined.Restaurant,
                isAvailable = station.accessibility.cleanFood == true
            ),
            Facility(
                fa = "آتش نشانی",
                en = "FIRE SYSTEM",
                icon = Icons.Outlined.LocalFireDepartment,
                isAvailable = station.safety.fireSuppressionSystem == true
            ),
            Facility(
                fa = "کپسول آتش نشانی",
                en = "EXTINGUISHER",
                icon = Icons.Outlined.FireExtinguisher,
                isAvailable = station.safety.fireExtinguisher == true
            ),
            Facility(
                fa = "پلیس مترو",
                en = "METRO POLICE",
                icon = Icons.Outlined.LocalPolice,
                isAvailable = station.safety.metroPolice == true
            ),
            Facility(
                fa = "فروش بلیط اعتباری",
                en = "CREDIT TICKET",
                icon = Icons.Outlined.CreditCard,
                isAvailable = station.safety.creditTicketSales == true
            ),
            Facility(
                fa = "دوربین",
                en = "CAMERA",
                icon = Icons.Outlined.Videocam,
                isAvailable = station.safety.camera == true
            ),
            Facility(
                fa = "سطل زباله",
                en = "TRASH CAN",
                icon = Icons.Outlined.Delete,
                isAvailable = station.safety.trashCan == true
            ),
            Facility(
                fa = "ممنوعیت سیگار",
                en = "NO SMOKING",
                icon = Icons.Outlined.SmokingRooms,
                isAvailable = station.safety.smoking != true
            ),
            Facility(
                fa = "ورود حیوانات",
                en = "PETS ALLOWED",
                icon = Icons.Outlined.Pets,
                isAvailable = station.safety.petsAllowed == true
            ),
        )
    }

    val (availableItems, unavailableItems) = remember(allFacilities) {
        allFacilities.partition { it.isAvailable }
    }

    val facilities = remember(availableItems, unavailableItems) {
        availableItems + unavailableItems
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.secondary)) {
                Appbar(
                    fa = lineName.fa,
                    en = lineName.en,
                    onBackClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondary),
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            item {
                TrainScheduleButton(
                    lineColor = lineColor,
                    onClick = {
                        onTrainScheduleClick(
                            station.name,
                            station.translations.fa,
                            lineNumber,
                            useBranch
                        )
                    }
                )
            }

            item { Spacer(Modifier.height(12.dp)) }

            item {
                FacilitySection(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    title = BilingualName(
                        fa = "امکانات و خدمات",
                        en = "FACILITIES & SERVICES"
                    )
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 4.dp,
                                alignment = Alignment.CenterHorizontally
                            ),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            facilities.forEach { item ->
                                FacilityChip(
                                    fa = item.fa,
                                    en = item.en,
                                    icon = item.icon,
                                    isAvailable = item.isAvailable
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(58.dp)) }
        }
    }
}