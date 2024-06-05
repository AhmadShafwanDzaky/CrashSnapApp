package com.capstone.crashsnap.data.remote.retrofit

import com.capstone.crashsnap.data.remote.request.SignupRequest
import com.capstone.crashsnap.data.remote.response.NearbyPlaceResponse
import com.capstone.crashsnap.data.remote.response.SignupResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("/api/auth/register")
    fun signup(
        @Body request: SignupRequest
    ): Call<SignupResponse>

    @GET("nearbysearch/json")
    fun NearbyPlaces(
        @Query("type") type: String,
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("key") apiKey: String
    ): Call<NearbyPlaceResponse>
}