package com.dicoding.intermediate.storyapp.data.remote

import android.util.Log
import com.dicoding.intermediate.storyapp.data.local.PreferenceDataSource
import com.dicoding.intermediate.storyapp.data.remote.response.FileUploadResponse
import com.dicoding.intermediate.storyapp.data.remote.response.GetAllStoriesResponse
import com.dicoding.intermediate.storyapp.data.remote.response.LoginResponse
import com.dicoding.intermediate.storyapp.data.remote.response.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(private val apiService: ApiService, private val preferenceDataSource: PreferenceDataSource) {
    suspend fun registerUser(name: String, email: String, password: String): Flow<Result<Response<RegisterResponse>>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.registerUser(name, email, password)
            if (response.code() == 201) {
                emit(Result.Success(response))
            } else if (response.code() == 400) {
                val errorBody = JSONObject(response.body()!!.message)
                emit(Result.Error(errorBody.toString()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun loginUser(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.loginUser(email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun saveAuthToken(token: String) {
        preferenceDataSource.saveAuthToken(token)
    }

    fun getAuthToken(): Flow<String?> = preferenceDataSource.getAuthToken()

    suspend fun clearAuthToken() {
        preferenceDataSource.clearAuthToken()
    }

    suspend fun getAllStories(token: String, page: Int? = null, size: Int? = null): Flow<kotlin.Result<GetAllStoriesResponse>> = flow {
        try {
            val bearerToken = generateBearerToken(token)
            Log.d("tokens", token)
            val response = apiService.getAllStories(bearerToken, page, size)
            emit(kotlin.Result.success(response))
        } catch (e : Exception) {
            emit(kotlin.Result.failure(e))
            Log.d("tokens", e.message.toString())
        }
    }.flowOn(Dispatchers.IO)

    suspend fun uploadStories(token: String, file: MultipartBody.Part, description: RequestBody): Flow<kotlin.Result<FileUploadResponse>> = flow {
        try {
            val bearerToken = generateBearerToken(token)
            Log.d("tokens", token)
            val response = apiService.uploadStory(bearerToken, file, description)
            emit(kotlin.Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(kotlin.Result.failure(e))
        }
    }

    private fun generateBearerToken(token: String): String {
        return "Bearer $token"
    }
}