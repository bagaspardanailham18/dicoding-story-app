package com.dicoding.intermediate.storyapp.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.intermediate.storyapp.data.remote.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(private val storyRepository: StoryRepository) : ViewModel() {

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