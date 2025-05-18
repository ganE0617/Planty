package com.example.planty.network

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed class PlantResult {
    object Success : PlantResult()
    data class Error(val message: String) : PlantResult()
}

class PlantRepository {
    private val plantService = ApiClient.plantService

    suspend fun registerPlant(name: String, type: String, wateringCycle: Int): Flow<PlantResult> = flow {
        try {
            val request = PlantRegistrationRequest(
                name = name,
                type = type,
                wateringCycle = wateringCycle
            )
            val response = plantService.registerPlant(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.success) {
                        emit(PlantResult.Success)
                    } else {
                        emit(PlantResult.Error(it.message))
                    }
                } ?: emit(PlantResult.Error("식물 등록 실패"))
            } else {
                response.body()?.let {
                    emit(PlantResult.Error(it.message))
                } ?: emit(PlantResult.Error("식물 등록에 실패했습니다."))
            }
        } catch (e: Exception) {
            Log.e("PlantRepository", "식물 등록 실패", e)
            emit(PlantResult.Error(e.message ?: "알 수 없는 오류가 발생했습니다."))
        }
    }

    fun getPlants(): Flow<List<PlantResponse>> = flow {
        try {
            val response = plantService.getPlants()
            if (response.isSuccessful) {
                response.body()?.let { plants ->
                    emit(plants)
                } ?: emit(emptyList())
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e("PlantRepository", "식물 목록 조회 실패", e)
            emit(emptyList())
        }
    }
}

data class Plant(
    val id: String? = null,
    val name: String,
    val type: String,
    val wateringCycle: Int,
    val lastWatered: Long? = null,
    val userId: String? = null
) 