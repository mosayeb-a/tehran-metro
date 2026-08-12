package com.ma.tehro.feature.map.city.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MapFabContainer(
    modifier: Modifier = Modifier,
    isSelectionMode: Boolean,
    isLocating: Boolean,
    onConfirmLocation: (() -> Unit)? = null,
    onFindCurrentLocationClick: () -> Unit,
) {
    if (isSelectionMode && onConfirmLocation != null) {
        Row(
            modifier = modifier.padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExtendedFloatingActionButton(
                onClick = onConfirmLocation,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.NearMe,
                        contentDescription = null
                    )
                },
                text = {
                    Text("یافتن ایستگاه‌های نزدیک")
                }
            )

            FloatingActionButton(
                onClick = { if (!isLocating) onFindCurrentLocationClick() },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                if (!isLocating) {
                    Icon(
                        imageVector = Icons.Rounded.MyLocation,
                        contentDescription = "یافتن موقعیت فعلی",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    } else {
        FloatingActionButton(
            onClick = { if (!isLocating) onFindCurrentLocationClick() },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = modifier
        ) {
            if (!isLocating) {
                Icon(
                    imageVector = Icons.Rounded.MyLocation,
                    contentDescription = "یافتن موقعیت فعلی",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}