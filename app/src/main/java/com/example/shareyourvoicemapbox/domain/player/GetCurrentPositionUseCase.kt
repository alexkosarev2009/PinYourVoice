package com.example.shareyourvoicemapbox.domain.player

import javax.inject.Inject

class GetCurrentPositionUseCase @Inject constructor(
    private val audioPlayer: AudioPlayer
) {
    operator fun invoke(): Int {
        return audioPlayer.getCurrentPosition()
    }
}