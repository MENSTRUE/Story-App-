package com.dicoding.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import androidx.paging.map
import com.dicoding.storyapp.data.local.room.StoryDatabase
import com.dicoding.storyapp.data.pref.User
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.remote.mediator.StoryRemoteMediator
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.response.LoginResult
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val storyDatabase: StoryDatabase
) {

    suspend fun login(email: String, password: String): Flow<Result<LoginResult>> {
        return flow {
            emit(Result.Loading)
            try {
                val successResponse = apiService.login(email, password)
                if (!successResponse.error) {
                    val loginResult = successResponse.loginResult

                    userPreference.saveUser(
                        User(
                            token = loginResult.token,
                            isLogin = true,
                            userId = loginResult.userId,
                            name = loginResult.name,
                            email = email
                        )
                    )
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
        return flow {
            emit(Result.Loading)
            try {
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

    fun getSession(): Flow<User> {
        return userPreference.getUser()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStoriesPaging(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData
            .map { pagingData ->
                pagingData.map { entity ->
                    ListStoryItem(
                        id = entity.id,
                        name = entity.name,
                        description = entity.description,
                        photoUrl = entity.photoUrl,
                        createdAt = entity.createdAt,
                        lat = entity.lat,
                        lon = entity.lon
                    )
                }
            }
    }

    suspend fun getStoriesWithLocation(token: String): Flow<Result<List<ListStoryItem>>> {
        return flow {
            emit(Result.Loading)
            try {
                val successResponse = apiService.getStoriesWithLocation("Bearer $token")
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
        return flow {
            emit(Result.Loading)
            try {
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())

                val latRequestBody = lat?.toString()?.toRequestBody("text/plain".toMediaType())
                val lonRequestBody = lon?.toString()?.toRequestBody("text/plain".toMediaType())

                val successResponse = apiService.uploadStory(
                    "Bearer $token",
                    multipartBody,
                    descriptionRequestBody,
                    latRequestBody,
                    lonRequestBody
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


    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
            storyDatabase: StoryDatabase
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference, storyDatabase).also { instance = it }
            }
    }
}