package com.example.planty

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.planty.adapter.PlantAdapter
import com.example.planty.data.Plant
import com.example.planty.plantdetail.PlantDetailActivity
import com.example.planty.R
import com.example.planty.databinding.ActivityMainBinding
import com.example.planty.settings.SettingsActivity
import com.example.planty.network.PlantRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import android.view.View
import android.widget.TextView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.LinearLayout
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var plantAdapter: PlantAdapter
    private val plantDataList = mutableListOf<Plant>()
    private val plantRepository = PlantRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadPlants()
        setupOtherClickListeners()

        // 알람 뱃지 예시 (실제 알람 개수로 대체)
        val alarmCount = getUnreadAlarmCount() // TODO: 실제 알람 개수로 대체
        val alarmBadge = findViewById<TextView>(R.id.tv_alarm_badge)
        if (alarmCount > 0) {
            alarmBadge.text = alarmCount.toString()
            alarmBadge.visibility = View.VISIBLE
        } else {
            alarmBadge.visibility = View.GONE
        }

        // 알람 아이콘 클릭 시 알람 목록 화면으로 이동
        findViewById<View>(R.id.iv_alarm).setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        }

        // 하단 네비게이션에 설정 탭 연결
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_community -> true
                R.id.navigation_stores -> true
                R.id.navigation_profile -> true
                R.id.navigation_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadPlants() {
        lifecycleScope.launch {
            Log.d("MainActivity", "Starting to load plants")
            plantRepository.getPlants().collect { plants ->
                Log.d("MainActivity", "Received plants: "+plants.size)
                plantDataList.clear()
                plants.forEach { plant ->
                    Log.d("MainActivity", "Plant: id=${plant.id}, name=${plant.name}")
                    plantDataList.add(Plant(
                        id = plant.id.toString(),
                        name = plant.name,
                        type = plant.type,
                        wateringCycle = plant.wateringCycle,
                        last_watered = plant.last_watered ?: LocalDate.now().toString(),
                        status = "건강하게 자라고 있어요",
                        imageResId = R.drawable.tlranf
                    ))
                }
                plantAdapter.notifyDataSetChanged()
                Log.d("MainActivity", "Updated plant list size: ${plantDataList.size}")
            }
        }
    }

    private fun calculateWaterDay(lastWatered: String, wateringCycle: Int): String {
        return try {
            // 다양한 포맷 지원: yyyy-MM-dd, yyyy-MM-ddTHH:mm:ss 등
            val datePart = lastWatered.substring(0, 10)
            val lastDate = LocalDate.parse(datePart)
            val today = LocalDate.now()
            val daysSince = ChronoUnit.DAYS.between(lastDate, today).toInt()
            val daysLeft = wateringCycle - daysSince
            android.util.Log.d("WaterDebug", "lastWatered=$lastWatered, datePart=$datePart, today=$today, daysSince=$daysSince, daysLeft=$daysLeft")
            if (daysLeft > 0) "D-$daysLeft" else "D-day"
        } catch (e: Exception) {
            android.util.Log.e("WaterDebug", "날짜 계산 오류: $e, lastWatered=$lastWatered")
            "D-?"
        }
    }

    private fun setupRecyclerView() {
        plantAdapter = PlantAdapter(plantDataList) { clickedPlant ->
            // 더미 데이터 생성
            val ledModes = mapOf(
                "기본모드" to Triple(255, 255, 255),
                "잎성장모드" to Triple(120, 200, 120),
                "개화모드" to Triple(255, 180, 200),
                "열매모드" to Triple(255, 220, 120),
                "묘목모드" to Triple(180, 255, 180)
            )
            val selectedMode = "잎성장모드"
            val (r, g, b) = ledModes[selectedMode] ?: Triple(255, 255, 255)
            val soilPercent = (40..80).random()

            val intent = Intent(this, com.example.planty.plantdetail.PlantStatusActivity::class.java).apply {
                putExtra("plant_id", clickedPlant.id.toInt())
                putExtra("plant_name", clickedPlant.name)
                putExtra("plant_type", clickedPlant.type)
                putExtra("water_day", calculateWaterDay(clickedPlant.last_watered, clickedPlant.wateringCycle))
                putExtra("soil_percent", soilPercent)
                putExtra("led_mode", selectedMode)
                putExtra("led_r", r)
                putExtra("led_g", g)
                putExtra("led_b", b)
                putExtra("led_strength", 50)
                putExtra("image_res_id", clickedPlant.imageResId)
                putExtra("watering_cycle", clickedPlant.wateringCycle)
                putExtra("last_watered", clickedPlant.last_watered)
            }
            startActivity(intent)
        }
        binding.rvMyPlants.apply {
            adapter = plantAdapter
        }
        Log.d("MainActivity", "RecyclerView 설정 완료. 아이템 개수: ${plantAdapter.itemCount}")
    }

    private fun setupOtherClickListeners() {
        binding.ivSearch.setOnClickListener {
            Toast.makeText(this, "검색 클릭됨 (구현 필요)", Toast.LENGTH_SHORT).show()
        }
        binding.tvMyPlantsSeeMore.setOnClickListener {
            Toast.makeText(this, "'내 식물 더보기' 클릭됨 (구현 필요)", Toast.LENGTH_SHORT).show()
        }
        binding.tvRecommendedSeeMore.setOnClickListener {
            Toast.makeText(this, "'추천 용품 더보기' 클릭됨 (구현 필요)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startPlantDetailActivity(plantId: String, plantName: String, plantImageResId: Int) {
        val intent = Intent(this, PlantDetailActivity::class.java).apply {
            putExtra(PlantDetailActivity.Extra_Plant_ID, plantId)
            putExtra(PlantDetailActivity.Extra_Plant_Name, plantName)
            putExtra(PlantDetailActivity.Extra_Plant_Image, plantImageResId)
            putExtra(PlantDetailActivity.Extra_Show_Live_Stream, true)
        }
        startActivity(intent)
    }

    // 알람 개수 가져오는 함수 (임시)
    private fun getUnreadAlarmCount(): Int {
        // TODO: 실제 알람 데이터와 연동
        return 3 // 예시
    }
}

class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 레이아웃 생성
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        // 백 버튼
        val backArrow = ImageView(this).apply {
            setImageResource(R.drawable.ic_back)
            setPadding(32, 32, 32, 32)
            setOnClickListener { finish() }
        }
        layout.addView(backArrow, LinearLayout.LayoutParams(100, 100))
        // 알람 리스트
        val listView = ListView(this)
        val alarms = listOf("물 줄 시간입니다!", "새로운 식물 등록 알림", "AI 분석 결과가 도착했습니다.")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, alarms)
        listView.adapter = adapter
        layout.addView(listView, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f))
        setContentView(layout)
    }
}