package com.example.planty.settings

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.R

class AccountSettingsActivity : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "AppSettings"
        private const val KEY_NICKNAME = "nickname"
        private const val KEY_EMAIL = "email" // 추후 실제 로그인 시스템 연결 시 활용
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val tvEmail = findViewById<TextView>(R.id.tv_email)
        val etNickname = findViewById<EditText>(R.id.et_nickname)
        val btnSaveNickname = findViewById<Button>(R.id.btn_save_nickname)

        val etOldPw = findViewById<EditText>(R.id.et_old_password)
        val etNewPw = findViewById<EditText>(R.id.et_new_password)
        val btnChangePw = findViewById<Button>(R.id.btn_change_password)

        // 임시 이메일 표시
        tvEmail.text = prefs.getString(KEY_EMAIL, "user@example.com")

        // 기존 닉네임 불러오기
        etNickname.setText(prefs.getString(KEY_NICKNAME, ""))

        btnSaveNickname.setOnClickListener {
            val nickname = etNickname.text.toString()
            if (nickname.isNotBlank()) {
                prefs.edit().putString(KEY_NICKNAME, nickname).apply()
                Toast.makeText(this, getString(R.string.toast_nickname_saved), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.toast_enter_nickname), Toast.LENGTH_SHORT).show()
            }
        }

        btnChangePw.setOnClickListener {
            val oldPw = etOldPw.text.toString()
            val newPw = etNewPw.text.toString()

            // 비밀번호는 지금은 로컬에서만 비교, 추후 서버 연동
            if (oldPw.isBlank() || newPw.isBlank()) {
                Toast.makeText(this, getString(R.string.toast_enter_passwords), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.toast_password_changed), Toast.LENGTH_SHORT).show()
            }
        }
    }
} 