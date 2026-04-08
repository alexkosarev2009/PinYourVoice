package com.example.shareyourvoicemapbox.domain.recorder

class StopRecordingUseCase(
    private val recorder: AudioRecorder
) {
    operator fun invoke(): String {
        return recorder.stopRecording()
    }
}