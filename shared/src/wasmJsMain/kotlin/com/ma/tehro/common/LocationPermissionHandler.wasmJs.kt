package com.ma.tehro.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.browser.window

actual class LocationPermissionHandler {
    var pendingSuccessCallback: (() -> Unit)? = null

    actual fun checkLocationPermission(onSuccess: () -> Unit) {
        pendingSuccessCallback = onSuccess

        navigator.geolocation?.getCurrentPosition(
            successCallback = { _ ->
                pendingSuccessCallback?.invoke()
                pendingSuccessCallback = null
            },
            errorCallback = { error ->
                handleGeolocationError(error)
            },
            options = createPositionOptions().apply {
                enableHighAccuracy = true
            }
        ) ?: run {
            showUnsupportedDialog()
            pendingSuccessCallback = null
        }
    }

    private fun handleGeolocationError(error: PositionError) {
        when (error.code.toInt()) {
            1 -> {
                showPermissionDeniedDialog()
            }
            else -> {
                showPermissionRationaleDialog()
            }
        }
        pendingSuccessCallback = null
    }

    private fun showUnsupportedDialog() {
        window.alert("مرورگر شما از دسترسی به موقعیت مکانی پشتیبانی نمی‌کند.")
    }
}

@Composable
actual fun rememberLocationPermissionHandler(): LocationPermissionHandler {
    return remember { LocationPermissionHandler() }
}

private fun LocationPermissionHandler.showPermissionRationaleDialog() {
    val retry = window.confirm(
        "برای یافتن نزدیک‌ترین ایستگاه، نیاز به دسترسی به موقعیت مکانی داریم.\n" +
                "آیا می‌خواهید دوباره تلاش کنید؟"
    )
    if (retry) {
        checkLocationPermission(pendingSuccessCallback ?: return)
    }
}

private fun showPermissionDeniedDialog() {
    window.alert(
        "دسترسی به موقعیت مکانی رد شد یا مسدود شده است.\n" +
                "برای استفاده از این ویژگی، لطفاً در تنظیمات مرورگر مجوز موقعیت مکانی را فعال کنید."
    )
}