package com.example.planty.settings

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.planty.R
import com.example.planty.login.LoginActivity

class ConfirmLogoutDialog(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_confirm)

        findViewById<TextView>(R.id.tvDialogMessage).text = context.getString(R.string.logout_confirm_message)

        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dismiss()
        }

        findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            // 로그아웃 처리: SharedPreferences 초기화
            val prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            // 로그인 화면으로 이동 (백스택 초기화)
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
            dismiss()
        }
    }
} 