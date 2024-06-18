package com.capstone.crashsnap.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.crashsnap.data.Repository
import com.capstone.crashsnap.data.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getHistory(token: String) = repository.getHistory(token)
}