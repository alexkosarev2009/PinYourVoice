package com.example.shareyourvoicemapbox.domain.amplituda

interface AudioProcessor {
    suspend fun processAudio(filePath: String): Result<List<Int>>
}