package com.example.shareyourvoicemapbox.data.recorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import com.example.shareyourvoicemapbox.domain.recorder.AudioRecorder
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                Log.d("RECORDER", "Start recording called")
            } catch (e: Exception) {
                Log.e("RECORDER", "Start recording failed", e)
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
        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd_hh.mm.ss", Locale.US)
        val date = simpleDateFormat.format(Date())
        val file = File(context.cacheDir, "audio_${date}.m4a")
        // ЗАГЛУШКА ДЛЯ ТЕСТИРОВАНИЯ
        context.cacheDir.listFiles()
            ?.filter { it.name.endsWith(".m4a") }
            ?.forEach { it.delete() }

        return file.absolutePath
    }

    override fun release() {
        recorder?.release()
        recorder = null
    }
}