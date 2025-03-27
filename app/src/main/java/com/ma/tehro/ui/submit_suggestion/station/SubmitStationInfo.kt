package com.ma.tehro.ui.submit_suggestion.station

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.Appbar
import com.ma.tehro.common.createBilingualMessage
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.data.Station
import com.ma.tehro.ui.submit_suggestion.SubmitInfoState

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
        topBar = {
            Column {
                Appbar(
                    title = createBilingualMessage(
                        fa ="ارسال اصلاحیه برای ایستگاه ${station.translations.fa}",
                        en = "submit station correction for ${station.name}"
                    ),
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
                .padding(horizontal = 8.dp)
        ) {
            item { Spacer(Modifier.height(16.dp)) }
            item("title") {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(getLineColorByNumber(lineNumber).copy(alpha = .61f))
                        .padding(6.dp)
                ) {
                    Text(
                        text = "در به‌روزرسانی داده‌های مترو به ما کمک کنید! اطلاعات ارسالی شما بررسی می شود و درصورت درست بودن، در نسخه بعدی اعمال خواهد شد",
                        textAlign = TextAlign.Right,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                    )
                    Text(
                        text = "Help us keep metro data up to date! Your submission will be reviewed and applied in the next update.",
                        textAlign = TextAlign.Left,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            item { Spacer(Modifier.height(12.dp)) }
            item("name") {
                CorrectionEditText(
                    value = name,
                    onValueChange = { name = it },
                    label = "نام انگلیسی",
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item { Spacer(Modifier.height(4.dp)) }
            item("fa_name") {
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

            item("address") {
                CorrectionEditText(
                    value = address,
                    onValueChange = { address = it },
                    label = "آدرس",
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item { Spacer(Modifier.height(4.dp)) }
            item("lat_long") {
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

            item("checkbox") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MyCheckbox(
                            label = createBilingualMessage(fa = "غیرفعال", en = "Disabled"),
                            checked = disabled,
                            onCheckedChange = { disabled = it },
                            modifier = Modifier.weight(1f)
                        )
                        MyCheckbox(
                            label = createBilingualMessage(fa = "دستشویی", en = "WC"),
                            checked = wc,
                            onCheckedChange = { wc = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MyCheckbox(
                            label = createBilingualMessage(fa = "کافی‌شاپ", en = "Coffee Shop"),
                            checked = coffeeShop,
                            onCheckedChange = { coffeeShop = it },
                            modifier = Modifier.weight(1f)
                        )
                        MyCheckbox(
                            label = createBilingualMessage(fa = "سوپرمارکت", en = "Grocery Store"),
                            checked = groceryStore,
                            onCheckedChange = { groceryStore = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MyCheckbox(
                            label = createBilingualMessage(fa = "فست‌فود", en = "Fast Food"),
                            checked = fastFood,
                            onCheckedChange = { fastFood = it },
                            modifier = Modifier.weight(1f)
                        )
                        MyCheckbox(
                            label = createBilingualMessage(fa = "خودپرداز", en = "ATM"),
                            checked = atm,
                            onCheckedChange = { atm = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }

            item("submit") {
                Button(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(76.dp),
                    onClick = {
                        onSubmitInfo(
                            Station(
                                name = name,
                                translations = station.translations.copy(fa = translations),
                                longitude = longitude.takeIf { it.isNotEmpty() },
                                latitude = latitude.takeIf { it.isNotEmpty() },
                                address = address.takeIf { it.isNotEmpty() },
                                disabled = disabled,
                                lines = station.lines,
                                wc = wc,
                                coffeeShop = coffeeShop,
                                groceryStore = groceryStore,
                                fastFood = fastFood,
                                atm = atm,
                                relations = station.relations,
                                positionsInLine = station.positionsInLine
                            )
                        )
                    },
                    enabled = !state.isLoading && isChanged,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        disabledContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = .5f)
                    ),
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(
                            createBilingualMessage(
                                fa = "ارسال اطلاعات",
                                en = "Submit Information"
                            ), textAlign = TextAlign.Center
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(56.dp)) }
        }
    }
}


@Composable
fun CorrectionEditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (value: String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = 1,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                textAlign = TextAlign.Center,
            )
        },
        trailingIcon = trailingIcon,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            focusedLabelColor = Color.White,
            focusedLeadingIconColor = Color.White,
            focusedTrailingIconColor = Color.White,
            cursorColor = Color.White,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            selectionColors = TextSelectionColors(
                handleColor = Color.White,
                backgroundColor = Color.White.copy(alpha = 0.3f)
            )
        ),
        maxLines = maxLines,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = keyboardOptions
    )
}

@Composable
fun MyCheckbox(
    modifier: Modifier = Modifier,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                onClick = { onCheckedChange(!checked) },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkmarkColor = MaterialTheme.colorScheme.primary,
                checkedColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        Spacer(Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}
