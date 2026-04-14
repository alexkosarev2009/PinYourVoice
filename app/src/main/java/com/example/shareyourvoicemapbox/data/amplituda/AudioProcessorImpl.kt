package com.example.shareyourvoicemapbox.data.amplituda

import android.content.Context
import android.util.Log
import com.example.shareyourvoicemapbox.domain.amplituda.AudioProcessor
import com.linc.amplituda.Amplituda
import com.linc.amplituda.exceptions.io.AmplitudaIOException
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AudioProcessorImpl(
    private val context: Context
) : AudioProcessor {
    private var amplituda: Amplituda = Amplituda(context)

    override suspend fun processAudio(filePath: String): Result<List<Int>> =
        runCatching {
            suspendCancellableCoroutine { continuation ->
                amplituda.processAudio(File(filePath))
                    .get(
                        { result ->
                            continuation.resume(result?.amplitudesAsList() ?: emptyList())
                        },
                        { exception ->
                            continuation.resumeWithException(exception)
                        }
                    )
            }
        }
}