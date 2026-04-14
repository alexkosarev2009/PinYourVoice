package com.example.shareyourvoicemapbox.domain.amplituda

import javax.inject.Inject

class ProcessAudioUseCase @Inject constructor(
    private val audioProcessor: AudioProcessor
) {
    suspend operator fun invoke(filePath: String): Result<List<Int>> =
         audioProcessor.processAudio(filePath)
}