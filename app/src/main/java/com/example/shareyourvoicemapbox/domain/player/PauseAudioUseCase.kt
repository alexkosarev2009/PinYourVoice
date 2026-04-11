package com.example.shareyourvoicemapbox.domain.player

import javax.inject.Inject

class PauseAudioUseCase @Inject constructor(
    private val audioPlayer: AudioPlayer
) {
    operator fun invoke() {
        audioPlayer.pause()
    }
}