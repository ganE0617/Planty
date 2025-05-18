package com.example.planty.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object SupabaseEmailService {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private const val SUPABASE_PROJECT_URL = "https://qldglddmasjspstclyxh.supabase.co" // TODO: 실제 값으로 교체
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFsZGdsZGRtYXNqc3BzdGNseXhoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDY0MjQ2MTcsImV4cCI6MjA2MjAwMDYxN30.duB8c2Z9wS-1XnhTGpluSYQt47K4GCC1ZLO3XBbPdZo" // TODO: 실제 값으로 교체

    suspend fun resendConfirmationEmail(email: String): Boolean = withContext(Dispatchers.IO) {
        val response: HttpResponse = client.post("$SUPABASE_PROJECT_URL/auth/v1/verify") {
            contentType(ContentType.Application.Json)
            header("apikey", SUPABASE_ANON_KEY)
            setBody(VerifyRequest(email = email, type = "signup"))
        }

        return@withContext response.status == HttpStatusCode.OK
    }

    @Serializable
    data class VerifyRequest(
        val email: String,
        val type: String // "signup" or "email_change" 등
    )
} 