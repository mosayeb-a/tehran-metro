package com.ma.tehro.domain.usecase

import com.ma.tehro.domain.CategorizedPlaces
import com.ma.tehro.domain.repo.PlacesRepository

class ShowPlacesByCategory(
    private val placesRepository: PlacesRepository
) {
    fun getPlacesByCategory(): List<CategorizedPlaces> {
        return placesRepository.getAll
            .groupBy { it.category }
            .map { (category, places) ->
                CategorizedPlaces(
                    category = category,
                    places = places.sortedBy { it.name }
                )
            }
            .sortedBy { it.category.value }
    }
}