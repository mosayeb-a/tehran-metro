package com.ma.tehro.common

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

actual class LocationPermissionHandler(
    private val context: Context
) {
    private var pendingSuccessCallback: (() -> Unit)? = null

    internal var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    internal var enableLocationLauncher: ActivityResultLauncher<Intent>? = null

    actual fun checkLocationPermission(onSuccess: () -> Unit) {
        if (hasLocationPermission()) {
            checkAndPromptEnableGPS(onSuccess)
        } else {
            pendingSuccessCallback = onSuccess
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        val activity = context as? androidx.activity.ComponentActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            activity?.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == true
        ) {
            showPermissionRationaleDialog()
        } else {
            requestPermissionLauncher?.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                ?: throw IllegalStateException("requestPermissionLauncher not initialized")
        }
    }

    internal fun handlePermissionResult(isGranted: Boolean) {
        if (isGranted) {
            pendingSuccessCallback?.let { checkAndPromptEnableGPS(it) }
        } else {
            handlePermissionDenied()
        }
        pendingSuccessCallback = null
    }

    private fun handlePermissionDenied() {
        val activity = context as? androidx.activity.ComponentActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            activity?.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == false
        ) {
            showSettingsDialog()
        } else {
            showPermissionRationaleDialog()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkAndPromptEnableGPS(onSuccess: () -> Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onSuccess()
        } else {
            pendingSuccessCallback = onSuccess
            showGpsPromptDialog()
        }
    }

    internal fun handleGpsEnableResult() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            pendingSuccessCallback?.invoke()
        } else {
            Toast.makeText(
                context,
                "برای یافتن نزدیک‌ترین ایستگاه، روشن بودن GPS ضروری است.",
                Toast.LENGTH_SHORT
            ).show()
        }
        pendingSuccessCallback = null
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(context)
            .setTitle("نیاز به دسترسی موقعیت مکانی")
            .setMessage("برای یافتن نزدیک‌ترین ایستگاه، نیاز به دسترسی به موقعیت مکانی داریم.")
            .setPositiveButton("اجازه دادن") { _, _ ->
                requestPermissionLauncher?.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton("رد کردن") { _, _ ->
                Toast.makeText(context, "دسترسی به موقعیت مکانی رد شد.", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(context)
            .setTitle("نیاز به دسترسی موقعیت مکانی")
            .setMessage("برای استفاده از این ویژگی، لطفا در تنظیمات برنامه، دسترسی موقعیت مکانی را فعال کنید.")
            .setPositiveButton("تنظیمات") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
            .setNegativeButton("انصراف") { _, _ ->
                Toast.makeText(context, "دسترسی به موقعیت مکانی رد شد.", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun showGpsPromptDialog() {
        AlertDialog.Builder(context)
            .setTitle("فعال‌سازی GPS")
            .setMessage("برای یافتن نزدیک‌ترین ایستگاه، لطفاً GPS را فعال کنید.")
            .setPositiveButton("فعال کردن") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                enableLocationLauncher?.launch(intent)
                    ?: throw IllegalStateException("enableLocationLauncher not initialized")
            }
            .setNegativeButton("لغو") { _, _ ->
                pendingSuccessCallback = null
                Toast.makeText(
                    context,
                    "GPS برای یافتن نزدیک‌ترین ایستگاه لازم است.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setCancelable(false)
            .show()
    }
}

@Composable
actual fun rememberLocationPermissionHandler(): LocationPermissionHandler {
    val context = LocalContext.current

    val handler = remember { LocationPermissionHandler(context) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        handler.handlePermissionResult(isGranted)
    }

    val enableLocationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        handler.handleGpsEnableResult()
    }

    remember {
        handler.requestPermissionLauncher = requestPermissionLauncher
        handler.enableLocationLauncher = enableLocationLauncher
    }

    return handler
}