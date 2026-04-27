package com.example.recircuitai.data.network

import com.example.recircuitai.data.RecycleItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val count: Int? = null,
    val data: T
)

interface ApiService {
    @GET("get-items")
    suspend fun getItems(): Response<ApiResponse<List<RecycleItem>>>

    @GET("get-item/{id}")
    suspend fun getItem(@Path("id") id: String): Response<ApiResponse<RecycleItem>>

    @Multipart
    @POST("upload-item")
    suspend fun uploadItem(
        @Part image: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("location") location: RequestBody
    ): Response<ApiResponse<RecycleItem>>

    @Multipart
    @POST("analyze-image")
    suspend fun analyzeImage(
        @Part image: MultipartBody.Part
    ): Response<ApiResponse<AnalysisResult>>
}

data class AnalysisResult(
    @SerializedName("object") val identifiedItem: String,
    val material: String,
    val confidence: Double,
    val tags: List<String>,
    val industry: String,
    val possible_products: List<String>
)

data class AIRequest(val itemId: String)
