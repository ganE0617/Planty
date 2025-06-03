package com.example.planty.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planty.R
import com.example.planty.data.CommunityPost

class CommunityPreviewAdapter(
    private val posts: List<CommunityPost>,
    private val onItemClick: (CommunityPost) -> Unit
) : RecyclerView.Adapter<CommunityPreviewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivImage: ImageView = itemView.findViewById(R.id.iv_community_image)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_community_title)
        private val tvContent: TextView = itemView.findViewById(R.id.tv_community_content_preview)
        private val tvLike: TextView = itemView.findViewById(R.id.tv_community_like_count)
        private val tvComment: TextView = itemView.findViewById(R.id.tv_community_comment_count)

        fun bind(post: CommunityPost) {
            ivImage.setImageResource(post.imageResId)
            tvTitle.text = post.title
            tvContent.text = if (post.content.length > 10) post.content.take(10) + "..." else post.content
            tvLike.text = post.likeCount.toString()
            tvComment.text = post.commentCount.toString()
            itemView.setOnClickListener { onItemClick(post) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_community_preview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size
} 