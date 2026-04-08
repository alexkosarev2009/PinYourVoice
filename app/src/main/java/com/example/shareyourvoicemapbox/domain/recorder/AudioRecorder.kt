package com.example.shareyourvoicemapbox.domain.recorder

import java.io.File

interface AudioRecorder {
    fun startRecording(filePath: String)
    fun stopRecording(): String
    fun createFile(): String
}