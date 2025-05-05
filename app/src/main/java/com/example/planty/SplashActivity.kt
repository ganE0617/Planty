package com.example.planty

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.databinding.ActivitySplashBinding
import com.example.planty.login.Login
import com.example.planty.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Add a small delay to show the splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            checkLoginStatus()
        }, 1500) // 1.5 seconds delay
    }

    private fun checkLoginStatus() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Check if Supabase client is initialized
                if (!SupabaseClient.isInitialized.value) {
                    startActivity(Intent(this@SplashActivity, Login::class.java))
                    finish()
                    return@launch
                }

                // Try to get current session
                val session = SupabaseClient.client.auth.currentSessionOrNull()
                if (session != null) {
                    // User is logged in, navigate to the main activity
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                } else {
                    // User is not logged in, navigate to the login activity
                    startActivity(Intent(this@SplashActivity, Login::class.java))
                }
            } catch (e: Exception) {
                // If there's any error, go to Login
                startActivity(Intent(this@SplashActivity, Login::class.java))
            } finally {
                finish() // Close the splash activity
            }
        }
    }
} 