package com.capstone.crashsnap.ui.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.crashsnap.data.Repository
import com.capstone.crashsnap.data.UserModel
import kotlinx.coroutines.launch
import java.io.File

class PreviewViewModel(private val repository: Repository): ViewModel() {
    fun uploadImage(token: String, imageFile: File) = repository.uploadImage(token, imageFile)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}