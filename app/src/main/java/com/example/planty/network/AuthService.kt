package com.example.planty.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>
}

data class SignupRequest(
    val nickname: String,
    val userId: String,
    val userPw: String,
    val email: String
)

data class SignupResponse(
    val success: Boolean,
    val message: String,
    val errorCode: String?
)

data class LoginRequest(
    val userId: String,
    val userPw: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val userData: UserData?,
    val requiresPlantRegistration: Boolean?
)

data class UserData(
    val userId: String,
    val nickname: String,
    val email: String
)

data class ResetPasswordRequest(
    val email: String
) 