package com.example.shareyourvoicemapbox.data.recorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import com.example.shareyourvoicemapbox.domain.recorder.AudioRecorder
import java.io.File

class AudioRecorderImpl(
    private val context: Context
) : AudioRecorder {
    private var recorder: MediaRecorder? = null
    private var currentFilePath: String = ""

    override fun startRecording(filePath: String) {
        if (recorder != null) return

        currentFilePath = filePath


        val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }

        recorder = mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(filePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            try {
                prepare()
                start()
            } catch (e: Exception) {
                Log.e("AudioRecorder", "Start recording failed", e)
                release()
                recorder = null
            }
        }
    }

    override fun stopRecording(): String {
        recorder?.apply {
            try {
                stop()
            } catch (e: Exception) {
                Log.e("AudioRecorder", "Stop failed", e)
            }
            finally {
                release()
            }
        }
        recorder = null
        return currentFilePath
    }

    override fun createFile(): String {
        val file = File(context.cacheDir, "audio_${System.currentTimeMillis()}.m4a")
        return file.absolutePath
    }
}