package com.ma.tehro.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Window
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
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Locale
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

fun calculateLineName(lineNumber: Int, useBranch: Boolean = false): String {
    val enEndpoints = LineEndpoints.getEn(lineNumber, useBranch)
    val faEndpoints = LineEndpoints.getFa(lineNumber, useBranch)

    val pathType = if (useBranch && LineEndpoints.hasBranch(lineNumber)) {
        "Branch Line" to "خط فرعی"
    } else {
        "Line" to "خط"
    }

    return """
        ${pathType.second} ${lineNumber.toFarsiNumber()} - ${faEndpoints?.first}/${faEndpoints?.second}
        ${pathType.first} $lineNumber - ${enEndpoints?.first}/${enEndpoints?.second}
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

fun fractionToTime(fraction: Double): String {
    // 24 * 60 * 60 * 1000
    val millisInDay = 86400000
    val totalMillis = (fraction * millisInDay).toLong()

    // 60 * 60 * 1000
    val hours = (totalMillis / 3600000) % 24
    //60 * 1000
    val minutes = (totalMillis / 60000) % 60
    val seconds = (totalMillis / 1000) % 60

    return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
}

fun setStatusBarColor(window: Window, color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            val statusBarInsets = insets.getInsets(android.view.WindowInsets.Type.statusBars())
            view.setBackgroundColor(color)
            view.setPadding(0, statusBarInsets.top, 0, 0)
            insets
        }
    } else {
        window.statusBarColor = color
    }


    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = false
    }
}
fun setNavigationBarColor(window: Window, color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            val navigationBarInsets = insets.getInsets(android.view.WindowInsets.Type.navigationBars())
            view.setPadding(0, 0, 0, navigationBarInsets.bottom)
            insets
        }
        window.navigationBarColor = color
    } else {
        window.navigationBarColor = color
    }


    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightNavigationBars = false
    }
}

fun isFarsi(text: String): Boolean {
    if (text.isEmpty()) return false
    val firstChar = text.trim().firstOrNull() ?: return false
    return firstChar in '\u0600'..'\u06FF'
}