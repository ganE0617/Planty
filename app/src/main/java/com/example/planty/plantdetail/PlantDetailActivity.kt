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
import androidx.lifecycle.lifecycleScope
import com.example.planty.network.ApiClient
import kotlinx.coroutines.launch
import android.app.Dialog
import android.view.Window
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient

class PlantDetailActivity : AppCompatActivity() {
    companion object {
        const val Extra_Plant_ID = "plant_id"
        const val Extra_Plant_Name = "plant_name"
        const val Extra_Plant_Image = "plant_image"
        const val Extra_Show_Live_Stream = "show_live_stream"
    }

    private lateinit var toolbar: Toolbar
    private lateinit var plantNickname: TextView
    private lateinit var plantType: TextView
    private lateinit var favoriteIcon: ImageView
    private lateinit var soilMoisturePercent: TextView
    private lateinit var waterTankPercent: TextView
    private lateinit var videoContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_detail)

        initializeViews()
        setupToolbar()
        getIntentData()
        setupLiveStream()

        // Toolbar 뒤로가기 동작 연결
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // AI 분석 결과 표시
        val plantId = intent.getStringExtra(Extra_Plant_ID)?.toIntOrNull()
        val aiAnalysisTextView = findViewById<TextView>(R.id.tv_ai_analysis)
        if (plantId != null) {
            lifecycleScope.launch {
                try {
                    val response = ApiClient.plantService.getPlantAIAnalysis(plantId)
                    if (response.isSuccessful && response.body()?.success == true) {
                        aiAnalysisTextView.text = response.body()?.analysis_text ?: "AI 분석 결과가 없습니다."
                    } else {
                        aiAnalysisTextView.text = "AI 분석 결과를 불러올 수 없습니다."
                    }
                } catch (e: Exception) {
                    aiAnalysisTextView.text = "AI 분석 결과를 불러오는 중 오류 발생."
                }
            }
        } else {
            aiAnalysisTextView.text = "식물 ID가 올바르지 않습니다."
        }
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
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
        val isFavorite = intent.getBooleanExtra("isFavorite", false)
        val soilMoisture = intent.getIntExtra("soilMoisture", 0)
        val waterTank = intent.getIntExtra("waterTank", 0)

        plantNickname.text = nickname
        plantType.text = type
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
            videoContainer.visibility = View.VISIBLE
        }
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

    // 실시간 스트리밍 화면 클릭 시 전체화면 WebView Dialog 표시
    fun onStreamClick(view: View) {
        val dialog = android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        val webView = android.webkit.WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = android.webkit.WebViewClient()
        webView.loadUrl("https://planty.gaeun.xyz/image_raw")
        dialog.setContentView(webView)
        dialog.show()
    }
} 