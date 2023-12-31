package com.lazzy.stories.ui.pagging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lazzy.stories.data.api.ApiConfig
import com.lazzy.stories.data.model.ListStoryItem
import com.lazzy.stories.util.UserPreferences
import kotlinx.coroutines.flow.first

class StoryPagingSource(private val preferences: UserPreferences) : PagingSource<Int, ListStoryItem>() {

    private companion object{
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
            val responseData = ApiConfig.getApiService()
                .getStoriesPage("Bearer ${preferences.getUserKey().first()}", page, params.loadSize)

            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page -1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else page + 1
            )
        }catch (exception : Exception){
            return LoadResult.Error(exception)
        }
    }
}