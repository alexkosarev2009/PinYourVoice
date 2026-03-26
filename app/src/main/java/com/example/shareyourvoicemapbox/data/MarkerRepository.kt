package com.example.shareyourvoicemapbox.data

import com.example.shareyourvoicemapbox.data.source.MarkerDataSource
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity

class MarkerRepository(
    val markerDataSource: MarkerDataSource
) {
    suspend fun getMarkers(): Result<List<MarkerEntity>> {
        return markerDataSource.getMarkers().map { markerDTOS ->
            markerDTOS.mapNotNull { markerDTO ->
                MarkerEntity(
                    title = markerDTO.title ?: return@mapNotNull null,
                    lat = markerDTO.lat ?: return@mapNotNull null,
                    lng = markerDTO.lng ?: return@mapNotNull null,
                    imageUrl = markerDTO.imageUrl,
                    audioUrl = markerDTO.audioUrl ?: return@mapNotNull null,
                    authorName = markerDTO.authorName ?: return@mapNotNull null,
                    authorUsername = markerDTO.authorUsername ?: return@mapNotNull null,
                    authorAvatarUrl = markerDTO.authorAvatarUrl ?: return@mapNotNull null,
                )
            }
        }
    }
}