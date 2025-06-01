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
import android.os.Handler
import android.os.Looper
import android.view.View
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.lifecycle.lifecycleScope
import com.example.planty.network.PlantRepository

class PlantStatusActivity : AppCompatActivity() {
    private val ledModeRgbMap = mapOf(
        "통합" to Triple(172, 26, 56),
        "잎 성장" to Triple(191, 0, 64),
        "개화" to Triple(181, 0, 64),
        "열매" to Triple(179, 26, 51),
        "줄기" to Triple(153, 255, 102)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_status)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

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

        // 서버에서 최신 식물 정보 받아와서 D-day 계산 및 UI 반영
        lifecycleScope.launch {
            val plantRepository = PlantRepository()
            val plant = plantRepository.getPlantById(plantId)
            if (plant != null) {
                val nickname = plant.name
                val type = plant.type
                val wateringCycle = plant.wateringCycle
                val lastWatered = plant.last_watered ?: LocalDate.now().toString()
                val soilPercent = 60 // TODO: 서버에서 받아오도록 수정 가능
                // D-day 계산 함수
                fun calculateWaterDay(lastWatered: String, wateringCycle: Int): String {
                    return try {
                        val datePart = lastWatered.substring(0, 10)
                        val lastDate = LocalDate.parse(datePart)
                        val today = LocalDate.now()
                        val daysSince = ChronoUnit.DAYS.between(lastDate, today).toInt()
                        val daysLeft = wateringCycle - daysSince
                        if (daysLeft > 0) "D-$daysLeft" else "D-day"
                    } catch (e: Exception) {
                        "D-?"
                    }
                }
                val ddayText = calculateWaterDay(lastWatered, wateringCycle)
                findViewById<TextView>(R.id.tv_water_day).text = ddayText
                findViewById<TextView>(R.id.tv_nickname).text = nickname
                findViewById<TextView>(R.id.tv_type).text = type
                findViewById<TextView>(R.id.tv_soil_percent).text = "${soilPercent}%"
            } else {
                Toast.makeText(this@PlantStatusActivity, "식물 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // --- 더미 데이터 및 UI 세팅 ---
        val type = intent.getStringExtra("plant_type") ?: "방울토마토"
        val wateringCycle = intent.getIntExtra("watering_cycle", 7)
        val lastWatered = intent.getStringExtra("last_watered") ?: LocalDate.now().toString()
        val soilPercent = intent.getIntExtra("soil_percent", 60)
        var ledStrength = intent.getIntExtra("led_strength", 50) // var로 통일, 기본값 50

        // D-day 계산 함수 (MainActivity 참고)
        fun calculateWaterDay(lastWatered: String, wateringCycle: Int): String {
            return try {
                val datePart = lastWatered.substring(0, 10)
                val lastDate = LocalDate.parse(datePart)
                val today = LocalDate.now()
                val daysSince = ChronoUnit.DAYS.between(lastDate, today).toInt()
                val daysLeft = wateringCycle - daysSince
                if (daysLeft > 0) "D-$daysLeft" else "D-day"
            } catch (e: Exception) {
                "D-?"
            }
        }

        val ddayText = calculateWaterDay(lastWatered, wateringCycle)
        findViewById<TextView>(R.id.tv_water_day).text = ddayText
        findViewById<TextView>(R.id.tv_type).text = type
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

        // 현재 선택된 모드/색상 정보 저장 (onCreate 상단에서 선언)
        var selectedMode = "통합"
        var selectedRgb = ledModeRgbMap[selectedMode] ?: Triple(172, 26, 56)

        // 서버에서 LED 모드 조회
        CoroutineScope(Dispatchers.Main).launch {
            val plantService = ApiClient.plantService
            val response = plantService.getPlantLed(plantId)
            if (response.isSuccessful && response.body()?.success == true && response.body()?.led != null) {
                val led = response.body()!!.led!!
                selectedMode = led.mode
                selectedRgb = Triple(led.r, led.g, led.b)
                ledStrength = led.strength // 서버에서 strength 값 받아서 반영
                val seekBar = findViewById<SeekBar>(R.id.seekbar_led)
                val percentText = findViewById<TextView>(R.id.tv_led_percent)
                seekBar.progress = (ledStrength / 2.55).toInt() // 0~255 → 0~100
                percentText.text = "${seekBar.progress}%"
            } else {
                // 없으면 '통합'으로 strength 포함 저장
                ApiClient.plantService.setPlantLed(
                    plantId,
                    PlantLedRequest(
                        plant_id = plantId,
                        mode = selectedMode,
                        r = selectedRgb.first,
                        g = selectedRgb.second,
                        b = selectedRgb.third,
                        strength = (ledStrength * 2.55).toInt()
                    )
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
                    selectedMode = mode
                    selectedRgb = rgb
                    modeButtons.forEach { b -> b.isSelected = b == btn }
                    CoroutineScope(Dispatchers.Main).launch {
                        ApiClient.plantService.setPlantLed(
                            plantId,
                            PlantLedRequest(
                                plant_id = plantId,
                                mode = mode,
                                r = rgb.first,
                                g = rgb.second,
                                b = rgb.third,
                                strength = (ledStrength * 2.55).toInt()
                            )
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
                ledStrength = progress
                val strength255 = (progress * 2.55).toInt()
                // 서버에 저장
                CoroutineScope(Dispatchers.Main).launch {
                    ApiClient.plantService.setPlantLed(
                        plantId,
                        PlantLedRequest(
                            plant_id = plantId,
                            mode = selectedMode,
                            r = selectedRgb.first,
                            g = selectedRgb.second,
                            b = selectedRgb.third,
                            strength = strength255
                        )
                    )
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val aiAnalysisTextView = findViewById<TextView>(R.id.tv_ai_analysis)
        val handler = Handler(Looper.getMainLooper())
        val updateInterval = 5000L // 5초마다 갱신
        fun fetchAIAnalysis() {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = ApiClient.plantService.getPlantAIAnalysis(plantId)
                    if (response.isSuccessful && response.body()?.success == true && !response.body()?.analysis_text.isNullOrBlank()) {
                        aiAnalysisTextView.text = response.body()?.analysis_text
                    } else {
                        aiAnalysisTextView.text = "AI 분석 결과가 여기에 표시됩니다."
                    }
                } catch (e: Exception) {
                    aiAnalysisTextView.text = "AI 분석 결과를 불러오는 중 오류 발생."
                } finally {
//                    handler.postDelayed({ fetchAIAnalysis() }, updateInterval)
                }
            }
        }
        fetchAIAnalysis()
    }

    // 스트리밍 화면 클릭 시 전체화면 WebView Dialog 표시
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