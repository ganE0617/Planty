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
import android.app.DatePickerDialog
import java.util.Calendar

class PlantRegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlantRegistrationBinding
    private val plantRepository = PlantRepository()
    private var userEmail: String? = null

    companion object {
        const val EXTRA_USER_NICKNAME = "extra_user_nickname"
        const val EXTRA_USER_EMAIL = "extra_user_email"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nickname = intent.getStringExtra(EXTRA_USER_NICKNAME) ?: "사용자"
        userEmail = intent.getStringExtra(EXTRA_USER_EMAIL)

        // Initialize UI with nickname
        binding.tvWelcome.text = "안녕하세요, ${nickname}님!"
        binding.tvWelcomeSub.text = "첫 식물을 등록해주세요."

        // 날짜 입력란 클릭 시 DatePickerDialog 표시
        binding.etLastWateredDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val dateStr = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    binding.etLastWateredDate.setText(dateStr)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

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
        val lastWateredDate = binding.etLastWateredDate.text.toString().trim()

        if (plantName.isEmpty()) {
            binding.tilPlantName.error = "식물 이름을 입력해주세요."
            return
        } else {
            binding.tilPlantName.error = null
        }

        if (plantType.isEmpty()) {
            binding.tilPlantType.error = "식물 종류를 입력해주세요."
            return
        } else {
            binding.tilPlantType.error = null
        }

        if (wateringCycle <= 0) {
            binding.tilWateringCycle.error = "물주기 주기는 1일 이상이어야 합니다."
            return
        } else {
            binding.tilWateringCycle.error = null
        }

        if (lastWateredDate.isEmpty()) {
            binding.tilLastWateredDate.error = "마지막으로 물준 날짜를 입력해주세요."
            return
        } else {
            binding.tilLastWateredDate.error = null
        }

        // Show loading state
        binding.btnRegisterPlant.isEnabled = false
        binding.progressBar.show()

        lifecycleScope.launch {
            plantRepository.registerPlant(
                name = plantName,
                type = plantType,
                wateringCycle = wateringCycle,
                lastWateredDate = lastWateredDate
            ).collect { result ->
                // Hide loading state
                binding.btnRegisterPlant.isEnabled = true
                binding.progressBar.hide()

                when (result) {
                    is PlantResult.Success -> {
                        Toast.makeText(this@PlantRegistrationActivity, "식물이 등록되었습니다!", Toast.LENGTH_SHORT).show()
                        // Navigate to main screen
                        val intent = Intent(this@PlantRegistrationActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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