package com.example.planty.data

data class Plant(
    val id: String,
    val name: String,
    val type: String,
    val wateringCycle: Int,
    val last_watered: String,
    val status: String,
    val imageResId: Int
) 