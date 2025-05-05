package com.example.planty.network

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

sealed class AuthResult {
    data class Success(val session: UserSession) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository {
    private val client = SupabaseClient.client

    fun signUp(email: String, password: String, nickname: String): Flow<AuthResult> = flow {
        try {
            val user = client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("nickname", nickname)
                }
            }

            val session = client.auth.currentSessionOrNull()

            if (session == null) {
                emit(AuthResult.Error("로그인 중 오류가 발생했습니다: 세션이 없습니다."))
            } else {
                emit(AuthResult.Success(session))
            }
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("duplicate") == true -> "이미 사용 중인 이메일입니다."
                e.message?.contains("password") == true -> "비밀번호는 6자 이상이어야 합니다."
                else -> "회원가입 중 오류가 발생했습니다: ${e.message}"
            }
            emit(AuthResult.Error(errorMessage))
        }
    }

    fun signIn(email: String, password: String): Flow<AuthResult> = flow {
        try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val session = client.auth.currentSessionOrNull()

            if (session == null) {
                emit(AuthResult.Error("로그인 중 오류가 발생했습니다: 세션이 없습니다."))
            } else {
                emit(AuthResult.Success(session))
            }
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("Invalid login credentials") == true -> "이메일 또는 비밀번호가 올바르지 않습니다."
                else -> "로그인 중 오류가 발생했습니다: ${e.message}"
            }
            emit(AuthResult.Error(errorMessage))
        }
    }

    fun resetPassword(email: String): Flow<AuthResult> = flow {
        try {
            client.auth.resetPasswordForEmail(email)
            emit(AuthResult.Success(client.auth.currentSessionOrNull() ?: throw Exception("No session")))
        } catch (e: Exception) {
            emit(AuthResult.Error("비밀번호 재설정 중 오류가 발생했습니다: ${e.message}"))
        }
    }

    suspend fun signOut() {
        try {
            client.auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurrentSession(): UserSession? {
        return try {
            client.auth.currentSessionOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getCurrentUserNickname(): String? {
        return try {
            client.auth.currentSessionOrNull()?.user?.userMetadata?.get("nickname") as? String
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
} 