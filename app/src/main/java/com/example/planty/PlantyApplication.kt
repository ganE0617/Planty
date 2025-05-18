package com.example.planty

import android.app.Application
import com.example.planty.network.TokenManager

class PlantyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
    }
} 