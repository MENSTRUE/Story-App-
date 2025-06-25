package com.dicoding.storyapp.data


import com.dicoding.storyapp.data.pref.User
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.response.LoginResult
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class UserRepository private constructor(
    private val userPreference: UserPreference
) {


    suspend fun login(email: String, password: String): Flow<Result<LoginResult>> {
        return kotlinx.coroutines.flow.flow {
            emit(Result.Loading)
            try {

                val apiService = ApiConfig.getApiService()
                val successResponse = apiService.login(email, password)
                if (!successResponse.error) {
                    val loginResult = successResponse.loginResult
                    userPreference.saveUser(User(loginResult.token, true, loginResult.userId, loginResult.name, email))
                    emit(Result.Success(loginResult))
                } else {
                    emit(Result.Error(successResponse.message))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }

    suspend fun register(name: String, email: String, password: String): Flow<Result<String>> {
        return kotlinx.coroutines.flow.flow {
            emit(Result.Loading)
            try {
                val apiService = ApiConfig.getApiService()
                val successResponse = apiService.register(name, email, password)
                if (!successResponse.error) {
                    emit(Result.Success(successResponse.message))
                } else {
                    emit(Result.Error(successResponse.message))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }

    suspend fun getStories(token: String): Flow<Result<List<ListStoryItem>>> {
        return kotlinx.coroutines.flow.flow {
            emit(Result.Loading)
            try {
                val apiService = ApiConfig.getApiService(token)
                val successResponse = apiService.getStories()
                if (!successResponse.error) {
                    emit(Result.Success(successResponse.listStory))
                } else {
                    emit(Result.Error(successResponse.message))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }

    suspend fun uploadStory(
        token: String,
        file: File,
        description: String,
        lat: Double? = null,
        lon: Double? = null
    ): Flow<Result<String>> {
        return kotlinx.coroutines.flow.flow {
            emit(Result.Loading)
            try {
                val apiService = ApiConfig.getApiService(token)

                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())

                val successResponse = apiService.uploadStory(
                    multipartBody,
                    descriptionRequestBody,
                    lat?.toString()?.toRequestBody("text/plain".toMediaType()),
                    lon?.toString()?.toRequestBody("text/plain".toMediaType())
                )

                if (!successResponse.error) {
                    emit(Result.Success(successResponse.message))
                } else {
                    emit(Result.Error(successResponse.message))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }


    fun getSession(): Flow<User> {
        return userPreference.getUser()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference).also { instance = it }
            }
    }
}
