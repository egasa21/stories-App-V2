package com.lazzy.stories.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lazzy.stories.R
import com.lazzy.stories.adapter.ListStoryAdapter
import com.lazzy.stories.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding : ActivityMainBinding?= null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}