package com.capstone.crashsnap.data

data class UserModel(
    val name: String,
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)