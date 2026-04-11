package com.example.shareyourvoicemapbox.data.player

import android.media.MediaPlayer
import android.util.Log
import com.example.shareyourvoicemapbox.domain.player.AudioPlayer

class AudioPlayerImpl: AudioPlayer{
    private var player: MediaPlayer? = null

    override fun play(filePath: String) {
        if (player != null) return

        val mediaPlayer = MediaPlayer()
        player = mediaPlayer.apply {
            setDataSource(filePath)
            try {
                prepare()
                start()
                Log.d("PLAYER", "play called")
            } catch (e: Exception) {
                Log.e("PLAYER", "play failed", e)
                release()
                player = null
            }
        }
    }

    override fun pause() {
        player?.apply {
            try {
                stop()
            } catch (e: Exception) {
                Log.e("PLAYER", "Stop failed", e)
            }
            finally {
                release()
            }
        }
        player = null
    }
}