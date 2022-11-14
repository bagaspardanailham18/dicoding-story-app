package com.dicoding.intermediate.storyapp.data.remote

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.dicoding.intermediate.storyapp.DataDummy
import com.dicoding.intermediate.storyapp.MainDispatcherRule
import com.dicoding.intermediate.storyapp.data.FakeApiService
import com.dicoding.intermediate.storyapp.data.local.PreferenceDataSource
import com.dicoding.intermediate.storyapp.data.local.database.StoryDatabase
import com.dicoding.intermediate.storyapp.data.remote.Result
import com.dicoding.intermediate.storyapp.data.remote.response.*
import com.dicoding.intermediate.storyapp.getOrAwaitValue
import com.dicoding.intermediate.storyapp.ui.auth.AuthActivity
import com.dicoding.intermediate.storyapp.ui.main.ListStoryAdapter
import com.dicoding.intermediate.storyapp.ui.main.MainActivity
import com.dicoding.intermediate.storyapp.ui.main.StoryPagingSource
import com.dicoding.intermediate.storyapp.ui.main.noopListUpdateCallback
import com.dicoding.intermediate.storyapp.ui.map.MapViewModelTest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
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
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

 private const val TEST_DATASTORE_NAME: String = "application"

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var storyDatabase: StoryDatabase

    @Mock
    private lateinit var preferenceDataSource: PreferenceDataSource

    private val context = mock(Context::class.java)
    //private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "application")

    private val testCoroutineDispatcher: TestCoroutineDispatcher =
        TestCoroutineDispatcher()
    private val testCoroutineScope =
        TestCoroutineScope(testCoroutineDispatcher + Job())
    private val testDataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = testCoroutineScope,
            produceFile = { context.preferencesDataStoreFile(TEST_DATASTORE_NAME) }
        )

    private val testKey: Preferences.Key<String> = stringPreferencesKey("token_data")

    @Mock
    private var mockFile = File("filename")

    @Before
    fun setUp() {
        apiService = FakeApiService()
        storyDatabase = Room.inMemoryDatabaseBuilder(context, StoryDatabase::class.java).build()
        preferenceDataSource = PreferenceDataSource(testDataStore)
        storyRepository = StoryRepository(storyDatabase, apiService, preferenceDataSource)
    }

    @Test
    fun `when registerUser is Success`() = runTest {
        val expected = DataDummy.generateDummyRegisterResponse()
        val actual = storyRepository.registerUser("name", "email@gmail.com", "email123").getOrAwaitValue()

        assertNotNull(actual)
        assertEquals(expected.message, (actual as Result.Success).data.message)

    }

    @Test
    fun `when loginUser is Success`() = runTest {
        val expected = DataDummy.generateDummyLoginResponse()
        val actual = storyRepository.loginUser("email@gmail.com", "email123").getOrAwaitValue()

        assertNotNull(actual)
        assertEquals(expected.message, (actual as Result.Success).data.message)
    }

    @ExperimentalPagingApi
    @Test
    fun `when getAllStories Success`() = runTest {
        val data = StoryPagingSource.snapshot(DataDummy.generateDummyStoryResponse())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data

        CoroutineScope(Dispatchers.IO).launch {
            val actualData: PagingData<ListStoryItem> = storyRepository.getAllStories(TOKEN).getOrAwaitValue()
            val differ = AsyncPagingDataDiffer(
                diffCallback = ListStoryAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                workerDispatcher = Dispatchers.Main
            )
            differ.submitData(actualData)

            assertNotNull(differ.snapshot())
            assertEquals(DataDummy.generateDummyStoryResponse().size, differ.snapshot().size)
            assertEquals(DataDummy.generateDummyStoryResponse()[0].name, differ.snapshot()[0]?.name)
        }
    }

    @Test
    fun `when getAllStoriesWithLocation Success`() = runTest {
        val dummyStories = DataDummy.generateDummyStoryWithLocationResponse()
        val expectedStories = MutableLiveData<Result<GetAllStoriesResponse>>()
        expectedStories.value = Result.Success(dummyStories)

        val actualStory = storyRepository.getAllStoriesWithLocation(TOKEN, null, null).getOrAwaitValue()

        assertNotNull(actualStory)
        assertEquals(dummyStories.listStory.size, (actualStory as Result.Success).data.listStory.size)
    }

    @Test
    fun `when uploadStories Success`() = runTest {
        val dummyUploadResponse = DataDummy.generateDummyAddNewStoryResponse()
        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part =
            MultipartBody.Part.createFormData(
                "photo",
                mockFile.name,
                requestImageFile
            )

        val descriptionRequestBody = "description testing".toRequestBody("text/plain".toMediaType())

        val expected = MutableLiveData<Result<FileUploadResponse>>()
        expected.value = Result.Success(dummyUploadResponse)

        val actual = storyRepository.uploadStories(
            TOKEN,
            imageMultipart,
            descriptionRequestBody,
            null,
            null
        ).getOrAwaitValue()

        assertNotNull(actual)
        assertTrue(actual is Result.Success)
        assertEquals((expected.value as Result.Success).data.message, (actual as Result.Success).data.message)
    }

    class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {

        companion object {
            fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int? {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): PagingSource.LoadResult<Int, LiveData<List<ListStoryItem>>> {
            return PagingSource.LoadResult.Page(emptyList(), 0, 1)
        }

    }

    val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    companion object {
        private const val TOKEN = "Bearer token"
    }

}