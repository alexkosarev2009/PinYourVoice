package com.example.shareyourvoicemapbox.domain.player

interface AudioPlayer {
    fun play(filePath: String)
    fun pause()
}