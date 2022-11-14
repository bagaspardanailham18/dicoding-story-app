package com.dicoding.intermediate.storyapp.ui.addstory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.intermediate.storyapp.DataDummy
import com.dicoding.intermediate.storyapp.MainDispatcherRule
import com.dicoding.intermediate.storyapp.data.remote.StoryRepository
import com.dicoding.intermediate.storyapp.data.remote.response.FileUploadResponse
import com.dicoding.intermediate.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var addStoryViewModel: AddStoryViewModel

    @Mock
    private var mockFile = File("filename")

    @Before
    fun setUp() {
        addStoryViewModel = AddStoryViewModel(storyRepository)
    }

    @Test
    fun `when addStory Should Not Null and Return Success`() = runTest {
        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            mockFile.name,
            requestImageFile
        )
        val descriptionRequestBody = "description testing".toRequestBody("text/plain".toMediaType())
        val expectedStories = MutableLiveData<com.dicoding.intermediate.storyapp.data.remote.Result<FileUploadResponse>>()
        expectedStories.value = com.dicoding.intermediate.storyapp.data.remote.Result.Success(DataDummy.generateDummyAddNewStoryResponse())

        `when`(storyRepository.uploadStories(
            TOKEN,
            imageMultipart,
            descriptionRequestBody,
            null,
            null
        )).thenReturn(expectedStories)

        val actual = addStoryViewModel.uploadStory(TOKEN, imageMultipart, descriptionRequestBody, null, null).getOrAwaitValue()
        verify(storyRepository).uploadStories(TOKEN, imageMultipart, descriptionRequestBody, null, null)
        assertNotNull(actual)
        assertTrue(actual is com.dicoding.intermediate.storyapp.data.remote.Result.Success)
    }

    @Test
    fun `when addStory Network Error Should Return Error`() = runTest {
        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            mockFile.name,
            requestImageFile
        )
        val descriptionRequestBody = "description testing".toRequestBody("text/plain".toMediaType())
        val expectedStories = MutableLiveData<com.dicoding.intermediate.storyapp.data.remote.Result<FileUploadResponse>>()
        expectedStories.value = com.dicoding.intermediate.storyapp.data.remote.Result.Error("Error")

        `when`(storyRepository.uploadStories(
            TOKEN,
            imageMultipart,
            descriptionRequestBody,
            null,
            null
        )).thenReturn(expectedStories)

        val actual = addStoryViewModel.uploadStory(TOKEN, imageMultipart, descriptionRequestBody, null, null).getOrAwaitValue()
        verify(storyRepository).uploadStories(TOKEN, imageMultipart, descriptionRequestBody, null, null)
        assertNotNull(actual)
        assertTrue(actual is com.dicoding.intermediate.storyapp.data.remote.Result.Error)
    }

    companion object {
        private const val TOKEN = "Bearer token"
    }

}