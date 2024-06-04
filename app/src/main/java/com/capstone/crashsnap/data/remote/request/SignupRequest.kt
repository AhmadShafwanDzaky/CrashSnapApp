package com.capstone.crashsnap.data.remote.request

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)