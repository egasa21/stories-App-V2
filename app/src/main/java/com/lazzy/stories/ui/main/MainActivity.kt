package com.lazzy.stories.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lazzy.stories.R
import com.lazzy.stories.adapter.ListStoryAdapter
import com.lazzy.stories.databinding.ActivityMainBinding
import com.lazzy.stories.ui.auth.login.LoginActivity
import com.lazzy.stories.ui.maps.MapsActivity
import com.lazzy.stories.ui.pagging.PagingViewModel
import com.lazzy.stories.ui.stories.CreateStoryActivity
import com.lazzy.stories.util.UserPreferences
import com.lazzy.stories.util.ViewModelFactory
import com.lazzy.stories.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _binding : ActivityMainBinding?= null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private var storyAdapter = ListStoryAdapter()
    private val Context.datastore : DataStore<Preferences> by preferencesDataStore(name = "user_key")

    private val pagingViewModel : PagingViewModel by viewModels {
        PagingViewModel.ViewModelFactory()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupView()
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    private fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            mainViewModel.getAllStories()
        }
    }

    private fun setupView() {
        with(binding.rvStories){
            adapter = storyAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupViewModel() {
        val preferences = UserPreferences.getInstance(datastore)
        val viewModelFactory = ViewModelFactory(preferences)
        viewModelFactory.setApplication(application)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        mainViewModel.stories.observe(this){
            when(it){
                is Result.Success -> {
                    it.data?.let {
                        dataStories(preferences)
                    }
                    showLoading(false)
                }
                is Result.Loading -> showLoading(true)
                is Result.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
        }

        binding.btnAddStory.setOnClickListener {
            startActivity(Intent(this, CreateStoryActivity::class.java))
        }
        fetchData()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun dataStories(preferences: UserPreferences) {
        mainViewModel.getUserKey().observe(this){
            pagingViewModel.getPagingStories(preferences).observe(this) {
                data -> storyAdapter.submitData(lifecycle, data)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_languages -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.menu_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                mainViewModel.logout()
                startActivity(Intent(this, LoginActivity::class.java))
                Toast.makeText(this@MainActivity, getString(R.string.alert_logout), Toast.LENGTH_SHORT).show()
                true
            }
            else -> true
        }
    }
}