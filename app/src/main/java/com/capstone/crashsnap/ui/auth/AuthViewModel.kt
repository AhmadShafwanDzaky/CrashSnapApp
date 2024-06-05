package com.capstone.crashsnap.ui.auth

import androidx.lifecycle.ViewModel
import com.capstone.crashsnap.data.Repository
import okhttp3.RequestBody

class AuthViewModel(private val repository: Repository): ViewModel() {

    fun signup(name: String, email: String, password: String) =
        repository.signup(name, email, password)

}