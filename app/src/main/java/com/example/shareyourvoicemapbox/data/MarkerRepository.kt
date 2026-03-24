package com.example.shareyourvoicemapbox.data

import com.example.shareyourvoicemapbox.data.source.MarkerDataSource
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity

class MarkerRepository(
    val markerDataSource: MarkerDataSource
) {
    suspend fun getUsers(): Result<List<MarkerEntity>> {
        return markerDataSource.getMarkers().map { markerDTOS ->
            markerDTOS.mapNotNull { markerDTO ->
                MarkerEntity(
                    title = markerDTO.title ?: return@mapNotNull null,
                    lat = markerDTO.lat ?: return@mapNotNull null,
                    lng = markerDTO.lng ?: return@mapNotNull null,
                    imgUrl = markerDTO.imgUrl,
                    audioUrl = markerDTO.audioUrl ?: return@mapNotNull null,
                )
            }
        }
    }
}