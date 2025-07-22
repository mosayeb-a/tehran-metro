package com.ma.tehro.data.repo

import com.ma.tehro.data.Place

interface PlacesRepository {
    val getAll: List<Place>
}

class PlacesRepositoryImpl(
    val places: List<Place>
) : PlacesRepository {

    override val getAll: List<Place>
        get() = places
}