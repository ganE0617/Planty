package com.example.planty.settings

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.R
import com.example.planty.MainActivity
import java.util.*

class LanguageSettingsActivity : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "AppSettings"
        private const val KEY_LANGUAGE = "language"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_settings)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentLang = prefs.getString(KEY_LANGUAGE, "ko")

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupLanguage)
        val btnApply = findViewById<Button>(R.id.btnApplyLanguage)

        if (currentLang == "ko") {
            findViewById<RadioButton>(R.id.radio_ko).isChecked = true
        } else {
            findViewById<RadioButton>(R.id.radio_en).isChecked = true
        }

        btnApply.setOnClickListener {
            val selectedLang = when (radioGroup.checkedRadioButtonId) {
                R.id.radio_ko -> "ko"
                R.id.radio_en -> "en"
                else -> "ko"
            }

            prefs.edit().putString(KEY_LANGUAGE, selectedLang).apply()
            changeAppLanguage(selectedLang)
        }
    }

    private fun changeAppLanguage(langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
} 