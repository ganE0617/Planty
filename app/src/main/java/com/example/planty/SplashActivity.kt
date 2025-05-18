package com.example.planty

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.databinding.ActivitySplashBinding
import com.example.planty.login.LoginActivity
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

        // Start the circle animation
        val circleAnimation = AnimationUtils.loadAnimation(this, R.anim.circle_animation)
        circleAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            
            override fun onAnimationEnd(animation: Animation?) {
                // Hide the circle and set background color before transitioning
                binding.circleBackground.visibility = android.view.View.INVISIBLE
                binding.root.setBackgroundColor(0xFFCDE7BE.toInt())
                // When animation ends, check login status
                checkLoginStatus()
            }
            
            override fun onAnimationRepeat(animation: Animation?) {}
        })
        binding.circleBackground.startAnimation(circleAnimation)
    }

    private fun checkLoginStatus() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Check if Supabase client is initialized
                if (!SupabaseClient.isInitialized.value) {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                    return@launch
                }

                // Try to get current session
                val session = SupabaseClient.client.auth.currentSessionOrNull()
                if (session != null) {
                    // User is logged in, navigate to the main activity
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                } else {
                    // User is not logged in, navigate to the login activity
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            } catch (e: Exception) {
                // If there's any error, go to Login
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            } finally {
                finish() // Close the splash activity
            }
        }
    }
} 