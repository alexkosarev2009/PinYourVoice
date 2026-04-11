package com.example.shareyourvoicemapbox.domain.recorder

import javax.inject.Inject

class StartRecordingUseCase @Inject constructor(
    private val recorder: AudioRecorder
) {
    operator fun invoke() {
        val path = recorder.createFile()
        recorder.startRecording(path)
    }
}