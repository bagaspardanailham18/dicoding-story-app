package com.dicoding.intermediate.storyapp.data

import com.dicoding.intermediate.storyapp.DataDummy
import com.dicoding.intermediate.storyapp.data.remote.ApiService
import com.dicoding.intermediate.storyapp.data.remote.response.FileUploadResponse
import com.dicoding.intermediate.storyapp.data.remote.response.GetAllStoriesResponse
import com.dicoding.intermediate.storyapp.data.remote.response.LoginResponse
import com.dicoding.intermediate.storyapp.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class FakeApiService : ApiService {

    private val dummyStoryResponse = DataDummy.generateDummyStoryWithLocationResponse()
    private val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()
    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()
    private val dummyAddNewStoryResponse = DataDummy.generateDummyAddNewStoryResponse()

    override suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): RegisterResponse {
        return dummyRegisterResponse
    }

    override suspend fun loginUser(email: String, password: String): LoginResponse {
        return dummyLoginResponse
    }

    override suspend fun getAllStories(
        token: String,
        page: Int?,
        size: Int?,
        location: Int?
    ): GetAllStoriesResponse {
        return dummyStoryResponse
    }

    override suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Float?,
        lon: Float?
    ): FileUploadResponse {
        return dummyAddNewStoryResponse
    }
}