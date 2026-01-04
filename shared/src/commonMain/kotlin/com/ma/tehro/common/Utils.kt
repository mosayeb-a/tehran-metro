package com.ma.tehro.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import com.ma.tehro.data.BilingualName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.math.PI
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
    override fun get(bundle: SavedState, key: String): T? =
        bundle.read { getString(key).let(json::decodeFromString) }

    override fun parseValue(value: String): T = json.decodeFromString(value)

    override fun serializeAsValue(value: T): String = json.encodeToString(value)

    override fun put(bundle: SavedState, key: String, value: T) =
        bundle.write { putString(key, json.encodeToString(value)) }
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
        1 -> Color(COLOR_LINE_1)
        2 -> Color(COLOR_LINE_2)
        3 -> Color(COLOR_LINE_3)
        4 -> Color(COLOR_LINE_4)
        5 -> Color(COLOR_LINE_5)
        6 -> Color(COLOR_LINE_6)
        7 -> Color(COLOR_LINE_7)
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
    val earthRadius = 6371000.0

    val lat1Rad = lat1 * PI / 180
    val lon1Rad = lon1 * PI / 180
    val lat2Rad = lat2 * PI / 180
    val lon2Rad = lon2 * PI / 180

    val deltaLat = lat2Rad - lat1Rad
    val deltaLon = lon2Rad - lon1Rad

    val a = sin(deltaLat / 2).pow(2.0) +
            cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2).pow(2.0)
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
    val totalSeconds = (fraction * 24 * 60 * 60).roundToLong()

    val hours = (totalSeconds / 3600) % 24
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    val hoursStr = hours.toString().padStart(2, '0')
    val minutesStr = minutes.toString().padStart(2, '0')
    val secondsStr = seconds.toString().padStart(2, '0')

    return "$hoursStr:$minutesStr:$secondsStr"
}

fun isFarsi(text: String): Boolean {
    if (text.isEmpty()) return true
    val firstChar = text.trim().firstOrNull() ?: return false
    return firstChar in '\u0600'..'\u06FF'
}

fun normalizeWords(text: String): List<String> {
    return text.trim()
        .split("\\s+".toRegex())
        .map { it.lowercase() }
}

@Composable
fun getWindowWidth(): Dp {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    return with(density) {
        windowInfo.containerSize.width.toDp()
    }
}

@Composable
fun getWindowHeight(): Dp {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    return with(density) {
        windowInfo.containerSize.height.toDp()
    }
}

expect val ioCoroutineDispatcher : CoroutineDispatcher