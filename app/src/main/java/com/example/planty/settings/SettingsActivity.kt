package com.example.planty.settings

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<LinearLayout>(R.id.notification_settings).setOnClickListener {
            startActivity(Intent(this, NotificationSettingsActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.dnd_settings).setOnClickListener {
            startActivity(Intent(this, DoNotDisturbActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.account_settings).setOnClickListener {
            startActivity(Intent(this, AccountSettingsActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.blocked_users).setOnClickListener {
            startActivity(Intent(this, BlockedUsersActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.language_settings).setOnClickListener {
            startActivity(Intent(this, LanguageSettingsActivity::class.java))
        }

        findViewById<TextView>(R.id.logout).setOnClickListener {
            ConfirmLogoutDialog(this).show()
        }

        findViewById<TextView>(R.id.delete_account).setOnClickListener {
            ConfirmDeleteDialog(this).show()
        }

        // Optional: handle back arrow and edit profile clicks
        findViewById<android.widget.ImageView>(R.id.back_arrow).setOnClickListener {
            finish()
        }
        findViewById<android.widget.ImageView>(R.id.edit_profile)?.setOnClickListener {
            // TODO: Implement profile edit action
        }
    }
} 