package com.ma.tehro.common

import androidx.compose.runtime.Composable

actual class LocationPermissionHandler {
    actual fun checkLocationPermission(onSuccess: () -> Unit) {
    }
}

@Composable
actual fun rememberLocationPermissionHandler(): LocationPermissionHandler {
    TODO("Not yet implemented")
}