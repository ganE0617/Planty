package com.example.planty.data

data class SignupRequest(
    val nickname: String,
    val userId: String,
    val userPw: String,
    val email: String
) 