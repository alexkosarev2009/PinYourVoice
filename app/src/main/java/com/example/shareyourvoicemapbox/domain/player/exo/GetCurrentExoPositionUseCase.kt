package com.example.shareyourvoicemapbox.domain.player.exo

import com.example.shareyourvoicemapbox.domain.player.ExoPlayer
import javax.inject.Inject

class GetCurrentExoPositionUseCase @Inject constructor(
    private val exoPlayer: ExoPlayer
) {
    operator fun invoke(): Long {
        return exoPlayer.getCurrentPosition()
    }
}