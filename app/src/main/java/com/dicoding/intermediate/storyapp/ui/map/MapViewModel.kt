package com.dicoding.intermediate.storyapp.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.intermediate.storyapp.data.remote.StoryRepository
import com.dicoding.intermediate.storyapp.data.remote.response.GetAllStoriesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val storyRepository: StoryRepository): ViewModel() {

    fun getAllStoriesWithLocation(token: String): LiveData<com.dicoding.intermediate.storyapp.data.remote.Result<GetAllStoriesResponse>> =
        storyRepository.getAllStoriesWithLocation(token, 1, 5)

}