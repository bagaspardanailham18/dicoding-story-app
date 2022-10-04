package com.dicoding.intermediate.storyapp.data.remote

import com.dicoding.intermediate.storyapp.data.remote.response.FileUploadResponse
import com.dicoding.intermediate.storyapp.data.remote.response.GetAllStoriesResponse
import com.dicoding.intermediate.storyapp.data.remote.response.LoginResponse
import com.dicoding.intermediate.storyapp.data.remote.response.RegisterResponse
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Response<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ) : LoginResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ) : GetAllStoriesResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ) : FileUploadResponse

}