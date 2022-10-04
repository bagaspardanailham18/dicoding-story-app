package com.dicoding.intermediate.storyapp.ui.addstory

import android.Manifest
import android.animation.ObjectAnimator
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
import com.dicoding.intermediate.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.intermediate.storyapp.ui.main.MainActivity
import com.dicoding.intermediate.storyapp.utils.uriToFile
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

    private var getFile: File? = null

    private val addStoryViewModel: AddStoryViewModel by viewModels()

    companion object {
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.customToolbar)
        supportActionBar?.title = "Add Post"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSION,
                REQUEST_CODE_PERMISSION
            )
        }

        binding.btnAddImgCamera.setOnClickListener { startCamera() }
        binding.btnAddImgGallery.setOnClickListener { startGallery() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionGranted()) {
                Toast.makeText(this, "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }

        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSION.all {
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

            lifecycleScope.launch {
                addStoryViewModel.getAuthToken().observe(this@AddStoryActivity) { token ->
                    lifecycleScope.launch {
                        addStoryViewModel.uploadStory(token, imageMultipart, description).collect { result ->
                            result.onSuccess { response ->
                                setLoadingState(false)
                                Toast.makeText(this@AddStoryActivity, response.message, Toast.LENGTH_SHORT).show()
//                                startActivity(Intent(this@AddStoryActivity, MainActivity::class.java))
                                finish()
                            }
                            result.onFailure {
                                Toast.makeText(this@AddStoryActivity, it.message, Toast.LENGTH_SHORT).show()
                                setLoadingState(false)
                            }
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
}