package com.example.shareyourvoicemapbox.domain.recorder

class StartRecordingUseCase(
    private val recorder: AudioRecorder
) {
    operator fun invoke() {
        val path = recorder.createFile()
        recorder.startRecording(path)
    }
}