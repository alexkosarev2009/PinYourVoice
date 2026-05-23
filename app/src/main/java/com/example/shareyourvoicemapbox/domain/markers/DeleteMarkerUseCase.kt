package com.example.shareyourvoicemapbox.domain.markers

import com.example.shareyourvoicemapbox.data.repo.MarkerRepository
import javax.inject.Inject

class DeleteMarkerUseCase @Inject constructor(
    private val markerRepository: MarkerRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return markerRepository.deleteMarker(id)
    }
}