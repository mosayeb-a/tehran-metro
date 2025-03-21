package com.ma.tehro.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavType
import com.ma.tehro.data.Station
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


data class AppCoroutineDispatchers(
    val io: CoroutineDispatcher,
    val databaseWrite: CoroutineDispatcher,
    val databaseRead: CoroutineDispatcher,
    val computation: CoroutineDispatcher,
    val main: CoroutineDispatcher,
)

inline fun <reified T> navTypeOf(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: Bundle, key: String): T? =
        bundle.getString(key)?.let(json::decodeFromString)

    override fun parseValue(value: String): T = json.decodeFromString(Uri.decode(value))

    override fun serializeAsValue(value: T): String = Uri.encode(json.encodeToString(value))

    override fun put(bundle: Bundle, key: String, value: T) =
        bundle.putString(key, json.encodeToString(value))
}

fun Painter.toImageBitmap(
    size: Size,
    density: Density,
    layoutDirection: LayoutDirection,
): ImageBitmap {
    val bitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
    val canvas = Canvas(bitmap)
    CanvasDrawScope().draw(density, layoutDirection, canvas, size) {
        draw(size)
    }
    return bitmap
}

fun getLineEnEndpoints(): Map<Int, Pair<String, String>> {
    return mapOf(
        1 to Pair("Tajrish", "Kahrizak"),
        2 to Pair("Farhangsara", "Sadeghiyeh"),
        3 to Pair("Qa'em", "Azadegan"),
        4 to Pair("Kolahdooz", "Allameh Jafari"),
        5 to Pair("Sadeghiyeh", "Qasem Soleimani"),
        6 to Pair("Haram-e Abdol Azim", "Kouhsar"),
        7 to Pair("Varzeshgah-e Takhti", "Meydan-e Ketab")
    )
}
fun getLineFaEndpoints(): Map<Int, Pair<String, String>> {
    return mapOf(
        1 to Pair("تجریش", "کهریزک"),
        2 to Pair("فرهنگسرا", "صادقیه"),
        3 to Pair("قائم", "آزادگان"),
        4 to Pair("کلاهدوز", "علامه جعفری"),
        5 to Pair("صادقیه", "قاسم سلیمانی"),
        6 to Pair("حرم عبدالعظیم", "کوهسار"),
        7 to Pair("ورزشگاه تختی", "میدان کتاب")
    )
}

fun getLineColorByNumber(lineNumber: Int): Color {
    return when (lineNumber) {
        1 -> Color(android.graphics.Color.parseColor(COLOR_LINE_1))
        2 -> Color(android.graphics.Color.parseColor(COLOR_LINE_2))
        3 -> Color(android.graphics.Color.parseColor(COLOR_LINE_3))
        4 -> Color(android.graphics.Color.parseColor(COLOR_LINE_4))
        5 -> Color(android.graphics.Color.parseColor(COLOR_LINE_5))
        6 -> Color(android.graphics.Color.parseColor(COLOR_LINE_6))
        7 -> Color(android.graphics.Color.parseColor(COLOR_LINE_7))
        else -> Color.Gray
    }
}

fun getLineNumberByColor(color: Color): Int {
    return when (color) {
        Color(android.graphics.Color.parseColor(COLOR_LINE_1)) -> 1
        Color(android.graphics.Color.parseColor(COLOR_LINE_2)) -> 2
        Color(android.graphics.Color.parseColor(COLOR_LINE_3)) -> 3
        Color(android.graphics.Color.parseColor(COLOR_LINE_4)) -> 4
        Color(android.graphics.Color.parseColor(COLOR_LINE_5)) -> 5
        Color(android.graphics.Color.parseColor(COLOR_LINE_6)) -> 6
        Color(android.graphics.Color.parseColor(COLOR_LINE_7)) -> 7
        else -> -1
    }
}

fun calculateLineName(lineNumber: Int): String {
    val enEndpoints = getLineEnEndpoints()
    val faEndpoints = getLineFaEndpoints()

    return """
        خط ${lineNumber.toFarsiNumber()} - ${faEndpoints[lineNumber]?.first}/${faEndpoints[lineNumber]?.second}
        Line $lineNumber - ${enEndpoints[lineNumber]?.first}/${enEndpoints[lineNumber]?.second}
    """.trimIndent()
}

fun String.toFarsiNumber(): String {
    val farsiNumbers = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.map {
        if (it.isDigit()) farsiNumbers[it.digitToInt()] else it
    }.joinToString("")
}

fun Int.toFarsiNumber(): String = this.toString().toFarsiNumber()

fun Double.toFarsiNumber(): String = this.toString().toFarsiNumber()


fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371e3

    val lat1Rad = Math.toRadians(lat1)
    val lon1Rad = Math.toRadians(lon1)
    val lat2Rad = Math.toRadians(lat2)
    val lon2Rad = Math.toRadians(lon2)

    val deltaLat = lat2Rad - lat1Rad
    val deltaLon = lon2Rad - lon1Rad

    val a = sin(deltaLat / 2).pow(2) +
            cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2).pow(2)

    val c = 2 * asin(sqrt(a))

    return earthRadius * c
}

@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle, key1, key2, flow) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}

fun createBilingualMessage(fa: String, en: String): String {
    return "$fa\n$en"
}