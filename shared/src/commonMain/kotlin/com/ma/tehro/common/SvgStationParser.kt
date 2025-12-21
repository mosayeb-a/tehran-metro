package com.ma.tehro.common

import com.ma.thero.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.adaptivity.xmlutil.XmlReader
import nl.adaptivity.xmlutil.EventType
import nl.adaptivity.xmlutil.xmlStreaming

object SvgStationParser {
    suspend fun parseStations(
        targetStations: List<String>
    ): Map<String, Pair<Float, Float>> = withContext(Dispatchers.Default) {
        val coordinates = mutableMapOf<String, Pair<Float, Float>>()

        val svgText = Res.readBytes("files/tehran_map.svg")
            .decodeToString()

        val reader: XmlReader = xmlStreaming.newReader(svgText)

        var pendingPosition: Pair<Float, Float>? = null

        while (reader.hasNext()) {
            val event = reader.next()
            when (event) {
                EventType.START_ELEMENT -> {
                    if (reader.localName == "use") {
                        val x = reader.getAttributeValue("", "x")?.toFloatOrNull()
                        val y = reader.getAttributeValue("", "y")?.toFloatOrNull()
                        if (x != null && y != null) {
                            pendingPosition = x to y
                        }
                    }
                }

                EventType.TEXT -> {
                    val text = reader.text.trim()
                    if (text.isNotEmpty() && pendingPosition != null && text in targetStations) {
                        coordinates[text] = pendingPosition
                    }
                    pendingPosition = null
                }

                EventType.END_ELEMENT -> {
                    if (reader.localName == "text") {
                        pendingPosition = null
                    }
                }

                else -> Unit
            }
        }
        reader.close()
        coordinates
    }
}