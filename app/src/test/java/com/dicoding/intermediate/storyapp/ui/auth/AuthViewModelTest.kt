package com.dicoding.intermediate.storyapp.ui.auth

import android.provider.Telephony
import android.provider.Telephony.Carriers.PASSWORD
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.dicoding.intermediate.storyapp.DataDummy
import com.dicoding.intermediate.storyapp.MainDispatcherRule
import com.dicoding.intermediate.storyapp.data.remote.Result
import com.dicoding.intermediate.storyapp.data.remote.StoryRepository
import com.dicoding.intermediate.storyapp.data.remote.StoryRepositoryTest
import com.dicoding.intermediate.storyapp.data.remote.response.LoginResponse
import com.dicoding.intermediate.storyapp.data.remote.response.RegisterResponse
import com.dicoding.intermediate.storyapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
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
class AuthViewModelTest {

    @get:Rule
    var instanceExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var authViewModel: AuthViewModel
    private val dummySignUp = DataDummy.generateDummyRegisterResponse()
    private val dummyLogin = DataDummy.generateDummyLoginResponse()

    @Before
    fun setUp() {
        authViewModel = AuthViewModel(storyRepository)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when SignUp Should Not Null and Return Success`() {
        val expectedUser = MutableLiveData<Result<RegisterResponse>>()
        expectedUser.value = Result.Success(dummySignUp)

        `when`(storyRepository.registerUser(NAME, EMAIL, PASSWORD)).thenReturn(expectedUser)

        val actualUser = authViewModel.registerUser(NAME, EMAIL, PASSWORD).getOrAwaitValue()

        verify(storyRepository).registerUser(NAME, EMAIL, PASSWORD)
        assertNotNull(actualUser)
        assertTrue(actualUser is Result.Success)
    }

    @Test
    fun `when SignUp Network Error Should Return Error`() {
        val expectedUser = MutableLiveData<Result<RegisterResponse>>()
        expectedUser.value = Result.Error("Error")

        `when`(storyRepository.registerUser(NAME, EMAIL, PASSWORD)).thenReturn(expectedUser)

        val actualUser = authViewModel.registerUser(NAME, EMAIL, PASSWORD).getOrAwaitValue()

        verify(storyRepository).registerUser(NAME, EMAIL, PASSWORD)
        assertTrue(actualUser is Result.Error)
    }

    @Test
    fun `when Login Should Not Null and Return Success`() = runTest {
        val expectedUser = MutableLiveData<Result<LoginResponse>>()
        expectedUser.value = Result.Success(dummyLogin)

        `when`(storyRepository.loginUser(EMAIL, PASSWORD)).thenReturn(expectedUser)

        val actualUser = authViewModel.loginUser(EMAIL, PASSWORD).getOrAwaitValue()

        verify(storyRepository).loginUser(EMAIL, PASSWORD)
        assertNotNull(actualUser)
        assertTrue(actualUser is Result.Success)
    }

    @Test
    fun `when Login Network Error Should Return Error`() = runTest {
        val expectedUser = MutableLiveData<Result<LoginResponse>>()
        expectedUser.value = Result.Error("Error")

        `when`(storyRepository.loginUser(EMAIL, PASSWORD)).thenReturn(expectedUser)

        val actualUser = authViewModel.loginUser(EMAIL, PASSWORD).getOrAwaitValue()

        verify(storyRepository).loginUser(EMAIL, PASSWORD)
        assertTrue(actualUser is Result.Error)
    }

    companion object {
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val PASSWORD = "password"
    }

}