package com.example.planty.network

import com.example.planty.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object SupabaseClient {
    private val clientScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Auth)
            install(Storage)
        }.also {
            clientScope.launch {
                try {
                    // Test connection
                    it.auth.currentSessionOrNull()
                    _isInitialized.value = true
                } catch (e: Exception) {
                    e.printStackTrace()
                    _isInitialized.value = false
                }
            }
        }
    }

    fun reset() {
        _isInitialized.value = false
    }
} 