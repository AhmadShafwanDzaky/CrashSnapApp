package com.capstone.crashsnap.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.crashsnap.data.Repository
import com.capstone.crashsnap.data.UserModel
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: Repository): ViewModel() {

    fun signup(name: String, email: String, password: String) =
        repository.signup(name, email, password)
    fun login(email: String, password: String) = repository.login(email, password)

    fun saveSession(token: String, name: String, email: String) {
        viewModelScope.launch {
            repository.saveSession(token, name, email)
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}