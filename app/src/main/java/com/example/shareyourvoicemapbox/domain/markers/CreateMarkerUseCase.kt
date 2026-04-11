package com.example.shareyourvoicemapbox.domain.markers

import com.example.shareyourvoicemapbox.data.MarkerRepository
import com.example.shareyourvoicemapbox.data.dto.CreateMarkerDTO
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import javax.inject.Inject

class CreateMarkerUseCase @Inject constructor(
    val markerRepository: MarkerRepository,
) {
    suspend operator fun invoke(markerDTO: CreateMarkerDTO): Result<MarkerEntity> {
        return markerRepository.createMarker(markerDTO)
    }
}