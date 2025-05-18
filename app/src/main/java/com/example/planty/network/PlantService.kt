package com.example.planty.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PlantService {
    @POST("plants")
    suspend fun registerPlant(@Body request: PlantRegistrationRequest): Response<PlantRegistrationResponse>

    @GET("plants")
    suspend fun getPlants(): Response<List<PlantResponse>>
}

data class PlantRegistrationRequest(
    val name: String,
    val type: String,
    val wateringCycle: Int
)

data class PlantRegistrationResponse(
    val success: Boolean,
    val message: String,
    val plant: PlantResponse?
)

data class PlantResponse(
    val id: Int,
    val name: String,
    val type: String,
    val wateringCycle: Int,
    val lastWatered: String,
    val createdAt: String,
    val ownerId: String
) 