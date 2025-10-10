package com.ma.tehro.feature.train_schedule.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.createBilingualMessage
import com.ma.tehro.data.ScheduleType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScheduleTypeChips(
    scheduleTypes: List<ScheduleType>,
    selectedType: ScheduleType?,
    onScheduleTypeSelected: (ScheduleType?) -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        FlowRow(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(bottom = 8.dp, top = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            scheduleTypes.forEach { scheduleType ->
                ScheduleDaySelection(
                    isSelected = selectedType == scheduleType,
                    onClick = {
                        if (selectedType != scheduleType) {
                            onScheduleTypeSelected(scheduleType)
                        }
                    },
                    label = bilingualScheduleTypeTitle(scheduleType)
                )
            }
        }
    }
}

private fun bilingualScheduleTypeTitle(scheduleType: ScheduleType): String {
    return when (scheduleType) {
        ScheduleType.SATURDAY_TO_WEDNESDAY -> createBilingualMessage(
            fa = "شنبه تا چهارشنبه",
            en = "Saturday to Wednesday"
        )

        ScheduleType.THURSDAY -> createBilingualMessage(
            fa = "پنجشنبه",
            en = "Thursday"
        )

        ScheduleType.FRIDAY -> createBilingualMessage(
            fa = "جمعه",
            en = "Friday"
        )

        ScheduleType.ALL_DAY -> createBilingualMessage(
            fa = "همه روزه",
            en = "All Days"
        )

        ScheduleType.SATURDAY_TO_THURSDAY -> createBilingualMessage(
            fa = "شنبه تا پنجشنبه",
            en = "Saturday to Thursday"
        )

        ScheduleType.HOLIDAYS_AND_FRIDAY -> createBilingualMessage(
            fa = "جمعه و تعطیلات",
            en = "Holidays And Friday"
        )
    }
}
