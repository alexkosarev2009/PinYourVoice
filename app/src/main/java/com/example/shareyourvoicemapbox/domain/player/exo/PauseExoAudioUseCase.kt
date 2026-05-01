package com.example.shareyourvoicemapbox.domain.player.exo

import com.example.shareyourvoicemapbox.domain.player.ExoPlayer
import javax.inject.Inject

class PauseExoAudioUseCase @Inject constructor(
    private val exoPlayer: ExoPlayer
) {
    operator fun invoke() {
        exoPlayer.pause()
    }
}