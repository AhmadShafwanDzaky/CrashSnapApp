package com.dicoding.picodiploma.loginwithanimation.di

import android.content.Context
import com.capstone.crashsnap.data.Repository
import com.capstone.crashsnap.data.UserPreference
import com.capstone.crashsnap.data.dataStore
import com.capstone.crashsnap.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val gmapsApiService = ApiConfig.getGooglePlacesService()
        return Repository.getInstance(pref, apiService, gmapsApiService)
    }
}