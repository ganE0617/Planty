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
            val token = TokenManager.getToken()
            Log.d("PlantRepository", "Using token for plant registration: $token")
            if (token == null) {
                emit(PlantResult.Error("로그인이 필요합니다."))
                return@flow
            }

            val request = PlantRegistrationRequest(
                name = name,
                type = type,
                watering_cycle = wateringCycle
            )
            val response = plantService.registerPlant(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.success) {
                        // Save the plant data locally if needed
                        it.plant?.let { plant ->
                            // TODO: Save plant to local database if needed
                        }
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

    suspend fun getPlants(): Flow<List<Plant>> = flow {
        try {
            val token = TokenManager.getToken()
            Log.d("PlantRepository", "Getting plants with token: $token")
            if (token == null) {
                Log.e("PlantRepository", "No token found")
                emit(emptyList())
                return@flow
            }

            val response = plantService.getPlants()
            Log.d("PlantRepository", "Get plants response code: ${response.code()}")
            
            if (response.isSuccessful) {
                response.body()?.let { plants ->
                    Log.d("PlantRepository", "Successfully got ${plants.size} plants")
                    emit(plants.map { it.toPlant() })
                } ?: run {
                    Log.e("PlantRepository", "Response body is null")
                    emit(emptyList())
                }
            } else {
                Log.e("PlantRepository", "Failed to get plants: ${response.code()} - ${response.message()}")
                response.errorBody()?.let {
                    Log.e("PlantRepository", "Error body: ${it.string()}")
                }
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e("PlantRepository", "식물 목록 조회 실패", e)
            emit(emptyList())
        }
    }
}

data class Plant(
    val id: Int? = null,
    val name: String,
    val type: String,
    val wateringCycle: Int,
    val last_watered: String? = null,
    val createdAt: String? = null,
    val ownerId: String? = null
)

private fun PlantResponse.toPlant(): Plant {
    return Plant(
        id = id,
        name = name,
        type = type,
        wateringCycle = wateringCycle,
        last_watered = last_watered,
        createdAt = createdAt,
        ownerId = ownerId
    )
} 