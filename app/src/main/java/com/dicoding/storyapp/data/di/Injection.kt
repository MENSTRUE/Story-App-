package com.dicoding.storyapp.data.di

import android.content.Context
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.local.room.StoryDatabase
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.pref.dataStore
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val database = StoryDatabase.getDatabase(context)

        val apiService = ApiConfig.getApiService()

        val pref = UserPreference.getInstance(context.dataStore)

        return UserRepository.getInstance(apiService, pref, database)
    }
}