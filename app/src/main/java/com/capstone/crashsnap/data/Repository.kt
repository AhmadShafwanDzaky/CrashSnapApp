package com.capstone.crashsnap.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.capstone.crashsnap.data.remote.request.SignupRequest
import com.capstone.crashsnap.data.remote.response.NearbyPlaceResponse
import com.capstone.crashsnap.data.remote.response.SignupResponse
import com.capstone.crashsnap.data.remote.retrofit.ApiService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository private constructor(
    private val apiService: ApiService,
    private val gmapsApiService: ApiService,
) {

    private val resultSignup = MediatorLiveData<NetResult<SignupResponse>>()
    private val resultNearbyPlace = MediatorLiveData<NetResult<NearbyPlaceResponse>>()
    fun signup(
        name: String,
        email: String,
        password: String
    ): LiveData<NetResult<SignupResponse>> {
        resultSignup.value = NetResult.Loading

        val signupRequest = SignupRequest(
            name = name,
            email = email,
            password = password
        )

        val client = apiService.signup(signupRequest)
        client.enqueue(object : Callback<SignupResponse> {
            override fun onResponse(
                call: Call<SignupResponse>,
                response: Response<SignupResponse>
            ) {
                val res = response.body()
                if (response.isSuccessful) {
                    if (res != null) {
                        Log.d("repo", "onResponse suk: nyampe")
                        resultSignup.value = NetResult.Success(res)
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorBody = Gson().fromJson(errorResponse, SignupResponse::class.java)
                    Log.d("repo", "onResponse error: ${errorBody.message}")
                    resultSignup.value = NetResult.Error(errorBody.message)
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                resultSignup.value = NetResult.Error("${t.message}")
            }
        })
        return resultSignup
    }

    fun nearbyPlaces(
        type: String,
        location: String,
        radius: Int,
        apiKey: String
    ): LiveData<NetResult<NearbyPlaceResponse>>{
        resultNearbyPlace.value = NetResult.Loading
        val client = gmapsApiService.NearbyPlaces(type, location, radius, apiKey)
        client.enqueue(object : Callback<NearbyPlaceResponse> {
            override fun onResponse(
                call: Call<NearbyPlaceResponse>,
                response: Response<NearbyPlaceResponse>
            ) {
                val res = response.body()
                if (response.isSuccessful) {
                    if (res != null) {
                        resultNearbyPlace.value = NetResult.Success(res)
                    }
                } else {
                    resultNearbyPlace.value = NetResult.Error("error ${res?.results}")
                }
            }

            override fun onFailure(call: Call<NearbyPlaceResponse>, t: Throwable) {
                resultNearbyPlace.value = NetResult.Error(" gksodo ${t.message}")
            }
        })
        return resultNearbyPlace
    }





    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService,
            gmapsApiService: ApiService,
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, gmapsApiService)
            }.also { instance = it }
    }
}