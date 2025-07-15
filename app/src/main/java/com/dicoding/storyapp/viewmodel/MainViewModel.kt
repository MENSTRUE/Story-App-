package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.pref.User
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<User> {
        return repository.getSession().asLiveData()
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return repository.getStoriesPaging(token).cachedIn(viewModelScope)
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}