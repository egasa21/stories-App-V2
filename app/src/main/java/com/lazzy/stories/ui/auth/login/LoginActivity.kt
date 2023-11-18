package com.lazzy.stories.ui.auth.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.lazzy.stories.R
import com.lazzy.stories.databinding.ActivityLoginBinding
import com.lazzy.stories.ui.auth.register.RegisterActivity
import com.lazzy.stories.ui.main.MainActivity
import com.lazzy.stories.util.Result
import com.lazzy.stories.util.UserPreferences
import com.lazzy.stories.util.ViewModelFactory

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_key")
    private var mShouldFinish = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupView()
    }

    override fun onStop() {
        super.onStop()
        if (mShouldFinish)
            finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupView(){
        with(binding) {
            tvRegister.setOnClickListener(this@LoginActivity)
            btnLogin.setOnClickListener(this@LoginActivity)
        }
    }


    private fun setupViewModel(){
        val pref = UserPreferences.getInstance(dataStore)
        loginViewModel = ViewModelProvider(this, ViewModelFactory(pref))[LoginViewModel::class.java]
        loginViewModel.login.observe(this){
            when(it){
                is Result.Success -> {
                    showLoading(false)
                    startActivity(Intent(this, MainActivity::class.java))
                    mShouldFinish = true
                }
                is Result.Loading -> showLoading(true)
                is Result.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
       binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.tvRegister -> startActivity(Intent(this, RegisterActivity::class.java))
            binding.btnLogin -> {
                if (binding.etEmail.error == null && binding.etPassword.error == null && !binding.etEmail.text.isNullOrEmpty() && !binding.etPassword.text.isNullOrEmpty()){
                    val email = binding.etEmail.text.toString()
                    val password = binding.etPassword.text.toString()

                    loginViewModel.loginPost(email, password)
                }else{
                    Toast.makeText(this, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}