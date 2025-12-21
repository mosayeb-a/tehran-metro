package com.ma.tehro.common

import androidx.compose.runtime.Composable

expect class LocationPermissionHandler {
    fun checkLocationPermission(onSuccess: () -> Unit)
}

@Composable
expect fun rememberLocationPermissionHandler(): LocationPermissionHandler