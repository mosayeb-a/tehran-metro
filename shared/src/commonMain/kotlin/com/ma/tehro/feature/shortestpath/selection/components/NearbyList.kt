package com.ma.tehro.feature.shortestpath.selection.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.common.ui.EmptyStatesFaces
import com.ma.tehro.common.ui.Message
import com.ma.tehro.domain.common.GeoPoint
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.path.Nearby
import com.ma.tehro.domain.path.Place
import com.ma.tehro.feature.shortestpath.selection.NearbyType
import com.ma.tehro.feature.shortestpath.selection.NearbyError
import com.ma.tehro.feature.shortestpath.selection.NearbySearchState

@Composable
fun <T : GeoPoint> NearbyList(
    modifier: Modifier = Modifier,
    locationName: String,
    nearbyState: NearbySearchState,
    items: List<Nearby<T>>,
    onItemSelected: (T) -> Unit,
    onBack: () -> Unit,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    itemContent: @Composable (T, Int) -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            NearbyHeader(
                modifier = Modifier.align(Alignment.End),
                onBack = onBack
            )

            when {
                nearbyState.isLoading -> {
                    val contentLabel = if (nearbyState.type == NearbyType.Stations) {
                        "ایستگاه‌های نزدیک"
                    } else {
                        "مکان‌های نزدیک"
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Message(
                            modifier = Modifier
                                .padding(0.dp)
                                .height(185.dp),
                            faMessage = "در حال پیدا کردن $contentLabel به «${locationName.trim()}»...",
                            faces = EmptyStatesFaces.happy,
                        )
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(32.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            strokeWidth = 3.dp,
                        )
                    }
                }

                nearbyState.error != null -> {
                    val (message, faces, actionText) = getErrorUi(
                        nearbyState.error,
                        nearbyState.type
                    )
                    Message(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        faMessage = message,
                        faces = faces,
                        actionText = actionText,
                        onAction = onRetry
                    )
                }

                items.isEmpty() -> {
                    val contentLabel =
                        if (nearbyState.type == NearbyType.Stations) "ایستگاهی" else "مکانی"
                    Message(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        faMessage = "هیچ $contentLabel در نزدیکی «${locationName.trim()}» پیدا نشد",
                        faces = EmptyStatesFaces.sad,
                        actionText = "تلاش مجدد",
                        onAction = onRetry
                    )
                }

                else -> {
                    val titleLabel = if (nearbyState.type == NearbyType.Stations) {
                        "نزدیک‌ترین ایستگاه‌ها به "
                    } else {
                        "مکان‌های نزدیک به "
                    }

                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth(),
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = .9f)
                                )
                            ) {
                                append(titleLabel)
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                append("«${locationName.trim()}»")
                            }
                        },
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(
                            items.size,
                            key = { index ->
                                when (val item = items[index].item) {
                                    is Station -> item.name
                                    is Place -> item.name
                                    else -> index.toString()
                                }
                            }
                        ) { index ->
                            val nearby = items[index]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(28.dp))
                                    .clickable {
                                        onItemSelected(nearby.item)
                                        onDismiss()
                                    }
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    )
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = (index + 1).toFarsiNumber(),
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Black
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Spacer(Modifier.width(2.dp))

                                        itemContent(nearby.item, index)
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    Row {
                                        Text(
                                            text = nearby.distanceNumber,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = nearby.distanceUnit,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NearbyHeader(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = .12f)),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(35.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "بازگشت",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun getErrorUi(
    error: NearbyError,
    content: NearbyType
): Triple<String, List<String>, String> {
    val label = if (content == NearbyType.Stations) "ایستگاه‌ها" else "مکان‌ها"
    return when (error) {
        NearbyError.PermissionDenied -> Triple(
            "برای پیدا کردن $label نزدیک، دسترسی به موقعیت مکانی لازم است",
            EmptyStatesFaces.suggestion,
            "درخواست دسترسی"
        )

        NearbyError.LocationDisabled -> Triple(
            "لطفا GPS را روشن کنید تا بتوانیم $label نزدیک را پیدا کنیم",
            EmptyStatesFaces.suggestion,
            "تنظیمات"
        )

        NearbyError.Timeout -> Triple(
            "دریافت موقعیت بیش از حد طول کشید، لطفا دوباره تلاش کنید",
            EmptyStatesFaces.sad,
            "تلاش مجدد"
        )

        NearbyError.Unknown -> Triple(
            "مشکلی در دریافت $label نزدیک رخ داد",
            EmptyStatesFaces.sad,
            "تلاش مجدد"
        )
    }
}