package com.ma.tehro.domain.repo

import com.ma.tehro.data.Place

interface PlacesRepository {
    val getAll: List<Place>
}