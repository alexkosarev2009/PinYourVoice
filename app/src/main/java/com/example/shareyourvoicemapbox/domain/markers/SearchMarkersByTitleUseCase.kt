package com.example.shareyourvoicemapbox.domain.markers

import com.example.shareyourvoicemapbox.data.repo.MarkerRepository
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import javax.inject.Inject

class SearchMarkersByTitleUseCase @Inject constructor(
    private val markerRepository: MarkerRepository
) {
    suspend operator fun invoke(query: String): Result<List<MarkerEntity>> {
        return markerRepository.searchMarkersByTitle(query)
    }
}