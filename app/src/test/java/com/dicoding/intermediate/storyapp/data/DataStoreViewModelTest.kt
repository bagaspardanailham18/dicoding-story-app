package com.dicoding.intermediate.storyapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.intermediate.storyapp.MainDispatcherRule
import com.dicoding.intermediate.storyapp.data.local.PreferenceDataSource
import com.dicoding.intermediate.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DataStoreViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var pref: PreferenceDataSource

    private lateinit var dataStoreViewModel: DataStoreViewModel

    @Before
    fun setUp() {
        dataStoreViewModel = DataStoreViewModel(pref)
    }

    @Test
    fun `when Get Auth Token Return The Right Data And Not Null`() = runTest {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = TOKEN
        `when`(pref.getAuthToken()).thenReturn(expectedToken)

        val actual = dataStoreViewModel.getAuthToken().getOrAwaitValue()

        verify(pref).getAuthToken()
        assertNotNull(actual)
        assertEquals(expectedToken.value, actual)
    }

    @Test
    fun `verify save auth token is working`() = runTest {
        dataStoreViewModel.saveAuthToken("token")
        verify(pref).saveAuthToken("token")
    }

    @Test
    fun `verify clear auth token is working`() = runTest {
        dataStoreViewModel.clearAuthToken()
        verify(pref).clearAuthToken()
    }

    companion object {
        private const val TOKEN = "Bearer token"
    }
}