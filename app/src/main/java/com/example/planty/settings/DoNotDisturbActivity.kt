package com.example.planty.settings

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.R

class DoNotDisturbActivity : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "AppSettings"
        private const val KEY_START_HOUR = "dnd_start_hour"
        private const val KEY_START_MIN = "dnd_start_minute"
        private const val KEY_END_HOUR = "dnd_end_hour"
        private const val KEY_END_MIN = "dnd_end_minute"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_do_not_disturb)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val timePickerStart = findViewById<TimePicker>(R.id.timePickerStart)
        val timePickerEnd = findViewById<TimePicker>(R.id.timePickerEnd)

        timePickerStart.setIs24HourView(true)
        timePickerEnd.setIs24HourView(true)

        val btnSave = findViewById<Button>(R.id.btnSave)

        // 이전 설정 불러오기
        timePickerStart.hour = prefs.getInt(KEY_START_HOUR, 22)
        timePickerStart.minute = prefs.getInt(KEY_START_MIN, 0)
        timePickerEnd.hour = prefs.getInt(KEY_END_HOUR, 7)
        timePickerEnd.minute = prefs.getInt(KEY_END_MIN, 0)

        btnSave.setOnClickListener {
            prefs.edit().apply {
                putInt(KEY_START_HOUR, timePickerStart.hour)
                putInt(KEY_START_MIN, timePickerStart.minute)
                putInt(KEY_END_HOUR, timePickerEnd.hour)
                putInt(KEY_END_MIN, timePickerEnd.minute)
                apply()
            }
            Toast.makeText(this, "설정이 저장되었습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
} 