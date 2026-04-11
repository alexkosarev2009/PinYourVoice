package com.example.shareyourvoicemapbox.domain.player

import javax.inject.Inject

class PlayAudioUseCase @Inject constructor(
    private val audioPlayer: AudioPlayer
) {
    operator fun invoke(filePath: String) {
        audioPlayer.play(filePath = filePath)
    }
}