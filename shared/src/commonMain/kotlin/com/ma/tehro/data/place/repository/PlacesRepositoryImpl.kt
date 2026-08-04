package com.ma.tehro.data.place.repository

import com.ma.tehro.domain.path.Place
import com.ma.tehro.domain.place.repository.PlacesRepository

class PlacesRepositoryImpl(
    val places: List<Place>
) : PlacesRepository {

    override val getAll: List<Place>
        get() = places
}