package com.lazzy.stories.ui.stories

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.lazzy.stories.R
import com.lazzy.stories.databinding.ActivityCreateStoryBinding
import com.lazzy.stories.ui.camera.CameraActivity
import com.lazzy.stories.util.UserPreferences
import com.lazzy.stories.util.ViewModelFactory
import java.io.File
import com.lazzy.stories.util.Result
import com.lazzy.stories.util.reduceFileImage
import com.lazzy.stories.util.rotateBitmap
import com.lazzy.stories.util.uriToFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@Suppress("DEPRECATION")
class CreateStoryActivity : AppCompatActivity(), View.OnClickListener {

    private var _binding: ActivityCreateStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var createStoryViewModel: CreateStoryViewModel
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_key")
    private var imageScaleZoom = true
    private var getFile: File? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLastLocation()
            }
        }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission_alert),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.elevation = 0f
        supportActionBar?.title = "AddStories"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupViewModel()
        setupView()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getMyLastLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    Toast.makeText(
                        this@CreateStoryActivity,
                        "location at $lat, $lon",
                        Toast.LENGTH_SHORT
                    ).show()
                    uploadImage(lat, lon)
                } else {
                    // Handle the case when last known location is null
                    Toast.makeText(
                        this@CreateStoryActivity,
                        "Last known location is not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                this@CreateStoryActivity,
                "Need your permission to use this",
                Toast.LENGTH_SHORT
            ).show()
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }



    private fun setupView() {
        with(binding) {
            ivPreview.setOnClickListener(this@CreateStoryActivity)
            btnCamera.setOnClickListener(this@CreateStoryActivity)
            btnGallery.setOnClickListener(this@CreateStoryActivity)
            btnUpload.setOnClickListener(this@CreateStoryActivity)
        }
    }

    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        createStoryViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[CreateStoryViewModel::class.java]
        createStoryViewModel.uploadStories.observe(this) {
            when (it) {
                is Result.Success -> {
                    Toast.makeText(this, it.data, Toast.LENGTH_SHORT).show()
                    finish()
                    showLoading(false)
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
            binding.ivPreview -> {
                imageScaleZoom = !imageScaleZoom
                binding.ivPreview.scaleType = if (imageScaleZoom) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_CENTER
            }
            binding.btnGallery -> startGallery()
            binding.btnCamera -> startCamera()
            binding.btnUpload -> getMyLastLocation()
        }
    }

    private fun startCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCamera.launch(intent)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )
            getFile = myFile
            binding.ivPreview.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@CreateStoryActivity)
            getFile = myFile
            binding.ivPreview.setImageURI(selectedImg)
        }
    }

    private fun uploadImage(lat: Double, lon: Double) {
        if (getFile != null) {
            showLoading(true)
            val file = reduceFileImage(getFile as File)

            val description =
                binding.etDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            CoroutineScope(Dispatchers.IO).launch {
                createStoryViewModel.upload(imageMultipart, description, lat, lon)
            }

        } else {
            Toast.makeText(this,
                getString(R.string.alert_upload_story),
                Toast.LENGTH_SHORT).show()
        }
    }

}