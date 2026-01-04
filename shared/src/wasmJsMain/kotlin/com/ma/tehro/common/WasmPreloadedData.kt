package com.ma.tehro.common

import com.ma.tehro.data.Place
import com.ma.tehro.data.Station
import com.ma.thero.resources.Res
import kotlinx.serialization.json.Json

object WasmPreloadedData {
    lateinit var stations: Map<String, Station>
    lateinit var places: List<Place>

    suspend fun load(json: Json) {
        val stationsText =
            Res.readBytes("files/stations.json").decodeToString()
        stations = json.decodeFromString(stationsText)

        val placesText =
            Res.readBytes("files/places.json").decodeToString()
        places = json.decodeFromString(placesText)
    }
}