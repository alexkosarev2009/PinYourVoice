package com.example.shareyourvoicemapbox.domain.recorder

import javax.inject.Inject

class ReleaseRecorderUseCase @Inject constructor(
    private val recorder: AudioRecorder
) {
    operator fun invoke() {
        recorder.release()
    }
}