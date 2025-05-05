package com.example.planty.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.planty.MainActivity
import com.example.planty.R
import com.example.planty.data.LoginRequest
import com.example.planty.data.User
import com.example.planty.databinding.ActivityLoginBinding
import com.example.planty.login.SignUp
import com.example.planty.network.RetrofitClient
import com.example.planty.start.PlantRegistrationActivity
import kotlinx.coroutines.launch
import java.io.IOException

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val PREFS_FILENAME = "UserPrefs"
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        const val KEY_AUTH_TOKEN = "authToken"
        const val KEY_USER_NICKNAME = "userNickname"
        const val KEY_USER_ID = "userId"
        const val KEY_REQUIRES_PLANT_REGISTRATION = "requiresPlantRegistration"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

        if (isUserLoggedIn()) {
            navigateToAppropriateScreen()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClickListeners()
    }

    private fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    private fun checkRequiresPlantRegistration(): Boolean {
        return sharedPreferences.getBoolean(KEY_REQUIRES_PLANT_REGISTRATION, true)
    }

    private fun getStoredUserNickname(): String? {
        return sharedPreferences.getString(KEY_USER_NICKNAME, null)
    }

    private fun getStoredUserIdOrToken(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    private fun navigateToAppropriateScreen() {
        val requiresPlantReg = checkRequiresPlantRegistration()

        if (requiresPlantReg) {
            val nickname = getStoredUserNickname() ?: "사용자"
            val userIdOrToken = getStoredUserIdOrToken()

            if (userIdOrToken == null) {
                Log.e("LoginActivity", "자동 로그인 실패: 사용자 ID/토큰 정보 없음")
                saveLoginState(false, null, null, true)
                return
            }

            val intent = Intent(this, PlantRegistrationActivity::class.java)
            intent.putExtra(PlantRegistrationActivity.EXTRA_USER_NICKNAME, nickname)
            intent.putExtra(PlantRegistrationActivity.EXTRA_USER_ID_TOKEN, userIdOrToken)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun saveLoginState(isLoggedIn: Boolean, token: String?, userData: User?, requiresPlantReg: Boolean?) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)

        if (isLoggedIn && token != null && userData != null && requiresPlantReg != null) {
            editor.putString(KEY_AUTH_TOKEN, token)
            editor.putString(KEY_USER_ID, userData.userId)
            editor.putString(KEY_USER_NICKNAME, userData.nickname)
            editor.putBoolean(KEY_REQUIRES_PLANT_REGISTRATION, requiresPlantReg)
        } else {
            editor.remove(KEY_AUTH_TOKEN)
            editor.remove(KEY_USER_ID)
            editor.remove(KEY_USER_NICKNAME)
            editor.remove(KEY_REQUIRES_PLANT_REGISTRATION)
        }
        editor.apply()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val id = binding.etId.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            var isValid = true

            if (id.isEmpty()) {
                binding.tilId.error = "아이디를 입력해주세요."
                isValid = false
            } else {
                binding.tilId.error = null
            }
            if (password.isEmpty()) {
                binding.tilPassword.error = "비밀번호를 입력해주세요."
                isValid = false
            } else {
                binding.tilPassword.error = null
            }
            if (!isValid) return@setOnClickListener

            val loginRequest = LoginRequest(userId = id, userPw = password)

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.loginUser(loginRequest)

                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse = response.body()!!
                        if (loginResponse.success) {
                            Toast.makeText(this@Login, "로그인 성공!", Toast.LENGTH_SHORT).show()

                            val requiresReg = loginResponse.requiresPlantRegistration ?: true
                            saveLoginState(true, loginResponse.token, loginResponse.userData, requiresReg)

                            navigateToAppropriateScreen()

                        } else {
                            Toast.makeText(this@Login, loginResponse.message, Toast.LENGTH_LONG).show()
                            saveLoginState(false, null, null, true)
                        }
                    } else {
                        Toast.makeText(this@Login, "로그인 실패 (코드: ${response.code()})", Toast.LENGTH_SHORT).show()
                        saveLoginState(false, null, null, true)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@Login, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", "Login Exception", e)
                    saveLoginState(false, null, null, true)
                }
            }
        }

        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, getString(R.string.forgot_password_clicked), Toast.LENGTH_SHORT).show()
        }
    }
} 