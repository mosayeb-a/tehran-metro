package com.ma.tehro.domain.place

import com.ma.tehro.domain.place.repository.PlacesRepository

class GetPlacesByCategory(
    private val placesRepository: PlacesRepository
) {
    fun getPlaces(): List<PlaceGroup> {
        return placesRepository.getAll
            .groupBy { it.category }
            .map { (category, places) ->
                PlaceGroup(
                    category = category,
                    places = places.sortedBy { it.name }
                )
            }
            .sortedBy { it.category.value }
    }
}