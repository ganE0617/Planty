package com.example.planty.login

import android.content.Intent
import android.os.Bundle
import com.example.planty.R
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.databinding.ActivitySignupBinding
import androidx.lifecycle.lifecycleScope
import com.example.planty.network.AuthRepository
import com.example.planty.network.AuthResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val authRepository = AuthRepository()

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
        val email = binding.etIdSignup.text.toString().trim()
        val password = binding.etPasswordSignup.text.toString().trim()
        val passwordConfirm = binding.etPasswordConfirm.text.toString().trim()

        if (!validateInput(nickname, email, password, passwordConfirm)) {
            return
        }

        lifecycleScope.launch {
            authRepository.signUp(email, password, nickname).collect { result ->
                when (result) {
                    is AuthResult.Success -> {
                        Toast.makeText(this@SignUp, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignUp, Login::class.java)
                        startActivity(intent)
                        finish()
                    }
                    is AuthResult.Error -> {
                        if (result.message.contains("이미 사용 중인 이메일")) {
                            binding.tilIdSignup.error = result.message
                        } else {
                            Toast.makeText(this@SignUp, "회원가입 실패: ${result.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }


    private fun validateInput(nickname: String, email: String, pass: String, passConfirm: String): Boolean {
        binding.tilNickname.error = null
        binding.tilIdSignup.error = null
        binding.tilPasswordSignup.error = null
        binding.tilPasswordConfirm.error = null

        var isValid = true

        if (nickname.isEmpty()) {
            binding.tilNickname.error = getString(R.string.error_empty_field)
            isValid = false
        }
        if (email.isEmpty()) {
            binding.tilIdSignup.error = getString(R.string.error_empty_field)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilIdSignup.error = getString(R.string.error_invalid_email)
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