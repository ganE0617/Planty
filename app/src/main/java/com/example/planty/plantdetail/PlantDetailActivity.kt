package com.example.planty.plantdetail

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.planty.R
import android.view.View
import android.widget.FrameLayout
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView

class PlantDetailActivity : AppCompatActivity() {
    companion object {
        const val Extra_Plant_ID = "plant_id"
        const val Extra_Plant_Name = "plant_name"
        const val Extra_Plant_Image = "plant_image"
        const val Extra_Show_Live_Stream = "show_live_stream"
    }

    private lateinit var toolbar: Toolbar
    private lateinit var plantImage: ImageView
    private lateinit var plantNickname: TextView
    private lateinit var plantType: TextView
    private lateinit var favoriteIcon: ImageView
    private lateinit var soilMoisturePercent: TextView
    private lateinit var waterTankPercent: TextView
    private lateinit var videoContainer: FrameLayout
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_detail)

        initializeViews()
        setupToolbar()
        getIntentData()
        setupLiveStream()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        plantImage = findViewById(R.id.iv_plant_detail_image)
        plantNickname = findViewById(R.id.tv_plant_detail_nickname)
        plantType = findViewById(R.id.tv_plant_detail_name)
        favoriteIcon = findViewById(R.id.iv_favorite)
        soilMoisturePercent = findViewById(R.id.tv_soil_percentage)
        waterTankPercent = findViewById(R.id.tv_water_percentage)
        videoContainer = findViewById(R.id.video_container)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun getIntentData() {
        val nickname = intent.getStringExtra(Extra_Plant_Name) ?: ""
        val type = intent.getStringExtra(Extra_Plant_ID) ?: ""
        val imageResId = intent.getIntExtra(Extra_Plant_Image, R.drawable.tlranf)
        val isFavorite = intent.getBooleanExtra("isFavorite", false)
        val soilMoisture = intent.getIntExtra("soilMoisture", 0)
        val waterTank = intent.getIntExtra("waterTank", 0)

        plantNickname.text = nickname
        plantType.text = type
        plantImage.setImageResource(imageResId)
        favoriteIcon.setImageResource(
            if (isFavorite) R.drawable.ic_favorite
            else R.drawable.ic_favorite_border
        )
        soilMoisturePercent.text = "$soilMoisture%"
        waterTankPercent.text = "$waterTank%"
    }

    private fun setupLiveStream() {
        val showLiveStream = intent.getBooleanExtra(Extra_Show_Live_Stream, false)
        if (showLiveStream) {
            plantImage.visibility = View.GONE
            videoContainer.visibility = View.VISIBLE
            
            // ExoPlayer 설정
            player = ExoPlayer.Builder(this).build()
            val playerView = StyledPlayerView(this)
            videoContainer.addView(playerView)
            playerView.player = player

            // RTSP 스트림 URL 설정 (실제 스트림 URL로 변경 필요)
            val streamUrl = "rtsp://your-stream-url"
            val mediaItem = MediaItem.fromUri(streamUrl)
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.playWhenReady = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
} 