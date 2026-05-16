package com.example.shareyourvoicemapbox.domain.markers

import com.example.shareyourvoicemapbox.data.repo.MarkerRepository
import com.example.shareyourvoicemapbox.data.repo.UserRepository
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import javax.inject.Inject

class GetFriendsMarkersUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val markerRepository: MarkerRepository,
) {

}