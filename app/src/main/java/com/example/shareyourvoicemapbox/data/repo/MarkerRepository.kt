package com.example.shareyourvoicemapbox.data.repo

import com.example.shareyourvoicemapbox.data.dto.CreateMarkerDTO
import com.example.shareyourvoicemapbox.data.source.marker.MarkerDataSource
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import javax.inject.Inject

class MarkerRepository @Inject constructor(
    private val markerDataSource: MarkerDataSource
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
    suspend fun getMarkersByAuthorId(authorId: Long): Result<List<MarkerEntity>> {
        return markerDataSource.getMarkersByAuthorId(authorId).map { markerDTOS ->
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
    suspend fun createMarker(dto: CreateMarkerDTO): Result<MarkerEntity> {
        return markerDataSource.postMarker(dto).map { markerDTO ->
            MarkerEntity(
                title = markerDTO.title ?: error("Invalid MarkerDTO from server"),
                lat = markerDTO.lat ?: error("Invalid MarkerDTO from server"),
                lng = markerDTO.lng ?: error("Invalid MarkerDTO from server"),
                imageUrl = markerDTO.imageUrl,
                audioUrl = markerDTO.audioUrl ?: error("Invalid MarkerDTO from server"),
                authorName = markerDTO.authorName ?: error("Invalid MarkerDTO from server"),
                authorUsername = markerDTO.authorUsername ?: error("Invalid MarkerDTO from server"),
                authorAvatarUrl = markerDTO.authorAvatarUrl ?: error("Invalid MarkerDTO from server"),
            )
        }
    }
}