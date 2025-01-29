package com.ma.tehro.services

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.ma.tehro.common.createBilingualMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface LocationClient {
    suspend fun getCurrentLocation(): Location
    suspend fun observeLocationUpdates(interval: Long): Flow<Location>
}

class DefaultLocationClient(
    private val client: FusedLocationProviderClient
) : LocationClient {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location {
        return suspendCancellableCoroutine { continuation ->

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

    @SuppressLint("MissingPermission")
    override suspend fun observeLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
                .setMinUpdateIntervalMillis(interval)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        launch { send(location) }
                    }
                }
            }

            client.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }
}
