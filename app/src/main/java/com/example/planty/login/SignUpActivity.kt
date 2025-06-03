package com.example.planty.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.planty.R
import com.example.planty.databinding.ActivitySignupBinding
import com.example.planty.network.AuthRepository
import com.example.planty.network.AuthResult
import com.example.planty.plant.PlantRegistrationActivity
import kotlinx.coroutines.launch
import android.widget.TextView

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEditTextCursorColor()
        setupClickListeners()
    }

    private fun setEditTextCursorColor() {
        val editTexts = listOf(binding.etNickname, binding.etEmail, binding.etPasswordSignup, binding.etPasswordConfirm)
        for (editText in editTexts) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                editText.textCursorDrawable = getDrawable(R.drawable.edittext_cursor_green)
            } else {
                try {
                    val f = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                    f.isAccessible = true
                    f.set(editText, R.drawable.edittext_cursor_green)
                } catch (e: Exception) {
                    // ignore
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSignupAction.setOnClickListener {
            performSignup()
        }
    }

    private fun performSignup() {
        val nickname = binding.etNickname.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPasswordSignup.text.toString().trim()
        val passwordConfirm = binding.etPasswordConfirm.text.toString().trim()

        if (!validateInput(nickname, email, password, passwordConfirm)) {
            return
        }

        lifecycleScope.launch {
            authRepository.signUp(email, password, nickname).collect { result ->
                when (result) {
                    is AuthResult.Success -> {
                        authRepository.signIn(email, password).collect { loginResult ->
                            when (loginResult) {
                                is AuthResult.Success -> {
                                    Log.d("SignUp", "회원가입 및 자동 로그인 성공 - 첫 식물 등록 화면으로 이동")
                                    Toast.makeText(this@SignUpActivity, "회원가입 성공! 첫 식물을 등록해주세요.", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@SignUpActivity, PlantRegistrationActivity::class.java)
                                    intent.putExtra(PlantRegistrationActivity.EXTRA_USER_NICKNAME, nickname)
                                    startActivity(intent)
                                    finish()
                                }
                                is AuthResult.Error -> {
                                    Log.e("SignUp", "자동 로그인 실패: ${loginResult.message}")
                                    Toast.makeText(this@SignUpActivity, "자동 로그인에 실패했습니다: ${loginResult.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    is AuthResult.Error -> {
                        Log.e("SignUp", "회원가입 실패: ${result.message}")
                        if (result.message.contains("이미 사용 중인 이메일")) {
                            binding.tilEmail.error = result.message
                        } else {
                            Toast.makeText(this@SignUpActivity, "회원가입 실패: ${result.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun validateInput(nickname: String, email: String, pass: String, passConfirm: String): Boolean {
        binding.tilNickname.error = null
        binding.tilEmail.error = null
        binding.tilPasswordSignup.error = null
        binding.tilPasswordConfirm.error = null

        var isValid = true

        if (nickname.isEmpty()) {
            binding.tilNickname.error = getString(R.string.error_empty_field)
            isValid = false
        }
        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.error_empty_field)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        }
        if (pass.isEmpty()) {
            binding.tilPasswordSignup.error = getString(R.string.error_empty_field)
            isValid = false
        }
        if (passConfirm.isEmpty()) {
            binding.tilPasswordConfirm.error = getString(R.string.error_empty_field)
            isValid = false
        } else if (pass != passConfirm) {
            binding.tilPasswordConfirm.error = getString(R.string.error_password_mismatch)
            binding.tilPasswordSignup.error = " "
            isValid = false
        }

        return isValid
    }
}
