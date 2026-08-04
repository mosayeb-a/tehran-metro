package com.ma.tehro.domain.place.repository

import com.ma.tehro.domain.path.Place

interface PlacesRepository {
    val getAll: List<Place>
}