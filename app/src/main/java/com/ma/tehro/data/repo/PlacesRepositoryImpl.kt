package com.ma.tehro.data.repo

import com.ma.tehro.data.Place
import com.ma.tehro.domain.repo.PlacesRepository

class PlacesRepositoryImpl(
    val places: List<Place>
) : PlacesRepository {

    override val getAll: List<Place>
        get() = places
}