package com.example.shareyourvoicemapbox.domain.markers

import com.example.shareyourvoicemapbox.data.dto.MarkerDTO
import com.example.shareyourvoicemapbox.data.repo.MarkerRepository
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import javax.inject.Inject

class GetAvailableMarkersUseCase @Inject constructor(
    private val markerRepository: MarkerRepository
) {
    suspend operator fun invoke(): Result<List<MarkerEntity>> {
        return markerRepository.getAvailableMarkers()
    }
}