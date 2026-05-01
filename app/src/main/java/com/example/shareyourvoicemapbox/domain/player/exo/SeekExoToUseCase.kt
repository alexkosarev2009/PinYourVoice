package com.example.shareyourvoicemapbox.domain.player.exo

import com.example.shareyourvoicemapbox.domain.player.ExoPlayer
import javax.inject.Inject

class SeekExoToUseCase @Inject constructor(
    private val exoPlayer: ExoPlayer
) {
    operator fun invoke(ms: Long) {
        exoPlayer.seekTo(ms)
    }
}