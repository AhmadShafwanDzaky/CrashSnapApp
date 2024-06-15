package com.capstone.crashsnap.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.crashsnap.data.Repository
import com.capstone.crashsnap.data.UserModel
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: Repository) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}