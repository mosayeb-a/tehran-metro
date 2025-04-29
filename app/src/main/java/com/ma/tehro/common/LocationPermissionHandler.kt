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
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

class LocationPermissionHandler(
    private val activity: ComponentActivity
) {
    private var pendingGpsCallback: (() -> Unit)? = null

    private val requestLocationPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pendingGpsCallback?.let { checkAndPromptEnableGPS(it) }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showPermissionRationaleDialog()
                } else {
                    showSettingsDialog()
                }
            } else {
                showSettingsDialog()
            }
        }
        pendingGpsCallback = null
    }

    private val enableLocationLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            pendingGpsCallback?.invoke()
        } else {
            Toast.makeText(
                activity,
                "برای یافتن نزدیک‌ترین ایستگاه، روشن بودن GPS ضروری است.",
                Toast.LENGTH_SHORT
            ).show()
        }
        pendingGpsCallback = null
    }

    fun checkLocationPermission(onSuccess: () -> Unit) {
        if (hasLocationPermission()) {
            checkAndPromptEnableGPS(onSuccess)
        } else {
            pendingGpsCallback = { checkAndPromptEnableGPS(onSuccess) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showPermissionRationaleDialog()
                } else {
                    requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(activity)
            .setTitle("نیاز به دسترسی موقعیت مکانی")
            .setMessage("برای یافتن نزدیک‌ترین ایستگاه، نیاز به دسترسی به موقعیت مکانی داریم.")
            .setPositiveButton("اجازه دادن") { _, _ ->
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton("رد کردن") { _, _ ->
                Toast.makeText(activity, "دسترسی به موقعیت مکانی رد شد.", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(activity)
            .setTitle("نیاز به دسترسی موقعیت مکانی")
            .setMessage("برای استفاده از این ویژگی، لطفا در تنظیمات برنامه، دسترسی موقعیت مکانی را فعال کنید.")
            .setPositiveButton("تنظیمات") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", activity.packageName, null)
                }
                activity.startActivity(intent)
            }
            .setNegativeButton("انصراف") { _, _ ->
                Toast.makeText(activity, "دسترسی به موقعیت مکانی رد شد.", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkAndPromptEnableGPS(onSuccess: () -> Unit) {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onSuccess()
        } else {
            pendingGpsCallback = onSuccess
            showGpsPromptDialog()
        }
    }

    private fun showGpsPromptDialog() {
        AlertDialog.Builder(activity)
            .setTitle("فعال‌سازی GPS")
            .setMessage("برای یافتن نزدیک‌ترین ایستگاه، لطفاً GPS را فعال کنید.")
            .setPositiveButton("فعال کردن") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                enableLocationLauncher.launch(intent)
            }
            .setNegativeButton("لغو") { _, _ ->
                pendingGpsCallback = null
                Toast.makeText(
                    activity,
                    "GPS برای یافتن نزدیک‌ترین ایستگاه لازم است.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setCancelable(false)
            .show()
    }
}