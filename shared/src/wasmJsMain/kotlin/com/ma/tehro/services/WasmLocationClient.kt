package com.ma.tehro.services

import com.ma.tehro.common.PositionError
import com.ma.tehro.common.createPositionOptions
import com.ma.tehro.common.navigator
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class WasmLocationClient : LocationClient {

    private val geolocation = navigator.geolocation

    private fun PositionError.toException(): Exception {
        return Exception(message)
    }

    override suspend fun getCurrentLocation(): PlatformLocation =
        suspendCancellableCoroutine { cont ->

            val geo = geolocation
            if (geo == null) {
                cont.resumeWithException(Exception("Geolocation not supported"))
                return@suspendCancellableCoroutine
            }

            val options = createPositionOptions()
            options.enableHighAccuracy = true
            options.timeout = 10_000
            options.maximumAge = 30_000

            geo.getCurrentPosition(
                successCallback = { pos ->
                    if (cont.isActive) cont.resume(pos.toPlatformLocation())
                },
                errorCallback = { err ->
                    if (cont.isActive) cont.resumeWithException(err.toException())
                },
                options = options
            )
        }

    override suspend fun observeLocationUpdates(
        interval: Long
    ) = callbackFlow {

        val geo = geolocation
        if (geo == null) {
            close(Exception("Geolocation not supported"))
            return@callbackFlow
        }

        val watchId = geo.watchPosition(
            successCallback = { trySend(it.toPlatformLocation()) },
            errorCallback = { close(it.toException()) }
        )

        awaitClose {
            geo.clearWatch(watchId)
        }
    }
}
