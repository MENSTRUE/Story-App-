package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.pref.User
import com.dicoding.storyapp.data.remote.response.LoginResult
import com.dicoding.storyapp.utils.EspressoIdlingResource
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResult>>()
    val loginResult: LiveData<Result<LoginResult>> = _loginResult

    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    fun getSession(): LiveData<User> {
        return repository.getSession().asLiveData()
    }

    fun login(email: String, password: String) {
        EspressoIdlingResource.increment()
        viewModelScope.launch {
            repository.login(email, password).collect { result ->
                _loginResult.value = result

                when (result) {
                    is Result.Loading -> {}
                    is Result.Success -> {
                        EspressoIdlingResource.decrement()
                    }
                    is Result.Error -> {
                        EspressoIdlingResource.decrement()
                    }
                }
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            repository.register(name, email, password).collect { result ->
                _registerResult.value = result
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}