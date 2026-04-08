package com.example.shareyourvoicemapbox.domain.markers

import com.example.shareyourvoicemapbox.data.MarkerRepository
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity

class GetMarkersUseCase(
    val markerRepository: MarkerRepository
) {
    suspend operator fun invoke(): Result<List<MarkerEntity>> {
        return markerRepository.getMarkers()
    }
}