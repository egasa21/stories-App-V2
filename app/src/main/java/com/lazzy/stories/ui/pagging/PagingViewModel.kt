package com.lazzy.stories.ui.pagging

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.lazzy.stories.data.model.ListStoryItem
import com.lazzy.stories.util.UserPreferences

class PagingViewModel : ViewModel(){
    fun getPagingStories(preferences: UserPreferences) : LiveData<PagingData<ListStoryItem>> = Pager(
        config = PagingConfig(
            pageSize = 5
        ),
        pagingSourceFactory = {
            StoryPagingSource(preferences)
        }
    ).liveData.cachedIn(viewModelScope)

    class ViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PagingViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return PagingViewModel() as T
            }else throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}