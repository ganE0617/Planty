package com.example.planty.network

import android.util.Log
import com.example.planty.data.LoginRequest
import com.example.planty.data.ResetPasswordRequest
import com.example.planty.data.SignupRequest
import com.example.planty.data.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed class AuthResult {
    data class Success(
        val token: String? = null,
        val userData: User? = null,
        val requiresPlantRegistration: Boolean = false
    ) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository {
    private val authService = ApiClient.authService

    // ✅ 회원가입
    suspend fun signUp(email: String, password: String, nickname: String): Flow<AuthResult> = flow {
        try {
            val request = SignupRequest(
                nickname = nickname,
                userId = email, // Using email as userId for now
                userPw = password,
                email = email
            )
            val response = authService.signup(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.success) {
                        emit(AuthResult.Success())
                    } else {
                        emit(AuthResult.Error(it.message))
                    }
                } ?: emit(AuthResult.Error("회원가입 실패"))
            } else {
                response.body()?.let {
                    emit(AuthResult.Error(it.message))
                } ?: emit(AuthResult.Error("이미 사용 중인 이메일입니다."))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "회원가입 실패", e)
            emit(AuthResult.Error(e.message ?: "알 수 없는 오류가 발생했습니다."))
        }
    }

    // ✅ 로그인
    suspend fun signIn(email: String, password: String): Flow<AuthResult> = flow {
        try {
            val request = LoginRequest(
                userId = email,
                userPw = password
            )
            val response = authService.login(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.success) {
                        it.token?.let { token ->
                            TokenManager.saveToken(token)
                        }
                        emit(AuthResult.Success(
                            token = it.token,
                            userData = it.userData,
                            requiresPlantRegistration = it.requiresPlantRegistration ?: false
                        ))
                    } else {
                        emit(AuthResult.Error(it.message))
                    }
                } ?: emit(AuthResult.Error("로그인 실패"))
            } else {
                response.body()?.let {
                    emit(AuthResult.Error(it.message))
                } ?: emit(AuthResult.Error("이메일 또는 비밀번호가 올바르지 않습니다."))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "로그인 실패", e)
            emit(AuthResult.Error(e.message ?: "알 수 없는 오류가 발생했습니다."))
        }
    }

    // ✅ 비밀번호 재설정 (이메일 전송)
    fun resetPassword(email: String): Flow<AuthResult> = flow {
        try {
            authService.resetPassword(ResetPasswordRequest(email))
            emit(AuthResult.Success())
        } catch (e: Exception) {
            emit(AuthResult.Error("비밀번호 재설정 중 오류가 발생했습니다: ${e.message}"))
        }
    }

    // ✅ 로그아웃
    fun logout() {
        TokenManager.clearToken()
    }

    // ✅ 현재 사용자 닉네임 불러오기
    fun getCurrentUserNickname(): String? {
        return try {
            // TODO: API를 통해 현재 사용자 정보를 가져오도록 수정
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
