package com.ma.tehro.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
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
    private val context: Context
) : LocationClient {

    private val locationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location {
        return suspendCancellableCoroutine { continuation ->

            val provider = when {
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                else -> {
                    val message = createBilingualMessage(
                        fa = "هیچ ارائه‌دهنده موقعیتی در دسترس نیست",
                        en = "No locationf provider available"
                    )
                    continuation.resumeWithException(Exception(message))
                    return@suspendCancellableCoroutine
                }
            }

            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    continuation.resume(location)
                    locationManager.removeUpdates(this)
                }

                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {
                    val message = createBilingualMessage(
                        fa = "ارائه‌دهنده موقعیت غیرفعال است",
                        en = "Location provider is disabled"
                    )
                    continuation.resumeWithException(Exception(message))
                    locationManager.removeUpdates(this)
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            }

            locationManager.requestLocationUpdates(
                provider,
                0L,
                0f,
                listener,
                Looper.getMainLooper()
            )

            continuation.invokeOnCancellation {
                locationManager.removeUpdates(listener)
            }
        }
    }


    @SuppressLint("MissingPermission")
    override suspend fun observeLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            val provider = when {
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                else -> {
                    close(Exception(createBilingualMessage(
                        fa = "هیچ ارائه‌دهنده موقعیتی در دسترس نیست",
                        en = "No location provider available"
                    )))
                    return@callbackFlow
                }
            }

            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    launch { send(location) }
                }

                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }


            locationManager.requestLocationUpdates(
                provider,
                interval,
                0f,
                listener,
                Looper.getMainLooper()
            )

            awaitClose {
                locationManager.removeUpdates(listener)
            }
        }
    }
}