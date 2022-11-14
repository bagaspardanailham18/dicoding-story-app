package com.dicoding.intermediate.storyapp.ui.addstory

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.intermediate.storyapp.R
import com.dicoding.intermediate.storyapp.data.DataStoreViewModel
import com.dicoding.intermediate.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.intermediate.storyapp.ui.main.MainActivity
import com.dicoding.intermediate.storyapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding

    private lateinit var currentPhotoPath: String

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var getFile: File? = null

    private val addStoryViewModel: AddStoryViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    private lateinit var token: String

    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0

    companion object {
        private val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private val LOCATION_PERMISSION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        private const val REQUEST_CODE_CAMERA_PERMISSION = 10
        private const val REQUEST_CODE_LOCATION_PERMISSION = 20
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActionBar()
        setActions()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (locationPermissionGranted()) {
            getCurrentLocation()
        }

        dataStoreViewModel.getAuthToken().observe(this) {
            if (it != null) {
                token = it
            }
        }
    }

    private fun setActions() {
        binding.btnAddImgCamera.setOnClickListener {
            if (!cameraPermissionGranted()) {
                ActivityCompat.requestPermissions(this,
                    CAMERA_PERMISSION, REQUEST_CODE_CAMERA_PERMISSION)
            } else {
                startCamera()
            }
        }

        binding.btnAddImgGallery.setOnClickListener { startGallery() }
        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (locationPermissionGranted()) {
                // binding.switchLocation.isChecked = true

                getCurrentLocation()
                if (isChecked) {
                    // setSwitchLocation()
                    binding.tvLatlong.visibility = View.VISIBLE
                    Log.d("AddStoryActivity", "Lat: $latitude, Lon: $longitude")
                } else {
                    binding.tvLatlong.visibility = View.INVISIBLE
                }
            } else {
                ActivityCompat.requestPermissions(this, LOCATION_PERMISSION, REQUEST_CODE_LOCATION_PERMISSION)
                binding.switchLocation.isChecked = false
                // requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                this.latitude = location.latitude
                this.longitude = location.longitude
                binding.tvLatlong.text = """
                                    Lat : ${location.latitude}
                                    Lon : ${location.longitude}
                                """.trimIndent()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (!cameraPermissionGranted()) {
                Toast.makeText(this, "Camera permission is not granted.", Toast.LENGTH_SHORT).show()
                // finish()
            }
        } else if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (!locationPermissionGranted()) {
                Toast.makeText(this, "Location permission is not granted.", Toast.LENGTH_SHORT).show()
                binding.switchLocation.isChecked = false
            } else {
                binding.switchLocation.isChecked = true
            }
        }
    }

    private fun cameraPermissionGranted() = CAMERA_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun locationPermissionGranted() = LOCATION_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        com.dicoding.intermediate.storyapp.utils.createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this, "com.dicoding.intermediate.storyapp", it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.tvImage.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            getFile = myFile
            binding.tvImage.setImageURI(selectedImg)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_story_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_post) {
            uploadStory()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun uploadStory() {
        if (binding.edtCaption.text.toString().isEmpty()) {
            Toast.makeText(this@AddStoryActivity, "Description is required!", Toast.LENGTH_SHORT).show()
        } else if (getFile == null) {
            Toast.makeText(this@AddStoryActivity, "Silakan masukkan berkas gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
        } else {
            setLoadingState(true)

            val file = reduceFileImage(getFile as File)

            val description = binding.edtCaption.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val latitude = if (binding.switchLocation.isChecked) this.latitude?.toFloat() else null
            val longitude = if (binding.switchLocation.isChecked) this.longitude?.toFloat() else null

            lifecycleScope.launch {
                addStoryViewModel.uploadStory(token, imageMultipart, description, latitude, longitude).observe(this@AddStoryActivity) { result ->
                    when (result) {
                        is com.dicoding.intermediate.storyapp.data.remote.Result.Success -> {
                            setLoadingState(false)
                            Toast.makeText(this@AddStoryActivity, result.data.message, Toast.LENGTH_SHORT).show()
                            Log.d("uploaded", "$latitude and $longitude")
                            finish()
                        }
                        is com.dicoding.intermediate.storyapp.data.remote.Result.Error -> {
                            Toast.makeText(this@AddStoryActivity, "Terjadi kesalahan" + result.error, Toast.LENGTH_SHORT).show()
                            setLoadingState(false)
                        }
                    }
                }
            }
        }
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun setLoadingState(state: Boolean) {
        binding.apply {
            if (state) {
                ObjectAnimator.ofFloat(binding.progressBar, View.ALPHA, 1f).start()
            } else {
                ObjectAnimator.ofFloat(binding.progressBar, View.ALPHA, 0f).start()
            }
        }
    }

    private fun setActionBar() {
        setSupportActionBar(binding.customToolbar)
        supportActionBar?.title = "Add Post"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}