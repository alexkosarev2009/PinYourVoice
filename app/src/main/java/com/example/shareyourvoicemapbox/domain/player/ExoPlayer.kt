package com.example.shareyourvoicemapbox.domain.player

interface ExoPlayer {
    suspend fun play(url: String, repeat: Int): Long
    fun resume()
    fun pause()
    fun release()
    fun seekTo(ms: Long)
    fun getCurrentPosition(): Long
}