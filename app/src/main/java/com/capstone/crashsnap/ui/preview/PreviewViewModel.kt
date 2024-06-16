package com.capstone.crashsnap.ui.preview

import androidx.lifecycle.ViewModel
import com.capstone.crashsnap.data.Repository
import java.io.File

class PreviewViewModel(private val repository: Repository): ViewModel() {
    fun uploadImage(imageFile: File) = repository.uploadImage(imageFile)
}