package com.example.shareyourvoicemapbox.domain.player

import javax.inject.Inject

class SeekToUseCase @Inject constructor(
    private val audioPlayer: AudioPlayer
) {
    operator fun invoke(ms: Long) {
        audioPlayer.seekTo(ms)
    }
}