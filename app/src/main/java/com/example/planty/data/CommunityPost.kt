package com.example.planty.data

data class CommunityPost(
    val id: Int,
    val title: String,
    val content: String,
    val imageResId: Int,
    val likeCount: Int,
    val commentCount: Int
) 