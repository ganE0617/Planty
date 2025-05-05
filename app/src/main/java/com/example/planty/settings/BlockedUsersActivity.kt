package com.example.planty.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planty.R

class BlockedUsersActivity : AppCompatActivity() {

    private lateinit var adapter: BlockedUserAdapter
    private val blockedUsers = mutableListOf("badguy123", "troll_master", "noob_killer")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_users)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerBlockedUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = BlockedUserAdapter(blockedUsers) { user ->
            // 차단 해제 로직 (현재는 로컬에서만 제거)
            blockedUsers.remove(user)
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "$user 차단 해제됨", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = adapter
    }
} 