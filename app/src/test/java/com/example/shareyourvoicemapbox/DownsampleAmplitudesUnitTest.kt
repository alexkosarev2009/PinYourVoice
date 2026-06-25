package com.example.shareyourvoicemapbox

import junit.framework.TestCase.assertEquals
import org.junit.Test

class DownsampleAmplitudesUnitTest {
    @Test
    fun `Возвращает исходный список, если длина нужного больше длины исходного`() {
        val amplitudes = listOf(1, 2, 3)

        val result = downsampleAmplitudes(amplitudes, 5)

        assertEquals(amplitudes, result)
    }

    @Test
    fun `Возвращает исходный список, если длина нужного равна длине исходного`() {
        val amplitudes = listOf(1, 2, 3, 4)

        val result = downsampleAmplitudes(amplitudes, 4)

        assertEquals(amplitudes, result)
    }

    @Test
    fun `Уменьшает длину списка с помощью максимальных величин из каждой части списка`() {
        val amplitudes = listOf(1, 5, 2, 8, 3, 6)

        val result = downsampleAmplitudes(amplitudes, 3)

        assertEquals(listOf(5, 8, 6), result)
    }

    @Test
    fun `Обрабатывает нечётное количество частей списка`() {
        val amplitudes = listOf(1, 2, 3, 4, 5, 6, 7)

        val result = downsampleAmplitudes(amplitudes, 3)

        assertEquals(listOf(2, 4, 7), result)
    }

    private fun downsampleAmplitudes(
        amplitudes: List<Int>,
        targetSize: Int
    ): List<Int> {
        if (amplitudes.size <= targetSize) return amplitudes

        val chunkSize = amplitudes.size.toFloat() / targetSize

        return List(targetSize) { index ->
            val start = (index * chunkSize).toInt()
            val end = ((index + 1) * chunkSize).toInt().coerceAtMost(amplitudes.size)

            amplitudes.subList(start, end).maxOrNull() ?: 0
        }
    }
}