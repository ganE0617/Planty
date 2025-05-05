package com.example.planty.settings

import android.content.Context
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.R

class NotificationSettingsActivity : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "AppSettings"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)

        val switch = findViewById<Switch>(R.id.switch_notifications)

        // SharedPreferences 불러오기
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        switch.isChecked = isEnabled

        // 상태 변경 시 저장
        switch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, isChecked).apply()
        }
    }
} 