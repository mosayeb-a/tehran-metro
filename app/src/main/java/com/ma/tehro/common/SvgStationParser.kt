package com.ma.tehro.common

import android.content.Context
import com.ma.tehro.R
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

object SvgStationParser {
    fun parseStations(
        context: Context,
        targetStations: List<String>,
    ): Map<String, Pair<Float, Float>> {
        val stationCoordinates = mutableMapOf<String, Pair<Float, Float>>()
        context.resources.openRawResource(R.raw.tehran_map).use { inputStream ->
            val parser = XmlPullParserFactory.newInstance()
                .apply { isNamespaceAware = true }
                .newPullParser()
                .apply { setInput(inputStream, null) }

            var currentStation: String? = null
            var lastX: Float? = null
            var lastY: Float? = null

            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                when (parser.eventType) {
                    XmlPullParser.START_TAG -> when (parser.name) {
                        "use" -> {
                            lastX = parser.getAttributeValue(null, "x")?.toFloatOrNull()
                            lastY = parser.getAttributeValue(null, "y")?.toFloatOrNull()
                        }
                        "text" -> {
                            parser.next()
                            if (parser.eventType == XmlPullParser.TEXT) {
                                currentStation = parser.text.trim()
                                if (currentStation in targetStations && lastX != null && lastY != null) {
                                    stationCoordinates[currentStation] = lastX to lastY
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> if (parser.name == "text") {
                        currentStation = null
                    }
                }
                parser.next()
            }
        }
        return stationCoordinates
    }
}
