package com.ma.tehro.common

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import com.ma.tehro.data.BilingualName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.Locale
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.math.sqrt

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
        1 -> Color(COLOR_LINE_1.toColorInt())
        2 -> Color(COLOR_LINE_2.toColorInt())
        3 -> Color(COLOR_LINE_3.toColorInt())
        4 -> Color(COLOR_LINE_4.toColorInt())
        5 -> Color(COLOR_LINE_5.toColorInt())
        6 -> Color(COLOR_LINE_6.toColorInt())
        7 -> Color(COLOR_LINE_7.toColorInt())
        else -> Color.Gray
    }
}

fun calculateLineName(lineNumber: Int, useBranch: Boolean = false): BilingualName {
    val enEndpoints = LineEndpoints.getEn(lineNumber, useBranch)
    val faEndpoints = LineEndpoints.getFa(lineNumber, useBranch)

    val pathType = if (useBranch && LineEndpoints.hasBranch(lineNumber)) {
        "Branch Line" to "خط فرعی"
    } else {
        "Line" to "خط"
    }

    return BilingualName(
        en = "${pathType.first} $lineNumber - ${enEndpoints?.first}/${enEndpoints?.second}",
        fa = "${pathType.second} ${lineNumber.toFarsiNumber()} - ${faEndpoints?.first}/${faEndpoints?.second}"
    )
}

fun calculateBilingualLineName(lineNumber: Int, useBranch: Boolean = false): BilingualName {
    val enEndpoints = LineEndpoints.getEn(lineNumber, useBranch)
    val faEndpoints = LineEndpoints.getFa(lineNumber, useBranch)

    val pathType = if (useBranch && LineEndpoints.hasBranch(lineNumber)) {
        "Branch Line" to "خط فرعی"
    } else {
        "Line" to "خط"
    }
    return BilingualName(
        en = "${pathType.first} $lineNumber - ${enEndpoints?.first} / ${enEndpoints?.second}",
        fa = "${pathType.second} ${lineNumber.toFarsiNumber()} - ${faEndpoints?.first} / ${faEndpoints?.second}"
    )
}

fun String.toFarsiNumber(): String {
    val farsiNumbers = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.map {
        if (it.isDigit()) farsiNumbers[it.digitToInt()] else it
    }.joinToString("")
}

fun Int.toFarsiNumber(): String = this.toString().toFarsiNumber()

fun Double.toFarsiNumber(): String = this.toString().toFarsiNumber()

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
    val millisInDay = 24 * 60 * 60 * 1000
    val totalMillis = (fraction * millisInDay).roundToLong()

    val hours = (totalMillis / (60 * 60 * 1000)) % 24
    val minutes = (totalMillis / (60 * 1000)) % 60
    val seconds = (totalMillis / 1000) % 60

    return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
}

fun isFarsi(text: String): Boolean {
    if (text.isEmpty()) return false
    val firstChar = text.trim().firstOrNull() ?: return false
    return firstChar in '\u0600'..'\u06FF'
}


fun Color.darken(factor: Float = 0.85f): Color {
    return Color(
        red = (red * factor).coerceIn(0f, 1f),
        green = (green * factor).coerceIn(0f, 1f),
        blue = (blue * factor).coerceIn(0f, 1f),
        alpha = alpha
    )
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }

    return hiltViewModel(parentEntry)
}


fun normalizeWords(text: String): List<String> {
    return text.trim()
        .split("\\s+".toRegex())
        .map { it.lowercase() }
}