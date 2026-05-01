package com.example.shareyourvoicemapbox.domain.amplituda

class ParseAmplitudesUseCase {
    operator fun invoke(amplitudes: String): List<Int> {
        return amplitudes
            .removePrefix("[")
            .removeSuffix("]")
            .split(",")
            .map { it.trim().toInt() }
    }
}