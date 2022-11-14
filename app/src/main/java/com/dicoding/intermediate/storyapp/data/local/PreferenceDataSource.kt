package com.dicoding.intermediate.storyapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferenceDataSource constructor(private val dataStore: DataStore<Preferences>) {

    fun getAuthToken(): LiveData<String?> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }.asLiveData()
    }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun clearAuthToken() {
        dataStore.edit {
//            for removing spesific item
//            it.remove(TOKEN_KEY)
            it.clear()
        }
    }

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token_data")
    }

}