package com.dicoding.storyapp.data.remote.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(token: String? = null): ApiService {
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .apply {
                    if (token != null) {
                        val authInterceptor = Interceptor { chain ->
                            val req = chain.request()
                            val requestHeaders = req.newBuilder()
                                .addHeader("Authorization", "Bearer $token")
                                .build()
                            chain.proceed(requestHeaders)
                        }
                        addInterceptor(authInterceptor)
                    }
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://story-api.dicoding.dev/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}