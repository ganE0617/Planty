package com.example.planty.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.MainActivity
import com.example.planty.R
import com.example.planty.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmailVerificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verification)

        val btnCheck = findViewById<Button>(R.id.btn_check_verification)
        val btnResend = findViewById<Button>(R.id.btn_resend_email)

        btnCheck.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val session = SupabaseClient.client.auth.currentSessionOrNull()
                    if (session != null) {
                        val refreshToken = session.refreshToken
                        val refreshedSession = SupabaseClient.client.auth.refreshSession(refreshToken)
                        val user = refreshedSession.user

                        if (user?.emailConfirmedAt != null) {
                            Toast.makeText(this@EmailVerificationActivity, "이메일 인증이 완료되었습니다!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@EmailVerificationActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@EmailVerificationActivity, "아직 인증이 완료되지 않았습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@EmailVerificationActivity, "세션이 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@EmailVerificationActivity, "인증 확인 중 오류 발생", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }

        btnResend.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val session = SupabaseClient.client.auth.currentSessionOrNull()
                    val email = session?.user?.email

                    if (email != null) {
                        val success = com.example.planty.network.SupabaseEmailService.resendConfirmationEmail(email)
                        if (success) {
                            Toast.makeText(this@EmailVerificationActivity, "인증 메일을 다시 보냈습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@EmailVerificationActivity, "메일 재전송 실패", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@EmailVerificationActivity, "이메일 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@EmailVerificationActivity, "재전송 중 오류 발생", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }
}
