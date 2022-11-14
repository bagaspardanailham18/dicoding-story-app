package com.dicoding.intermediate.storyapp.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.dicoding.intermediate.storyapp.data.StoryPagingSource
import com.dicoding.intermediate.storyapp.data.StoryRemoteMediator
import com.dicoding.intermediate.storyapp.data.local.PreferenceDataSource
import com.dicoding.intermediate.storyapp.data.local.database.StoryDatabase
import com.dicoding.intermediate.storyapp.data.remote.response.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(private val storyDatabase: StoryDatabase, private val apiService: ApiService, private val preferenceDataSource: PreferenceDataSource) {
    fun registerUser(name: String, email: String, password: String): LiveData<com.dicoding.intermediate.storyapp.data.remote.Result<RegisterResponse>> = liveData {
        try {
            val response = apiService.registerUser(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    fun loginUser(email: String, password: String): LiveData<com.dicoding.intermediate.storyapp.data.remote.Result<LoginResponse>> = liveData {
        try {
            val response = apiService.loginUser(email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getAllStories(token: String): LiveData<PagingData<ListStoryItem>>  {
        val bearerToken = generateBearerToken(token)
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 2
            ),
            remoteMediator = StoryRemoteMediator(bearerToken, storyDatabase, apiService),
            pagingSourceFactory = {
                // StoryPagingSource(apiService, bearerToken)
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getAllStoriesWithLocation(token: String, page: Int? = null, size: Int? = null): LiveData<com.dicoding.intermediate.storyapp.data.remote.Result<GetAllStoriesResponse>> = liveData {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiService.getAllStories(bearerToken, page, size)
            emit(Result.Success(response))
        } catch (e : Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun uploadStories(token: String, file: MultipartBody.Part, description: RequestBody, lat: Float?, lon: Float?): LiveData<com.dicoding.intermediate.storyapp.data.remote.Result<FileUploadResponse>> = liveData {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiService.uploadStory(bearerToken, file, description, lat, lon)
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    private fun generateBearerToken(token: String): String {
        return "Bearer $token"
    }
}