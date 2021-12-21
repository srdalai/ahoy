package com.sdtechnocrat.ahoy.views

import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.databinding.ActivityPlayerBinding
import com.sdtechnocrat.ahoy.ui.VerticalSeekBar
import java.io.File

class PlayerActivity : AppCompatActivity() {

    lateinit var binding: ActivityPlayerBinding

    private var player: ExoPlayer? = null

    private var m3u8Url = "https://cdn.theoplayer.com/video/big_buck_bunny/big_buck_bunny.m3u8"

    lateinit var videoUrl : String

    var offlinePlayback = false

    lateinit var am : AudioManager

    lateinit var seekBarVol : VerticalSeekBar
    lateinit var seekBarBright : VerticalSeekBar
    lateinit var imageViewVolume: ImageView

    var previousVol : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        hideSystemBars()

        videoUrl = m3u8Url

        offlinePlayback = intent.getBooleanExtra("offline_playback", false)
        if (offlinePlayback) {
            val fileUri = intent.getStringExtra("file_url").toString()
            val myFile = File(fileUri)
            videoUrl = Uri.fromFile(myFile).toString()
        } else {
            videoUrl = intent.getStringExtra("video_url").toString()
        }

        am = getSystemService(AUDIO_SERVICE) as AudioManager

        initiatePlayerViews()
        setSeekbars()
        imageViewVolume.setOnClickListener {
            val volume = am.getStreamVolume(AudioManager.STREAM_MUSIC)
            val volumeMax = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            if (volume == 0) {
                var newVol = previousVol
                if (newVol == 0) {
                    newVol = (volumeMax * 0.1).toInt()
                }
                am.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, AudioManager.FX_KEY_CLICK)
                seekBarVol.progress = newVol
                imageViewVolume.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_volume_up))
            } else {
                previousVol = volume
                am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FX_KEY_CLICK)
                seekBarVol.progress = 0
                imageViewVolume.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.volume_off))
            }

        }
        binding.playerView.setControllerVisibilityListener {
            if (it == View.VISIBLE) {
                updateSeekbar()
            }
        }

        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    fun initiatePlayerViews() {
        seekBarBright  = binding.playerView.findViewById(R.id.seekbar_brightness)
        seekBarVol  = binding.playerView.findViewById(R.id.seekbar_volume)
        imageViewVolume  = binding.playerView.findViewById(R.id.image_view_volume)
    }

    private fun setSeekbars() {
        val musicVolLevel = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolLevel = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        seekBarVol.max = maxVolLevel
        seekBarVol.progress = musicVolLevel
        seekBarVol.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FX_KEY_CLICK)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        /*val brightness = Settings.System.getInt(contentResolver, Settings.System. SCREEN_BRIGHTNESS, 0)
        seekBarBright.progress = brightness
        seekBarBright.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                Settings.System.putInt(contentResolver, Settings.System. SCREEN_BRIGHTNESS, progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })*/
    }

    private fun updateSeekbar() {
        val musicVolLevel = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolLevel = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        seekBarVol.max = maxVolLevel
        seekBarVol.progress = musicVolLevel

        if (musicVolLevel == 0) {
            imageViewVolume.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_volume_up))
        } else {
            imageViewVolume.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.volume_off))
        }

        /*val brightness = Settings.System.getInt(contentResolver, Settings.System. SCREEN_BRIGHTNESS, 0)
        seekBarBright.progress = brightness*/
    }

    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(this).build()
        }

        binding.playerView.player = player
        val mediaItem : MediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()


    }

    private fun releasePlayer() {
        if (player != null) {
            //player!!.removeListener(playbackStateListener)
            player!!.release()
            player = null
        }
    }

    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }
}