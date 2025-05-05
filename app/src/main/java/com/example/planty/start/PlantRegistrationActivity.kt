package com.example.planty.start

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.MainActivity
import com.example.planty.R
import com.example.planty.databinding.ActivityPlantRegistrationBinding
import com.example.planty.login.Login
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlantRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlantRegistrationBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    companion object {
        const val EXTRA_USER_NICKNAME = "user_nickname"
        const val EXTRA_USER_ID_TOKEN = "user_id_token"
        const val EXTRA_USER_EMAIL = "extra_user_email"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(Login.PREFS_FILENAME, Context.MODE_PRIVATE)

        val userNickname = intent.getStringExtra(EXTRA_USER_NICKNAME) ?: "사용자"
        binding.tvWelcome.text = getString(R.string.welcome_plant_registration, userNickname)

        setupDatePicker()
        setupClickListeners()
    }

    private fun setupDatePicker() {
        binding.etPlantStartDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                binding.etPlantStartDate.setText(dateFormatter.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setupClickListeners() {
        binding.btnRegisterPlant.setOnClickListener {
            val plantType = binding.etPlantType.text.toString().trim()
            val plantNickname = binding.etPlantNickname.text.toString().trim()
            val startDate = binding.etPlantStartDate.text.toString().trim()

            if (validateInput(plantType, plantNickname, startDate)) {
                // TODO: Implement API call to register plant
                Toast.makeText(this, R.string.plant_registration_success, Toast.LENGTH_SHORT).show()
                
                // Update SharedPreferences to indicate plant registration is complete
                sharedPreferences.edit()
                    .putBoolean(Login.KEY_REQUIRES_PLANT_REGISTRATION, false)
                    .apply()

                // Navigate to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun validateInput(plantType: String, plantNickname: String, startDate: String): Boolean {
        var isValid = true

        if (plantType.isEmpty()) {
            binding.tilPlantType.error = getString(R.string.error_empty_field)
            isValid = false
        } else {
            binding.tilPlantType.error = null
        }

        if (plantNickname.isEmpty()) {
            binding.tilPlantNickname.error = getString(R.string.error_empty_field)
            isValid = false
        } else {
            binding.tilPlantNickname.error = null
        }

        if (startDate.isEmpty()) {
            binding.tilPlantStartDate.error = getString(R.string.error_empty_field)
            isValid = false
        } else {
            binding.tilPlantStartDate.error = null
        }

        return isValid
    }
} 