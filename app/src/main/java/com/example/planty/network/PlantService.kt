package com.example.planty.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PlantService {
    @POST("plants")
    suspend fun registerPlant(
        @Header("Authorization") token: String,
        @Body request: PlantRegistrationRequest
    ): Response<PlantRegistrationResponse>

    @GET("plants")
    suspend fun getPlants(@Header("Authorization") token: String): Response<List<PlantResponse>>

    // LED 모드 저장
    @POST("plants/{plant_id}/led")
    suspend fun setPlantLed(
        @Header("Authorization") token: String,
        @Path("plant_id") plantId: Int,
        @Body request: PlantLedRequest
    ): Response<PlantLedResponse>

    // LED 모드 조회
    @GET("plants/{plant_id}/led")
    suspend fun getPlantLed(
        @Header("Authorization") token: String,
        @Path("plant_id") plantId: Int
    ): Response<PlantLedResponse>
}

data class PlantRegistrationRequest(
    val name: String,
    val type: String,
    val watering_cycle: Int
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

data class PlantLedRequest(
    val plant_id: Int,
    val mode: String,
    val r: Int,
    val g: Int,
    val b: Int
)

data class PlantLedResponse(
    val success: Boolean,
    val message: String,
    val led: PlantLedRequest?
) 