package com.example.shareyourvoicemapbox.domain.recorder

interface AudioRecorder {
    fun startRecording(filePath: String)
    fun stopRecording(): String
    fun createFile(): String
    fun release()
}