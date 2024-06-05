package com.capstone.crashsnap.ui.maps

import androidx.lifecycle.ViewModel
import com.capstone.crashsnap.data.Repository

class MapsViewModel(private val repository: Repository): ViewModel() {

    fun nearbyPlaces( type: String, location: String, radius: Int, apiKey: String)
    =  repository.nearbyPlaces(type, location, radius, apiKey)
}