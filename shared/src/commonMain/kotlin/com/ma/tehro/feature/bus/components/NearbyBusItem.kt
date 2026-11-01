package com.ma.tehro.feature.bus.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.domain.line.NearbyBus
import com.ma.tehro.domain.line.NearbyBusLine

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NearbyBusItem(
    modifier: Modifier = Modifier,
    bus: NearbyBus,
    index: Int
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = (index + 1).toFarsiNumber(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.width(24.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = bus.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    DistanceAnnotated(
                        number = bus.distanceNumber,
                        unit = bus.distanceUnit,
                    )
                }

                Spacer(Modifier.height(8.dp))

                if (bus.lines.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        bus.lines.forEach { line -> LineChip(line) }
                    }
                } else {
                    Text(
                        text = "اطلاعات خطوط ثبت نشده است",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
    }
}

@Composable
private fun DistanceAnnotated(number: String, unit: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                append(number)
            }
            if (unit.isNotEmpty()) {
                append(" ")
                withStyle(SpanStyle(fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))) {
                    append(unit)
                }
            }
        },
        color = Color.White
    )
}

@Composable
private fun LineChip(line: NearbyBusLine) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.06f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.DirectionsBus,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = line.line.toFarsiNumber(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 12.sp
            )

            if (!line.destination.isNullOrBlank()) {
                Spacer(Modifier.width(6.dp))
                Text(
                    text = line.displayLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 10.sp
                )
            }
        }
    }
}