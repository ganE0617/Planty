package com.example.planty.plantdetail

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.R
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.planty.network.TokenManager
import com.example.planty.network.ApiClient
import com.example.planty.network.PlantLedRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class PlantStatusActivity : AppCompatActivity() {
    private val ledModeRgbMap = mapOf(
        "통합" to Triple(172, 26, 56),
        "잎 성장" to Triple(191, 0, 64),
        "개화" to Triple(181, 0, 64),
        "열매" to Triple(179, 26, 51),
        "줄기" to Triple(153, 0, 102)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_status)

        val plantId = intent.getIntExtra("plant_id", -1)
        val token = com.example.planty.network.TokenManager.getToken()
        if (plantId == -1 || token == null) {
            Toast.makeText(this, "식물 정보 또는 토큰이 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

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

        // 서버에서 LED 모드 조회
        CoroutineScope(Dispatchers.Main).launch {
            val plantService = ApiClient.plantService
            val response = plantService.getPlantLed("Bearer $token", plantId)
            var selectedMode = "통합"
            var selectedRgb = ledModeRgbMap[selectedMode] ?: Triple(172, 26, 56)
            if (response.isSuccessful && response.body()?.success == true && response.body()?.led != null) {
                val led = response.body()!!.led!!
                selectedMode = led.mode
                selectedRgb = Triple(led.r, led.g, led.b)
            } else {
                // 없으면 '통합'으로 저장
                plantService.setPlantLed(
                    "Bearer $token", plantId,
                    PlantLedRequest(plant_id = plantId, mode = selectedMode, r = selectedRgb.first, g = selectedRgb.second, b = selectedRgb.third)
                )
            }
            // UI 반영
            modeButtons.forEach { btn ->
                btn.isSelected = btn.text == selectedMode
            }

            // 버튼 클릭 시 서버에 저장
            modeButtons.forEach { btn ->
                btn.setOnClickListener {
                    val mode = btn.text.toString()
                    val rgb = ledModeRgbMap[mode] ?: Triple(172, 26, 56)
                    modeButtons.forEach { b -> b.isSelected = b == btn }
                    CoroutineScope(Dispatchers.Main).launch {
                        plantService.setPlantLed(
                            "Bearer $token", plantId,
                            PlantLedRequest(plant_id = plantId, mode = mode, r = rgb.first, g = rgb.second, b = rgb.third)
                        )
                    }
                }
            }
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