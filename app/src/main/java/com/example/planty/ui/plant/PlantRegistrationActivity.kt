package com.example.planty.ui.plant

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.R
import com.example.planty.databinding.ActivityPlantRegistrationBinding
import com.example.planty.network.ApiClient
import com.example.planty.network.TokenManager
import com.example.planty.login.LoginActivity

class PlantRegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlantRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if user is logged in
        if (TokenManager.getToken() == null) {
            navigateToLogin()
            return
        }

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "식물 등록"
        }

        // Set up authentication callback
        ApiClient.setOnAuthNeededListener {
            runOnUiThread {
                Toast.makeText(this, "세션이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_LONG).show()
                navigateToLogin()
            }
        }

        // Set up back button
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Add logout menu item
        binding.toolbar.inflateMenu(R.menu.main_menu)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }

        // Set up plant registration button
        binding.btnRegisterPlant.setOnClickListener {
            registerPlant()
        }
    }

    private fun registerPlant() {
        val plantName = binding.etPlantName.text.toString()
        val plantType = binding.etPlantType.text.toString()
        val wateringCycle = binding.etWateringCycle.text.toString().toIntOrNull() ?: 7

        if (plantName.isBlank() || plantType.isBlank()) {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // Show progress
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.btnRegisterPlant.isEnabled = false

        // Register plant
        // TODO: Add your plant registration logic here
    }

    private fun logout() {
        TokenManager.clearToken()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
} 