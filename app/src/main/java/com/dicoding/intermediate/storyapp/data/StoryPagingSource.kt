package com.dicoding.intermediate.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.intermediate.storyapp.data.remote.ApiService
import com.dicoding.intermediate.storyapp.data.remote.response.ListStoryItem

class StoryPagingSource(private val apiService: ApiService, private val token: String): PagingSource<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getAllStories(token, page, params.loadSize, 0)
            val listStory = responseData.listStory

            LoadResult.Page(
                data = listStory,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (listStory.isNullOrEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}