package com.ma.tehro.feature.shortestpath.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ma.tehro.common.fractionToTime
import com.ma.tehro.common.toFarsiNumber
import java.util.Calendar
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionalInfo(
    lineChangeDelay: Int,
    currentTime: Double,
    dayOfWeek: Int,
    onLineChangeDelayChanged: (Int) -> Unit,
    onTimeChanged: (Double) -> Unit,
    onDayOfWeekChanged: (Int) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val days = remember {
        mutableListOf(
            "شنبه" to Calendar.SATURDAY,
            "یکشنبه" to Calendar.SUNDAY,
            "دوشنبه" to Calendar.MONDAY,
            "سه‌شنبه" to Calendar.TUESDAY,
            "چهارشنبه" to Calendar.WEDNESDAY,
            "پنج‌شنبه" to Calendar.THURSDAY,
            "جمعه" to Calendar.FRIDAY
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "مدت زمان لازم برای تعویض خط",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onPrimary
            ),
            textAlign = TextAlign.End
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                modifier = Modifier
                    .weight(0.7f)
                    .scale(1f, 0.75f),
                value = lineChangeDelay.toFloat(),
                onValueChange = { onLineChangeDelayChanged(it.roundToInt()) },
                valueRange = 1f..20f,
                steps = 18,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.onPrimary,
                    activeTrackColor = MaterialTheme.colorScheme.tertiary,
                    inactiveTrackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                    inactiveTickColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = .5f),
                    activeTickColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = " دقیقه",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = " ${lineChangeDelay.toFarsiNumber()}",
                style = MaterialTheme.typography.bodyMedium
                    .copy(color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.W600)
            )
        }

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomOutlinedTextField(
                modifier = Modifier.weight(1f),
                value = fractionToTime(currentTime),
                hint = "انتخاب زمان دلخواه",
                onClick = { showTimePicker = true }
            )
            Spacer(modifier = Modifier.width(12.dp))

            Box(modifier = Modifier.weight(1f)) {
                CustomOutlinedTextField(
                    value = days.first { it.second == dayOfWeek }.first,
                    hint = "انتخاب روز دلخواه",
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .width(180.dp)
                            .height(220.dp)
                    ) {
                        items(days, key = { it.first }) { (name, value) ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.End,
                                        text = name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                onClick = {
                                    onDayOfWeekChanged(value)
                                    expanded = false
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onConfirm = { hour, minute ->
                val newTime = (hour * 3600 + minute * 60).toDouble() / 86400.0
                onTimeChanged(newTime)
                showTimePicker = false
            },
            initialHour = (currentTime * 24).toInt(),
            initialMinute = ((currentTime * 24 * 60) % 60).toInt()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomOutlinedTextField(
    value: String,
    hint: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(hint) },
            modifier = Modifier
                .fillMaxWidth(),
            trailingIcon = trailingIcon,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                disabledBorderColor = MaterialTheme.colorScheme.onSurface,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(16.dp),
            textStyle = MaterialTheme.typography.bodyMedium
        )

        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .matchParentSize()
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = androidx.compose.material3.ripple(),
                    onClick = onClick
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    initialHour: Int,
    initialMinute: Int
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = MaterialTheme.colorScheme.secondary,
                            selectorColor = MaterialTheme.colorScheme.tertiary,
                            timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.tertiary,
                            timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                            timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.secondary.copy(
                                alpha = .3f
                            ),
                            timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSecondary,
                            clockDialSelectedContentColor = MaterialTheme.colorScheme.onSecondary,
                            clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
                            periodSelectorBorderColor = Red,
                        ),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "لغو",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    TextButton(
                        onClick = {
                            onConfirm(timePickerState.hour, timePickerState.minute)
                        }
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "تایید",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
}