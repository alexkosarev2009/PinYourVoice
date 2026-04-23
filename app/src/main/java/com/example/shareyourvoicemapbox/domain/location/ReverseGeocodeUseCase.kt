package com.example.shareyourvoicemapbox.domain.location

import com.example.shareyourvoicemapbox.data.source.location.LocationDataSource
import javax.inject.Inject

class ReverseGeocodeUseCase @Inject constructor(
    private val locationDataSource: LocationDataSource
) {
    suspend operator fun invoke(
        lat: Double,
        lng: Double,
    ): String {
        return locationDataSource.reverseGeocode(lat, lng)
    }

}