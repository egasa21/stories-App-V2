package com.lazzy.stories.ui.auth.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lazzy.stories.data.api.ApiConfig
import com.lazzy.stories.data.model.RegisterRequest
import com.lazzy.stories.data.model.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.lazzy.stories.util.Result

class RegisterViewModel: ViewModel() {
    private val _register = MutableLiveData<Result<String>>()
    val register: LiveData<Result<String>> = _register

    fun registerPost(name: String, email: String, password: String) {
        _register.postValue(Result.Loading())
        val client = ApiConfig.getApiService().register(RegisterRequest(name, email, password))

        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>,
            ) {
                val responseBody = response.body()?.message
                if (response.isSuccessful && responseBody != null) {
                    _register.postValue(Result.Success(responseBody))
                } else {
                    _register.postValue(Result.Error(response.message().toString()))
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.d("RegisterViewModel", "onFailure: Register")
                _register.postValue(Result.Error(t.message))
            }
        })
    }

}