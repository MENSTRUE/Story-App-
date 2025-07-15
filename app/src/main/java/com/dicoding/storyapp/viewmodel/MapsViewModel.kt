package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.pref.User
import com.dicoding.storyapp.data.remote.response.ListStoryItem

class MapsViewModel(private val repository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<User> {
        return repository.getSession().asLiveData()
    }

    fun getStoriesWithLocation(token: String): LiveData<Result<List<ListStoryItem>>> = liveData {
        repository.getStoriesWithLocation(token)
            .collect { result ->
                emit(result)
            }
    }
}