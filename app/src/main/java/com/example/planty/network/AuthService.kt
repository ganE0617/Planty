package com.example.planty.network

import com.example.planty.data.LoginRequest
import com.example.planty.data.LoginResponse
import com.example.planty.data.ResetPasswordRequest
import com.example.planty.data.SignupRequest
import com.example.planty.data.SignupResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("auth/signup")
    suspend fun signup(@Body signupRequest: SignupRequest): Response<SignupResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>

    @POST("auth/refresh")
    fun refreshToken(@Header("Authorization") token: String): Call<LoginResponse>
} 