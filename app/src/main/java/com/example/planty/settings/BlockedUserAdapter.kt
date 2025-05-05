package com.example.planty.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planty.R

class BlockedUserAdapter(
    private val users: List<String>,
    private val onUnblockClicked: (String) -> Unit
) : RecyclerView.Adapter<BlockedUserAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUserName: TextView = view.findViewById(R.id.tvUserName)
        val btnUnblock: Button = view.findViewById(R.id.btnUnblock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blocked_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.tvUserName.text = user
        holder.btnUnblock.setOnClickListener {
            onUnblockClicked(user)
        }
    }
} 