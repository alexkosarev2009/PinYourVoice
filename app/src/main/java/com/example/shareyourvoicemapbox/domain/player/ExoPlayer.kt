package com.example.shareyourvoicemapbox.domain.player

interface ExoPlayer {
    fun play(url: String, repeat: Int)
    fun resume()
    fun pause()
    fun release()
    fun seekTo(ms: Long)
    fun getCurrentPosition(): Long
}