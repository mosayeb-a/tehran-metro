package com.ma.tehro.domain.place.repository

import com.ma.tehro.data.place.Place

interface PlacesRepository {
    val getAll: List<Place>
}