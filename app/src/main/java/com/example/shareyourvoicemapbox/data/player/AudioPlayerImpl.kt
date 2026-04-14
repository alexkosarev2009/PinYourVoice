package com.example.shareyourvoicemapbox.data.player

import android.media.MediaPlayer
import android.util.Log
import com.example.shareyourvoicemapbox.domain.player.AudioPlayer

class AudioPlayerImpl: AudioPlayer{
    private var player: MediaPlayer? = null

    override fun play(filePath: String) {
        try {
            if (player == null) {
                player = MediaPlayer().apply {
                    setDataSource(filePath)
                    prepare()
                }
            }
            player?.start()
        } catch (e: Exception) {
            Log.e("PLAYER", "Play failed", e)
            player?.release()
            player = null
        }
    }

    override fun resume() {
        player?.start()
    }

    override fun pause() {
        player?.pause()
    }

    override fun release() {
        player?.release()
    }

    override fun seekTo(ms: Long) {
        player?.apply {
            seekTo(ms.toInt())
        }
    }
    override fun getCurrentPosition(): Int {
        return player?.currentPosition ?: 0
    }

}