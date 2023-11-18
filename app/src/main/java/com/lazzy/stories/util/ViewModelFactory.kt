package com.lazzy.stories.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lazzy.stories.ui.auth.login.LoginViewModel
import com.lazzy.stories.ui.auth.register.RegisterViewModel
import com.lazzy.stories.ui.main.MainViewModel
import com.lazzy.stories.ui.stories.CreateStoryViewModel

class ViewModelFactory(private val preferences: UserPreferences) : ViewModelProvider.NewInstanceFactory() {
    private lateinit var mApplication: Application

    fun setApplication(application: Application){
        mApplication = application
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(preferences) as T
        }
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel() as T
        }
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(preferences) as T
        }
        if (modelClass.isAssignableFrom(CreateStoryViewModel::class.java)) {
            return CreateStoryViewModel(preferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}