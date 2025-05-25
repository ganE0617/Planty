package com.example.planty.plantdetail

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.R
import android.webkit.WebView
import android.webkit.WebViewClient

class PlantStatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_status)

        // WebView로 실시간 스트림 표시
        val webView = findViewById<WebView>(R.id.web_stream)
        webView.settings.javaScriptEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://planty.gaeun.xyz/image_raw")

        // --- 더미 데이터 및 UI 세팅 ---
        val nickname = intent.getStringExtra("plant_name") ?: "토토"
        val type = intent.getStringExtra("plant_type") ?: "방울토마토"
        val waterDay = intent.getStringExtra("water_day") ?: "D-7"
        val soilPercent = intent.getIntExtra("soil_percent", 60)
        val ledMode = intent.getStringExtra("led_mode") ?: "기본모드"
        val ledR = intent.getIntExtra("led_r", 255)
        val ledG = intent.getIntExtra("led_g", 255)
        val ledB = intent.getIntExtra("led_b", 255)
        val ledStrength = intent.getIntExtra("led_strength", 50)

        findViewById<TextView>(R.id.tv_nickname).text = nickname
        findViewById<TextView>(R.id.tv_type).text = type
        findViewById<TextView>(R.id.tv_water_day).text = waterDay
        findViewById<TextView>(R.id.tv_soil_percent).text = "${soilPercent}%"
        findViewById<SeekBar>(R.id.seekbar_led).progress = ledStrength
        findViewById<TextView>(R.id.tv_led_percent).text = "$ledStrength%"

        val modeButtons = listOf(
            findViewById<Button>(R.id.btn_mode_default),
            findViewById<Button>(R.id.btn_mode_leaf),
            findViewById<Button>(R.id.btn_mode_flower),
            findViewById<Button>(R.id.btn_mode_fruit),
            findViewById<Button>(R.id.btn_mode_seed)
        )
        modeButtons.forEach { btn ->
            btn.isSelected = btn.text == ledMode
            btn.setBackgroundColor(
                if (btn.isSelected) android.graphics.Color.rgb(ledR, ledG, ledB)
                else android.graphics.Color.parseColor("#F5F6F8")
            )
        }

        val seekBar = findViewById<SeekBar>(R.id.seekbar_led)
        val percentText = findViewById<TextView>(R.id.tv_led_percent)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                percentText.text = "$progress%"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
} 