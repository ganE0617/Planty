package com.example.planty.login

import android.content.Intent
import android.os.Bundle
import com.example.planty.R
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.databinding.ActivitySignupBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.util.Log
import com.example.planty.data.SignupRequest
import com.example.planty.network.RetrofitClient
import java.io.IOException

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSignupAction.setOnClickListener {
            performSignup()
        }
    }

    private fun performSignup() {
        val nickname = binding.etNickname.text.toString().trim()
        val id = binding.etIdSignup.text.toString().trim()
        val password = binding.etPasswordSignup.text.toString().trim()
        val passwordConfirm = binding.etPasswordConfirm.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        if (!validateInput(nickname, id, password, passwordConfirm, email)) {
            return
        }

        val signupRequest = SignupRequest(nickname, id, password, email)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.signupUser(signupRequest)

                if (response.isSuccessful && response.body() != null) {
                    val signupResponse = response.body()!!
                    if (signupResponse.success) {
                        Toast.makeText(this@SignUp, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignUp, Login::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        when (signupResponse.errorCode) {
                            "DUPLICATE_ID" -> {
                                binding.tilIdSignup.error = signupResponse.message
                            }
                            "DUPLICATE_EMAIL" -> {
                                binding.tilEmail.error = signupResponse.message
                            }
                            else -> {
                                Toast.makeText(this@SignUp, "회원가입 실패: ${signupResponse.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("SignupActivity", "API Error: Code=${response.code()}, Message=${response.message()}, Body=$errorBody")
                    Toast.makeText(this@SignUp, "회원가입 중 오류 발생 (코드: ${response.code()})", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Log.e("SignupActivity", "Network IOException: ${e.message}", e)
                Toast.makeText(this@SignUp, "네트워크 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("SignupActivity", "Exception: ${e.message}", e)
                Toast.makeText(this@SignUp, "알 수 없는 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInput(nickname: String, id: String, pass: String, passConfirm: String, email: String): Boolean {
        binding.tilNickname.error = null
        binding.tilIdSignup.error = null
        binding.tilPasswordSignup.error = null
        binding.tilPasswordConfirm.error = null
        binding.tilEmail.error = null

        var isValid = true

        if (nickname.isEmpty()) {
            binding.tilNickname.error = getString(R.string.error_empty_field)
            isValid = false
        }
        if (id.isEmpty()) {
            binding.tilIdSignup.error = getString(R.string.error_empty_field)
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
        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.error_empty_field)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        return isValid
    }
} 