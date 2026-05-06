package com.example.shareyourvoicemapbox.domain.player.exo

import androidx.media3.common.Player
import com.example.shareyourvoicemapbox.domain.player.ExoPlayer
import javax.inject.Inject

class PlayExoAudioUseCase @Inject constructor(
    private val exoPlayer: ExoPlayer
) {
    operator fun invoke(url: String, repeatMode: Int = Player.REPEAT_MODE_OFF) {
        exoPlayer.play(url, repeat = repeatMode)
    }
}