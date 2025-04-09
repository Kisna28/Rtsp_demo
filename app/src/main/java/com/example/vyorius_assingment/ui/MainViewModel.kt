package com.example.vyorius_assingment.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.vyorius_assingment.player.VLCPlayerManager
import org.videolan.libvlc.util.VLCVideoLayout

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val playerManager = VLCPlayerManager(application)
    fun initPlayer(videoLayout: VLCVideoLayout) {
        playerManager.initPlayer(videoLayout)
    }

    fun playStream(rtspUrl: String) {
        playerManager.playStream(rtspUrl)
    }

    fun startRecording(): Boolean {
        return playerManager.startRecording()
    }

    fun stopRecording() {
        playerManager.stopRecording()
    }

    public override fun onCleared() {
        super.onCleared()
        playerManager.releasePlayer()

    }
}
