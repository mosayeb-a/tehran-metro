package com.ma.tehro.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.ma.tehro.common.createBilingualMessage
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface LocationClient {
    suspend fun getCurrentLocation(): Location
}

class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location {
        return suspendCancellableCoroutine { continuation ->
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGpsEnabled && !isNetworkEnabled) {
                val message = createBilingualMessage(
                    fa = "GPS غیرفعال است",
                    en = "GPS is disabled"
                )
                continuation.resumeWithException(Exception(message))
                return@suspendCancellableCoroutine
            }

            client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                if (location == null) {
                    val message = createBilingualMessage(
                        fa = "دریافت موقعیت مکانی با شکست مواجه شد",
                        en = "Failed to get location"
                    )
                    continuation.resumeWithException(Exception(message))
                } else {
                    continuation.resume(location)
                }
            }.addOnFailureListener { e ->
                val message = createBilingualMessage(
                    fa = "خطا در دریافت موقعیت مکانی",
                    en = e.message ?: "Unknown error"
                )
                continuation.resumeWithException(Exception(message))
            }
        }
    }
}