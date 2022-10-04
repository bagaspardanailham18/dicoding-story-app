package com.dicoding.intermediate.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.intermediate.storyapp.data.remote.StoryRepository
import com.dicoding.intermediate.storyapp.data.remote.response.GetAllStoriesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val storyRepository: StoryRepository) : ViewModel() {

    fun clearAuthToken() {
        viewModelScope.launch {
            storyRepository.clearAuthToken()
        }
    }

    fun getAuthToken(): LiveData<String> {
        val result  = MutableLiveData<String>()
        viewModelScope.launch {
            storyRepository.getAuthToken().collect {
                result.postValue(it)
            }
        }
        return result
    }

    suspend fun getAllStories(token: String): Flow<Result<GetAllStoriesResponse>> =
        storyRepository.getAllStories(token, null, null)

}