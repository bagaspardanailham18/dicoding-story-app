package com.dicoding.intermediate.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.intermediate.storyapp.data.local.PreferenceDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataStoreViewModel @Inject constructor(private val pref: PreferenceDataSource): ViewModel() {

    fun getAuthToken(): LiveData<String?> {
        return pref.getAuthToken()
    }

    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            pref.saveAuthToken(token)
        }
    }

    fun clearAuthToken() {
        viewModelScope.launch {
            pref.clearAuthToken()
        }
    }

}