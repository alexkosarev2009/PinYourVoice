package com.example.shareyourvoicemapbox.data.player

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ExoPLayerImpl @Inject constructor(
    private val context: Context,
): com.example.shareyourvoicemapbox.domain.player.ExoPlayer {
    private var player: ExoPlayer? = null

    override suspend fun play(url: String, repeat: Int): Long = suspendCoroutine { continuation ->
        if (player == null) {
            player = ExoPlayer.Builder(context).build()
        }

        val mediaItem = MediaItem.fromUri(
            url.toUri(),
        )

        player?.apply {
            setMediaItem(mediaItem)
            repeatMode = repeat
            prepare()
            addListener(object: Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        val actualDuration = player?.duration ?: 0L
                        Log.d("DURATION", actualDuration.toString())

                        removeListener(this)

                        continuation.resume(actualDuration)
                    }
                }
                override fun onPlayerError(error: PlaybackException) {
                    Log.e("PLAYER_ERROR", "Не удалось загрузить аудио: ${error.message}")

                    removeListener(this)
                    continuation.resume(0L) //
                }
            })
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