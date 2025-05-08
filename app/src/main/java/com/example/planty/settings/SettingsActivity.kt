package com.example.planty.settings

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.R
import com.example.planty.MainActivity

class SettingsActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_GALLERY = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<TextView>(R.id.notification_settings).setOnClickListener {
            startActivity(Intent(this, NotificationSettingsActivity::class.java))
        }

        findViewById<TextView>(R.id.dnd_settings).setOnClickListener {
            startActivity(Intent(this, DoNotDisturbActivity::class.java))
        }

        findViewById<TextView>(R.id.account_settings).setOnClickListener {
            startActivity(Intent(this, AccountSettingsActivity::class.java))
        }

        findViewById<TextView>(R.id.blocked_users).setOnClickListener {
            startActivity(Intent(this, BlockedUsersActivity::class.java))
        }

        findViewById<TextView>(R.id.language_settings).setOnClickListener {
            startActivity(Intent(this, LanguageSettingsActivity::class.java))
        }

        findViewById<TextView>(R.id.logout).setOnClickListener {
            ConfirmLogoutDialog(this).show()
        }

        findViewById<TextView>(R.id.delete_account).setOnClickListener {
            ConfirmDeleteDialog(this).show()
        }

        val backButton = findViewById<ImageView>(R.id.iv_back)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish() // 현재 화면 종료
        }

//        프로필 사진 변경

        val profileImage = findViewById<ImageView>(R.id.iv_profile)

        profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_GALLERY)
        }



    }

    // onActivityResult에서 이미지 적용
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            val uri = data?.data
            uri?.let {
                // 1. ImageView에 적용
                val ivProfile = findViewById<ImageView>(R.id.iv_profile)
                ivProfile.setImageURI(it)

                //SharedPreferences에 저장
                val prefs = getSharedPreferences("profile", MODE_PRIVATE)
                prefs.edit().putString("uri", it.toString()).apply()

            }
        }
    }
} 