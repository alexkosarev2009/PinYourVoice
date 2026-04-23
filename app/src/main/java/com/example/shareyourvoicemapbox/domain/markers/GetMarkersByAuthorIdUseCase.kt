package com.example.shareyourvoicemapbox.domain.markers

import com.example.shareyourvoicemapbox.data.repo.MarkerRepository
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import javax.inject.Inject

class GetMarkersByAuthorIdUseCase @Inject constructor(
    private val markerRepository: MarkerRepository
) {
    suspend operator fun invoke(authorId: Long): Result<List<MarkerEntity>> {
        return markerRepository.getMarkersByAuthorId(authorId)
    }
}