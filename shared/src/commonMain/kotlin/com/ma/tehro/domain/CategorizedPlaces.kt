package com.ma.tehro.domain

import com.ma.tehro.data.Place
import com.ma.tehro.data.PlaceCategory

data class CategorizedPlaces(
    val category: PlaceCategory,
    val places: List<Place>
)