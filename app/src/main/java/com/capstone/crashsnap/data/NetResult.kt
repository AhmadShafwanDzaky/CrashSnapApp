package com.capstone.crashsnap.data

sealed class NetResult<out R> private constructor() {
    data class Success<out T>(val data: T) : NetResult<T>()
    data class Error(val error: String) : NetResult<Nothing>()
    object Loading : NetResult<Nothing>()
}