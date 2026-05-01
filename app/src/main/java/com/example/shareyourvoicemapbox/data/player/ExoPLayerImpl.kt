package com.example.shareyourvoicemapbox.data.player

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import javax.inject.Inject

class ExoPLayerImpl @Inject constructor(
    private val context: Context,
): com.example.shareyourvoicemapbox.domain.player.ExoPlayer {
    private var player: ExoPlayer? = null

    override fun play(url: String) {
        if (player == null) {
            player = ExoPlayer.Builder(context).build()
        }

        val mediaItem = MediaItem.fromUri(
            url.toUri(),
        )

        player?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    override fun resume() {
        player?.play()
    }

    override fun pause() {
        player?.pause()
    }

    override fun seekTo(ms: Long) {
        player?.apply {
            seekTo(ms)
        }
    }

    override fun getCurrentPosition(): Long {
        return player?.currentPosition ?: 0
    }

    override fun release() {
        player?.release()
        player = null
    }
}