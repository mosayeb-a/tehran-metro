@file:Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")

package com.ma.tehro.common

import kotlin.js.JsAny
import kotlin.js.JsName
import kotlin.js.definedExternally

@JsName("navigator")
external val navigator: Navigator

external interface Navigator : JsAny {
    val geolocation: Geolocation?
}

external interface Geolocation : JsAny {

    fun getCurrentPosition(
        successCallback: (Position) -> Unit,
        errorCallback: ((PositionError) -> Unit)? = definedExternally,
        options: PositionOptions? = definedExternally
    )

    fun watchPosition(
        successCallback: (Position) -> Unit,
        errorCallback: ((PositionError) -> Unit)? = definedExternally,
        options: PositionOptions? = definedExternally
    ): Int

    fun clearWatch(watchId: Int)
}

external interface Position : JsAny {
    val coords: Coordinates
    val timestamp: Double
}

external interface Coordinates : JsAny {
    val latitude: Double
    val longitude: Double
    val accuracy: Double
    val altitude: Double?
    val altitudeAccuracy: Double?
    val heading: Double?
    val speed: Double?
}

external interface PositionError : JsAny {
    val code: Short
    val message: String
}

external interface PositionOptions : JsAny {
    var enableHighAccuracy: Boolean?
    var timeout: Int?
    var maximumAge: Int?
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
fun createPositionOptions(): PositionOptions =
    js("{}")