package com.dicoding.intermediate.storyapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.intermediate.storyapp.data.remote.Result
import com.dicoding.intermediate.storyapp.data.remote.StoryRepository
import com.dicoding.intermediate.storyapp.data.remote.response.LoginResponse
import com.dicoding.intermediate.storyapp.data.remote.response.RegisterResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val storyRepository: StoryRepository): ViewModel() {

    fun registerUser(name: String, email: String, password: String) = storyRepository.registerUser(name, email, password)

    fun loginUser(email: String, password: String) = storyRepository.loginUser(email, password)

}