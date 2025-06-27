package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.utils.Event
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _storyList = MutableLiveData<Result<List<ListStoryItem>>>()
    val storyList: LiveData<Result<List<ListStoryItem>>> = _storyList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    fun getStories(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getStories(token).collect { result ->
                _storyList.value = result

                when (result) {
                    is Result.Loading -> _isLoading.value = true
                    is Result.Success -> {
                        _isLoading.value = false
                        _toastText.value = Event("Data berhasil dimuat!")
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