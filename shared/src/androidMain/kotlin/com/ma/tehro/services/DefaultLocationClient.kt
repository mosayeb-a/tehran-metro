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
import kotlin.getValue

class DefaultLocationClient(
    private val context: Context
) : LocationClient {
    companion object {
        private const val LOCATION_TIMEOUT = 10000L
        private const val MIN_DISTANCE = 0f
    }

    private val locationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): PlatformLocation {
        return suspendCancellableCoroutine { continuation ->
            val lastKnownLocation = getBestLastKnownLocation()
            if (lastKnownLocation != null && isLocationFresh(lastKnownLocation)) {
                continuation.resume(lastKnownLocation.toPlatformLocation())
                return@suspendCancellableCoroutine
            }

            var locationReceived = false
            val providers = mutableListOf<String>()

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                providers.add(LocationManager.GPS_PROVIDER)
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                providers.add(LocationManager.NETWORK_PROVIDER)
            }

            if (providers.isEmpty()) {
                continuation.resumeWithException(Exception(
                    createBilingualMessage(
                        fa = "هیچ ارائه‌دهنده موقعیتی در دسترس نیست",
                        en = "No location provider available"
                    )
                ))
                return@suspendCancellableCoroutine
            }

            val listeners = mutableMapOf<String, LocationListener>()

            val timeoutRunnable = Runnable {
                if (!locationReceived && continuation.isActive) {
                    listeners.forEach { (_, listener) ->
                        locationManager.removeUpdates(listener)
                    }
                    val fallbackLocation = getBestLastKnownLocation()
                    if (fallbackLocation != null) {
                        continuation.resume(fallbackLocation.toPlatformLocation())
                    } else {
                        continuation.resumeWithException(Exception(
                            createBilingualMessage(
                                fa = "دریافت موقعیت با تاخیر مواجه شد",
                                en = "Location request timed out"
                            )
                        ))
                    }
                }
            }

            val mainHandler = android.os.Handler(Looper.getMainLooper())
            mainHandler.postDelayed(timeoutRunnable, LOCATION_TIMEOUT)

            providers.forEach { provider ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        if (!locationReceived && continuation.isActive) {
                            locationReceived = true
                            mainHandler.removeCallbacks(timeoutRunnable)
                            listeners.forEach { (_, listener) ->
                                locationManager.removeUpdates(listener)
                            }
                            continuation.resume(location.toPlatformLocation())
                        }
                    }

                    override fun onProviderEnabled(provider: String) {}

                    override fun onProviderDisabled(provider: String) {
                        listeners[provider]?.let { locationManager.removeUpdates(it) }
                        listeners.remove(provider)

                        if (listeners.isEmpty() && !locationReceived && continuation.isActive) {
                            mainHandler.removeCallbacks(timeoutRunnable)
                            continuation.resumeWithException(Exception(
                                createBilingualMessage(
                                    fa = "ارائه‌دهنده موقعیت غیرفعال است",
                                    en = "Location provider is disabled"
                                )
                            ))
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                }

                listeners[provider] = listener

                try {
                    locationManager.requestLocationUpdates(
                        provider,
                        0L,
                        MIN_DISTANCE,
                        listener,
                        Looper.getMainLooper()
                    )
                } catch (e: Exception) {
                    listeners.remove(provider)
                }
            }

            continuation.invokeOnCancellation {
                mainHandler.removeCallbacks(timeoutRunnable)
                listeners.forEach { (_, listener) ->
                    locationManager.removeUpdates(listener)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getBestLastKnownLocation(): Location? {
        try {
            val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            return when {
                gpsLocation != null && networkLocation != null ->
                    if (gpsLocation.time >= networkLocation.time) gpsLocation else networkLocation
                gpsLocation != null -> gpsLocation
                networkLocation != null -> networkLocation
                else -> null
            }
        } catch (e: Exception) {
            return null
        }
    }

    private fun isLocationFresh(location: Location): Boolean {
        val locationAge = System.currentTimeMillis() - location.time
        return locationAge < 30000
    }

    @SuppressLint("MissingPermission")
    override suspend fun observeLocationUpdates(interval: Long): Flow<PlatformLocation> {
        return callbackFlow {
            val providers = mutableListOf<String>()
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                providers.add(LocationManager.GPS_PROVIDER)
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                providers.add(LocationManager.NETWORK_PROVIDER)
            }

            if (providers.isEmpty()) {
                close(Exception(createBilingualMessage(
                    fa = "هیچ ارائه‌دهنده موقعیتی در دسترس نیست",
                    en = "No location provider available"
                )))
                return@callbackFlow
            }

            var lastLocation: Location? = null
            val listeners = mutableMapOf<String, LocationListener>()

            providers.forEach { provider ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        if (isBetterLocation(location, lastLocation)) {
                            lastLocation = location
                            launch { send(location.toPlatformLocation()) }
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {
                        listeners[provider]?.let { locationManager.removeUpdates(it) }
                        listeners.remove(provider)
                    }
                }

                listeners[provider] = listener

                try {
                    locationManager.requestLocationUpdates(
                        provider,
                        interval,
                        MIN_DISTANCE,
                        listener,
                        Looper.getMainLooper()
                    )
                } catch (e: Exception) {
                    listeners.remove(provider)
                }
            }

            awaitClose {
                listeners.forEach { (_, listener) ->
                    locationManager.removeUpdates(listener)
                }
            }
        }
    }

    private fun isBetterLocation(location: Location, currentBestLocation: Location?): Boolean {
        if (currentBestLocation == null) {
            return true
        }

        val timeDelta = location.time - currentBestLocation.time
        val isSignificantlyNewer = timeDelta > 60000
        val isSignificantlyOlder = timeDelta < -60000

        when {
            isSignificantlyNewer -> return true
            isSignificantlyOlder -> return false
        }

        val accuracyDelta = (location.accuracy - currentBestLocation.accuracy)
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200
        val isFromSameProvider = location.provider == currentBestLocation.provider

        return when {
            isMoreAccurate -> true
            isSignificantlyLessAccurate -> false
            isFromSameProvider -> !isLessAccurate
            else -> true
        }
    }
}