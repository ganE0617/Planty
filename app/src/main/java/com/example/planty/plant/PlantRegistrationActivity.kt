package com.example.planty.plant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.planty.MainActivity
import com.example.planty.databinding.ActivityPlantRegistrationBinding
import com.example.planty.network.PlantRepository
import com.example.planty.network.PlantResult
import kotlinx.coroutines.launch

class PlantRegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlantRegistrationBinding
    private val plantRepository = PlantRepository()

    companion object {
        const val EXTRA_USER_NICKNAME = "extra_user_nickname"
        const val EXTRA_USER_EMAIL = "extra_user_email"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nickname = intent.getStringExtra(EXTRA_USER_NICKNAME) ?: "사용자"
        val email = intent.getStringExtra(EXTRA_USER_EMAIL)

        // TODO: Initialize UI with nickname and email

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnRegisterPlant.setOnClickListener {
            registerPlant()
        }
    }

    private fun registerPlant() {
        val plantName = binding.etPlantName.text.toString().trim()
        val plantType = binding.etPlantType.text.toString().trim()
        val wateringCycle = binding.etWateringCycle.text.toString().trim().toIntOrNull() ?: 7

        if (plantName.isEmpty() || plantType.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            plantRepository.registerPlant(
                name = plantName,
                type = plantType,
                wateringCycle = wateringCycle
            ).collect { result ->
                when (result) {
                    is PlantResult.Success -> {
                        Toast.makeText(this@PlantRegistrationActivity, "식물이 등록되었습니다!", Toast.LENGTH_SHORT).show()
                        // 메인 화면으로 이동
                        val intent = Intent(this@PlantRegistrationActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    is PlantResult.Error -> {
                        Log.e("PlantRegistration", "식물 등록 실패: ${result.message}")
                        Toast.makeText(this@PlantRegistrationActivity, "식물 등록에 실패했습니다: ${result.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
} 