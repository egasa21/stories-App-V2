package com.lazzy.stories.ui.auth.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lazzy.stories.data.api.ApiConfig
import com.lazzy.stories.data.model.LoginRequest
import com.lazzy.stories.data.model.LoginResponse
import com.lazzy.stories.util.UserPreferences
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.lazzy.stories.util.Result

class LoginViewModel(private val preferences: UserPreferences) : ViewModel() {
    private val _login = MutableLiveData<Result<String>>()
    val login: LiveData<Result<String>> = _login

    fun loginPost(email: String, password: String) {
        _login.postValue(Result.Loading())
        val client = ApiConfig.getApiService().login(LoginRequest(email, password))
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.loginResult?.token
                    responseBody?.let { saveUser(it) }
                    _login.postValue(Result.Success(responseBody))
                } else {
                    _login.postValue(Result.Error(response.message().toString()))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("LoginViewModel", "onFailure: login")
                _login.postValue(Result.Error(t.message))
            }
        })
    }

    private fun saveUser(key: String) {
        viewModelScope.launch {
            preferences.saveUser(key)
        }
    }
}