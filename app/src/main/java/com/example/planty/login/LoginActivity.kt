package com.example.planty.login


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.planty.MainActivity
import com.example.planty.databinding.ActivityLoginBinding
import com.example.planty.network.AuthRepository
import com.example.planty.network.AuthResult
import com.example.planty.plant.PlantRegistrationActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val authRepository = AuthRepository()

    companion object {
        const val PREFS_FILENAME = "UserPrefs"
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        const val KEY_USER_EMAIL = "userEmail"
        const val KEY_USER_NICKNAME = "userNickname"
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

    private fun getStoredUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    private fun navigateToAppropriateScreen() {
        val requiresPlantReg = checkRequiresPlantRegistration()

        if (requiresPlantReg) {
            val nickname = getStoredUserNickname() ?: "사용자"
            val email = getStoredUserEmail()

            if (email == null) {
                android.util.Log.e("LoginActivity", "자동 로그인 실패: 사용자 이메일 정보 없음")
                saveLoginState(false, null, null, true)
                return
            }

            val intent = Intent(this, PlantRegistrationActivity::class.java)
            intent.putExtra(PlantRegistrationActivity.EXTRA_USER_NICKNAME, nickname)
            intent.putExtra(PlantRegistrationActivity.EXTRA_USER_EMAIL, email)
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

    private fun saveLoginState(isLoggedIn: Boolean, email: String?, nickname: String?, requiresPlantReg: Boolean?) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)

        if (isLoggedIn && email != null && nickname != null && requiresPlantReg != null) {
            editor.putString(KEY_USER_EMAIL, email)
            editor.putString(KEY_USER_NICKNAME, nickname)
            editor.putBoolean(KEY_REQUIRES_PLANT_REGISTRATION, requiresPlantReg)
        } else {
            editor.remove(KEY_USER_EMAIL)
            editor.remove(KEY_USER_NICKNAME)
            editor.remove(KEY_REQUIRES_PLANT_REGISTRATION)
        }
        editor.apply()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etId.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            var isValid = true

            if (email.isEmpty()) {
                binding.tilId.error = "이메일을 입력해주세요."
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

            lifecycleScope.launch {
                authRepository.signIn(email, password).collect { result ->
                    when (result) {
                        is AuthResult.Success -> {
                            val nickname = result.userData?.nickname ?: "사용자"
                            Toast.makeText(this@LoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()
                            saveLoginState(true, email, nickname, result.requiresPlantRegistration)
                            navigateToAppropriateScreen()
                        }
                        is AuthResult.Error -> {
                            Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_SHORT).show()
                            saveLoginState(false, null, null, true)
                        }
                    }
                }
            }
        }

        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.tvForgotPassword.setOnClickListener {
            showPasswordResetDialog()
        }
    }

    private fun showPasswordResetDialog() {
        val email = binding.etId.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(this, "이메일을 먼저 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            authRepository.resetPassword(email).collect { result ->
                when (result) {
                    is AuthResult.Success -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "비밀번호 재설정 링크가 이메일로 전송되었습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is AuthResult.Error -> {
                        Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
} 