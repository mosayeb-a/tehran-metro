package com.ma.tehro.domain.place

import com.ma.tehro.data.place.Place
import com.ma.tehro.data.place.PlaceCategory

data class PlaceGroup(
    val category: PlaceCategory,
    val places: List<Place>
)