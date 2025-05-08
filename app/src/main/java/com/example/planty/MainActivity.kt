package com.example.planty

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.adapter.PlantAdapter
import com.example.planty.data.Plant
import com.example.planty.plantdetail.PlantDetailActivity
import com.example.planty.R
import com.example.planty.databinding.ActivityMainBinding
import com.example.planty.settings.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var plantAdapter: PlantAdapter
    private val plantDataList = mutableListOf<Plant>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareInitalPlantData()
        setupRecyclerView()
        setupOtherClickListeners()
    }

    private fun prepareInitalPlantData() {
        plantDataList.add(Plant("1", "토토", "열매가 발견되었어요", R.drawable.tlranf))
        plantDataList.add(Plant("plant_2", "몬스테라", "건강하게 자라고 있어요", R.drawable.tlranf))
    }

    private fun setupRecyclerView() {
        plantAdapter = PlantAdapter(plantDataList) { clickedPlant ->
            Toast.makeText(this, "${clickedPlant.name} 클릭됨", Toast.LENGTH_SHORT).show()
            Log.d("MainActivity", "Plant clicked: ${clickedPlant.id}")

            startPlantDetailActivity(clickedPlant.id, clickedPlant.name, clickedPlant.imageResId)
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
        binding.ivLock.setOnClickListener {
            Toast.makeText(this, "설정 버튼 클릭됨", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        binding.tvMyPlantsSeeMore.setOnClickListener {
            Toast.makeText(this, "'내 식물 더보기' 클릭됨 (구현 필요)", Toast.LENGTH_SHORT).show()
        }
        binding.tvRecommendedSeeMore.setOnClickListener {
            Toast.makeText(this, "'추천 용품 더보기' 클릭됨 (구현 필요)", Toast.LENGTH_SHORT).show()
        }
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_community -> true
                R.id.navigation_stores -> true
                R.id.navigation_profile -> true
                else -> false
            }
        }
    }

    private fun startPlantDetailActivity(plantId: String, plantName: String, plantImageResId: Int) {
        val intent = Intent(this, PlantDetailActivity::class.java).apply {
            putExtra(PlantDetailActivity.Extra_Plant_ID, plantId)
            putExtra(PlantDetailActivity.Extra_Plant_Name, plantName)
            putExtra(PlantDetailActivity.Extra_Plant_Image, plantImageResId)
        }
        startActivity(intent)
    }
}