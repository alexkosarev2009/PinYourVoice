package com.example.shareyourvoicemapbox.domain.player

interface ExoPlayer {
    fun play(url: String)
    fun resume()
    fun pause()
    fun release()
    fun seekTo(ms: Long)
    fun getCurrentPosition(): Long
}