package com.capstone.crashsnap.data.remote.retrofit

import com.capstone.crashsnap.data.remote.request.LoginRequest
import com.capstone.crashsnap.data.remote.request.SignupRequest
import com.capstone.crashsnap.data.remote.response.HistoryResponse
import com.capstone.crashsnap.data.remote.response.LoginResponse
import com.capstone.crashsnap.data.remote.response.NearbyPlaceResponse
import com.capstone.crashsnap.data.remote.response.SignupResponse
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
    fun getHistory(
        @Header("Authorization") token: String,
    ): Call<HistoryResponse>

    @GET("/api/predictions")
    fun getAllHistory(
        @Header("Authorization") token: String,
    ): Call<HistoryResponse>

    @GET("nearbysearch/json")
    fun NearbyPlaces(
        @Query("keyword") type: String,
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("key") apiKey: String
    ): Call<NearbyPlaceResponse>
}