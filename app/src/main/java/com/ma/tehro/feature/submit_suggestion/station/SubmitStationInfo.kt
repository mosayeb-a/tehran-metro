package com.ma.tehro.feature.submit_suggestion.station

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.common.ui.BilingualText
import com.ma.tehro.data.Station
import com.ma.tehro.feature.submit_suggestion.SubmitInfoState
import com.ma.tehro.feature.submit_suggestion.station.components.CorrectionEditText
import com.ma.tehro.feature.submit_suggestion.station.components.FacilitateCheckbox

@Composable
fun SubmitStationInfo(
    onBack: () -> Unit,
    onSubmitInfo: (station: Station) -> Unit,
    station: Station,
    state: SubmitInfoState,
    lineNumber: Int
) {
    var name by rememberSaveable { mutableStateOf(station.name) }
    var translations by rememberSaveable { mutableStateOf(station.translations.fa) }
    var longitude by rememberSaveable { mutableStateOf(station.longitude ?: "") }
    var latitude by rememberSaveable { mutableStateOf(station.latitude ?: "") }
    var address by rememberSaveable { mutableStateOf(station.address ?: "") }
    var disabled by rememberSaveable { mutableStateOf(station.disabled) }
    var wc by rememberSaveable { mutableStateOf(station.wc ?: false) }
    var coffeeShop by rememberSaveable { mutableStateOf(station.coffeeShop ?: false) }
    var groceryStore by rememberSaveable { mutableStateOf(station.groceryStore ?: false) }
    var fastFood by rememberSaveable { mutableStateOf(station.fastFood ?: false) }
    var atm by rememberSaveable { mutableStateOf(station.atm ?: false) }
    var selectedLine by rememberSaveable {
        mutableStateOf(station.lines.joinToString(", "))
    }

    val isChanged = name != station.name ||
            translations != station.translations.fa ||
            longitude != (station.longitude ?: "") ||
            latitude != (station.latitude ?: "") ||
            address != (station.address ?: "") ||
            disabled != station.disabled ||
            wc != (station.wc ?: false) ||
            coffeeShop != (station.coffeeShop ?: false) ||
            groceryStore != (station.groceryStore ?: false) ||
            fastFood != (station.fastFood ?: false) ||
            atm != (station.atm ?: false) ||
            selectedLine != station.lines.joinToString(", ")

    Scaffold(
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

            }
        },
        topBar = {
            Column {
                Appbar(
                    fa = "ارسال اصلاحیه برای ایستگاه ${station.translations.fa}",
                    en = "submit station correction for ${station.name}",
                    handleBack = true,
                    onBackClick = onBack
                )
                HorizontalDivider()
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            item { Spacer(Modifier.height(16.dp)) }
            item("title") {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(getLineColorByNumber(lineNumber).copy(alpha = .61f))
                        .padding(6.dp)
                ) {
                    Text(
                        text = "در به‌روزرسانی داده‌های مترو به ما کمک کنید! اطلاعات ارسالی شما بررسی می شود و درصورت درست بودن، در نسخه بعدی اعمال خواهد شد",
                        textAlign = TextAlign.Right,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 17.sp,
                        modifier = Modifier
                            .padding(6.dp)
                            .fillMaxWidth(),
                    )
                }
            }
            item { Spacer(Modifier.height(12.dp)) }
            item {
                CorrectionEditText(
                    value = name,
                    onValueChange = { name = it },
                    label = "نام انگلیسی",
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item { Spacer(Modifier.height(4.dp)) }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CorrectionEditText(
                        value = translations,
                        onValueChange = { translations = it },
                        label = "نام فارسی",
                        modifier = Modifier.weight(0.8f)
                    )
                    CorrectionEditText(
                        value = selectedLine,
                        onValueChange = { selectedLine = it },
                        label = "خط",
                        modifier = Modifier.weight(0.2f),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                    )
                }
            }
            item { Spacer(Modifier.height(4.dp)) }

            item {
                CorrectionEditText(
                    value = address,
                    onValueChange = { address = it },
                    label = "آدرس",
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item { Spacer(Modifier.height(4.dp)) }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CorrectionEditText(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = "طول جغرافیایی",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        )
                    )
                    CorrectionEditText(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = "عرض جغرافیایی",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Row(
                            modifier = Modifier
                                .wrapContentWidth()
                                .align(Alignment.CenterHorizontally),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FacilitateCheckbox(
                                fa = "غیرفعال",
                                en = "Disabled",
                                checked = disabled,
                                onCheckedChange = { disabled = it },
                                modifier = Modifier
                            )
                            FacilitateCheckbox(
                                fa = "دستشویی",
                                en = "WC",
                                checked = wc,
                                onCheckedChange = { wc = it },
                                modifier = Modifier
                            )
                        }

                        Row(
                            modifier = Modifier
                                .wrapContentWidth()
                                .align(Alignment.CenterHorizontally),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FacilitateCheckbox(
                                fa = "کافی‌شاپ",
                                en = "Coffee Shop",
                                checked = coffeeShop,
                                onCheckedChange = { coffeeShop = it },
                                modifier = Modifier
                            )
                            FacilitateCheckbox(
                                fa = "سوپرمارکت",
                                en = "Grocery Store",
                                checked = groceryStore,
                                onCheckedChange = { groceryStore = it },
                                modifier = Modifier
                            )
                        }

                        Row(
                            modifier = Modifier
                                .wrapContentWidth()
                                .align(Alignment.CenterHorizontally),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FacilitateCheckbox(
                                fa = "فست‌فود",
                                en = "Fast Food",
                                checked = fastFood,
                                onCheckedChange = { fastFood = it },
                                modifier = Modifier
                            )
                            FacilitateCheckbox(
                                fa = "خودپرداز",
                                en = "ATM",
                                checked = atm,
                                onCheckedChange = { atm = it },
                                modifier = Modifier
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(12.dp)) }

            item {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { onSubmitInfo(station) },
                    enabled = !state.isLoading && isChanged,
                    colors  = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    BilingualText(
                        fa = "ارسال اطلاعات",
                        en = "SUBMIT INFO",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLine = 2,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            item { Spacer(Modifier.height(58.dp)) }
        }
    }
}


