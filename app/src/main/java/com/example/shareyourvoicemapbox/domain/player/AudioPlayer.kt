package com.example.shareyourvoicemapbox.domain.player

interface AudioPlayer {
    fun play(filePath: String)
    fun resume()
    fun pause()
    fun seekTo(ms: Long)
    fun getCurrentPosition(): Int
    fun release()
}