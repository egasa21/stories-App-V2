package com.lazzy.stories.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val preferences: UserPreferences) : ViewModelProvider.NewInstanceFactory() {
    private lateinit var mApplication: Application

    fun setApplication(application: Application){
        mApplication = application
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return super.create(modelClass)
    }
}