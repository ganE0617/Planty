package com.example.planty

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.databinding.ActivityCommunityWriteBinding

class CommunityWriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommunityWriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnRegister.setOnClickListener {
            // TODO: 실제 저장 로직 구현
            Toast.makeText(this, "글이 등록되었습니다!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
} 