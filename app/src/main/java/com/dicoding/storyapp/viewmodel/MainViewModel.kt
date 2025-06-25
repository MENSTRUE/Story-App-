package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _storyList = MutableLiveData<Result<List<ListStoryItem>>>()
    val storyList: LiveData<Result<List<ListStoryItem>>> = _storyList

    fun getStories(token: String) {
        viewModelScope.launch {
            repository.getStories(token).collect { result ->
                _storyList.value = result
            }
        }
    }
}
