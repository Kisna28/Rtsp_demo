package com.example.vyorius_assingment

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vyorius_assingment.databinding.ActivityMainBinding
import com.example.vyorius_assingment.ui.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.initPlayer(binding.vlcVideoLayout)

        binding.btnPlay.setOnClickListener {
            val url = binding.edRtspUrl.text.toString()
            if (url.isNotBlank()){
                viewModel.playStream(url)

            }
        }
        binding.btnRecord.setOnClickListener {
            if(isRecording){
                viewModel.stopRecording()
                binding.btnRecord.text = "Record"
            }else{
                val started = viewModel.startRecording()
                if (started) binding.btnRecord.text = "Stop"
            }
            isRecording =! isRecording

        }
        binding.btnPip.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                enterPipMode()
            }
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun enterPipMode() {
        hideNonVideoViews()
        val aspectRatio = Rational(binding.vlcVideoLayout.width, binding.vlcVideoLayout.height)
        val pipBuilder = PictureInPictureParams.Builder()
            .setAspectRatio(aspectRatio)
        enterPictureInPictureMode(pipBuilder.build())
    }
    override fun onStop() {
        super.onStop()
        if (isInPictureInPictureMode) {
            // Stream keeps playing
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        viewModel.onCleared()
    }
    private fun hideNonVideoViews() {
        // Example: if you have a toolbar or controls
        binding.edRtspUrl.visibility = View.GONE
        binding.Linear.visibility = View.GONE

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        if (isInPictureInPictureMode) {
            // Hide all other UI elements in PiP mode
            binding.edRtspUrl.visibility = View.GONE
            binding.Linear.visibility = View.GONE
        } else {
            // Restore UI when exiting PiP mode
            binding.edRtspUrl.visibility = View.GONE
            binding.Linear.visibility = View.GONE
        }
    }

}