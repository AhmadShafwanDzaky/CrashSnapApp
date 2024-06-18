package com.capstone.crashsnap.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.crashsnap.data.Repository
import com.capstone.crashsnap.data.UserModel

class MapsViewModel(private val repository: Repository): ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun nearbyPlaces( type: String, location: String, radius: Int, apiKey: String)
    =  repository.nearbyPlaces(type, location, radius, apiKey)
}