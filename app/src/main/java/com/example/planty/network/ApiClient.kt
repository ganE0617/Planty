package com.example.planty.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // 운영 환경에서는 도메인 사용
    private const val BASE_URL = "https://planty.gaeun.xyz/"  // 운영 서버 주소

    // Create a separate client for token refresh without interceptors
    private val refreshClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val refreshRetrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(refreshClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val refreshAuthService = refreshRetrofit.create(AuthService::class.java)

    // Callback to notify when authentication is needed
    private var onAuthNeeded: (() -> Unit)? = null

    fun setOnAuthNeededListener(listener: () -> Unit) {
        onAuthNeeded = listener
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val token = TokenManager.getToken()
            Log.d("ApiClient", "Current token: $token")
            
            var request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            
            var response = chain.proceed(request)
            
            // If we get a 401, try to refresh the token
            if (response.code == 401 && token != null) {
                try {
                    // Use the separate client for token refresh
                    val refreshCall = refreshAuthService.refreshToken("Bearer $token")
                    val refreshResponse = refreshCall.execute()
                    
                    if (refreshResponse.isSuccessful) {
                        refreshResponse.body()?.token?.let { newToken ->
                            Log.d("ApiClient", "Token refreshed successfully")
                            TokenManager.saveToken(newToken)
                            
                            // Retry the original request with the new token
                            request = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer $newToken")
                                .build()
                            response.close()
                            response = chain.proceed(request)
                        }
                    } else {
                        // Token refresh failed, clear token and notify that authentication is needed
                        Log.d("ApiClient", "Token refresh failed, redirecting to login")
                        TokenManager.clearToken()
                        onAuthNeeded?.invoke()
                    }
                } catch (e: Exception) {
                    Log.e("ApiClient", "Token refresh failed", e)
                    // Clear token and notify that authentication is needed
                    TokenManager.clearToken()
                    onAuthNeeded?.invoke()
                }
            }
            
            response
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthService = retrofit.create(AuthService::class.java)
    val plantService: PlantService = retrofit.create(PlantService::class.java)
} 