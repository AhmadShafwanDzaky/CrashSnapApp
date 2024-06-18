package com.capstone.crashsnap.ui.history.historydetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.crashsnap.data.Repository
import com.capstone.crashsnap.data.UserModel
import kotlinx.coroutines.launch

class HistoryDetailViewModel(private val repository: Repository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getHistoryDetail(token: String, id: String) = repository.getHistoryDetail(token, id)
    fun deleteHistoryId(token: String, id: String) = repository.deleteHistoryId(token, id)

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}