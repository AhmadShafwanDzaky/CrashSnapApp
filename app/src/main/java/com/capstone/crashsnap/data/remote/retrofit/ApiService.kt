package com.capstone.crashsnap.data.remote.retrofit

import com.capstone.crashsnap.data.remote.request.LoginRequest
import com.capstone.crashsnap.data.remote.request.SignupRequest
import com.capstone.crashsnap.data.remote.response.FileUploadResponse
import com.capstone.crashsnap.data.remote.response.HistoryDetailResponse
import com.capstone.crashsnap.data.remote.response.HistoryResponse
import com.capstone.crashsnap.data.remote.response.LoginResponse
import com.capstone.crashsnap.data.remote.response.NearbyPlaceResponse
import com.capstone.crashsnap.data.remote.response.SignupResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("/api/auth/register")
    fun signup(
        @Body request: SignupRequest
    ): Call<SignupResponse>

    @POST("/api/auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @GET("/api/predictions")
    suspend fun getHistory(
        @Header("Authorization") token: String,
    ): HistoryResponse

    @GET("/api/predictions")
    suspend fun getAllHistory(
        @Header("Authorization") token: String,
    ): HistoryResponse

    @GET("nearbysearch/json")
    fun NearbyPlaces(
        @Query("keyword") type: String,
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("key") apiKey: String
    ): Call<NearbyPlaceResponse>

    @Multipart
    @POST("api/predictions/cost")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): FileUploadResponse

    @GET("/api/predictions/{id}/detail")
    suspend fun getHistoryDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): HistoryDetailResponse

    @DELETE("/api/predictions/{id}/delete")
    suspend fun deleteHistoryId(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): HistoryDetailResponse
}