package com.capstone.crashsnap.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import com.capstone.crashsnap.data.remote.request.LoginRequest
import com.capstone.crashsnap.data.remote.request.SignupRequest
import com.capstone.crashsnap.data.remote.response.HistoryResponse
import com.capstone.crashsnap.data.remote.response.LoginResponse
import com.capstone.crashsnap.data.remote.response.NearbyPlaceResponse
import com.capstone.crashsnap.data.remote.response.SignupResponse
import com.capstone.crashsnap.data.remote.retrofit.ApiService
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val gmapsApiService: ApiService,
) {

    private val resultSignup = MediatorLiveData<NetResult<SignupResponse>>()
    private val resultNearbyPlace = MediatorLiveData<NetResult<NearbyPlaceResponse>>()
    private val resultLogin = MediatorLiveData<NetResult<LoginResponse>>()
    private val resultHistory = MediatorLiveData<NetResult<HistoryResponse>>()
    private val resultAllHistory = MediatorLiveData<NetResult<HistoryResponse>>()


    suspend fun saveSession(token: String, name: String, email: String) {
        userPreference.saveSession(token, name, email)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }



    fun getAllHistory(token: String): LiveData<NetResult<HistoryResponse>> {
        resultAllHistory.value = NetResult.Loading
        userPreference.getSession()
        val bearerToken = "Bearer $token"
        val client = apiService.getAllHistory(bearerToken)
        client.enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {
                val res = response.body()
                if (response.isSuccessful) {
                    if (res != null) {
                        resultAllHistory.value = NetResult.Success(res)
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorBody = Gson().fromJson(errorResponse, LoginResponse::class.java)
                    resultAllHistory.value = NetResult.Error(errorBody.message)
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                resultAllHistory.value = NetResult.Error("fail ${t.message}")
            }
        })
        return resultAllHistory
    }

    fun getHistory(token: String): LiveData<NetResult<HistoryResponse>> {
        resultHistory.value = NetResult.Loading
        userPreference.getSession()
        val bearerToken = "Bearer $token"
        val client = apiService.getHistory(bearerToken)
        client.enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {
                val res = response.body()
                if (response.isSuccessful) {
                    if (res != null) {
                        resultHistory.value = NetResult.Success(res)
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorBody = Gson().fromJson(errorResponse, LoginResponse::class.java)
                    resultHistory.value = NetResult.Error(errorBody.message)
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                resultHistory.value = NetResult.Error("fail ${t.message}")
            }
        })
        return resultHistory
    }



    fun login(
        email: String,
        password: String
    ): LiveData<NetResult<LoginResponse>> {
        resultLogin.value = NetResult.Loading

        val loginRequest = LoginRequest(
            email = email,
            password = password
        )

        val client = apiService.login(loginRequest)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                val res = response.body()
                CoroutineScope(Dispatchers.Main).launch{
                    if (response.isSuccessful) {
                        if (res != null) {
                            saveSession(res.loginResult.token, res.loginResult.displayName, email)
                            resultLogin.value = NetResult.Success(res)
                        }
                    } else {
                        val errorResponse = response.errorBody()?.string()
                        val errorBody = Gson().fromJson(errorResponse, LoginResponse::class.java)
                        resultLogin.value = NetResult.Error(errorBody.message)
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                resultLogin.value = NetResult.Error("${t.message}")
            }
        })
        return resultLogin
    }

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
                        resultSignup.value = NetResult.Success(res)
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorBody = Gson().fromJson(errorResponse, SignupResponse::class.java)
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
    ): LiveData<NetResult<NearbyPlaceResponse>> {
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
                    resultNearbyPlace.value = NetResult.Error("Error ${res?.results}")
                }
            }

            override fun onFailure(call: Call<NearbyPlaceResponse>, t: Throwable) {
                resultNearbyPlace.value = NetResult.Error(" Error ${t.message}")
            }
        })
        return resultNearbyPlace
    }


    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            gmapsApiService: ApiService,
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(userPreference, apiService, gmapsApiService)
            }.also { instance = it }
    }
}