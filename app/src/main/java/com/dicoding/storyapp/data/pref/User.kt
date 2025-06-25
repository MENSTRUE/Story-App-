package com.dicoding.storyapp.data.pref

data class User(
    val token: String,
    val isLogin: Boolean,
    val userId: String,
    val name: String,
    val email: String
)
