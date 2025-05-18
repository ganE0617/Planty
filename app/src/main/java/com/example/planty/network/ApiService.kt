package com.example.planty.network

import com.example.planty.data.LoginRequest
import com.example.planty.data.LoginResponse
import com.example.planty.data.ResetPasswordRequest
import com.example.planty.data.SignupRequest
import com.example.planty.data.SignupResponse
import com.example.planty.data.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("auth/signup")
    suspend fun signup(@Body signupRequest: SignupRequest): Response<SignupResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>
}

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