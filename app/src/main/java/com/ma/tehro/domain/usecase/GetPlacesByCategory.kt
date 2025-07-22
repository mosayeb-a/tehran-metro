package com.ma.tehro.domain.usecase

import com.ma.tehro.data.Place
import com.ma.tehro.data.PlaceCategory
import com.ma.tehro.data.repo.PlacesRepository
import javax.inject.Inject

class ShowPlacesByCategory @Inject constructor(
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

data class CategorizedPlaces(
    val category: PlaceCategory,
    val places: List<Place>
)