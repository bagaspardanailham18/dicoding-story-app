package com.dicoding.intermediate.storyapp.ui.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.intermediate.storyapp.data.remote.Result
import com.dicoding.intermediate.storyapp.data.remote.StoryRepository
import com.dicoding.intermediate.storyapp.data.remote.response.FileUploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(private val storyRepository: StoryRepository) : ViewModel() {

    suspend fun uploadStory(token: String, file: MultipartBody.Part, description: RequestBody) : Flow<kotlin.Result<FileUploadResponse>> =
        storyRepository.uploadStories(token, file, description)

    fun getAuthToken(): LiveData<String> {
        val result  = MutableLiveData<String>()
        viewModelScope.launch {
            storyRepository.getAuthToken().collect {
                result.postValue(it)
            }
        }
        return result
    }

}