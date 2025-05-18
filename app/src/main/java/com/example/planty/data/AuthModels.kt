package com.example.planty.data

data class LoginRequest(
    val userId: String,
    val userPw: String
)

data class SignupRequest(
    val nickname: String,
    val userId: String,
    val userPw: String,
    val email: String
)

data class ResetPasswordRequest(
    val email: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val userData: User?,
    val requiresPlantRegistration: Boolean?
)

data class SignupResponse(
    val success: Boolean,
    val message: String,
    val errorCode: String?
) 