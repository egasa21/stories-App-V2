package com.lazzy.stories.ui.auth.register

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.lazzy.stories.databinding.ActivityRegisterBinding
import com.lazzy.stories.ui.auth.login.LoginActivity
import com.lazzy.stories.util.UserPreferences
import com.lazzy.stories.util.Result
import com.lazzy.stories.util.ViewModelFactory

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var registerViewModel: RegisterViewModel
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_key")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
    }

    private fun setupViewModel() {
        val preferences = UserPreferences.getInstance(dataStore)
        registerViewModel = ViewModelProvider(this, ViewModelFactory(preferences))[RegisterViewModel::class.java]

        registerViewModel.register.observe(this) {
            when (it) {
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, it.data, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
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

    private fun setupView() {
        with(binding) {
            tvLogin.setOnClickListener(this@RegisterActivity)
            btnRegister.setOnClickListener(this@RegisterActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.tvLogin -> startActivity(Intent(this, LoginActivity::class.java))
            binding.btnRegister -> {
                val name = binding.etName.text.toString()
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()

                if (binding.etEmail.error == null && binding.etEmail.error == null) {
                    closeKeyboard(this)
                    registerViewModel.registerPost(name, email, password)
                }
            }
        }
    }

    private fun closeKeyboard(activity: AppCompatActivity) {
        val view : View? = activity.currentFocus
        if (view != null){
            val imm : InputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}