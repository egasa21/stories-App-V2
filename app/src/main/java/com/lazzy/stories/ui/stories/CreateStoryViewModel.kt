package com.lazzy.stories.ui.stories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lazzy.stories.data.api.ApiConfig
import com.lazzy.stories.data.model.StoriesResponse
import com.lazzy.stories.util.UserPreferences
import com.lazzy.stories.util.Result
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateStoryViewModel(private val preferences: UserPreferences) : ViewModel() {
    private val _uploadStories = MutableLiveData<Result<String>>()
    val uploadStories : LiveData<Result<String>> = _uploadStories

    companion object {
        private const val TAG = "AddStoriesViewModel"
    }

    suspend fun upload(
        imageMultipart: MultipartBody.Part,
        description: RequestBody,
        lat: Double,
        lon: Double
    ){
        val service = ApiConfig.getApiService()
            .addStories(token = "Bearer ${preferences.getUserKey().first()}",
                imageMultipart, description, lat, lon)

        service.enqueue(object : Callback<StoriesResponse>{
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                val responseBody = response.body()?.message
                if(response.isSuccessful && responseBody != null){
                    _uploadStories.postValue(Result.Error(responseBody))
                }else{
                    _uploadStories.postValue(Result.Error(response.message().toString()))
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                Log.d(TAG, "onFailure: Test")
                _uploadStories.postValue(Result.Error(t.message))
            }
        })
    }
}