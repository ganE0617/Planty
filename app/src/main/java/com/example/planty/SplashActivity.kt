package com.example.planty

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.databinding.ActivitySplashBinding
import com.example.planty.login.LoginActivity
import com.example.planty.network.SupabaseClient
import com.example.planty.network.TokenManager
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
                val token = TokenManager.getToken()
                if (token != null) {
                    // 토큰이 있으면 MainActivity로 이동
                    Log.d("SplashActivity", "Token found, proceeding to MainActivity")
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                } else {
                    // 토큰이 없으면 LoginActivity로 이동
                    Log.d("SplashActivity", "No token found, proceeding to LoginActivity")
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            } catch (e: Exception) {
                Log.e("SplashActivity", "Error checking login status", e)
                // 에러 발생 시 LoginActivity로 이동
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            } finally {
                finish() // Close the splash activity
            }
        }
    }
} 