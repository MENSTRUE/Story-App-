package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.Result
import kotlinx.coroutines.launch
import java.io.File

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uploadResult = MutableLiveData<Result<String>>()
    val uploadResult: LiveData<Result<String>> = _uploadResult

    fun uploadStory(token: String, file: File, description: String, lat: Double?, lon: Double?) {
        viewModelScope.launch {
            repository.uploadStory(token, file, description, lat, lon).collect { result ->
                _uploadResult.value = result
            }
        }
    }
}
