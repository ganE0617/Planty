package com.example.planty.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://planty.gaeun.xyz/" // 운영 서버 주소

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
} 