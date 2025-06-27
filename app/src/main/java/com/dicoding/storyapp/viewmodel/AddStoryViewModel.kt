package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.utils.Event
import kotlinx.coroutines.launch
import java.io.File

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {

    private val _addStoryResult = MutableLiveData<Result<String>>()
    val addStoryResult: LiveData<Result<String>> = _addStoryResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    fun addStory(token: String, imageFile: File, description: String, lat: Double? = null, lon: Double? = null) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.uploadStory(token, imageFile, description, lat, lon).collect { result ->
                _addStoryResult.value = result

                when (result) {
                    is Result.Loading -> _isLoading.value = true
                    is Result.Success -> {
                        _isLoading.value = false
                        _toastText.value = Event(result.data)
                    }
                    is Result.Error -> {
                        _isLoading.value = false
                        _toastText.value = Event(result.error)
                    }
                }
            }
        }
    }
}