package com.example.planty.network

import com.example.planty.data.LoginRequest
import com.example.planty.data.SignupRequest
import com.example.planty.data.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("auth/signup")
    suspend fun signupUser(@Body signupRequest: SignupRequest): Response<SignupResponse>
}
//
//data class LoginResponse(
//    val success: Boolean,
//    val message: String,
//    val token: String?,
//    val userData: User?,
//    val requiresPlantRegistration: Boolean?
//)
//
//data class SignupResponse(
//    val success: Boolean,
//    val message: String,
//    val errorCode: String?
//)