package com.example.vyorius_assingment.player

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.SurfaceView
import android.widget.Toast
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout
import java.io.File
import org.videolan.libvlc.MediaPlayer.Event

class VLCPlayerManager(private val context: Context) {
    private var libVLC: LibVLC? = null
    private var mediaPlayer: MediaPlayer? = null

    fun initPlayer(videoLayout: VLCVideoLayout) {
        val options = ArrayList<String>().apply {
            add("--no-drop-late-frames")
            add("--no-skip-frames")
            add("-vvv") // verbose log level
            add("--rtsp-tcp") // Use TCP for better stability
            add(":no-audio-time-stretch")
            add(":codec=avcodec") // Force use of standard codec
            add(":live-caching=300")
            add(":file-caching=300")
            add(":network-caching=300")
            add(":clock-jitter=0")
            add(":clock-synchro=0")
            add(":no-audio")
        }
        libVLC = LibVLC(context, options)
        mediaPlayer = MediaPlayer(libVLC).apply {
            attachViews(videoLayout, null, true, false)
        }
    }

    fun playStream(rtspUrl: String) {
        val media = Media(libVLC, Uri.parse(rtspUrl)).apply {
            setHWDecoderEnabled(true, false) // Safer
            addOption(":rtsp-tcp")
            addOption(":network-caching=300") // Already added above, but OK to repeat here
            addOption(":codec=avcodec")
        }
        mediaPlayer?.media = media
        media.release()
        mediaPlayer?.play()
        mediaPlayer?.setEventListener { event ->
            when (event.type) {
                Event.Buffering -> {
                    Log.d("VLC", "Buffering... ${event.buffering}%")
                }

                Event.Playing -> {
                    Log.d("VLC", "Stream is now playing!")
                }

                Event.Stopped -> {
                    Log.d("VLC", "Playback stopped")
                }

                Event.EncounteredError -> {
                    Log.d("VLC", "Error: Failed to play stream")
                }

                Event.Opening -> {
                    Log.d("VLC", "Opening stream...")
                }

                Event.EndReached -> {
                    Log.d("VLC", "Stream ended")
                }

            }
        }

    }

    fun startRecording(): Boolean {
        val dir = File(context.getExternalFilesDir(null), "Recordings")
        if (!dir.exists()) dir.mkdirs()

        val filePath = File(dir, "recorded_${System.currentTimeMillis()}.ts").absolutePath
        val result = mediaPlayer?.record(filePath) ?: false
        Log.d("VLC", "Recording started? $result — Path: $filePath")
        return result
    }

    fun stopRecording() {
        mediaPlayer?.record(null) // Passing null stops recording
        Log.d("VLC", "Recording stopped")
    }

    fun releasePlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.detachViews()
        mediaPlayer?.release()
        libVLC?.release()
        mediaPlayer = null
        libVLC = null
    }

}