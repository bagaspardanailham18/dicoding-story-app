package com.dicoding.intermediate.storyapp.ui.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.dicoding.intermediate.storyapp.DataDummy
import com.dicoding.intermediate.storyapp.MainDispatcherRule
import com.dicoding.intermediate.storyapp.data.FakeApiService
import com.dicoding.intermediate.storyapp.data.remote.StoryRepository
import com.dicoding.intermediate.storyapp.data.remote.response.GetAllStoriesResponse
import com.dicoding.intermediate.storyapp.data.remote.response.ListStoryItem
import com.dicoding.intermediate.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.Flow

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var stortRepository: StoryRepository
    private lateinit var mapViewModel: MapViewModel

    @Before
    fun setUp() {
        mapViewModel = MapViewModel(stortRepository)
    }

    @Test
    fun `when getAllStoriesWithLocation Should Not Null and Return Success`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryWithLocationResponse()
        val expectedStories = MutableLiveData<com.dicoding.intermediate.storyapp.data.remote.Result<GetAllStoriesResponse>>()
        expectedStories.value = com.dicoding.intermediate.storyapp.data.remote.Result.Success(dummyStory)

        `when`(stortRepository.getAllStoriesWithLocation(TOKEN, 1, 5)).thenReturn(expectedStories)

        val actual = mapViewModel.getAllStoriesWithLocation(TOKEN).getOrAwaitValue()
        verify(stortRepository).getAllStoriesWithLocation(TOKEN, 1, 5)
        assertNotNull(actual)
        assertTrue(actual is com.dicoding.intermediate.storyapp.data.remote.Result.Success)
        assertEquals(dummyStory.listStory, (actual as com.dicoding.intermediate.storyapp.data.remote.Result.Success).data.listStory)
        assertEquals(dummyStory.listStory.size, (actual as com.dicoding.intermediate.storyapp.data.remote.Result.Success).data.listStory.size)
    }

    companion object {
        private const val TOKEN = "Bearer token"
    }

}