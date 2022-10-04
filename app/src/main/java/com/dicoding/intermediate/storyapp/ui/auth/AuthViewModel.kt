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

    fun registerUser(name: String, email: String, password: String): LiveData<Result<Response<RegisterResponse>>> {
        val result = MutableLiveData<Result<Response<RegisterResponse>>>()
        viewModelScope.launch {
            storyRepository.registerUser(name, email, password).collect {
                result.postValue(it)
            }
        }
        return result
    }

    fun loginUser(email: String, password: String): LiveData<Result<LoginResponse>> {
        val result = MutableLiveData<Result<LoginResponse>>()
        viewModelScope.launch {
            storyRepository.loginUser(email, password).collect {
                result.postValue(it)
            }
        }
        return result
    }


    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            storyRepository.saveAuthToken(token)
        }
    }

}