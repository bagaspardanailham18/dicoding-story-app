package com.dicoding.intermediate.storyapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.intermediate.storyapp.data.remote.StoryRepository
import com.dicoding.intermediate.storyapp.di.ApiModule
import com.dicoding.intermediate.storyapp.ui.auth.AuthViewModel

class ViewModelFactory private constructor(private val storyRepository: StoryRepository): ViewModelProvider.NewInstanceFactory() {

//    companion object {
//        @Volatile
//        private var instance: ViewModelFactory? = null
//        fun getInstance(context: Context): ViewModelFactory =
//            instance ?: synchronized(this) {
//                instance ?: ViewModelFactory(ApiModule.provideRepository(context))
//            } .also { instance = it }
//    }
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
//            return AuthViewModel(storyRepository) as T
//        }
//
//        throw IllegalArgumentException("Unknown viewmodel class: ${modelClass.name}")
//    }

}