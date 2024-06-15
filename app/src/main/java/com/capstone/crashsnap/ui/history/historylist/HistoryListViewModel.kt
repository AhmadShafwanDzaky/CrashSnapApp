package com.capstone.crashsnap.ui.history.historylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.crashsnap.data.Repository
import com.capstone.crashsnap.data.UserModel

class HistoryListViewModel(private val repository: Repository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getAllHistory(token: String) = repository.getAllHistory(token)
}