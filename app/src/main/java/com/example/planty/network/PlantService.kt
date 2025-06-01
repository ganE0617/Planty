package com.example.planty.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import com.google.gson.annotations.SerializedName

interface PlantService {
    @POST("plants")
    suspend fun registerPlant(
        @Body request: PlantRegistrationRequest
    ): Response<PlantRegistrationResponse>

    @GET("plants")
    suspend fun getPlants(): Response<List<PlantResponse>>

    // LED 모드 저장
    @POST("plants/{plant_id}/led")
    suspend fun setPlantLed(
        @Path("plant_id") plantId: Int,
        @Body request: PlantLedRequest
    ): Response<PlantLedResponse>

    // LED 모드 조회
    @GET("plants/{plant_id}/led")
    suspend fun getPlantLed(
        @Path("plant_id") plantId: Int
    ): Response<PlantLedResponse>

    // AI 분석 결과 조회
    @GET("plants/{plant_id}/ai-analysis")
    suspend fun getPlantAIAnalysis(
        @Path("plant_id") plantId: Int
    ): Response<AIAnalysisResponse>

    @GET("plants/{plant_id}")
    suspend fun getPlant(
        @Path("plant_id") plantId: Int
    ): Response<PlantRegistrationResponse>
}

data class PlantRegistrationRequest(
    val name: String,
    val type: String,
    val watering_cycle: Int,
    val last_watered: String
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
    @SerializedName("watering_cycle")
    val wateringCycle: Int,
    @SerializedName("last_watered")
    val last_watered: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("owner_id")
    val ownerId: String
)

data class PlantLedRequest(
    val plant_id: Int,
    val mode: String,
    val r: Int,
    val g: Int,
    val b: Int,
    val strength: Int
)

data class PlantLedResponse(
    val success: Boolean,
    val message: String,
    val led: PlantLedRequest?
)

data class AIAnalysisResponse(
    val success: Boolean,
    val analysis_text: String?,
    val created_at: String?
) 